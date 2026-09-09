/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.meta.tools.github;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.io.nonblocking.NonBlockingBufferedReader;
import com.helger.base.string.StringParser;
import com.helger.base.version.Version;
import com.helger.io.file.FileHelper;
import com.helger.io.file.FileSystemIterator;
import com.helger.io.file.IFileFilter;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

public class MainCheckGitHubActionVersions
{
  private enum EAction
  {
    CACHE ("actions/cache", 6),
    CHECKOUT ("actions/checkout", 7),
    SETUP_JAVA ("actions/setup-java", 6),
    CODEQL_INIT ("github/codeql-action/init", 4),
    CODEQL_AUTOBUILD ("github/codeql-action/autobuild", 4),
    CODEQL_ANALYZE ("github/codeql-action/analyze", 4),
    DOCKER_LOGIN ("docker/login-action", 4),
    POSTGRES ("ikalnytskyi/action-setup-postgres", 8),
    MONGODB ("supercharge/mongodb-github-action", "1.12.1");

    private final String m_sName;
    private final String m_sSearch;
    private final String m_sReplace;
    private final Predicate <String> m_aNeedsUpdateTest;

    EAction (final String sName, final int nLatestVersion)
    {
      this (sName, sName + "@v", sName + "@v" + nLatestVersion, sFileVersion -> {
        final int nFileVersion = StringParser.parseInt (sFileVersion, -1);
        return nFileVersion > 0 && nLatestVersion > nFileVersion;
      });
    }

    EAction (final String sName, final String sLatestVersion)
    {
      this (sName, sName + "@", sName + "@" + sLatestVersion, sFileVersion -> {
        final Version aFileVersion = Version.parse (sFileVersion);
        return aFileVersion != null && Version.parse (sLatestVersion).compareTo (aFileVersion) > 0;
      });
    }

    EAction (final String sName, final String sSearch, final String sReplace, final Predicate <String> aTest)
    {
      m_sName = sName;
      m_sSearch = sSearch;
      m_sReplace = sReplace;
      m_aNeedsUpdateTest = aTest;
    }

    @NonNull
    static String findInLineWhenOutOfDate (@NonNull final String sLine)
    {
      if (!sLine.startsWith ("#"))
        for (final EAction e : values ())
        {
          final int n = sLine.indexOf (e.m_sSearch);
          if (n > 0)
          {
            final int nStartIndex = n + e.m_sSearch.length ();
            final String sFileVersion = sLine.substring (nStartIndex);
            if (e.m_aNeedsUpdateTest.test (sFileVersion))
            {
              LOGGER.warn ("  Action " + e.m_sName + " uses " + sFileVersion + " but an update");

              // Replace
              return sLine.replace (e.m_sSearch + sFileVersion, e.m_sReplace);
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
    for (final IProject aProject : ProjectList.getAllProjects (p -> p.isPhProject () &&
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
