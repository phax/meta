/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.meta.tools.cmdline;

import java.io.File;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.CMeta;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Create a set of batch files that contains content that in most cases is
 * relevant to all projects.
 *
 * @author Philip Helger
 */
public final class MainCreateBatchFiles extends AbstractProjectMain
{
  private static void _createBatchFile (@Nonnull @Nonempty final String sCommand,
                                        @Nonnull @Nonempty final String sBatchFileName,
                                        final boolean bWithErrorCheck)
  {
    final ICommonsList <IProject> aProjects = ProjectList.getAllProjects (aProject -> aProject.isBuildInProject () &&
                                                                                      !aProject.isDeprecated () &&
                                                                                      !aProject.isNestedProject ());

    final StringBuilder aSB = new StringBuilder ();
    aSB.append (BATCH_HEADER);
    int nIndex = 1;
    for (final IProject aProject : aProjects)
    {
      aSB.append ("echo ")
         .append (aProject.getProjectName ())
         .append (" [")
         .append (nIndex)
         .append ('/')
         .append (aProjects.size ())
         .append ("]\ncd ")
         .append (aProject.getFullBaseDirName ())
         .append ("\n")
         .append (sCommand);
      if (bWithErrorCheck)
        aSB.append ("\nif errorlevel 1 goto ").append (getBatchLabel ("error", aProject));
      aSB.append ("\ncd ..\n");
      ++nIndex;
    }
    aSB.append ("goto end\n");

    if (bWithErrorCheck)
    {
      nIndex = 1;
      for (final IProject aProject : aProjects)
      {
        aSB.append (':')
           .append (getBatchLabel ("error", aProject))
           .append ("\necho .\necho Error building ")
           .append (aProject.getProjectName ())
           .append (" [")
           .append (nIndex)
           .append ('/')
           .append (aProjects.size ())
           .append ("]\ngoto error\n");
        ++nIndex;
      }
    }

    aSB.append (BATCH_FOOTER);
    SimpleFileIO.writeFile (new File (CMeta.GIT_BASE_DIR, sBatchFileName), aSB.toString (), BATCH_CHARSET);
  }

  private static void _createMvnBatchFile (@Nonnull @Nonempty final String sMavenCommand,
                                           @Nonnull @Nonempty final String sBatchFileName)
  {
    _createBatchFile ("call mvn " + sMavenCommand + " %*", sBatchFileName, true);
  }

  public static void main (final String [] args)
  {
    _createMvnBatchFile ("license:format", "mvn_license_format.cmd");
    _createMvnBatchFile ("dependency:go-offline dependency:sources", "mvn_dependency_go_offline_and_sources.cmd");
    _createMvnBatchFile ("clean", "mvn_clean.cmd");
    _createMvnBatchFile ("clean install", "mvn_clean_install.cmd");
    _createMvnBatchFile ("clean install -DskipTests=true", "mvn_clean_install_skip_tests.cmd");
    _createMvnBatchFile ("clean install sonar:sonar", "mvn_clean_install_sonar.cmd");
    _createMvnBatchFile ("clean install forbiddenapis:check forbiddenapis:testCheck",
                         "mvn_clean_install_forbiddenapis.cmd");
    _createBatchFile ("call mvn javadoc:javadoc %* > ../javadoc-results.txt 2>&1", "mvn_javadoc.cmd", true);
    _createBatchFile ("git pull", "git_pull.cmd", true);
    _createBatchFile ("git gc", "git_gc.cmd", true);
    _createBatchFile ("git add . -u\n" +
                      "git commit -m \"Saving files before refreshing line endings\"\n" +
                      "git rm --cached -r .\n" +
                      "git reset --hard\n" +
                      "git add .\n" +
                      "git commit -m \"Normalize all the line endings\"",
                      "git_normalize_crlf.cmd",
                      false);
    System.out.println ("Batch files created in " + CMeta.GIT_BASE_DIR);
  }
}
