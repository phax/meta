/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Defines all the deprecated projects.
 *
 * @author Philip Helger
 */
@SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
public enum EProjectDeprecated implements IProject
{
  CIPA_START_JMS_API (null, "cipa-start-jms-api", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.5.0"),
  CIPA_START_JMSRECEIVER (null,
                          "cipa-start-jmsreceiver",
                          EProjectType.JAVA_LIBRARY,
                          EHasPages.FALSE,
                          EHasWiki.FALSE,
                          "1.0.2"),
  CIPA_START_JMSSENDER (null,
                        "cipa-start-jmssender",
                        EProjectType.JAVA_WEB_APPLICATION,
                        EHasPages.FALSE,
                        EHasWiki.FALSE,
                        "1.0.2"),
  JGATSP (null, "jgatsp", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null),

  PEPPOL_LIME_PARENT_POM (null,
                          IProject.DEFAULT_PROJECT_OWNER,
                          "peppol-lime-parent-pom",
                          "peppol-lime",
                          EProjectType.MAVEN_POM,
                          EHasPages.FALSE,
                          EHasWiki.FALSE,
                          "3.0.1",
                          EJDK.JDK8),
  PEPPOL_LIME_API (PEPPOL_LIME_PARENT_POM, "peppol-lime-api", EProjectType.JAVA_LIBRARY),
  PEPPOL_LIME_CLIENT (PEPPOL_LIME_PARENT_POM, "peppol-lime-client", EProjectType.JAVA_LIBRARY),
  PEPPOL_LIME_SERVER (PEPPOL_LIME_PARENT_POM, "peppol-lime-server", EProjectType.JAVA_WEB_APPLICATION),

  PH_BOOTSTRAP3 (null, "ph-bootstrap3", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.2"),
  PH_JDK5 (null, "ph-jdk5", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_JMS (null, "ph-jms", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.0"),
  PH_TINYMCE4 (null, "ph-tinymce4", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "0.3.5"),
  PH_WEBAPP_DEMO (null, "ph-webapp-demo", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_WEBBASICS (null, "ph-webbasics", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.0.2"),
  PH_WEBCTRLS (null, "ph-webctrls", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.0.2"),
  PH_WEBSCOPES (null, "ph-webscopes", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "6.8.1"),
  PH_PDF_LAYOUT (null,
                 IProject.DEFAULT_PROJECT_OWNER,
                 "ph-pdf-layout",
                 "ph-pdf-layout",
                 EProjectType.JAVA_LIBRARY,
                 EHasPages.FALSE,
                 EHasWiki.FALSE,
                 "3.5.3",
                 EJDK.JDK8),

  PH_HTML_PARENT_POM (null,
                      IProject.DEFAULT_PROJECT_OWNER,
                      "ph-html-parent-pom",
                      "ph-html",
                      EProjectType.MAVEN_POM,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "6.0.4",
                      EJDK.JDK8),
  PH_HTML (PH_HTML_PARENT_POM, "ph-html", EProjectType.JAVA_LIBRARY),
  PH_HTML_JSCODE (PH_HTML_PARENT_POM, "ph-html-jscode", EProjectType.JAVA_LIBRARY),
  PH_HTML_JQUERY (PH_HTML_PARENT_POM, "ph-html-jquery", EProjectType.JAVA_LIBRARY),

  PH_LOCALES_PARENT_POM (null,
                         IProject.DEFAULT_PROJECT_OWNER,
                         "ph-locales-parent-pom",
                         "ph-locales",
                         EProjectType.MAVEN_POM,
                         EHasPages.FALSE,
                         EHasWiki.FALSE,
                         "2.0.0",
                         EJDK.JDK8),
  PH_LOCALES (PH_LOCALES_PARENT_POM, "ph-locales", EProjectType.JAVA_LIBRARY),
  PH_LOCALES16 (PH_LOCALES_PARENT_POM, "ph-locales16", EProjectType.JAVA_LIBRARY),

  PH_STX_PARENT_POM (null,
                     IProject.DEFAULT_PROJECT_OWNER,
                     "ph-stx-parent-pom",
                     "ph-stx",
                     EProjectType.MAVEN_POM,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     null,
                     EJDK.JDK8),
  PH_STX_PARSER (PH_STX_PARENT_POM, "ph-stx-parser", EProjectType.JAVA_LIBRARY),
  PH_STX_ENGINE (PH_STX_PARENT_POM, "ph-stx-engine", EProjectType.JAVA_LIBRARY),

  PH_DEE_PARENT_POM (null,
                     IProject.DEFAULT_PROJECT_OWNER,
                     "ph-dee-parent-pom",
                     "ph-dee",
                     EProjectType.MAVEN_POM,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     null,
                     EJDK.JDK8),
  PH_DEE_API (PH_DEE_PARENT_POM, "ph-dee-api", EProjectType.JAVA_LIBRARY),
  PH_DEE_ENGINE (PH_DEE_PARENT_POM, "ph-dee-engine", EProjectType.JAVA_LIBRARY),

  PH_WSDL_GEN (null,
               IProject.DEFAULT_PROJECT_OWNER,
               "ph-wsdl-gen",
               "ph-wsdl-gen",
               EProjectType.JAVA_LIBRARY,
               EHasPages.FALSE,
               EHasWiki.FALSE,
               null,
               EJDK.JDK8),
  PH_ZEROMQ (null,
             IProject.DEFAULT_PROJECT_OWNER,
             "ph-zeromq",
             "ph-zeromq",
             EProjectType.JAVA_LIBRARY,
             EHasPages.FALSE,
             EHasWiki.FALSE,
             null,
             EJDK.JDK8),
  PH_UBL_JAXB_PLUGIN (null,
                      IProject.DEFAULT_PROJECT_OWNER,
                      "ph-ubl-jaxb-plugin",
                      "ph-ubl-jaxb-plugin",
                      EProjectType.OTHER_PLUGIN,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "6.1.0",
                      EJDK.JDK8),
  // 8.1.3 is the last
  PH_OTON_BASIC (null,
                 IProject.DEFAULT_PROJECT_OWNER,
                 "ph-oton",
                 "ph-oton-basic",
                 EProjectType.JAVA_LIBRARY,
                 EHasPages.FALSE,
                 EHasWiki.FALSE,
                 "8.1.3",
                 EJDK.JDK8),

  PEPPOL_DIRECTORY_PARENT_POM (null,
                               IProject.DEFAULT_PROJECT_OWNER,
                               "peppol-directory-parent-pom",
                               "peppol-directory",
                               EProjectType.MAVEN_POM,
                               EHasPages.FALSE,
                               EHasWiki.FALSE,
                               "0.7.2",
                               EJDK.JDK8),
  PEPPOL_DIRECTORY_BUSINESSCARD (PEPPOL_DIRECTORY_PARENT_POM,
                                 "peppol-directory-businesscard",
                                 EProjectType.JAVA_LIBRARY),
  PEPPOL_DIRECTORY_INDEXER (PEPPOL_DIRECTORY_PARENT_POM, "peppol-directory-indexer", EProjectType.JAVA_LIBRARY),
  PEPPOL_DIRECTORY_PUBLISHER (PEPPOL_DIRECTORY_PARENT_POM,
                              "peppol-directory-publisher",
                              EProjectType.JAVA_WEB_APPLICATION),
  PEPPOL_DIRECTORY_CLIENT (PEPPOL_DIRECTORY_PARENT_POM, "peppol-directory-client", EProjectType.JAVA_LIBRARY),
  PEPPOL_DIRECTORY_SEARCHAPI (PEPPOL_DIRECTORY_PARENT_POM, "peppol-directory-searchapi", EProjectType.JAVA_LIBRARY),

  PH_XPATH2 (null,
             IProject.DEFAULT_PROJECT_OWNER,
             "ph-xpath2",
             "ph-xpath2",
             EProjectType.JAVA_LIBRARY,
             EHasPages.FALSE,
             EHasWiki.FALSE,
             "0.1.0",
             EJDK.JDK8),

  PH_AS4_PARENT_POM (null,
                     IProject.DEFAULT_PROJECT_OWNER,
                     "ph-as4-parent-pom",
                     "ph-as4",
                     EProjectType.MAVEN_POM,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     "0.8.2",
                     EJDK.JDK8),
  PH_AS4_LIB (PH_AS4_PARENT_POM, "ph-as4-lib", EProjectType.JAVA_LIBRARY),
  PH_AS4_ESENS (PH_AS4_PARENT_POM, "ph-as4-esens", EProjectType.JAVA_LIBRARY),
  PH_AS4_SERVLET (PH_AS4_PARENT_POM, "ph-as4-servlet", EProjectType.JAVA_LIBRARY),
  PH_AS4_SERVER_WEBAPP_DEMO (PH_AS4_PARENT_POM, "ph-as4-server-webapp-demo", EProjectType.JAVA_WEB_APPLICATION),
  PH_AS4_SERVER_WEBAPP_TEST (PH_AS4_PARENT_POM, "ph-as4-server-webapp-test", EProjectType.JAVA_WEB_APPLICATION),

  TOOP_PARENT_POM (null,
                   IProject.PROJECT_OWNER_TOOP,
                   "toop-parent-pom",
                   "toop-parent-pom",
                   EProjectType.MAVEN_POM,
                   EHasPages.FALSE,
                   EHasWiki.FALSE,
                   "1.1.1",
                   EJDK.JDK8),

  MAVEN_JAXB2_PLUGIN_PROJECT (null,
                              IProject.DEFAULT_PROJECT_OWNER,
                              "maven-jaxb2-plugin-project",
                              "maven-jaxb2-plugin",
                              EProjectType.MAVEN_POM,
                              EHasPages.FALSE,
                              EHasWiki.FALSE,
                              "0.14.1",
                              EJDK.JDK8),
  MAVEN_JAXB22_PLUGIN_CORE (MAVEN_JAXB2_PLUGIN_PROJECT,
                            "maven-jaxb22-plugin-core",
                            "plugin-core",
                            EProjectType.MAVEN_PLUGIN),
  MAVEN_JAXB22_PLUGIN (MAVEN_JAXB2_PLUGIN_PROJECT, "maven-jaxb22-plugin", "plugin-2.2", EProjectType.MAVEN_PLUGIN),
  MAVEN_JAXB23_PLUGIN (MAVEN_JAXB2_PLUGIN_PROJECT, "maven-jaxb23-plugin", "plugin-2.3", EProjectType.MAVEN_PLUGIN),
  MAVEN_JAXB2_PLUGIN (MAVEN_JAXB2_PLUGIN_PROJECT, "maven-jaxb2-plugin", "plugin", EProjectType.MAVEN_PLUGIN),;

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
  private EProjectDeprecated (@Nonnull final EProjectDeprecated eParentProject,
                              @Nonnull @Nonempty final String sProjectName,
                              @Nonnull final EProjectType eProjectType)
  {
    // Project name equals project base directory name
    this (eParentProject, sProjectName, sProjectName, eProjectType);
  }

  private EProjectDeprecated (@Nonnull final EProjectDeprecated eParentProject,
                              @Nonnull @Nonempty final String sProjectName,
                              @Nonnull @Nonempty final String sProjectBaseDirName,
                              @Nonnull final EProjectType eProjectType)
  {
    this (eParentProject,
          IProject.DEFAULT_PROJECT_OWNER,
          sProjectName,
          sProjectBaseDirName,
          eProjectType,
          EHasPages.FALSE,
          EHasWiki.FALSE,
          eParentProject.getLastPublishedVersionString (),
          EJDK.JDK8);
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
          IProject.DEFAULT_PROJECT_OWNER,
          sProjectName,
          sProjectName,
          eProjectType,
          eHasPagesProject,
          eHasWikiProject,
          sLastPublishedVersion,
          EJDK.JDK8);
  }

  @SuppressFBWarnings ("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
  private EProjectDeprecated (@Nullable final EProjectDeprecated eParentProject,
                              @Nonnull @Nonempty final String sProjectOwner,
                              @Nonnull @Nonempty final String sProjectName,
                              @Nonnull @Nonempty final String sProjectBaseDirName,
                              @Nonnull final EProjectType eProjectType,
                              @Nonnull final EHasPages eHasPagesProject,
                              @Nonnull final EHasWiki eHasWikiProject,
                              @Nullable final String sLastPublishedVersion,
                              @Nonnull final EJDK eMinJDK)
  {
    m_aProject = new SimpleProject (eParentProject,
                                    sProjectOwner,
                                    sProjectName,
                                    eProjectType,
                                    new File (eParentProject != null ? eParentProject.getBaseDir ()
                                                                     : CMeta.GIT_BASE_DIR,
                                              sProjectBaseDirName),
                                    EIsDeprecated.TRUE,
                                    eHasPagesProject,
                                    eHasWikiProject,
                                    sLastPublishedVersion,
                                    eMinJDK,
                                    true);
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
