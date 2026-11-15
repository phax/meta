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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.io.stream.StreamHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.json.IJson;
import com.helger.json.IJsonObject;
import com.helger.json.serialize.JsonReader;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EHostingPlatform;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Check for all active phax projects on GitHub, whether the currently logged in GitHub user is
 * watching the repository with "All Activity". Repositories that are not watched, that are ignored
 * or whose subscription state cannot be determined are reported.<br>
 * Requires the GitHub CLI <code>gh</code> to be installed and logged in.
 *
 * @author Philip Helger
 */
public final class MainCheckGitHubWatchStatus extends AbstractProjectMain
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCheckGitHubWatchStatus.class);

  /**
   * Invoke <code>gh api</code> for the provided path and parse the result as a JSON object. In
   * case of an HTTP error, <code>gh</code> exits with a non-zero exit code but still prints the
   * JSON error body (containing a <code>status</code> property) on stdout, so the parsed body is
   * returned in that case as well.
   *
   * @param aProject
   *        The project for logging purposes.
   * @param sAPIPath
   *        The GitHub REST API path, relative to <code>https://api.github.com/</code>.
   * @return <code>null</code> if invoking <code>gh</code> or parsing the result failed.
   */
  @Nullable
  private static IJsonObject _callGitHubAPIForObject (@NonNull final IProject aProject, @NonNull final String sAPIPath)
  {
    try
    {
      // stderr is discarded, because an expected 404 would otherwise be logged
      final Process aProcess = new ProcessBuilder ("gh", "api", sAPIPath).redirectError (
                                                                                         ProcessBuilder.Redirect.DISCARD)
                                                                         .start ();
      final String sOutput = StreamHelper.getAllBytesAsString (aProcess.getInputStream (), StandardCharsets.UTF_8);
      final int nExitCode = aProcess.waitFor ();
      final IJson aJson = JsonReader.builder ().source (sOutput).read ();
      if (aJson == null || !aJson.isObject ())
      {
        _warn (aProject, "'gh api " + sAPIPath + "' failed with exit code " + nExitCode + " and no JSON object");
        return null;
      }
      return aJson.getAsObject ();
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

  private static boolean _isNotFound (@NonNull final IJsonObject aObj)
  {
    return "404".equals (aObj.getAsString ("status"));
  }

  private static void _checkProject (@NonNull final IProject aProject, @NonNull final ICommonsList <String> aProblems)
  {
    final String sRepo = aProject.getProjectOwner ().getGitOrgaName () + "/" + aProject.getBaseDir ().getName ();

    final IJsonObject aSubscription = _callGitHubAPIForObject (aProject, "repos/" + sRepo + "/subscription");
    if (aSubscription == null)
    {
      aProblems.add (_getLogPrefix (aProject) + sRepo + ": failed to determine the subscription");
      return;
    }

    final String sMsg;
    if (_isNotFound (aSubscription))
    {
      // 404 means either "not watching" or "repository does not exist"
      final IJsonObject aRepo = _callGitHubAPIForObject (aProject, "repos/" + sRepo);
      if (aRepo != null && _isNotFound (aRepo))
        sMsg = sRepo + ": repository does not exist on GitHub";
      else
        sMsg = sRepo + ": not watching";
    }
    else
      if (aSubscription.getAsString ("status") != null)
        sMsg = sRepo +
               ": GitHub API error " +
               aSubscription.getAsString ("status") +
               " - " +
               aSubscription.getAsString ("message");
      else
        if (aSubscription.getAsBoolean ("ignored", false))
          sMsg = sRepo + ": ignoring";
        else
          if (!aSubscription.getAsBoolean ("subscribed", false))
            sMsg = sRepo + ": not subscribed to all activity";
          else
          {
            _info (aProject, sRepo + ": watching all activity");
            return;
          }

    _warn (aProject, sMsg);
    aProblems.add (_getLogPrefix (aProject) + sMsg);
  }

  public static void main (final String [] args)
  {
    final ICommonsList <IProject> aProjects = ProjectList.getAllProjects (p -> p.isPhProject () &&
                                                                               !p.isDeprecated () &&
                                                                               !p.isNestedProject () &&
                                                                               p.getHostingPlatform () == EHostingPlatform.GITHUB &&
                                                                               p.getBaseDir ().exists ());
    LOGGER.info ("Checking the GitHub watch status of " + aProjects.size () + " projects");

    final ICommonsList <String> aProblems = new CommonsArrayList <> ();
    for (final IProject aProject : aProjects)
      _checkProject (aProject, aProblems);

    if (aProblems.isEmpty ())
      LOGGER.info ("All " + aProjects.size () + " repositories are watched with all activity");
    else
    {
      LOGGER.warn (aProblems.size () + " repository(s) not watched with all activity:");
      for (final String sProblem : aProblems)
        LOGGER.warn ("  " + sProblem);
    }
    LOGGER.info ("Done with " + getWarnCount () + " warning(s)");
  }
}
