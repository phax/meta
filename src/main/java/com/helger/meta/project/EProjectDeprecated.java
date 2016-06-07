/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
import com.helger.commons.version.Version;
import com.helger.meta.CMeta;

/**
 * Defines all the deprecated projects.
 *
 * @author Philip Helger
 */
public enum EProjectDeprecated implements IProject
{
  CIPA_START_JMS_API (null, "cipa-start-jms-api", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.5.0"),
  CIPA_START_JMSRECEIVER (null, "cipa-start-jmsreceiver", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.2"),
  CIPA_START_JMSSENDER (null, "cipa-start-jmssender", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, "1.0.2"),
  JGATSP (null, "jgatsp", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null),

  PH_AS4_PARENT_POM ("ph-as4-parent-pom", "ph-as4", EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_AS4_LIB (PH_AS4_PARENT_POM, "ph-as4-lib", EProjectType.JAVA_LIBRARY),
  PH_AS4_SERVER (PH_AS4_PARENT_POM, "ph-as4-server", EProjectType.JAVA_WEB_APPLICATION),

  PH_BOOTSTRAP3 (null, "ph-bootstrap3", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.2"),
  PH_JDK5 (null, "ph-jdk5", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_JMS (null, "ph-jms", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.0"),
  PH_SCOPES (null, "ph-scopes", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "6.6.0"),
  PH_TINYMCE4 (null, "ph-tinymce4", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "0.3.5"),
  PH_WEBAPP_DEMO (null, "ph-webapp-demo", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_WEBBASICS (null, "ph-webbasics", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.0.2"),
  PH_WEBCTRLS (null, "ph-webctrls", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.0.2"),
  PH_WEBSCOPES (null, "ph-webscopes", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "6.8.1");

  private final SimpleProject m_aProject;

  /**
   * Constructor for parent poms
   *
   * @param eParentProject
   * @param sProjectName
   *        Project name as stated in the POM
   * @param sProjectBaseDirName
   *        Project base directory name
   * @param eHasPagesProject
   *        pages project present?
   * @param eHasWikiProject
   *        wiki project present?
   * @param sLastPublishedVersion
   *        Last published version or <code>null</code>
   * @param eMinJDK
   *        Minimum JDK version to use
   */
  private EProjectDeprecated (@Nonnull @Nonempty final String sProjectName,
                              @Nonnull @Nonempty final String sProjectBaseDirName,
                              @Nonnull final EHasPages eHasPagesProject,
                              @Nonnull final EHasWiki eHasWikiProject,
                              @Nullable final String sLastPublishedVersion)
  {
    this (null,
          sProjectName,
          sProjectBaseDirName,
          EProjectType.MAVEN_POM,
          eHasPagesProject,
          eHasWikiProject,
          sLastPublishedVersion);
  }

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
  private EProjectDeprecated (@Nonnull final EProjectDeprecated eParentProject,
                              @Nonnull @Nonempty final String sProjectName,
                              @Nonnull final EProjectType eProjectType)
  {
    this (eParentProject,
          sProjectName,
          eProjectType,
          EHasPages.FALSE,
          EHasWiki.FALSE,
          eParentProject.getLastPublishedVersionString ());
  }

  private EProjectDeprecated (@Nullable final EProjectDeprecated eParentProject,
                              @Nonnull @Nonempty final String sProjectName,
                              @Nonnull final EProjectType eProjectType,
                              @Nonnull final EHasPages eHasPagesProject,
                              @Nonnull final EHasWiki eHasWikiProject,
                              @Nullable final String sLastPublishedVersion)
  {
    // Project name equals project base directory name
    this (eParentProject,
          sProjectName,
          sProjectName,
          eProjectType,
          eHasPagesProject,
          eHasWikiProject,
          sLastPublishedVersion);
  }

  private EProjectDeprecated (@Nullable final EProjectDeprecated eParentProject,
                              @Nonnull @Nonempty final String sProjectName,
                              @Nonnull @Nonempty final String sProjectBaseDirName,
                              @Nonnull final EProjectType eProjectType,
                              @Nonnull final EHasPages eHasPagesProject,
                              @Nonnull final EHasWiki eHasWikiProject,
                              @Nullable final String sLastPublishedVersion)
  {
    m_aProject = new SimpleProject (eParentProject,
                                    sProjectName,
                                    eProjectType,
                                    new File (eParentProject != null ? eParentProject.getBaseDir ()
                                                                     : CMeta.GIT_BASE_DIR,
                                              sProjectBaseDirName),
                                    EIsDeprecated.TRUE,
                                    eHasPagesProject,
                                    eHasWikiProject,
                                    sLastPublishedVersion,
                                    EJDK.JDK6);
  }

  public boolean isBuildInProject ()
  {
    return true;
  }

  @Nullable
  public IProject getParentProject ()
  {
    return m_aProject.getParentProject ();
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
  public File getPOMFile ()
  {
    return m_aProject.getPOMFile ();
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
