package com.helger.meta.project;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.version.Version;

public interface IProject
{
  @Nonnull
  @Nonempty
  String getProjectName ();

  @Nonnull
  File getBaseDir ();

  @Nonnull
  File getPOMFile ();

  @Nonnull
  EProjectType getProjectType ();

  boolean isDeprecated ();

  /**
   * @return <code>true</code> if this project has the auto-generated
   *         <code>gh-pages</code> branch.
   */
  boolean hasPagesProject ();

  @Nonnull
  @Nonempty
  String getPagesProjectName ();

  /**
   * @return <code>true</code> if this project has a special Wiki project.
   */
  boolean hasWikiProject ();

  @Nonnull
  @Nonempty
  String getWikiProjectName ();

  /**
   * @return <code>true</code> if this project had at least one release,
   *         <code>false</code> if not.
   */
  boolean isPublished ();

  @Nullable
  String getLastPublishedVersionString ();

  @Nullable
  Version getLastPublishedVersion ();

  int compareTo (@Nonnull IProject aProject);
}
