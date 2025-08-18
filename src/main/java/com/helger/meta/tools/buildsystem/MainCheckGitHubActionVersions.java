/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.meta.tools.buildsystem;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.io.nonblocking.NonBlockingBufferedReader;
import com.helger.base.string.StringParser;
import com.helger.io.file.FileHelper;
import com.helger.io.file.FileSystemIterator;
import com.helger.io.file.IFileFilter;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

import jakarta.annotation.Nonnull;

public class MainCheckGitHubActionVersions
{
  private enum EAction
  {
    CACHE ("actions/cache", 4),
    CHECKOUT ("actions/checkout", 4),
    SETUP_JAVA ("actions/setup-java", 4),
    CODEQL_INIT ("github/codeql-action/init", 3),
    CODEQL_AUTOBUILD ("github/codeql-action/autobuild", 3),
    CODEQL_ANALYZE ("github/codeql-action/analyze", 3);

    private final String m_sName;
    private final String m_sSearch;
    private final int m_nLastVersion;

    EAction (final String sName, final int nVersion)
    {
      m_sName = sName;
      m_sSearch = sName + "@v";
      m_nLastVersion = nVersion;
    }

    @Nonnull
    static String findInLineWhenOutOfDate (@Nonnull final String sLine)
    {
      if (!sLine.startsWith ("#"))
        for (final EAction e : values ())
        {
          final int n = sLine.indexOf (e.m_sSearch);
          if (n > 0)
          {
            final int nStart = n + e.m_sSearch.length ();
            final int nVersion = StringParser.parseInt (sLine.substring (nStart), -1);
            if (nVersion > 0 && nVersion < e.m_nLastVersion)
            {
              LOGGER.warn ("  Action " + e.m_sName + " uses v" + nVersion + " but requires v" + e.m_nLastVersion);

              // Replace
              final String sSearchWithVer = e.m_sSearch + nVersion;
              final String sNewWithVer = e.m_sSearch + e.m_nLastVersion;
              return sLine.replace (sSearchWithVer, sNewWithVer);
            }
            break;
          }
        }
      return sLine;
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (MainCheckGitHubActionVersions.class);

  public static void main (final String [] args) throws Exception
  {
    for (final IProject aProject : ProjectList.getAllProjects (p -> p.isBuildInProject () &&
                                                                    p.getBaseDir ().exists () &&
                                                                    !p.isDeprecated ()))
    {
      final File fGitHub = new File (aProject.getBaseDir (), ".github/workflows");
      if (fGitHub.isDirectory ())
      {
        for (final File fYml : new FileSystemIterator (fGitHub).withFilter (IFileFilter.filenameEndsWith (".yml")))
        {
          LOGGER.info (aProject.getProjectName () + " - " + fYml.getName ());
          final StringBuilder aNewFile = new StringBuilder ();
          boolean bChanged = false;
          try (final NonBlockingBufferedReader r = FileHelper.getBufferedReader (fYml, StandardCharsets.UTF_8))
          {
            String sLine = null;
            while ((sLine = r.readLine ()) != null)
            {
              final String sNewLine = EAction.findInLineWhenOutOfDate (sLine);
              if (!bChanged && !sNewLine.equals (sLine))
                bChanged = true;
              aNewFile.append (sNewLine).append ('\n');
            }
          }
          if (bChanged)
          {
            LOGGER.info ("  Writing updated YML file");
            SimpleFileIO.writeFile (fYml, aNewFile.toString (), StandardCharsets.UTF_8);
          }
        }
      }
    }
    LOGGER.info ("Done");
  }
}
