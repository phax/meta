package com.helger.meta;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.string.StringHelper;

/**
 * Defines all the available projects.
 *
 * @author Philip Helger
 */
public enum EProject
{
  AS2_LIB ("as2-lib", EProjectType.JAVA_LIBRARY),
  AS2_SERVER ("as2-server", EProjectType.JAVA_APPLICATION),
  BOTANIK_MANAGER ("botanik-manager", EProjectType.JAVA_WEB_APPLICATION),
  CIPA_START_JMS_API ("cipa-start-jms-api", EProjectType.JAVA_LIBRARY),
  CIPA_START_JMSRECEIVER ("cipa-start-jmsreceiver", EProjectType.JAVA_LIBRARY),
  CIPA_START_JMSSENDER ("cipa-start-jmssender", EProjectType.JAVA_WEB_APPLICATION),
  ERECHNUNG_WS_CLIENT ("erechnung.gv.at-webservice-client", EProjectType.JAVA_LIBRARY),
  JCODEMODEL ("jcodemodel", EProjectType.JAVA_LIBRARY),
  JGATSP ("jgatsp", EProjectType.JAVA_LIBRARY),
  META ("meta", EProjectType.JAVA_APPLICATION),
  PEPPOL_SBDH ("peppol-sbdh", EProjectType.JAVA_LIBRARY),
  PH_BOOTSTRAP3 ("ph-bootstrap3", EProjectType.JAVA_LIBRARY),
  PH_BUILDINFO_MAVEN_PLUGIN ("ph-buildinfo-maven-plugin", EProjectType.MAVEN_PLUGIN),
  PH_CHARSET ("ph-charset", EProjectType.JAVA_LIBRARY),
  PH_COMMONS ("ph-commons", EProjectType.JAVA_LIBRARY),
  PH_CSS ("ph-css", EProjectType.JAVA_LIBRARY),
  PH_CSSCOMPRESS_MAVEN_PLUGIN ("ph-csscompress-maven-plugin", EProjectType.MAVEN_PLUGIN),
  PH_DATETIME ("ph-datetime", EProjectType.JAVA_LIBRARY),
  PH_DB_API ("ph-db-api", EProjectType.JAVA_LIBRARY),
  PH_DB_JDBC ("ph-db-jdbc", EProjectType.JAVA_LIBRARY),
  PH_DB_JPA ("ph-db-jpa", EProjectType.JAVA_LIBRARY),
  PH_DIRINDEX_MAVEN_PLUGIN ("ph-dirindex-maven-plugin", EProjectType.MAVEN_PLUGIN),
  PH_EBINTERFACE ("ph-ebinterface", EProjectType.JAVA_LIBRARY),
  PH_FONTS ("ph-fonts", EProjectType.JAVA_LIBRARY),
  PH_GENERICODE ("ph-genericode", EProjectType.JAVA_LIBRARY),
  PH_HTML ("ph-html", EProjectType.JAVA_LIBRARY),
  PH_JAVACC_MAVEN_PLUGIN ("ph-javacc-maven-plugin", EProjectType.MAVEN_PLUGIN),
  PH_JAXB22_PLUGIN ("ph-jaxb22-plugin", EProjectType.JAXB_PLUGIN),
  PH_JDK5 ("ph-jdk5", EProjectType.JAVA_LIBRARY),
  PH_JMS ("ph-jms", EProjectType.JAVA_LIBRARY),
  PH_JSCOMPRESS_MAVEN_PLUGIN ("ph-jscompress-maven-plugin", EProjectType.MAVEN_PLUGIN),
  PH_JSON ("ph-json", EProjectType.JAVA_LIBRARY),
  PH_MASTERDATA ("ph-masterdata", EProjectType.JAVA_LIBRARY),
  PH_MATH ("ph-math", EProjectType.JAVA_LIBRARY),
  PH_PARENT_POM ("ph-parent-pom", EProjectType.MAVEN_POM),
  PH_PDF_LAYOUT ("ph-pdf-layout", EProjectType.JAVA_LIBRARY),
  PH_POI ("ph-poi", EProjectType.JAVA_LIBRARY),
  PH_SBDH ("ph-sbdh", EProjectType.JAVA_LIBRARY),
  PH_SCHEDULE ("ph-schedule", EProjectType.JAVA_LIBRARY),
  PH_SCHEMATRON ("ph-schematron", EProjectType.JAVA_LIBRARY),
  PH_SCHEMATRON_TESTFILES ("ph-schematron-testfiles", EProjectType.JAVA_LIBRARY),
  PH_SCOPES ("ph-scopes", EProjectType.JAVA_LIBRARY),
  PH_SETTINGS ("ph-settings", EProjectType.JAVA_LIBRARY),
  PH_TINYMCE4 ("ph-tinymce4", EProjectType.JAVA_LIBRARY),
  PH_UBL ("ph-ubl", EProjectType.JAVA_LIBRARY),
  PH_UBL_JAXB_PLUGIN ("ph-ubl-jaxb-plugin", EProjectType.JAXB_PLUGIN),
  PH_UBL20 ("ph-ubl20", EProjectType.JAVA_LIBRARY),
  PH_UBL20_CODELISTS ("ph-ubl20-codelists", EProjectType.JAVA_LIBRARY),
  PH_UBL21 ("ph-ubl21", EProjectType.JAVA_LIBRARY),
  PH_UBL21_CODELISTS ("ph-ubl21-codelists", EProjectType.JAVA_LIBRARY),
  PH_VALIDATION ("ph-validation", EProjectType.JAVA_LIBRARY),
  PH_WEB ("ph-web", EProjectType.JAVA_LIBRARY),
  PH_WEBBASICS ("ph-webbasics", EProjectType.JAVA_LIBRARY),
  PH_WEBCTRLS ("ph-webctrls", EProjectType.JAVA_LIBRARY),
  PH_WEBSCOPES ("ph-webscopes", EProjectType.JAVA_LIBRARY),
  PH_WSDL_GEN ("ph-wsdl-gen", EProjectType.JAVA_APPLICATION),
  PH_XMLDSIG ("ph-xmldsig", EProjectType.JAVA_LIBRARY);

  private final String m_sProjectName;
  private final File m_aBaseDir;
  private final EProjectType m_eProjectType;

  private EProject (@Nonnull @Nonempty final String sProjectName, @Nonnull final EProjectType eProjectType)
  {
    m_sProjectName = sProjectName;
    m_eProjectType = eProjectType;
    m_aBaseDir = new File (CMeta.GIT_BASE_DIR, sProjectName);
    if (!m_aBaseDir.exists ())
      throw new IllegalStateException ("Project base directory does not exist: " + m_aBaseDir);
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

  @Nullable
  public static EProject getFromProjectNameOrNull (@Nullable final String sProjectName)
  {
    if (StringHelper.hasText (sProjectName))
      for (final EProject e : values ())
        if (e.getProjectName ().equals (sProjectName))
          return e;
    return null;
  }
}
