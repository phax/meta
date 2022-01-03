/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

public final class MainUpdateSTALE_YML extends AbstractProjectMain
{
  public static void main (final String [] args)
  {
    final String sContent = "# Number of days of inactivity before an issue becomes stale\n" +
                            "daysUntilStale: 90\n" +
                            "# Number of days of inactivity before a stale issue is closed\n" +
                            "daysUntilClose: 7\n" +
                            "# Issues with these labels will never be considered stale\n" +
                            "exemptLabels:\n" +
                            "  - pinned\n" +
                            "  - security\n" +
                            "# Label to use when marking an issue as stale\n" +
                            "staleLabel: wontfix\n" +
                            "# Comment to post when marking an issue as stale. Set to `false` to disable\n" +
                            "markComment: >\n" +
                            "  This issue has been automatically marked as stale because it has not had\n" +
                            "  recent activity. It will be closed if no further activity occurs. Thank you\n" +
                            "  for your contributions.\n" +
                            "# Comment to post when closing a stale issue. Set to `false` to disable\n" +
                            "closeComment: false";

    for (final IProject aProject : ProjectList.getAllProjects (p -> p.isBuildInProject () &&
                                                                    p.getBaseDir ().exists () &&
                                                                    !p.isNestedProject () &&
                                                                    !p.isGitHubPrivate ()))
    {
      final File f = new File (aProject.getBaseDir (), ".github/stale.yml");
      LOGGER.info ("Writing " + f.getAbsolutePath ());
      FileOperationManager.INSTANCE.createDirRecursiveIfNotExisting (f.getParentFile ());
      SimpleFileIO.writeFile (f, sContent, StandardCharsets.ISO_8859_1);
    }

    LOGGER.info ("Done");
  }
}
