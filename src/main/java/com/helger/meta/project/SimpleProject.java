/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.version.Version;

public class SimpleProject implements IProject
{
  public static final String EXTENSION_PAGES_PROJECT = ".pages";
  public static final String EXTENSION_WIKI_PROJECT = ".wiki";

  private final IProject m_aParentProject;
  private final String m_sProjectName;
  private final String m_sFullProjectName;
  private final EProjectType m_eProjectType;
  private final File m_aBaseDir;
  private final boolean m_bIsDeprecated;
  private final boolean m_bHasPagesProject;
  private final boolean m_bHasWikiProject;
  private final String m_sLastPublishedVersion;
  private final Version m_aLastPublishedVersion;

  public SimpleProject (@Nonnull @Nonempty final String sProjectName,
                        @Nonnull final EProjectType eProjectType,
                        @Nonnull final File aBaseDir,
                        @Nullable final String sLastPublishedVersion)
  {
    this ((IProject) null,
          sProjectName,
          eProjectType,
          aBaseDir,
          EIsDeprecated.FALSE,
          EHasPages.FALSE,
          EHasWiki.FALSE,
          sLastPublishedVersion);
  }

  public SimpleProject (@Nullable final IProject aParentProject,
                        @Nonnull @Nonempty final String sProjectName,
                        @Nonnull final EProjectType eProjectType,
                        @Nonnull final File aBaseDir,
                        @Nonnull final EIsDeprecated eIsDeprecated,
                        @Nonnull final EHasPages eHasPagesProject,
                        @Nonnull final EHasWiki eHasWikiProject,
                        @Nullable final String sLastPublishedVersion)
  {
    m_aParentProject = aParentProject;
    m_sProjectName = ValueEnforcer.notEmpty (sProjectName, "ProjectName");
    m_sFullProjectName = (aParentProject != null ? aParentProject.getFullProjectName () + "/" : "") + m_sProjectName;
    m_eProjectType = ValueEnforcer.notNull (eProjectType, "ProjectType");
    m_aBaseDir = ValueEnforcer.notNull (aBaseDir, "BaseDir");
    if (!m_aBaseDir.exists ())
      throw new IllegalStateException ("Project base directory does not exist: " + m_aBaseDir);
    m_bIsDeprecated = eIsDeprecated.isTrue ();
    m_bHasPagesProject = eHasPagesProject.isTrue ();
    m_bHasWikiProject = eHasWikiProject.isTrue ();
    m_sLastPublishedVersion = sLastPublishedVersion;
    m_aLastPublishedVersion = sLastPublishedVersion == null ? null : new Version (sLastPublishedVersion);
  }

  public boolean isBuildInProject ()
  {
    return false;
  }

  @Nullable
  public IProject getParentProject ()
  {
    return m_aParentProject;
  }

  public boolean isNestedProject ()
  {
    return m_aParentProject != null;
  }

  @Nonnull
  @Nonempty
  public String getProjectName ()
  {
    return m_sProjectName;
  }

  @Nonnull
  @Nonempty
  public String getFullProjectName ()
  {
    return m_sFullProjectName;
  }

  @Nonnull
  public EProjectType getProjectType ()
  {
    return m_eProjectType;
  }

  @Nonnull
  public File getBaseDir ()
  {
    return m_aBaseDir;
  }

  @Nonnull
  public File getPOMFile ()
  {
    return new File (m_aBaseDir, "pom.xml");
  }

  public boolean isDeprecated ()
  {
    return m_bIsDeprecated;
  }

  /**
   * @return <code>true</code> if this project has the auto-generated
   *         <code>gh-pages</code> branch.
   */
  public boolean hasPagesProject ()
  {
    return m_bHasPagesProject;
  }

  @Nonnull
  @Nonempty
  public String getPagesProjectName ()
  {
    return m_sProjectName + EXTENSION_PAGES_PROJECT;
  }

  /**
   * @return <code>true</code> if this project has a special Wiki project.
   */
  public boolean hasWikiProject ()
  {
    return m_bHasWikiProject;
  }

  @Nonnull
  @Nonempty
  public String getWikiProjectName ()
  {
    return m_sProjectName + EXTENSION_WIKI_PROJECT;
  }

  /**
   * @return <code>true</code> if this project had at least one release,
   *         <code>false</code> if not.
   */
  public boolean isPublished ()
  {
    return m_sLastPublishedVersion != null;
  }

  @Nullable
  public String getLastPublishedVersionString ()
  {
    return m_sLastPublishedVersion;
  }

  @Nullable
  public Version getLastPublishedVersion ()
  {
    return m_aLastPublishedVersion;
  }

  public int compareTo (@Nonnull final IProject aProject)
  {
    return m_sProjectName.compareTo (aProject.getProjectName ());
  }
}
