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
import com.helger.collection.commons.CommonsArrayList;
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
  /**
   * A single directory a generated shell script iterates over. This is either the directory of a
   * project or the <code>.wiki</code> directory of a project.
   *
   * @author Philip Helger
   */
  private static final class ScriptTarget
  {
    private final IProject m_aProject;
    private final boolean m_bIsWiki;

    ScriptTarget (@NonNull final IProject aProject, final boolean bIsWiki)
    {
      m_aProject = aProject;
      m_bIsWiki = bIsWiki;
    }

    @NonNull
    public EProjectOwner getProjectOwner ()
    {
      return m_aProject.getProjectOwner ();
    }

    /**
     * @return The name to be used in progress messages. E.g. <code>ph-commons-parent-pom</code> or
     *         <code>ph-commons.wiki</code>
     */
    @NonNull
    @Nonempty
    public String getDisplayName ()
    {
      return m_bIsWiki ? m_aProject.getWikiProjectName () : m_aProject.getProjectName ();
    }

    /**
     * @return The directory name relative to the local Git directory of the project owner. E.g.
     *         <code>ph-commons</code> or <code>ph-commons.wiki</code>
     */
    @NonNull
    @Nonempty
    public String getDirName ()
    {
      return m_bIsWiki ? m_aProject.getWikiProjectName () : m_aProject.getFullBaseDirName ();
    }

    /**
     * @return The name of the Git repository to be cloned. E.g. <code>ph-commons</code> or
     *         <code>ph-commons.wiki.git</code>
     */
    @NonNull
    @Nonempty
    public String getGitRepoName ()
    {
      return m_bIsWiki ? m_aProject.getWikiProjectName () + ".git" : m_aProject.getBaseDir ().getName ();
    }
  }

  @NonNull
  @Nonempty
  private static String _getBatchLabel (@NonNull final String sPrefix, final int nIndex)
  {
    return sPrefix + nIndex;
  }

  @NonNull
  private static ICommonsList <ScriptTarget> _getAllScriptTargets (final boolean bIncludeWiki)
  {
    final ICommonsList <IProject> aProjects = ProjectList.getAllProjects (x -> x.isPhProject () &&
      !x.isDeprecated () &&
      !x.isNestedProject () &&
      (x.getProjectOwner () == EProjectOwner.PROJECT_OWNER_PHAX ||
        x.getProjectOwner () == EProjectOwner.PROJECT_OWNER_HELGER_IT ||
        x.getProjectOwner () == EProjectOwner.PROJECT_OWNER_AUSTRIAPRO));

    final ICommonsList <ScriptTarget> ret = new CommonsArrayList <> ();
    for (final IProject aProject : aProjects)
    {
      ret.add (new ScriptTarget (aProject, false));
      if (bIncludeWiki && aProject.hasWikiProject ())
        ret.add (new ScriptTarget (aProject, true));
    }
    return ret;
  }

  private static void _createShellScript (@NonNull final String sPreamble,
                                          @NonNull @Nonempty final Function <ScriptTarget, String> aCommandProvider,
                                          final boolean bEchoProgress,
                                          final boolean bIncludeWiki,
                                          @NonNull @Nonempty final String sBatchFileName) throws IOException
  {
    final ICommonsList <ScriptTarget> aTargets = _getAllScriptTargets (bIncludeWiki);

    final StringBuilder aSB = new StringBuilder ();
    aSB.append (SHELL_HEADER);
    aSB.append (sPreamble);
    int nIndex = 1;
    for (final ScriptTarget aTarget : aTargets)
    {
      if (bEchoProgress)
        aSB.append ("echo ")
           .append (aTarget.getProjectOwner ().getLocalGitDirName ())
           .append ('/')
           .append (aTarget.getDisplayName ())
           .append (" [")
           .append (nIndex)
           .append ('/')
           .append (aTargets.size ())
           .append ("]\n");
      aSB.append (aCommandProvider.apply (aTarget));
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
                                                final boolean bIncludeWiki,
                                                @NonNull @Nonempty final String sBatchFileName) throws IOException
  {
    _createShellScript ("",
                        t -> "cd ../" +
                             t.getProjectOwner ().getLocalGitDirName () +
                             "/" +
                             t.getDirName () +
                             "\n" +
                             sCommand +
                             "\ncd ../../" +
                             EProjectOwner.PROJECT_OWNER_PHAX.getLocalGitDirName () +
                             "\n",
                        true,
                        bIncludeWiki,
                        sBatchFileName);
  }

  private static void _createMvnShellScript (@NonNull @Nonempty final String sMavenCommand,
                                             @NonNull @Nonempty final String sBatchFileName) throws IOException
  {
    // Wiki projects contain no POM
    _createShellScriptPerDir ("mvn " + sMavenCommand + " $@", false, sBatchFileName);
  }

  private static void _createGhSetSecretShellScript () throws IOException
  {
    final String sPreamble = "if [ $# -ne 2 ]; then\n" +
                             "  echo \"Usage: $0 <SECRET_KEY> <SECRET_VALUE>\" >&2\n" +
                             "  exit 1\n" +
                             "fi\n";
    // Wiki projects are no separate GitHub repositories and have no own secrets
    _createShellScript (sPreamble,
                        t -> "gh secret set \"$1\" --repo " +
                             t.getProjectOwner ().getGitOrgaName () +
                             "/" +
                             t.getGitRepoName () +
                             " --body \"$2\"\n",
                        true,
                        false,
                        "gh_secret_set.sh");
  }

  private static void _createGitBranchCheckShellScript () throws IOException
  {
    _createShellScript ("echo Projects on a branch other than main or master:\n",
                        t -> "cd ../" +
                             t.getProjectOwner ().getLocalGitDirName () +
                             "/" +
                             t.getDirName () +
                             "\n" +
                             "BRANCH=$(git rev-parse --abbrev-ref HEAD)\n" +
                             "if [ \"$BRANCH\" != \"main\" ] && [ \"$BRANCH\" != \"master\" ]; then\n" +
                             "  echo \"  " +
                             t.getProjectOwner ().getLocalGitDirName () +
                             "/" +
                             t.getDisplayName () +
                             ": $BRANCH\"\n" +
                             "fi\n" +
                             "cd ../../" +
                             EProjectOwner.PROJECT_OWNER_PHAX.getLocalGitDirName () +
                             "\n",
                        false,
                        true,
                        "git_branch_check.sh");
  }

  private static void _warnOnMissingWikiDirectories ()
  {
    for (final IProject aProject : ProjectList.getAllProjects (x -> x.hasWikiProject () && !x.isDeprecated ()))
    {
      final File aWikiDir = new File (aProject.getProjectOwner ().getLocalGitDir (), aProject.getWikiProjectName ());
      if (!aWikiDir.isDirectory ())
        _warn (aProject, "The Wiki directory '" + aWikiDir.getAbsolutePath () + "' does not exist");
    }
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
    _createShellScriptPerDir ("mvn javadoc:javadoc $@ > ../javadoc-results.txt 2>&1", false, "mvn_javadoc.sh");
    _createShellScriptPerDir ("git pull", true, "git_pull.sh");
    _createShellScriptPerDir ("git gc", true, "git_gc.sh");
    _createShellScriptPerDir ("git gc --auto", true, "git_gc_auto.sh");
    _createShellScriptPerDir ("git add . -u\n" +
                              "git commit -m \"Saving files before refreshing line endings\"\n" +
                              "git rm --cached -r .\n" +
                              "git reset --hard\n" +
                              "git add .\n" +
                              "git commit -m \"Normalize all the line endings\"",
                              true,
                              "git_normalize_crlf.sh");
    _createShellScriptPerDir ("git fetch --prune", true, "git_fetch_prune.sh");
    _createShellScriptPerDir ("git diff --quiet", true, "git_status.sh");
    _createShellScriptPerDir ("git reset --hard", true, "git_reset_hard.sh");
    _createShellScript ("",
                        t -> "[ ! -d " +
                             t.getDirName () +
                             " ] && git clone https://github.com/phax/" +
                             t.getGitRepoName () +
                             "\n",
                        true,
                        true,
                        "git_clone.sh");
    _createGitBranchCheckShellScript ();
    _createGhSetSecretShellScript ();

    // Enable when needed
    if (false)
      _createShellScriptPerDir ("git mv LICENSE.txt LICENSE && git commit -m \"Renamed file\" && git push",
                                false,
                                "git_migrate_x.sh");
    _warnOnMissingWikiDirectories ();
    System.out.println ("Shell scripts created in " + CMeta.GIT_BASE_DIR);
  }
}
