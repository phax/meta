/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import com.helger.commons.collection.impl.CommonsTreeSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * List all active Github repos
 *
 * @author Philip Helger
 */
public final class MainCreateRepoList extends AbstractProjectMain
{
  @Nonnull
  static String getGitHubRepoName (@Nonnull final IProject aProject)
  {
    if (aProject.isNestedProject ())
      return getGitHubRepoName (aProject.getParentProject ());
    return aProject.getBaseDir ().getName ();
  }

  public static void main (final String [] args)
  {
    final ICommonsList <IProject> aSortedProjects = ProjectList.getAllProjects (p -> p.isBuildInProject () &&
                                                                                     !p.isDeprecated ());

    final ICommonsList <String> repoNames = new CommonsTreeSet <> (aSortedProjects,
                                                                   MainCreateRepoList::getGitHubRepoName).getCopyAsList ();
    repoNames.forEach (System.out::println);
  }
}
