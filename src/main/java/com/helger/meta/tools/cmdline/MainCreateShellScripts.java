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
package com.helger.meta.tools.cmdline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.CMeta;
import com.helger.meta.project.EProjectOwner;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Create a set of Mac/Linux shell scripts that contains content that in most cases is relevant to
 * all projects.
 *
 * @author Philip Helger
 */
public final class MainCreateShellScripts extends AbstractProjectMain
{
  @NonNull
  @Nonempty
  private static String _getBatchLabel (@NonNull final String sPrefix, final int nIndex)
  {
    return sPrefix + nIndex;
  }

  private static void _createShellScript (@NonNull final String sPreamble,
                                          @NonNull @Nonempty final Function <IProject, String> aCommandProvider,
                                          @NonNull @Nonempty final String sBatchFileName) throws IOException
  {
    final ICommonsList <IProject> aProjects = ProjectList.getAllProjects (x -> x.isBuildInProject () &&
                                                                               !x.isDeprecated () &&
                                                                               !x.isNestedProject () &&
                                                                               x.getProjectOwner () ==
                                                                                                        EProjectOwner.DEFAULT_PROJECT_OWNER);

    final StringBuilder aSB = new StringBuilder ();
    aSB.append (SHELL_HEADER);
    aSB.append (sPreamble);
    int nIndex = 1;
    for (final IProject aProject : aProjects)
    {
      aSB.append ("echo ")
         .append (aProject.getProjectName ())
         .append (" [")
         .append (nIndex)
         .append ('/')
         .append (aProjects.size ())
         .append ("]\n");
      aSB.append (aCommandProvider.apply (aProject));
      ++nIndex;
    }
    aSB.append (SHELL_FOOTER);
    final File f = new File (CMeta.GIT_BASE_DIR, sBatchFileName);
    SimpleFileIO.writeFile (f, aSB.toString (), BATCH_CHARSET);

    final var aPerms = Files.getPosixFilePermissions (f.toPath ());
    aPerms.add (PosixFilePermission.OWNER_EXECUTE);
    Files.setPosixFilePermissions (f.toPath (), aPerms);
  }

  private static void _createShellScriptPerDir (@NonNull @Nonempty final String sCommand,
                                                @NonNull @Nonempty final String sBatchFileName) throws IOException
  {
    _createShellScript ("", p -> "cd " + p.getFullBaseDirName () + "\n" + sCommand + "\ncd ..\n", sBatchFileName);
  }

  private static void _createMvnShellScript (@NonNull @Nonempty final String sMavenCommand,
                                             @NonNull @Nonempty final String sBatchFileName) throws IOException
  {
    _createShellScriptPerDir ("mvn " + sMavenCommand + " $@", sBatchFileName);
  }

  private static void _createGhSetSecretShellScript () throws IOException
  {
    final String sPreamble = "if [ $# -ne 2 ]; then\n" +
                             "  echo \"Usage: $0 <SECRET_KEY> <SECRET_VALUE>\" >&2\n" +
                             "  exit 1\n" +
                             "fi\n";
    _createShellScript (sPreamble,
                        p -> "gh secret set \"$1\" --repo " +
                             p.getProjectOwner ().getGitOrgaName () +
                             "/" +
                             p.getBaseDir ().getName () +
                             " --body \"$2\"\n",
                        "gh_secret_set.sh");
  }

  public static void main (final String [] args) throws IOException
  {
    _createMvnShellScript ("license:format", "mvn_license_format.sh");
    _createMvnShellScript ("dependency:go-offline dependency:sources", "mvn_dependency_go_offline_and_sources.sh");
    _createMvnShellScript ("clean", "mvn_clean.sh");
    _createMvnShellScript ("clean install", "mvn_clean_install.sh");
    _createMvnShellScript ("clean install -DskipTests=true", "mvn_clean_install_skip_tests.sh");
    if (false)
      _createMvnShellScript ("clean install sonar:sonar", "mvn_clean_install_sonar.sh");
    _createMvnShellScript ("clean install forbiddenapis:check forbiddenapis:testCheck",
                           "mvn_clean_install_forbiddenapis.sh");
    _createShellScriptPerDir ("mvn javadoc:javadoc $@ > ../javadoc-results.txt 2>&1", "mvn_javadoc.sh");
    _createShellScriptPerDir ("git pull", "git_pull.sh");
    _createShellScriptPerDir ("git gc", "git_gc.sh");
    _createShellScriptPerDir ("git gc --auto", "git_gc_auto.sh");
    _createShellScriptPerDir ("git add . -u\n" +
                              "git commit -m \"Saving files before refreshing line endings\"\n" +
                              "git rm --cached -r .\n" +
                              "git reset --hard\n" +
                              "git add .\n" +
                              "git commit -m \"Normalize all the line endings\"",
                              "git_normalize_crlf.sh");
    _createShellScriptPerDir ("git fetch --prune", "git_fetch_prune.sh");
    _createShellScriptPerDir ("git diff --quiet", "git_status.sh");
    _createShellScriptPerDir ("git reset --hard", "git_reset_hard.sh");
    _createShellScript ("",
                        p -> "[ ! -d " +
                             p.getBaseDir ().getName () +
                             " ] && git clone https://github.com/phax/" +
                             p.getBaseDir ().getName () +
                             "\n",
                        "git_clone.sh");
    _createGhSetSecretShellScript ();
    System.out.println ("Shell scripts created in " + CMeta.GIT_BASE_DIR);
  }
}
