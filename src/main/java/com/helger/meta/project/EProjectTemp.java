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
package com.helger.meta.project;

import java.io.File;
import java.lang.reflect.Field;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.version.Version;

/**
 * Defines other projects.
 *
 * @author Philip Helger
 */
public enum EProjectTemp implements IProject
{
  @IsGitLab
  @IsPrivateRepo
  ENTWERTER (null,
             EProjectOwner.PROJECT_ECOSIO_PH,
             "entwerter-parent-pom",
             "entwerter",
             EProjectType.MAVEN_POM,
             EHasPages.FALSE,
             EHasWiki.FALSE,
             null,
             EJDK.JDK8),
  @IsGitLab
  @IsPrivateRepo
  ENTWERTER_ENGINE (ENTWERTER, "entwerter-engine", EProjectType.JAVA_LIBRARY),
  @IsGitLab
  @IsPrivateRepo
  ENTWERTER_WEBAPP (ENTWERTER, "entwerter-webapp", EProjectType.JAVA_WEB_APPLICATION);

  private final SimpleProject m_aProject;

  /**
   * Constructor for child projects where project name equals directory name and the last published
   * version is identical to the one of the parent project
   *
   * @param eParentProject
   *        Parent project. May not be <code>null</code>.
   * @param sProjectName
   *        Project name
   * @param eProjectType
   *        Project type
   */
  EProjectTemp (@NonNull final EProjectTemp eParentProject,
                @NonNull @Nonempty final String sProjectName,
                @NonNull final EProjectType eProjectType)
  {
    this (eParentProject, sProjectName, sProjectName, eProjectType);
  }

  EProjectTemp (@NonNull final EProjectTemp eParentProject,
                @NonNull @Nonempty final String sProjectName,
                @NonNull @Nonempty final String sProjectBaseDirName,
                @NonNull final EProjectType eProjectType)
  {
    this (eParentProject,
          sProjectName,
          sProjectBaseDirName,
          eProjectType,
          eParentProject.getLastPublishedVersionString ());
  }

  EProjectTemp (@NonNull final EProjectTemp eParentProject,
                @NonNull @Nonempty final String sProjectName,
                @NonNull final EProjectType eProjectType,
                @Nullable final String sLastPublishedVersion)
  {
    this (eParentProject, sProjectName, sProjectName, eProjectType, sLastPublishedVersion);
  }

  /**
   * Constructor for child projects where project name equals directory name
   *
   * @param eParentProject
   *        Parent project. May not be <code>null</code>.
   * @param sProjectName
   *        Project name
   * @param eProjectType
   *        Project type
   * @param sLastPublishedVersion
   *        Last published version
   */
  EProjectTemp (@NonNull final EProjectTemp eParentProject,
                @NonNull @Nonempty final String sProjectName,
                @NonNull @Nonempty final String sProjectBaseDirName,
                @NonNull final EProjectType eProjectType,
                @Nullable final String sLastPublishedVersion)
  {
    // Project name equals project base directory name
    this (eParentProject,
          eParentProject.getProjectOwner (),
          sProjectName,
          sProjectBaseDirName,
          eProjectType,
          EHasPages.FALSE,
          EHasWiki.FALSE,
          sLastPublishedVersion,
          eParentProject.getMinimumJDKVersion ());
  }

  /**
   * Most generic constructor.
   *
   * @param eParentProject
   *        Parent project. May be <code>null</code>.
   * @param sProjectName
   *        Project name as stated in the POM.xml
   * @param sProjectBaseDirName
   *        Project base directory name
   * @param eProjectType
   *        Project type
   * @param eHasPagesProject
   *        Has pages?
   * @param eHasWikiProject
   *        Has wiki?
   * @param sLastPublishedVersion
   *        Last published version number
   * @param eMinJDK
   *        Minimum JDK version to use
   */
  EProjectTemp (@Nullable final EProjectTemp eParentProject,
                @NonNull final EProjectOwner eProjectOwner,
                @NonNull @Nonempty final String sProjectName,
                @NonNull @Nonempty final String sProjectBaseDirName,
                @NonNull final EProjectType eProjectType,
                @NonNull final EHasPages eHasPagesProject,
                @NonNull final EHasWiki eHasWikiProject,
                @Nullable final String sLastPublishedVersion,
                @NonNull final EJDK eMinJDK)
  {
    final boolean bIsGitLab;
    try
    {
      final Field aField = EProjectTemp.class.getField (name ());
      bIsGitLab = aField.isAnnotationPresent (IsGitLab.class);
    }
    catch (final Exception ex)
    {
      throw new IllegalStateException (ex);
    }
    final boolean bIsPrivateRepo;
    try
    {
      final Field aField = EProjectTemp.class.getField (name ());
      bIsPrivateRepo = aField.isAnnotationPresent (IsPrivateRepo.class);
    }
    catch (final Exception ex)
    {
      throw new IllegalStateException (ex);
    }

    m_aProject = new SimpleProject (bIsGitLab ? EHostingPlatform.GITLAB : EHostingPlatform.GITHUB,
                                    eParentProject,
                                    eProjectOwner,
                                    sProjectName,
                                    eProjectType,
                                    new File (eParentProject != null ? eParentProject.getBaseDir () : eProjectOwner
                                                                                                                   .getLocalGitDir (),
                                              sProjectBaseDirName),
                                    EIsDeprecated.FALSE,
                                    eHasPagesProject,
                                    eHasWikiProject,
                                    sLastPublishedVersion,
                                    eMinJDK,
                                    bIsPrivateRepo);
  }

  public boolean isBuildInProject ()
  {
    return true;
  }

  @NonNull
  public EHostingPlatform getHostingPlatform ()
  {
    return m_aProject.getHostingPlatform ();
  }

  @Nullable
  public IProject getParentProject ()
  {
    return m_aProject.getParentProject ();
  }

  @NonNull
  public EProjectOwner getProjectOwner ()
  {
    return m_aProject.getProjectOwner ();
  }

  @NonNull
  @Nonempty
  public String getProjectName ()
  {
    return m_aProject.getProjectName ();
  }

  @NonNull
  public EProjectType getProjectType ()
  {
    return m_aProject.getProjectType ();
  }

  @NonNull
  public File getBaseDir ()
  {
    return m_aProject.getBaseDir ();
  }

  @NonNull
  @Nonempty
  public String getFullBaseDirName ()
  {
    return m_aProject.getFullBaseDirName ();
  }

  @NonNull
  public EJDK getMinimumJDKVersion ()
  {
    return m_aProject.getMinimumJDKVersion ();
  }

  @NonNull
  @Nonempty
  public String getMavenGroupID ()
  {
    return m_aProject.getMavenGroupID ();
  }

  @NonNull
  @Nonempty
  public String getMavenArtifactID ()
  {
    return m_aProject.getMavenArtifactID ();
  }

  public boolean isDeprecated ()
  {
    return m_aProject.isDeprecated ();
  }

  public boolean hasPagesProject ()
  {
    return m_aProject.hasPagesProject ();
  }

  public boolean hasWikiProject ()
  {
    return m_aProject.hasWikiProject ();
  }

  public String getLastPublishedVersionString ()
  {
    return m_aProject.getLastPublishedVersionString ();
  }

  @Nullable
  public Version getLastPublishedVersion ()
  {
    return m_aProject.getLastPublishedVersion ();
  }

  public int compareTo (@NonNull final IProject aProject)
  {
    return m_aProject.compareTo (aProject);
  }
}
