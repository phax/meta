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
package com.helger.meta.tools.cmdline;

import java.io.File;

import com.helger.annotation.Nonempty;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.CMeta;
import com.helger.meta.project.EProjectOwner;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

import jakarta.annotation.Nonnull;

/**
 * Create a set of batch files that contains content that in most cases is relevant to all projects.
 *
 * @author Philip Helger
 */
public final class MainCreateShellScripts extends AbstractProjectMain
{
  @Nonnull
  @Nonempty
  private static String getBatchLabel (@Nonnull final String sPrefix, final int nIndex)
  {
    return sPrefix + nIndex;
  }

  private static void _createShellScript (@Nonnull @Nonempty final String sCommand,
                                          @Nonnull @Nonempty final String sBatchFileName)
  {
    final ICommonsList <IProject> aProjects = ProjectList.getAllProjects (x -> x.isBuildInProject () &&
                                                                               !x.isDeprecated () &&
                                                                               !x.isNestedProject () &&
                                                                               x.getProjectOwner () ==
                                                                                                        EProjectOwner.DEFAULT_PROJECT_OWNER);

    final StringBuilder aSB = new StringBuilder ();
    aSB.append (SHELL_HEADER);
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
      aSB.append ("\ncd ..\n");
      ++nIndex;
    }
    aSB.append (SHELL_FOOTER);
    SimpleFileIO.writeFile (new File (CMeta.GIT_BASE_DIR, sBatchFileName), aSB.toString (), BATCH_CHARSET);
  }

  private static void _createMvnShellScript (@Nonnull @Nonempty final String sMavenCommand,
                                             @Nonnull @Nonempty final String sBatchFileName)
  {
    _createShellScript ("mvn " + sMavenCommand + " $@", sBatchFileName);
  }

  public static void main (final String [] args)
  {
    _createMvnShellScript ("license:format", "mvn_license_format.sh");
    _createMvnShellScript ("dependency:go-offline dependency:sources", "mvn_dependency_go_offline_and_sources.sh");
    _createMvnShellScript ("clean", "mvn_clean.sh");
    _createMvnShellScript ("clean install", "mvn_clean_install.sh");
    _createMvnShellScript ("clean install -DskipTests=true", "mvn_clean_install_skip_tests.sh");
    _createMvnShellScript ("clean install sonar:sonar", "mvn_clean_install_sonar.sh");
    _createMvnShellScript ("clean install forbiddenapis:check forbiddenapis:testCheck",
                           "mvn_clean_install_forbiddenapis.sh");
    _createShellScript ("mvn javadoc:javadoc $@ > ../javadoc-results.txt 2>&1", "mvn_javadoc.sh");
    _createShellScript ("git pull", "git_pull.sh");
    _createShellScript ("git gc", "git_gc.sh");
    _createShellScript ("git gc --auto", "git_gc_auto.sh");
    _createShellScript ("git add . -u\n" +
                        "git commit -m \"Saving files before refreshing line endings\"\n" +
                        "git rm --cached -r .\n" +
                        "git reset --hard\n" +
                        "git add .\n" +
                        "git commit -m \"Normalize all the line endings\"",
                        "git_normalize_crlf.sh");
    _createShellScript ("git fetch --prune", "git_fetch_prune.sh");
    System.out.println ("Shell scripts created in " + CMeta.GIT_BASE_DIR);
  }
}
