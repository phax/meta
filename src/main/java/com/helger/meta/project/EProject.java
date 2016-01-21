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
 * Defines all the active projects.
 *
 * @author Philip Helger
 */
public enum EProject implements IProject
{
  PH_PARENT_POM (null, "parent-pom", "ph-parent-pom", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "1.4.1"),

  PH_LOCALES_PARENT_POM (null, "ph-locales-parent-pom", "ph-locales", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_LOCALES (PH_LOCALES_PARENT_POM, "ph-locales", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_LOCALES_PARENT_POM.getLastPublishedVersionString ()),
  PH_LOCALES16 (PH_LOCALES_PARENT_POM, "ph-locales16", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_LOCALES_PARENT_POM.getLastPublishedVersionString ()),

  JCODEMODEL (null, "jcodemodel", EProjectType.JAVA_LIBRARY, EHasPages.TRUE, EHasWiki.FALSE, "2.8.2"),

  PH_JAVACC_MAVEN_PLUGIN (null, "ph-javacc-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.8.0"),

  PH_COMMONS_PARENT_POM (null, "ph-commons-parent-pom", "ph-commons", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "6.2.3"),
  PH_COMMONS (PH_COMMONS_PARENT_POM, "ph-commons", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_COMMONS_PARENT_POM.getLastPublishedVersionString ()),
  PH_LESS_COMMONS (PH_COMMONS_PARENT_POM, "ph-less-commons", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_JAXB (PH_COMMONS_PARENT_POM, "ph-jaxb", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_COMMONS_PARENT_POM.getLastPublishedVersionString ()),

  META (null, "meta", EProjectType.JAVA_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),

  PH_CHARSET (null, "ph-charset", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.0"),

  PH_MATH (null, "ph-math", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.0"),

  PH_DATETIME_PARENT_POM (null, "ph-datetime-parent-pom", "ph-datetime", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "4.2.1"),
  PH_DATETIME (PH_DATETIME_PARENT_POM, "ph-datetime", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_DATETIME_PARENT_POM.getLastPublishedVersionString ()),
  PH_HOLIDAY (PH_DATETIME_PARENT_POM, "ph-holiday", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_DATETIME_PARENT_POM.getLastPublishedVersionString ()),

  PH_BUILDINFO_MAVEN_PLUGIN (null, "ph-buildinfo-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.3.0"),
  PH_DIRINDEX_MAVEN_PLUGIN (null, "ph-dirindex-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.2.1"),
  PH_JAXB22_PLUGIN (null, "ph-jaxb22-plugin", EProjectType.OTHER_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.2.11.6"),
  PH_JSCOMPRESS_MAVEN_PLUGIN (null, "ph-jscompress-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.1.1"),
  PH_WSIMPORT_PLUGIN (null, "ph-wsimport-plugin", EProjectType.OTHER_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "2.2.8.1"),

  PH_DB_PARENT_POM (null, "ph-db-parent-pom", "ph-db", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "4.1.1"),
  PH_DB_API (PH_DB_PARENT_POM, "ph-db-api", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_DB_PARENT_POM.getLastPublishedVersionString ()),
  PH_DB_JDBC (PH_DB_PARENT_POM, "ph-db-jdbc", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_DB_PARENT_POM.getLastPublishedVersionString ()),
  PH_DB_JPA (PH_DB_PARENT_POM, "ph-db-jpa", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_DB_PARENT_POM.getLastPublishedVersionString ()),

  PH_WEB (null, "ph-web", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "7.1.0"),

  PH_JSON (null, "ph-json", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.1"),

  PH_CSS (null, "ph-css", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.0.1"),
  PH_CSSCOMPRESS_MAVEN_PLUGIN (null, "ph-csscompress-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, "1.5.1"),

  PH_HTML_PARENT_POM (null, "ph-html-parent-pom", "ph-html", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "5.1.1"),
  PH_HTML (PH_HTML_PARENT_POM, "ph-html", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_HTML_PARENT_POM.getLastPublishedVersionString ()),
  PH_HTML_JSCODE (PH_HTML_PARENT_POM, "ph-html-jscode", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_HTML_PARENT_POM.getLastPublishedVersionString ()),
  PH_HTML_JQUERY (PH_HTML_PARENT_POM, "ph-html-jquery", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_HTML_PARENT_POM.getLastPublishedVersionString ()),

  PH_MASTERDATA_PARENT_POM (null, "ph-masterdata-parent-pom", "ph-masterdata", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "4.1.0"),
  PH_VALIDATION (PH_MASTERDATA_PARENT_POM, "ph-validation", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_MASTERDATA_PARENT_POM.getLastPublishedVersionString ()),
  PH_MASTERDATA (PH_MASTERDATA_PARENT_POM, "ph-masterdata", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_MASTERDATA_PARENT_POM.getLastPublishedVersionString ()),
  PH_MASTERDATA_VALIDATION (PH_MASTERDATA_PARENT_POM, "ph-masterdata-validation", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_MASTERDATA_PARENT_POM.getLastPublishedVersionString ()),

  PH_SCHEDULE (null, "ph-schedule", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.1.0"),
  PH_SETTINGS (null, "ph-settings", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.0"),
  PH_SMTP (null, "ph-smtp", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.3"),

  PH_OTON_PARENT_POM (null, "ph-oton-parent-pom", "ph-oton", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "6.2.0"),
  PH_OTON_BASIC (PH_OTON_PARENT_POM, "ph-oton-basic", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_BOOTSTRAP3 (PH_OTON_PARENT_POM, "ph-oton-bootstrap3", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_BOOTSTRAP3_PAGES (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-pages", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_BOOTSTRAP3_STUB (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-stub", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_BOOTSTRAP3_UICTRLS (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-uictrls", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_CONNECT (PH_OTON_PARENT_POM, "ph-oton-connect", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_CORE (PH_OTON_PARENT_POM, "ph-oton-core", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_EXCHANGE (PH_OTON_PARENT_POM, "ph-oton-exchange", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_SECURITY (PH_OTON_PARENT_POM, "ph-oton-security", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_TINYMCE4 (PH_OTON_PARENT_POM, "ph-oton-tinymce4", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_UICORE (PH_OTON_PARENT_POM, "ph-oton-uicore", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),
  PH_OTON_UICTRLS (PH_OTON_PARENT_POM, "ph-oton-uictrls", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_OTON_PARENT_POM.getLastPublishedVersionString ()),

  // PH_FONTS (null, "ph-fonts", EProjectType.JAVA_LIBRARY, EHasPages.FALSE,
  // EHasWiki.FALSE, "2.0.0"),
  PH_FONTS_PARENT_POM (null, "ph-fonts-parent-pom", "ph-fonts", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "2.1.0"),
  PH_FONTS_API (PH_FONTS_PARENT_POM, "ph-fonts-api", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_FONTS_PARENT_POM.getLastPublishedVersionString ()),
  PH_FONTS_ALEGREYA_SANS (PH_FONTS_PARENT_POM, "ph-fonts-alegreya-sans", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_FONTS_PARENT_POM.getLastPublishedVersionString ()),
  PH_FONTS_ANAHEIM (PH_FONTS_PARENT_POM, "ph-fonts-anaheim", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_FONTS_PARENT_POM.getLastPublishedVersionString ()),
  PH_FONTS_EXO2 (PH_FONTS_PARENT_POM, "ph-fonts-exo2", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_FONTS_PARENT_POM.getLastPublishedVersionString ()),
  PH_FONTS_LATO2 (PH_FONTS_PARENT_POM, "ph-fonts-lato2", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_FONTS_PARENT_POM.getLastPublishedVersionString ()),
  PH_FONTS_OPEN_SANS (PH_FONTS_PARENT_POM, "ph-fonts-open-sans", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_FONTS_PARENT_POM.getLastPublishedVersionString ()),
  PH_FONTS_ROBOTO (PH_FONTS_PARENT_POM, "ph-fonts-roboto", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_FONTS_PARENT_POM.getLastPublishedVersionString ()),

  PH_EVENTS (null, "ph-events", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.0.0"),
  PH_PDF_LAYOUT (null, "ph-pdf-layout", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.2.4"),
  PH_POI (null, "ph-poi", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.0.1"),
  PH_XMLDSIG (null, "ph-xmldsig", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.2"),

  PH_SCHEMATRON_PARENT_POM (null, "ph-schematron-parent-pom", "ph-schematron", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "3.0.1"),
  PH_SCH2XSLT_MAVEN_PLUGIN (PH_SCHEMATRON_PARENT_POM, "ph-sch2xslt-maven-plugin", EProjectType.MAVEN_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, PH_SCHEMATRON_PARENT_POM.getLastPublishedVersionString ()),
  PH_SCHEMATRON (PH_SCHEMATRON_PARENT_POM, "ph-schematron", EProjectType.JAVA_LIBRARY, EHasPages.TRUE, EHasWiki.FALSE, PH_SCHEMATRON_PARENT_POM.getLastPublishedVersionString ()),
  PH_SCHEMATRON_TESTFILES (PH_SCHEMATRON_PARENT_POM, "ph-schematron-testfiles", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_SCHEMATRON_PARENT_POM.getLastPublishedVersionString ()),
  PH_SCHEMATRON_VALIDATOR (PH_SCHEMATRON_PARENT_POM, "ph-schematron-validator", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_SCHEMATRON_PARENT_POM.getLastPublishedVersionString ()),

  PH_UBL_PARENT_POM (null, "ph-ubl-parent-pom", "ph-ubl", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "4.5.0"),
  PH_UBL_API (PH_UBL_PARENT_POM, "ph-ubl-api", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_UBL_PARENT_POM.getLastPublishedVersionString ()),
  PH_UBL_JAXB_PLUGIN (PH_UBL_PARENT_POM, "ph-ubl-jaxb-plugin", EProjectType.OTHER_PLUGIN, EHasPages.FALSE, EHasWiki.FALSE, PH_UBL_PARENT_POM.getLastPublishedVersionString ()),
  PH_UBL_TESTFILES (PH_UBL_PARENT_POM, "ph-ubl-testfiles", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_UBL_PARENT_POM.getLastPublishedVersionString ()),
  PH_UBL20 (PH_UBL_PARENT_POM, "ph-ubl20", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_UBL_PARENT_POM.getLastPublishedVersionString ()),
  PH_UBL20_CODELISTS (PH_UBL_PARENT_POM, "ph-ubl20-codelists", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_UBL_PARENT_POM.getLastPublishedVersionString ()),
  PH_UBL21 (PH_UBL_PARENT_POM, "ph-ubl21", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_UBL_PARENT_POM.getLastPublishedVersionString ()),
  PH_UBL21_CODELISTS (PH_UBL_PARENT_POM, "ph-ubl21-codelists", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PH_UBL_PARENT_POM.getLastPublishedVersionString ()),

  PH_EBINTERFACE (null, "ph-ebinterface", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.TRUE, "4.0.2"),
  PH_GENERICODE (null, "ph-genericode", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.1.1"),
  PH_SBDH (null, "ph-sbdh", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.1"),

  AS2_LIB_PARENT_POM (null, "as2-lib-parent-pom", "as2-lib", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "2.2.5"),
  AS2_LIB (AS2_LIB_PARENT_POM, "as2-lib", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, AS2_LIB_PARENT_POM.getLastPublishedVersionString ()),
  AS2_PARTNERSHIP_MONGODB (AS2_LIB_PARENT_POM, "as2-partnership-mongodb", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, AS2_LIB_PARENT_POM.getLastPublishedVersionString ()),
  AS2_SERVLET (AS2_LIB_PARENT_POM, "as2-servlet", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, AS2_LIB_PARENT_POM.getLastPublishedVersionString ()),

  AS2_SERVER (null, "as2-server", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.2.5"),

  AS2_PEPPOL_CLIENT (null, "as2-peppol-client", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.4"),
  AS2_PEPPOL_SERVLET (null, "as2-peppol-servlet", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.0.0"),
  AS2_PEPPOL_SERVER (null, "as2-peppol-server", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  ERECHNUNG_WS_CLIENT (null, "webservice-client", "erechnung.gv.at-webservice-client", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.0"),

  PEPPOL_COMMONS_PARENT_POM (null, "peppol-commons-parent-pom", "peppol-commons", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "4.3.3"),
  PEPPOL_COMMONS (PEPPOL_COMMONS_PARENT_POM, "peppol-commons", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_COMMONS_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_TESTFILES (PEPPOL_COMMONS_PARENT_POM, "peppol-testfiles", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_COMMONS_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_SBDH (PEPPOL_COMMONS_PARENT_POM, "peppol-sbdh", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_COMMONS_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_SML_CLIENT (PEPPOL_COMMONS_PARENT_POM, "peppol-sml-client", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_COMMONS_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_SMP_CLIENT (PEPPOL_COMMONS_PARENT_POM, "peppol-smp-client", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_COMMONS_PARENT_POM.getLastPublishedVersionString ()),

  PEPPOL_VALIDATION_ENGINE_PARENT_POM (null, "peppol-validation-engine-parent-pom", "peppol-validation-engine", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, null),
  PEPPOL_VALIDATION_ENGINE (PEPPOL_VALIDATION_ENGINE_PARENT_POM, "peppol-validation-engine", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.0"),

  PEPPOL_SMP_SERVER_PARENT_POM (null, "peppol-smp-server-parent-pom", "peppol-smp-server", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.TRUE, "4.1.0"),
  PEPPOL_SMP_SERVER_LIBRARY (PEPPOL_SMP_SERVER_PARENT_POM, "peppol-smp-server-library", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_SMP_SERVER_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_SMP_SERVER_SQL (PEPPOL_SMP_SERVER_PARENT_POM, "peppol-smp-server-sql", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_SMP_SERVER_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_SMP_SERVER_XML (PEPPOL_SMP_SERVER_PARENT_POM, "peppol-smp-server-xml", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_SMP_SERVER_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_SMP_SERVER_WEBAPP (PEPPOL_SMP_SERVER_PARENT_POM, "peppol-smp-server-webapp", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_SMP_SERVER_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_SMP_SERVER_WEBAPP_SQLMIN (PEPPOL_SMP_SERVER_PARENT_POM, "peppol-smp-server-webapp-sqlmin", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),

  PEPPOL_LIME_PARENT_POM (null, "peppol-lime-parent-pom", "peppol-lime", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "3.0.1"),
  PEPPOL_LIME_API (PEPPOL_LIME_PARENT_POM, "peppol-lime-api", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_LIME_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_LIME_CLIENT (PEPPOL_LIME_PARENT_POM, "peppol-lime-client", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_LIME_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_LIME_SERVER (PEPPOL_LIME_PARENT_POM, "peppol-lime-server", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_LIME_PARENT_POM.getLastPublishedVersionString ()),

  PEPPOL_DIRECTORY_PARENT_POM (null, "peppol-directory-parent-pom", "peppol-directory", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, null),
  PEPPOL_DIRECTORY_API (PEPPOL_DIRECTORY_PARENT_POM, "peppol-directory-api", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_DIRECTORY_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_DIRECTORY_INDEXER (PEPPOL_DIRECTORY_PARENT_POM, "peppol-directory-indexer", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_DIRECTORY_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_DIRECTORY_PUBLISHER (PEPPOL_DIRECTORY_PARENT_POM, "peppol-directory-publisher", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_DIRECTORY_PARENT_POM.getLastPublishedVersionString ()),
  PEPPOL_DIRECTORY_CLIENT (PEPPOL_DIRECTORY_PARENT_POM, "peppol-directory-client", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, PEPPOL_DIRECTORY_PARENT_POM.getLastPublishedVersionString ()),

  PH_AS4_PARENT_POM (null, "ph-as4-parent-pom", "ph-as4", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_AS4_LIB (PH_AS4_PARENT_POM, "ph-as4-lib", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_AS4_SERVER (PH_AS4_PARENT_POM, "ph-as4-server", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),

  PH_WSDL_GEN (null, "ph-wsdl-gen", EProjectType.JAVA_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_XPATH2 (null, "ph-xpath2", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null),

  BOZOO (null, "bozoo", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PEPPOL_PRACTICAL (null, "peppol-practical", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null);

  private final SimpleProject m_aProject;

  private EProject (@Nullable final EProject eParentProject,
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

  private EProject (@Nullable final EProject eParentProject,
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
