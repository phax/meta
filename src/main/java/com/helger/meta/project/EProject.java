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

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.version.Version;
import com.helger.meta.CMeta;

/**
 * Defines all the active projects.
 *
 * @author Philip Helger
 */
public enum EProject implements IProject
{
  AS2_LIB (null, "as2-lib", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.8"),
  AS2_PEPPOL_CLIENT (null, "as2-peppol-client", EProjectType.JAVA_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  AS2_PEPPOL_SERVLET (null, "as2-peppol-servlet", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.1"),
  AS2_PEPPOL_SERVER (null, "as2-peppol-server", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  AS2_SERVER (null, "as2-server", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.1"),
  AS4_LIB (null, "as4-lib", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null),
  BOZOO (null, "bozoo", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  ERECHNUNG_WS_CLIENT (null, "webservice-client", "erechnung.gv.at-webservice-client", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.0"),
  JCODEMODEL (null, "jcodemodel", EProjectType.JAVA_LIBRARY, EHasPages.TRUE, EHasWiki.FALSE, "2.7.9"),
  META (null, "meta", EProjectType.JAVA_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PEPPOL_COMMONS (null, "peppol-commons", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.1.3"),
  PEPPOL_PRACTICAL (null, "peppol-practical", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PEPPOL_SBDH (null, "peppol-sbdh", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.1"),
  PEPPOL_SML_CLIENT (null, "peppol-sml-client", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.1.3"),
  PEPPOL_SMP_CLIENT (null, "peppol-smp-client", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.1.3"),
  PEPPOL_SMP_SERVER (null, "peppol-smp-server", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, "3.1.0"),
  PEPPOL_SMP_SERVER_LIBRARY (null, "peppol-smp-server-library", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.1.3"),
  PEPPOL_SMP_SERVER_LIGHTWEIGHT (null, "peppol-smp-server-lightweight", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, "3.1.0"),
  PEPPOL_VALIDATION_ENGINE (null, "peppol-validation-engine", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_BUILDINFO_MAVEN_PLUGIN (null, "ph-buildinfo-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.2.2"),
  PH_CHARSET (null, "ph-charset", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.1"),
  PH_COMONS (null, "ph-commons", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "5.7.1"),
  PH_CSS (null, "ph-css", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.9.2"),
  PH_CSSCOMPRESS_MAVEN_PLUGIN (null, "ph-csscompress-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.3.3"),
  PH_DATETIME (null, "ph-datetime", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.1"),
  PH_DB_API (null, "ph-db-api", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_DB_JDBC (null, "ph-db-jdbc", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.1"),
  PH_DB_JPA (null, "ph-db-jpa", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.2"),
  PH_DIRINDEX_MAVEN_PLUGIN (null, "ph-dirindex-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.1.1"),
  PH_EBINTERFACE (null, "ph-ebinterface", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.TRUE, "3.1.3"),
  PH_EVENTS (null, "ph-events", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.0.1"),
  PH_FONTS (null, "ph-fonts", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_GENERICODE (null, "ph-genericode", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.1"),
  PH_HTML (null, "ph-html", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.7.3"),
  PH_JAVACC_MAVEN_PLUGIN (null, "ph-javacc-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.8.0"),
  PH_JAXB22_PLUGIN (null, "ph-jaxb22-plugin", EProjectType.OTHER_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.2.11.4"),
  PH_JDK5 (null, "ph-jdk5", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_JMS (null, "ph-jms", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.2.0"),
  PH_JSCOMPRESS_MAVEN_PLUGIN (null, "ph-jscompress-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.0.0"),
  PH_JSON (null, "ph-json", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.3.3"),
  PH_MASTERDATA (null, "ph-masterdata", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.8.0"),
  PH_MATH (null, "ph-math", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.2.2"),
  PH_OTON_PARENT_POM (null, "ph-oton-parent-pom", "ph-oton", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "5.0.0"),
  PH_OTON_BASIC (PH_OTON_PARENT_POM, "ph-oton-basic", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_BOOTSTRAP3 (PH_OTON_PARENT_POM, "ph-oton-bootstrap3", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_BOOTSTRAP3_PAGES (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-pages", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_BOOTSTRAP3_STUB (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-stub", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_BOOTSTRAP3_UICTRLS (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-uictrls", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_CONNECT (PH_OTON_PARENT_POM, "ph-oton-connect", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_CORE (PH_OTON_PARENT_POM, "ph-oton-core", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_EXCHANGE (PH_OTON_PARENT_POM, "ph-oton-exchange", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_TINYMCE4 (PH_OTON_PARENT_POM, "ph-oton-tinymce4", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_UICORE (PH_OTON_PARENT_POM, "ph-oton-uicore", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_UICTRLS (PH_OTON_PARENT_POM, "ph-oton-uictrls", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_PARENT_POM (null, "parent-pom", "ph-parent-pom", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "1.3.4"),
  PH_PDF_LAYOUT (null, "ph-pdf-layout", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.3.5"),
  PH_POI (null, "ph-poi", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.9.4"),
  PH_SBDH (null, "ph-sbdh", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.1"),
  PH_SCH2XSLT_MAVEN_PLUGIN (null, "ph-sch2xslt-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.8.1"),
  PH_SCHEDULE (null, "ph-schedule", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.8.3"),
  PH_SCHEMATRON (null, "ph-schematron", EProjectType.JAVA_LIBRARY, EHasPages.TRUE, EHasWiki.FALSE, "2.9.2"),
  PH_SCHEMATRON_TESTFILES (null, "ph-schematron-testfiles", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.2"),
  PH_SCHEMATRON_VALIDATOR (null, "ph-schematron-validator", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.8.1"),
  PH_SETTINGS (null, "ph-settings", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.2"),
  PH_SMTP (null, "ph-smtp", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_UBL_PARENT_POM (null, "ph-ubl-parent-pom", "ph-ubl", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_UBL (PH_UBL_PARENT_POM, "ph-ubl", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.1"),
  PH_UBL_JAXB_PLUGIN (PH_UBL_PARENT_POM, "ph-ubl-jaxb-plugin", EProjectType.OTHER_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.2.1"),
  PH_UBL20 (PH_UBL_PARENT_POM, "ph-ubl20", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_UBL20_CODELISTS (PH_UBL_PARENT_POM, "ph-ubl20-codelists", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_UBL21 (PH_UBL_PARENT_POM, "ph-ubl21", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_UBL21_CODELISTS (PH_UBL_PARENT_POM, "ph-ubl21-codelists", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.1"),
  PH_VALIDATION (null, "ph-validation", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.5"),
  PH_WEB (null, "ph-web", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "6.4.3"),
  PH_WSDL_GEN (null, "ph-wsdl-gen", EProjectType.JAVA_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_WSIMPORT_PLUGIN (null, "ph-wsimport-plugin", EProjectType.OTHER_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.2.8"),
  PH_XMLDSIG (null, "ph-xmldsig", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.3"),
  PH_XPATH2 (null, "ph-xpath2", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null);

  private final SimpleProject m_aProject;

  private EProject (@Nonnull final EProject eParentProject,
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

  private EProject (@Nonnull final EProject eParentProject,
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
                                                                    : CMeta.GIT_BASE_DIR, sProjectBaseDirName),
                                    EIsDeprecated.FALSE,
                                    eHasPagesProject,
                                    eHasWikiProject,
                                    sLastPublishedVersion);
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

  public boolean isNestedProject ()
  {
    return m_aProject.isNestedProject ();
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

  public boolean isDeprecated ()
  {
    return m_aProject.isDeprecated ();
  }

  public boolean hasPagesProject ()
  {
    return m_aProject.hasPagesProject ();
  }

  @Nonnull
  @Nonempty
  public String getPagesProjectName ()
  {
    return m_aProject.getPagesProjectName ();
  }

  public boolean hasWikiProject ()
  {
    return m_aProject.hasWikiProject ();
  }

  @Nonnull
  @Nonempty
  public String getWikiProjectName ()
  {
    return m_aProject.getWikiProjectName ();
  }

  public boolean isPublished ()
  {
    return m_aProject.isPublished ();
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
