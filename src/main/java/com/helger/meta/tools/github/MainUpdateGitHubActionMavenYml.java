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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.string.StringReplace;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Perform customizable plain text replacements in the GitHub Action file
 * <code>.github/workflows/maven.yml</code> of all active projects. Example use case: change the JDK
 * distribution from "adopt" to "temurin". Add or remove entries in <code>REPLACEMENTS</code> to
 * customize the behaviour before running.
 *
 * @author Philip Helger
 */
public final class MainUpdateGitHubActionMavenYml extends AbstractProjectMain
{
  /**
   * A single ordered search/replace text pair applied to the whole file content.
   *
   * @author Philip Helger
   */
  private static final record Replacement (String search, String replace)
  {}

  /** The name of the GitHub Action file to modify. */
  private static final String YML_FILENAME = "maven.yml";

  /**
   * The list of text replacements to perform. Customize this list to your needs. Replacements are
   * applied in the defined order.
   */
  private static final ICommonsList <Replacement> REPLACEMENTS = new CommonsArrayList <> (new Replacement ("distribution: adopt",
                                                                                                           "distribution: temurin"),
                                                                                          new Replacement ("distribution: 'adopt'",
                                                                                                           "distribution: 'temurin'"),
                                                                                          new Replacement ("if: matrix.java == 17",
                                                                                                           "if: github.event_name == 'push' && github.ref == format('refs/heads/{0}', github.event.repository.default_branch) && matrix.java == 17"),
                                                                                          new Replacement ("if: matrix.java != 17",
                                                                                                           "if: github.event_name != 'push' || github.ref != format('refs/heads/{0}', github.event.repository.default_branch) || matrix.java != 17"));

  /** The read-only permissions block to be inserted before the top-level "jobs:" element. */
  private static final String PERMISSIONS_BLOCK = "\npermissions:\n  contents: read\n";

  private static final Logger LOGGER = LoggerFactory.getLogger (MainUpdateGitHubActionMavenYml.class);

  public static void main (final String [] args)
  {
    for (final IProject aProject : ProjectList.getAllProjects (p -> p.isBuildInProject () &&
                                                                    p.getBaseDir ().exists () &&
                                                                    !p.isDeprecated ()))
    {
      final File fYml = new File (aProject.getBaseDir (), ".github/workflows/" + YML_FILENAME);
      if (!fYml.exists ())
        continue;

      final String sCurrent = SimpleFileIO.getFileAsString (fYml, StandardCharsets.UTF_8);
      if (sCurrent == null)
      {
        _warn (aProject, "Failed to read " + fYml.getName ());
        continue;
      }

      String sNew = sCurrent;

      // Limit the GITHUB_TOKEN to read-only repository access (only if not yet present)
      if (!sNew.contains ("\npermissions:") && sNew.contains ("\njobs:\n"))
      {
        _info (aProject, "Adding read-only 'permissions' block");
        sNew = StringReplace.replaceAll (sNew, "\njobs:\n", PERMISSIONS_BLOCK + "\njobs:\n");
      }

      for (final Replacement aReplacement : REPLACEMENTS)
        if (sNew.contains (aReplacement.search))
        {
          _info (aProject, "Replacing '" + aReplacement.search + "' with '" + aReplacement.replace + "'");
          sNew = StringReplace.replaceAll (sNew, aReplacement.search, aReplacement.replace);
        }

      if (!sNew.equals (sCurrent))
      {
        _info (aProject, "Writing updated " + fYml.getName ());
        SimpleFileIO.writeFile (fYml, sNew, StandardCharsets.UTF_8);
      }
    }
    LOGGER.info ("Done");
  }
}
