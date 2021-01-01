/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.version.Version;
import com.helger.meta.CMeta;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Defines all the active TOOP projects.
 *
 * @author Philip Helger
 */
public enum EProjectToop implements IProject
{
  // toop-commons
  TOOP_COMMONS_NG_PARENT_POM (null,
                              IProject.PROJECT_OWNER_TOOP,
                              "toop-commons-ng-parent-pom",
                              "toop-commons-ng",
                              EProjectType.MAVEN_POM,
                              EHasPages.FALSE,
                              EHasWiki.FALSE,
                              "2.1.0-1",
                              EJDK.JDK8),
  TOOP_REGREP (TOOP_COMMONS_NG_PARENT_POM, "toop-regrep", EProjectType.JAVA_LIBRARY),
  TOOP_KAFKA_CLIENT (TOOP_COMMONS_NG_PARENT_POM, "toop-kafka-client", EProjectType.JAVA_LIBRARY),
  TOOP_EDM (TOOP_COMMONS_NG_PARENT_POM, "toop-edm", EProjectType.JAVA_LIBRARY),
  TOOP_COMMON (TOOP_COMMONS_NG_PARENT_POM, "toop-commons", EProjectType.JAVA_LIBRARY),

  // DSD
  DATA_SERVICES_DIRECTORY_POM (null,
                               IProject.PROJECT_OWNER_TOOP,
                               "data-services-directory",
                               "data-services-directory",
                               EProjectType.MAVEN_POM,
                               EHasPages.FALSE,
                               EHasWiki.FALSE,
                               "2.1.0-1",
                               EJDK.JDK8),
  DSD_API (DATA_SERVICES_DIRECTORY_POM, "dsd-api", EProjectType.JAVA_LIBRARY),
  DSD_CLIENT (DATA_SERVICES_DIRECTORY_POM, "dsd-client", EProjectType.JAVA_LIBRARY),
  DSD_SERVICE (DATA_SERVICES_DIRECTORY_POM, "dsd-service", EProjectType.JAVA_WEB_APPLICATION),

  // Toop Connector
  TOOP_CONNECTOR_NG_PARENT_POM (null,
                                IProject.PROJECT_OWNER_TOOP,
                                "tc-parent-pom",
                                "toop-connector-ng",
                                EProjectType.MAVEN_POM,
                                EHasPages.FALSE,
                                EHasWiki.FALSE,
                                "2.1.0-1",
                                EJDK.JDK8),
  TC_API (TOOP_CONNECTOR_NG_PARENT_POM, "tc-api", EProjectType.JAVA_LIBRARY),
  TC_MEM_EXTERNAL (TOOP_CONNECTOR_NG_PARENT_POM, "tc-mem-external", EProjectType.JAVA_LIBRARY),
  TC_MEM_PHASE4 (TOOP_CONNECTOR_NG_PARENT_POM, "tc-mem-phase4", EProjectType.JAVA_LIBRARY),
  TC_MAIN (TOOP_CONNECTOR_NG_PARENT_POM, "tc-main", EProjectType.JAVA_LIBRARY),
  TC_WEB_API (TOOP_CONNECTOR_NG_PARENT_POM, "tc-web-api", EProjectType.JAVA_LIBRARY),
  TC_WEBAPP (TOOP_CONNECTOR_NG_PARENT_POM, "tc-webapp", EProjectType.JAVA_WEB_APPLICATION),
  TC_JETTY (TOOP_CONNECTOR_NG_PARENT_POM, "tc-jetty", EProjectType.JAVA_APPLICATION);

  private final SimpleProject m_aProject;

  /**
   * Constructor for child projects where project name equals directory name and
   * the last published version is identical to the one of the parent project
   *
   * @param eParentProject
   *        Parent project. May not be <code>null</code>.
   * @param sProjectName
   *        Project name
   * @param eProjectType
   *        Project type
   */
  EProjectToop (@Nonnull final EProjectToop eParentProject,
                @Nonnull @Nonempty final String sProjectName,
                @Nonnull final EProjectType eProjectType)
  {
    this (eParentProject, sProjectName, sProjectName, eProjectType);
  }

  EProjectToop (@Nonnull final EProjectToop eParentProject,
                @Nonnull @Nonempty final String sProjectName,
                @Nonnull @Nonempty final String sProjectBaseDirName,
                @Nonnull final EProjectType eProjectType)
  {
    this (eParentProject, sProjectName, sProjectBaseDirName, eProjectType, eParentProject.getLastPublishedVersionString ());
  }

  EProjectToop (@Nonnull final EProjectToop eParentProject,
                @Nonnull @Nonempty final String sProjectName,
                @Nonnull final EProjectType eProjectType,
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
  @SuppressFBWarnings ("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
  EProjectToop (@Nonnull final EProjectToop eParentProject,
                @Nonnull @Nonempty final String sProjectName,
                @Nonnull @Nonempty final String sProjectBaseDirName,
                @Nonnull final EProjectType eProjectType,
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
  @SuppressFBWarnings ("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
  EProjectToop (@Nullable final EProjectToop eParentProject,
                @Nonnull @Nonempty final String sProjectOwner,
                @Nonnull @Nonempty final String sProjectName,
                @Nonnull @Nonempty final String sProjectBaseDirName,
                @Nonnull final EProjectType eProjectType,
                @Nonnull final EHasPages eHasPagesProject,
                @Nonnull final EHasWiki eHasWikiProject,
                @Nullable final String sLastPublishedVersion,
                @Nonnull final EJDK eMinJDK)
  {
    final boolean bIsGitLab;
    try
    {
      final Field aField = EProjectToop.class.getField (name ());
      bIsGitLab = aField.isAnnotationPresent (IsGitLab.class);
    }
    catch (final Exception ex)
    {
      throw new IllegalStateException (ex);
    }
    final boolean bIsPrivateRepo;
    try
    {
      final Field aField = EProjectToop.class.getField (name ());
      bIsPrivateRepo = aField.isAnnotationPresent (IsPrivateRepo.class);
    }
    catch (final Exception ex)
    {
      throw new IllegalStateException (ex);
    }

    m_aProject = new SimpleProject (bIsGitLab ? EHostingPlatform.GITLAB : EHostingPlatform.GITHUB,
                                    eParentProject,
                                    sProjectOwner,
                                    sProjectName,
                                    eProjectType,
                                    new File (eParentProject != null ? eParentProject.getBaseDir () : CMeta.GIT_BASE_DIR,
                                              sProjectBaseDirName.equals ("ph-pdf-layout4") ? "ph-pdf-layout" : sProjectBaseDirName),
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

  @Nonnull
  public EHostingPlatform getHostingPlatform ()
  {
    return m_aProject.getHostingPlatform ();
  }

  @Nullable
  public IProject getParentProject ()
  {
    return m_aProject.getParentProject ();
  }

  @Nonnull
  @Nonempty
  public String getProjectOwner ()
  {
    return m_aProject.getProjectOwner ();
  }

  @Nonnull
  @Nonempty
  public String getProjectName ()
  {
    return m_aProject.getProjectName ();
  }

  @Nonnull
  public EProjectType getProjectType ()
  {
    return m_aProject.getProjectType ();
  }

  @Nonnull
  public File getBaseDir ()
  {
    return m_aProject.getBaseDir ();
  }

  @Nonnull
  @Nonempty
  public String getFullBaseDirName ()
  {
    return m_aProject.getFullBaseDirName ();
  }

  @Nonnull
  public EJDK getMinimumJDKVersion ()
  {
    return m_aProject.getMinimumJDKVersion ();
  }

  @Nonnull
  @Nonempty
  public String getMavenGroupID ()
  {
    return m_aProject.getMavenGroupID ();
  }

  @Nonnull
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

  public int compareTo (@Nonnull final IProject aProject)
  {
    return m_aProject.compareTo (aProject);
  }
}
