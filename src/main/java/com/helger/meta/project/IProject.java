/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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
package com.helger.meta.project;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.StringHelper;
import com.helger.commons.version.Version;

public interface IProject
{
  String DEFAULT_PROJECT_OWNER = "phax";
  String PROJECT_OWNER_AUSTRIAPRO = "austriapro";
  String PROJECT_OWNER_TOOP = "TOOP4EU";
  String PROJECT_OWNER_CENTC434 = "CenPc434";
  String EXTENSION_PAGES_PROJECT = ".pages";
  String EXTENSION_WIKI_PROJECT = ".wiki";

  /**
   * @return <code>true</code> if this project is part of {@link EProject},
   *         <code>false</code> otherwise.
   */
  boolean isBuildInProject ();

  @Nullable
  IProject getParentProject ();

  /**
   * @return <code>true</code> if this project is a module of a parent project.
   * @see #getParentProject()
   */
  default boolean isNestedProject ()
  {
    return getParentProject () != null;
  }

  /**
   * @return The project owner. E.g. <code>phax</code>
   */
  @Nonnull
  @Nonempty
  String getProjectOwner ();

  /**
   * @return The project name. E.g. <code>ph-commons</code>
   */
  @Nonnull
  @Nonempty
  String getProjectName ();

  /**
   * @return The project type.
   */
  @Nonnull
  EProjectType getProjectType ();

  @Nonnull
  File getBaseDir ();

  /**
   * @return The complete base directory name with all parent projects included.
   *         E.g. <code>ph-oton/ph-oton-basic</code>. If a project has no parent
   *         project, the result is the same as from
   *         <code>getBaseDir ().getName ()</code>.
   */
  @Nonnull
  @Nonempty
  String getFullBaseDirName ();

  @Nonnull
  default File getPOMFile ()
  {
    return new File (getBaseDir (), "pom.xml");
  }

  @Nonnull
  EJDK getMinimumJDKVersion ();

  @Nonnull
  @Nonempty
  String getMavenGroupID ();

  default boolean hasMavenGroupID (@Nullable final String sGroupID)
  {
    return getMavenGroupID ().equals (sGroupID);
  }

  @Nonnull
  @Nonempty
  String getMavenArtifactID ();

  default boolean hasMavenArtifactID (@Nullable final String sGroupID)
  {
    return getMavenArtifactID ().equals (sGroupID);
  }

  @Nonnull
  @Nonempty
  default String getMavenID ()
  {
    return getMavenGroupID () + "::" + getMavenArtifactID () + "::" + getLastPublishedVersionString ();
  }

  default boolean isParentPOM ()
  {
    return getMavenArtifactID ().equals (EProject.PH_PARENT_POM.getMavenArtifactID ());
  }

  /**
   * @return <code>true</code> if this project is deprecated and no longer
   *         maintained, <code>false</code> for active projects
   */
  boolean isDeprecated ();

  /**
   * @return <code>true</code> if this project has the auto-generated
   *         <code>gh-pages</code> branch.
   */
  boolean hasPagesProject ();

  @Nonnull
  @Nonempty
  default String getPagesProjectName ()
  {
    return getProjectName () + EXTENSION_PAGES_PROJECT;
  }

  /**
   * @return <code>true</code> if this project has a special Wiki project.
   */
  boolean hasWikiProject ();

  @Nonnull
  @Nonempty
  default String getWikiProjectName ()
  {
    return getProjectName () + EXTENSION_WIKI_PROJECT;
  }

  /**
   * @return <code>true</code> if this project had at least one release,
   *         <code>false</code> if not.
   */
  default boolean isPublished ()
  {
    return StringHelper.hasText (getLastPublishedVersionString ());
  }

  @Nullable
  String getLastPublishedVersionString ();

  @Nullable
  Version getLastPublishedVersion ();

  /**
   * @return <code>true</code> if it is private on GitHub, <code>false</code> if
   *         it is public
   */
  default boolean isGitHubPrivate ()
  {
    return false;
  }

  int compareTo (@Nonnull IProject aProject);
}
