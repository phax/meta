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
package com.helger.meta;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.string.StringHelper;
import com.helger.commons.version.Version;

/**
 * Defines all the available projects.
 *
 * @author Philip Helger
 */
public enum EProject
{
  AS2_LIB ("as2-lib", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.5"),
  AS2_PEPPOL_CLIENT ("as2-peppol-client", EProjectType.JAVA_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  AS2_PEPPOL_SERVLET ("as2-peppol-servlet", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.0"),
  AS2_PEPPOL_SERVER ("as2-peppol-server", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  AS2_SERVER ("as2-server", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.0"),
  BOTANIK_MANAGER ("botanik-manager", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  CIPA_START_JMS_API ("cipa-start-jms-api", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.5.0"),
  CIPA_START_JMSRECEIVER ("cipa-start-jmsreceiver", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.2"),
  CIPA_START_JMSSENDER ("cipa-start-jmssender", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, "1.0.2"),
  ERECHNUNG_WS_CLIENT ("erechnung.gv.at-webservice-client", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.0"),
  JCODEMODEL ("jcodemodel", EProjectType.JAVA_LIBRARY, EHasPages.TRUE, EHasWiki.FALSE, "2.7.7"),
  JGATSP ("jgatsp", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null),
  META ("meta", EProjectType.JAVA_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PEPPOL_PRACTICAL ("peppol-practical", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PEPPOL_SBDH ("peppol-sbdh", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.0"),
  PEPPOL_VALIDATION_ENGINE ("peppol-validation-engine", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_BOOTSTRAP3 ("ph-bootstrap3", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.7.1"),
  PH_BUILDINFO_MAVEN_PLUGIN ("ph-buildinfo-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.2.1"),
  PH_CHARSET ("ph-charset", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_COMMONS ("ph-commons", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "5.3.0"),
  PH_CSS ("ph-css", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.9.0"),
  PH_CSSCOMPRESS_MAVEN_PLUGIN ("ph-csscompress-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.3.2"),
  PH_DATETIME ("ph-datetime", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.2.2"),
  PH_DB_API ("ph-db-api", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_DB_JDBC ("ph-db-jdbc", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_DB_JPA ("ph-db-jpa", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.2.2"),
  PH_DIRINDEX_MAVEN_PLUGIN ("ph-dirindex-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_EBINTERFACE ("ph-ebinterface", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.TRUE, "3.1.0"),
  PH_FONTS ("ph-fonts", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_GENERICODE ("ph-genericode", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_HTML ("ph-html", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.5.2"),
  PH_JAVACC_MAVEN_PLUGIN ("ph-javacc-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.8.0"),
  PH_JAXB22_PLUGIN ("ph-jaxb22-plugin", EProjectType.JAXB_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.2.11"),
  PH_JDK5 ("ph-jdk5", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_JMS ("ph-jms", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.2.0"),
  PH_JSCOMPRESS_MAVEN_PLUGIN ("ph-jscompress-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_JSON ("ph-json", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.3.1"),
  PH_MASTERDATA ("ph-masterdata", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.6.3"),
  PH_MATH ("ph-math", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.2.0"),
  PH_PARENT_POM ("ph-parent-pom", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "1.3.1"),
  PH_PDF_LAYOUT ("ph-pdf-layout", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.3.3"),
  PH_POI ("ph-poi", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.9.2"),
  PH_SBDH ("ph-sbdh", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_SCH2XSLT_MAVEN_PLUGIN ("ph-sch2xslt-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.8.0"),
  PH_SCHEDULE ("ph-schedule", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.6.3"),
  PH_SCHEMATRON ("ph-schematron", EProjectType.JAVA_LIBRARY, EHasPages.TRUE, EHasWiki.FALSE, "2.9.0"),
  PH_SCHEMATRON_TESTFILES ("ph-schematron-testfiles", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.2"),
  PH_SCHEMATRON_VALIDATOR ("ph-schematron-validator", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.8.0"),
  PH_SCOPES ("ph-scopes", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "6.6.0"),
  PH_SETTINGS ("ph-settings", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.1"),
  PH_TINYMCE4 ("ph-tinymce4", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "0.3.3"),
  PH_UBL ("ph-ubl", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_UBL_JAXB_PLUGIN ("ph-ubl-jaxb-plugin", EProjectType.JAXB_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.2.0"),
  PH_UBL20 ("ph-ubl20", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_UBL20_CODELISTS ("ph-ubl20-codelists", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_UBL21 ("ph-ubl21", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_UBL21_CODELISTS ("ph-ubl21-codelists", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.0"),
  PH_VALIDATION ("ph-validation", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.3.3"),
  PH_WEB ("ph-web", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "6.0.4"),
  PH_WEBAPP_DEMO ("ph-webapp-demo", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_WEBBASICS ("ph-webbasics", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.5.0"),
  PH_WEBCTRLS ("ph-webctrls", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.5.1"),
  PH_WEBSCOPES ("ph-webscopes", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "6.6.1"),
  PH_WSDL_GEN ("ph-wsdl-gen", EProjectType.JAVA_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_XMLDSIG ("ph-xmldsig", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.1");

  private final String m_sProjectName;
  private final File m_aBaseDir;
  private final EProjectType m_eProjectType;
  private final boolean m_bHasPagesProject;
  private final boolean m_bHasWikiProject;
  private final String m_sLastPublishedVersion;
  private final Version m_aLastPublishedVersion;

  private EProject (@Nonnull @Nonempty final String sProjectName,
                    @Nonnull final EProjectType eProjectType,
                    @Nonnull final EHasPages eHasPagesProject,
                    @Nonnull final EHasWiki eHasWikiProject,
                    @Nullable final String sLastPublishedVersion)
  {
    m_sProjectName = sProjectName;
    m_eProjectType = eProjectType;
    m_aBaseDir = new File (CMeta.GIT_BASE_DIR, sProjectName);
    if (!m_aBaseDir.exists ())
      throw new IllegalStateException ("Project base directory does not exist: " + m_aBaseDir);
    m_bHasPagesProject = eHasPagesProject.isTrue ();
    m_bHasWikiProject = eHasWikiProject.isTrue ();
    m_sLastPublishedVersion = sLastPublishedVersion;
    m_aLastPublishedVersion = sLastPublishedVersion == null ? null : new Version (sLastPublishedVersion);
  }

  @Nonnull
  @Nonempty
  public String getProjectName ()
  {
    return m_sProjectName;
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

  @Nonnull
  public EProjectType getProjectType ()
  {
    return m_eProjectType;
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
    return m_sProjectName + CMeta.EXTENSION_PAGES_PROJECT;
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
    return m_sProjectName + CMeta.EXTENSION_WIKI_PROJECT;
  }

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
