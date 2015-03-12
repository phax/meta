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
import com.helger.commons.string.StringHelper;
import com.helger.commons.version.Version;
import com.helger.meta.CMeta;

/**
 * Defines all the available projects.
 *
 * @author Philip Helger
 */
public enum EProject implements IProject
{
  AS2_LIB ("as2-lib", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.0.7"),
  AS2_PEPPOL_CLIENT ("as2-peppol-client", EProjectType.JAVA_APPLICATION, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, null),
  AS2_PEPPOL_SERVLET ("as2-peppol-servlet", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.0.1"),
  AS2_PEPPOL_SERVER ("as2-peppol-server", EProjectType.JAVA_WEB_APPLICATION, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, null),
  AS2_SERVER ("as2-server", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.0.1"),
  BOTANIK_MANAGER ("botanik-manager", EProjectType.JAVA_WEB_APPLICATION, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, null),
  ERECHNUNG_WS_CLIENT ("erechnung.gv.at-webservice-client", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.0.0"),
  JCODEMODEL ("jcodemodel", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.TRUE, EHasWiki.FALSE, "2.7.8"),
  META ("meta", EProjectType.JAVA_APPLICATION, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, null),
  PEPPOL_PRACTICAL ("peppol-practical", EProjectType.JAVA_WEB_APPLICATION, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, null),
  PEPPOL_SBDH ("peppol-sbdh", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.0.1"),
  PEPPOL_VALIDATION_ENGINE ("peppol-validation-engine", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_BOOTSTRAP3 ("ph-bootstrap3", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.9.1"),
  PH_BUILDINFO_MAVEN_PLUGIN ("ph-buildinfo-maven-plugin", EProjectType.MAVEN_PLUGIN, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.2.2"),
  PH_CHARSET ("ph-charset", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.1.1"),
  PH_COMMONS ("ph-commons", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "5.6.0"),
  PH_CSS ("ph-css", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.9.1"),
  PH_CSSCOMPRESS_MAVEN_PLUGIN ("ph-csscompress-maven-plugin", EProjectType.MAVEN_PLUGIN, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.3.3"),
  PH_DATETIME ("ph-datetime", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.3.1"),
  PH_DB_API ("ph-db-api", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_DB_JDBC ("ph-db-jdbc", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.1.1"),
  PH_DB_JPA ("ph-db-jpa", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.3.1"),
  PH_DIRINDEX_MAVEN_PLUGIN ("ph-dirindex-maven-plugin", EProjectType.MAVEN_PLUGIN, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.1.1"),
  PH_EBINTERFACE ("ph-ebinterface", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.TRUE, "3.1.1"),
  PH_EVENTS ("ph-events", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.0.1"),
  PH_FONTS ("ph-fonts", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_GENERICODE ("ph-genericode", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.3.1"),
  PH_HTML ("ph-html", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "4.6.2"),
  PH_JAVACC_MAVEN_PLUGIN ("ph-javacc-maven-plugin", EProjectType.MAVEN_PLUGIN, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "2.8.0"),
  PH_JAXB22_PLUGIN ("ph-jaxb22-plugin", EProjectType.OTHER_PLUGIN, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "2.2.11.3"),
  PH_JDK5 ("ph-jdk5", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_JMS ("ph-jms", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.2.0"),
  PH_JSCOMPRESS_MAVEN_PLUGIN ("ph-jscompress-maven-plugin", EProjectType.MAVEN_PLUGIN, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "2.0.0"),
  PH_JSON ("ph-json", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.3.3"),
  PH_MASTERDATA ("ph-masterdata", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.7.1"),
  PH_MATH ("ph-math", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.2.1"),
  PH_PARENT_POM ("ph-parent-pom", EProjectType.MAVEN_POM, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.3.3"),
  PH_PDF_LAYOUT ("ph-pdf-layout", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.3.4"),
  PH_POI ("ph-poi", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "2.9.3"),
  PH_SBDH ("ph-sbdh", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.1.1"),
  PH_SCH2XSLT_MAVEN_PLUGIN ("ph-sch2xslt-maven-plugin", EProjectType.MAVEN_PLUGIN, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "2.8.1"),
  PH_SCHEDULE ("ph-schedule", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.8.1"),
  PH_SCHEMATRON ("ph-schematron", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.TRUE, EHasWiki.FALSE, "2.9.2"),
  PH_SCHEMATRON_TESTFILES ("ph-schematron-testfiles", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.1.2"),
  PH_SCHEMATRON_VALIDATOR ("ph-schematron-validator", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "2.8.1"),
  PH_SETTINGS ("ph-settings", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.1.2"),
  PH_SMTP ("ph-smtp", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.0.2"),
  PH_TINYMCE4 ("ph-tinymce4", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "0.3.4"),
  PH_UBL ("ph-ubl", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.3.1"),
  PH_UBL_JAXB_PLUGIN ("ph-ubl-jaxb-plugin", EProjectType.OTHER_PLUGIN, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.2.1"),
  PH_UBL20 ("ph-ubl20", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_UBL20_CODELISTS ("ph-ubl20-codelists", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_UBL21 ("ph-ubl21", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_UBL21_CODELISTS ("ph-ubl21-codelists", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.3.1"),
  PH_VALIDATION ("ph-validation", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.3.4"),
  PH_WEB ("ph-web", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "6.4.1"),
  PH_WEBAPP_DEMO ("ph-webapp-demo", EProjectType.JAVA_WEB_APPLICATION, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_WEBBASICS ("ph-webbasics", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "3.8.1"),
  PH_WEBCTRLS ("ph-webctrls", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "2.7.1"),
  PH_WSDL_GEN ("ph-wsdl-gen", EProjectType.JAVA_APPLICATION, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_WSIMPORT_PLUGIN ("ph-wsimport-plugin", EProjectType.OTHER_PLUGIN, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "2.2.8"),
  PH_XMLDSIG ("ph-xmldsig", EProjectType.JAVA_LIBRARY, EIsDeprecated.FALSE, EHasPages.FALSE, EHasWiki.FALSE, "1.1.2"),
  // Deprecated projects:
  CIPA_START_JMS_API ("cipa-start-jms-api", EProjectType.JAVA_LIBRARY, EIsDeprecated.TRUE, EHasPages.FALSE, EHasWiki.FALSE, "1.5.0"),
  CIPA_START_JMSRECEIVER ("cipa-start-jmsreceiver", EProjectType.JAVA_LIBRARY, EIsDeprecated.TRUE, EHasPages.FALSE, EHasWiki.FALSE, "1.0.2"),
  CIPA_START_JMSSENDER ("cipa-start-jmssender", EProjectType.JAVA_WEB_APPLICATION, EIsDeprecated.TRUE, EHasPages.FALSE, EHasWiki.FALSE, "1.0.2"),
  JGATSP ("jgatsp", EProjectType.JAVA_LIBRARY, EIsDeprecated.TRUE, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_SCOPES ("ph-scopes", EProjectType.JAVA_LIBRARY, EIsDeprecated.TRUE, EHasPages.FALSE, EHasWiki.FALSE, "6.6.0"),
  PH_WEBSCOPES ("ph-webscopes", EProjectType.JAVA_LIBRARY, EIsDeprecated.TRUE, EHasPages.FALSE, EHasWiki.FALSE, "6.8.1");

  private final SimpleProject m_aProject;

  private EProject (@Nonnull @Nonempty final String sProjectName,
                    @Nonnull final EProjectType eProjectType,
                    @Nonnull final EIsDeprecated eIsDeprecated,
                    @Nonnull final EHasPages eHasPagesProject,
                    @Nonnull final EHasWiki eHasWikiProject,
                    @Nullable final String sLastPublishedVersion)
  {
    m_aProject = new SimpleProject (sProjectName,
                                    eProjectType,
                                    new File (CMeta.GIT_BASE_DIR, sProjectName),
                                    eIsDeprecated,
                                    eHasPagesProject,
                                    eHasWikiProject,
                                    sLastPublishedVersion);
  }

  public boolean isBuildInProject ()
  {
    return true;
  }

  @Nonnull
  @Nonempty
  public String getProjectName ()
  {
    return m_aProject.getProjectName ();
  }

  @Nonnull
  public File getBaseDir ()
  {
    return m_aProject.getBaseDir ();
  }

  @Nonnull
  public File getPOMFile ()
  {
    return m_aProject.getPOMFile ();
  }

  @Nonnull
  public EProjectType getProjectType ()
  {
    return m_aProject.getProjectType ();
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

  @Nullable
  public static EProject getFromProjectNameOrNull (@Nullable final String sProjectName)
  {
    if (StringHelper.hasText (sProjectName))
    {
      for (final EProject e : values ())
        if (e.getProjectName ().equals (sProjectName))
          return e;

      // Handle differences between directory name and project name
      if ("parent-pom".equals (sProjectName))
        return EProject.PH_PARENT_POM;
      if ("webservice-client".equals (sProjectName))
        return EProject.ERECHNUNG_WS_CLIENT;
    }
    return null;
  }
}
