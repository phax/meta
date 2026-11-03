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
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.io.stream.StreamHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsSet;
import com.helger.io.file.SimpleFileIO;
import com.helger.json.IJson;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.serialize.JsonReader;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EHostingPlatform;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Check for all active phax projects on GitHub that contain at least one workflow file in
 * <code>.github/workflows</code>, whether the latest completed run of each workflow on the default
 * branch (or triggered by a release) failed. Only workflows whose file is present on the default
 * branch are considered. Workflows that are not defined in a file (like the dynamic Dependabot and CodeQL
 * default setup workflows) are ignored. All failed workflows are written to
 * <code>failed-github-actions.txt</code> in the meta project directory, overwriting the previous
 * content.<br>
 * Requires the GitHub CLI <code>gh</code> to be installed and logged in.
 *
 * @author Philip Helger
 */
public final class MainCheckGitHubActionRunStatus extends AbstractProjectMain
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCheckGitHubActionRunStatus.class);
  /** The number of latest completed runs per workflow searched for a relevant run. */
  private static final int RUNS_PER_WORKFLOW = 30;
  /** The file in the meta project directory that receives all failed workflows. */
  private static final String FAILED_FILENAME = "failed-github-actions.txt";

  private static boolean _hasLocalWorkflowFile (@NonNull final IProject aProject)
  {
    final File [] aFiles = new File (aProject.getBaseDir (), ".github/workflows").listFiles ();
    if (aFiles != null)
      for (final File aFile : aFiles)
        if (aFile.isFile () && (aFile.getName ().endsWith (".yml") || aFile.getName ().endsWith (".yaml")))
          return true;
    return false;
  }

  /**
   * Invoke <code>gh api</code> for the provided path and parse the result as JSON.
   *
   * @param aProject
   *        The project for logging purposes.
   * @param sAPIPath
   *        The GitHub REST API path, relative to <code>https://api.github.com/</code>.
   * @return <code>null</code> if invoking <code>gh</code> or parsing the result failed.
   */
  @Nullable
  private static IJson _callGitHubAPI (@NonNull final IProject aProject, @NonNull final String sAPIPath)
  {
    try
    {
      final Process aProcess = new ProcessBuilder ("gh", "api", sAPIPath).redirectError (
                                                                                         ProcessBuilder.Redirect.INHERIT)
                                                                         .start ();
      final String sOutput = StreamHelper.getAllBytesAsString (aProcess.getInputStream (), StandardCharsets.UTF_8);
      final int nExitCode = aProcess.waitFor ();
      if (nExitCode != 0)
      {
        _warn (aProject, "'gh api " + sAPIPath + "' failed with exit code " + nExitCode);
        return null;
      }
      final IJson ret = JsonReader.builder ().source (sOutput).read ();
      if (ret == null)
        _warn (aProject, "Failed to parse the result of 'gh api " + sAPIPath + "' as JSON");
      return ret;
    }
    catch (final IOException ex)
    {
      LOGGER.error (_getLogPrefix (aProject) + "Failed to run 'gh api " + sAPIPath + "'", ex);
      return null;
    }
    catch (final InterruptedException ex)
    {
      Thread.currentThread ().interrupt ();
      LOGGER.error (_getLogPrefix (aProject) + "Interrupted while running 'gh api " + sAPIPath + "'", ex);
      return null;
    }
  }

  @Nullable
  private static IJsonObject _callGitHubAPIForObject (@NonNull final IProject aProject,
                                                      @NonNull final String sAPIPath)
  {
    final IJson aJson = _callGitHubAPI (aProject, sAPIPath);
    if (aJson == null)
      return null;
    if (!aJson.isObject ())
    {
      _warn (aProject, "The result of 'gh api " + sAPIPath + "' is not a JSON object");
      return null;
    }
    return aJson.getAsObject ();
  }

  /**
   * Get the paths of all workflow files that are present on the provided branch. The workflow list
   * of the GitHub API also contains workflows that only exist on other branches.
   *
   * @param aProject
   *        The project for logging purposes.
   * @param sRepo
   *        The repository in the form <code>owner/name</code>.
   * @param sBranch
   *        The branch to check.
   * @return <code>null</code> if the files could not be determined.
   */
  @Nullable
  private static ICommonsSet <String> _getAllWorkflowPathsOnBranch (@NonNull final IProject aProject,
                                                                   @NonNull final String sRepo,
                                                                   @NonNull final String sBranch)
  {
    final IJson aJson = _callGitHubAPI (aProject, "repos/" + sRepo + "/contents/.github/workflows?ref=" + sBranch);
    if (aJson == null)
      return null;
    if (!aJson.isArray ())
    {
      _warn (aProject, "The content of '.github/workflows' on branch '" + sBranch + "' is not a JSON array");
      return null;
    }

    final ICommonsSet <String> ret = new CommonsHashSet <> ();
    for (final IJsonObject aEntry : aJson.getAsArray ().iteratorObjects ())
      if ("file".equals (aEntry.getAsString ("type")))
        ret.add (aEntry.getAsString ("path"));
    return ret;
  }

  private static boolean _isFailedConclusion (@Nullable final String sConclusion)
  {
    return !"success".equals (sConclusion) && !"skipped".equals (sConclusion) && !"neutral".equals (sConclusion);
  }

  /**
   * Find the latest relevant run. Runs triggered by a release use the tag name as the head branch,
   * so they cannot be filtered by the default branch in the API call.
   *
   * @param aRunList
   *        The completed runs, newest first. May be <code>null</code>.
   * @param sDefaultBranch
   *        The default branch of the repository.
   * @return The first run that either ran on the default branch or was triggered by a release.
   *         <code>null</code> if there is no such run.
   */
  @Nullable
  private static IJsonObject _findLastRelevantRun (@Nullable final IJsonArray aRunList,
                                                   @Nullable final String sDefaultBranch)
  {
    if (aRunList != null)
      for (final IJsonObject aRun : aRunList.iteratorObjects ())
        if ("release".equals (aRun.getAsString ("event")) ||
            (sDefaultBranch != null && sDefaultBranch.equals (aRun.getAsString ("head_branch"))))
          return aRun;
    return null;
  }

  private static void _checkProject (@NonNull final IProject aProject, @NonNull final ICommonsList <String> aFailures)
  {
    final String sRepo = aProject.getProjectOwner ().getGitOrgaName () + "/" + aProject.getBaseDir ().getName ();

    final IJsonObject aRepo = _callGitHubAPIForObject (aProject, "repos/" + sRepo);
    if (aRepo == null)
      return;
    final String sDefaultBranch = aRepo.getAsString ("default_branch");

    final ICommonsSet <String> aWorkflowPathsOnDefaultBranch = _getAllWorkflowPathsOnBranch (aProject,
                                                                                             sRepo,
                                                                                             sDefaultBranch);
    if (aWorkflowPathsOnDefaultBranch == null)
      return;

    final IJsonObject aWorkflows = _callGitHubAPIForObject (aProject, "repos/" + sRepo + "/actions/workflows?per_page=100");
    if (aWorkflows == null)
      return;
    final IJsonArray aWorkflowList = aWorkflows.getAsArray ("workflows");
    if (aWorkflowList == null)
    {
      _warn (aProject, "No 'workflows' array found in the GitHub API response");
      return;
    }

    for (final IJsonObject aWorkflow : aWorkflowList.iteratorObjects ())
    {
      final String sWorkflowPath = aWorkflow.getAsString ("path");
      // Ignore dynamic workflows, disabled workflows and workflows not present on the default branch
      if (sWorkflowPath == null ||
          !sWorkflowPath.startsWith (".github/workflows/") ||
          !"active".equals (aWorkflow.getAsString ("state")) ||
          !aWorkflowPathsOnDefaultBranch.contains (sWorkflowPath))
        continue;

      final String sWorkflowName = aWorkflow.getAsString ("name") + " (" + sWorkflowPath + ")";
      final IJsonObject aRuns = _callGitHubAPIForObject (aProject,
                                                         "repos/" +
                                                                   sRepo +
                                                                   "/actions/workflows/" +
                                                                   aWorkflow.getAsString ("id") +
                                                                   "/runs?status=completed&exclude_pull_requests=true&per_page=" +
                                                                   RUNS_PER_WORKFLOW);
      if (aRuns == null)
        continue;

      final IJsonObject aLastRun = _findLastRelevantRun (aRuns.getAsArray ("workflow_runs"), sDefaultBranch);
      if (aLastRun == null)
      {
        _info (aProject,
               sWorkflowName +
                         ": no completed run on branch '" +
                         sDefaultBranch +
                         "' or from a release in the last " +
                         RUNS_PER_WORKFLOW +
                         " runs");
        continue;
      }

      final String sConclusion = aLastRun.getAsString ("conclusion");
      if (_isFailedConclusion (sConclusion))
      {
        final String sMsg = sWorkflowName +
                            ": last run on '" +
                            aLastRun.getAsString ("head_branch") +
                            "' (" +
                            aLastRun.getAsString ("event") +
                            ") from " +
                            aLastRun.getAsString ("created_at") +
                            " has conclusion '" +
                            sConclusion +
                            "' - " +
                            aLastRun.getAsString ("html_url");
        _warn (aProject, sMsg);
        aFailures.add (_getLogPrefix (aProject) + sMsg);
      }
      else
        _info (aProject, sWorkflowName + ": " + sConclusion);
    }
  }

  public static void main (final String [] args)
  {
    final ICommonsList <IProject> aProjects = ProjectList.getAllProjects (p -> p.isPhProject () &&
                                                                               !p.isDeprecated () &&
                                                                               !p.isNestedProject () &&
                                                                               p.getHostingPlatform () == EHostingPlatform.GITHUB &&
                                                                               p.getBaseDir ().exists () &&
                                                                               _hasLocalWorkflowFile (p));
    LOGGER.info ("Checking the GitHub Action runs of " + aProjects.size () + " projects");

    final ICommonsList <String> aFailures = new CommonsArrayList <> ();
    for (final IProject aProject : aProjects)
      _checkProject (aProject, aFailures);

    final StringBuilder aSB = new StringBuilder ();
    if (aFailures.isEmpty ())
      LOGGER.info ("The last run of all workflows succeeded");
    else
    {
      LOGGER.warn (aFailures.size () + " workflow(s) with a failed last run:");
      for (final String sFailure : aFailures)
      {
        LOGGER.warn ("  " + sFailure);
        aSB.append (sFailure).append ('\n');
      }
    }

    // Always overwrite, so that an empty file indicates no failures
    final File aFailedFile = new File (FAILED_FILENAME);
    SimpleFileIO.writeFile (aFailedFile, aSB.toString (), StandardCharsets.UTF_8);
    LOGGER.info ("Wrote " + aFailures.size () + " failure(s) to '" + aFailedFile.getAbsolutePath () + "'");
    LOGGER.info ("Done with " + getWarnCount () + " warning(s)");
  }
}
