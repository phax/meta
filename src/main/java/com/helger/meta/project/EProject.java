/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
  PH_PARENT_POM ("parent-pom", "ph-parent-pom", EHasPages.FALSE, EHasWiki.FALSE, "1.9.3", EJDK.JDK8),
  PH_JAXWS ("ph-jaxws", EProjectType.MAVEN_POM, EHasPages.FALSE, EHasWiki.FALSE, "1.0.3", EJDK.JDK6),
  PH_FORBIDDEN_APIS ("ph-forbidden-apis",
                     EProjectType.RESOURCES_ONLY,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     "1.0.0",
                     EJDK.JDK8),
  META ("meta", EProjectType.JAVA_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null, EJDK.JDK8),

  JCODEMODEL ("jcodemodel", EProjectType.JAVA_LIBRARY, EHasPages.TRUE, EHasWiki.FALSE, "3.0.0", EJDK.JDK8),

  PH_COMMONS_PARENT_POM ("ph-commons-parent-pom", "ph-commons", EHasPages.FALSE, EHasWiki.FALSE, "8.6.6", EJDK.JDK8),
  PH_COMMONS (PH_COMMONS_PARENT_POM, "ph-commons", EProjectType.JAVA_LIBRARY),
  PH_SECURITY (PH_COMMONS_PARENT_POM, "ph-security", EProjectType.JAVA_LIBRARY),
  PH_SCOPES (PH_COMMONS_PARENT_POM, "ph-scopes", EProjectType.JAVA_LIBRARY, (String) null),
  PH_COLLECTION (PH_COMMONS_PARENT_POM, "ph-collection", EProjectType.JAVA_LIBRARY, (String) null),
  PH_CLI (PH_COMMONS_PARENT_POM, "ph-cli", EProjectType.JAVA_LIBRARY, "2.1.0"),
  PH_XML (PH_COMMONS_PARENT_POM, "ph-xml", EProjectType.JAVA_LIBRARY),
  PH_JAXB (PH_COMMONS_PARENT_POM, "ph-jaxb", EProjectType.JAVA_LIBRARY),
  PH_TREE (PH_COMMONS_PARENT_POM, "ph-tree", EProjectType.JAVA_LIBRARY),
  PH_JSON (PH_COMMONS_PARENT_POM, "ph-json", EProjectType.JAVA_LIBRARY),
  PH_MATRIX (PH_COMMONS_PARENT_POM, "ph-matrix", EProjectType.JAVA_LIBRARY),
  PH_GRAPH (PH_COMMONS_PARENT_POM, "ph-graph", EProjectType.JAVA_LIBRARY),
  PH_SETTINGS (PH_COMMONS_PARENT_POM, "ph-settings", EProjectType.JAVA_LIBRARY),
  PH_DATETIME (PH_COMMONS_PARENT_POM, "ph-datetime", EProjectType.JAVA_LIBRARY),
  PH_CHARSET (PH_COMMONS_PARENT_POM, "ph-charset", EProjectType.JAVA_LIBRARY),
  PH_LESS_COMMONS (PH_COMMONS_PARENT_POM, "ph-less-commons", EProjectType.JAVA_LIBRARY),
  PH_DAO (PH_COMMONS_PARENT_POM, "ph-dao", EProjectType.JAVA_LIBRARY, (String) null),

  PH_XSDS_PARENT_POM ("ph-xsds-parent-pom", "ph-xsds", EHasPages.FALSE, EHasWiki.FALSE, "1.0.1", EJDK.JDK8),
  PH_XSDS_XMLDSIG (PH_XSDS_PARENT_POM, "ph-xsds-xmldsig", EProjectType.JAVA_LIBRARY),
  PH_XSDS_XMLDSIG11 (PH_XSDS_PARENT_POM, "ph-xsds-xmldsig11", EProjectType.JAVA_LIBRARY),
  PH_XSDS_XADES132 (PH_XSDS_PARENT_POM, "ph-xsds-xades132", EProjectType.JAVA_LIBRARY),
  PH_XSDS_XADES141 (PH_XSDS_PARENT_POM, "ph-xsds-xades141", EProjectType.JAVA_LIBRARY),
  PH_XSDS_CCTS_CCT_SCHEMAMODULE (PH_XSDS_PARENT_POM, "ph-xsds-ccts-cct-schemamodule", EProjectType.JAVA_LIBRARY),

  PH_BDE ("ph-bde", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.0.0", EJDK.JDK8),
  PH_EBINTERFACE ("ph-ebinterface", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.TRUE, "5.1.2", EJDK.JDK8),
  PH_EVENTS ("ph-events", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "5.0.0", EJDK.JDK8),
  PH_GENERICODE ("ph-genericode", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "5.0.1", EJDK.JDK8),
  PH_ISORELAX ("ph-isorelax", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.1", EJDK.JDK8),
  PH_PDF_LAYOUT4 ("ph-pdf-layout4", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.0.1", EJDK.JDK8),
  PH_POI ("ph-poi", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.1.1", EJDK.JDK8),
  PH_SBDH ("ph-sbdh", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.0.2", EJDK.JDK8),
  PH_XMLDSIG ("ph-xmldsig", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.0.2", EJDK.JDK8),

  PH_MATH_PARENT_POM ("ph-math-parent-pom", "ph-math", EHasPages.FALSE, EHasWiki.FALSE, "3.0.0", EJDK.JDK8),
  PH_MATH (PH_MATH_PARENT_POM, "ph-math", EProjectType.JAVA_LIBRARY),

  PH_FONTS_PARENT_POM ("ph-fonts-parent-pom", "ph-fonts", EHasPages.FALSE, EHasWiki.FALSE, "3.1.1", EJDK.JDK8),
  PH_FONTS_API (PH_FONTS_PARENT_POM, "ph-fonts-api", EProjectType.JAVA_LIBRARY),
  PH_FONTS_ALEGREYA_SANS (PH_FONTS_PARENT_POM, "ph-fonts-alegreya-sans", EProjectType.JAVA_LIBRARY),
  PH_FONTS_ANAHEIM (PH_FONTS_PARENT_POM, "ph-fonts-anaheim", EProjectType.JAVA_LIBRARY),
  PH_FONTS_EXO2 (PH_FONTS_PARENT_POM, "ph-fonts-exo2", EProjectType.JAVA_LIBRARY),
  PH_FONTS_LATO2 (PH_FONTS_PARENT_POM, "ph-fonts-lato2", EProjectType.JAVA_LIBRARY),
  PH_FONTS_OPEN_SANS (PH_FONTS_PARENT_POM, "ph-fonts-open-sans", EProjectType.JAVA_LIBRARY),
  PH_FONTS_ROBOTO (PH_FONTS_PARENT_POM, "ph-fonts-roboto", EProjectType.JAVA_LIBRARY),

  PH_JAXB22_PLUGIN ("ph-jaxb22-plugin",
                    EProjectType.OTHER_PLUGIN,
                    EHasPages.FALSE,
                    EHasWiki.FALSE,
                    "2.2.11.9",
                    EJDK.JDK8),
  PH_WSIMPORT_PLUGIN ("ph-wsimport-plugin",
                      EProjectType.OTHER_PLUGIN,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "2.2.10",
                      EJDK.JDK8),

  PH_JAVACC_MAVEN_PLUGIN ("ph-javacc-maven-plugin",
                          EProjectType.MAVEN_PLUGIN,
                          EHasPages.FALSE,
                          EHasWiki.FALSE,
                          "2.8.2",
                          EJDK.JDK6),
  PH_BUILDINFO_MAVEN_PLUGIN ("ph-buildinfo-maven-plugin",
                             EProjectType.MAVEN_PLUGIN,
                             EHasPages.FALSE,
                             EHasWiki.FALSE,
                             "2.1.0",
                             EJDK.JDK8),
  PH_DIRINDEX_MAVEN_PLUGIN ("ph-dirindex-maven-plugin",
                            EProjectType.MAVEN_PLUGIN,
                            EHasPages.FALSE,
                            EHasWiki.FALSE,
                            "2.0.0",
                            EJDK.JDK8),
  PH_JSCOMPRESS_MAVEN_PLUGIN ("ph-jscompress-maven-plugin",
                              EProjectType.MAVEN_PLUGIN,
                              EHasPages.FALSE,
                              EHasWiki.FALSE,
                              "2.1.1",
                              EJDK.JDK7),

  PH_DATETIME_PARENT_POM ("ph-datetime-parent-pom", "ph-datetime", EHasPages.FALSE, EHasWiki.FALSE, "5.0.1", EJDK.JDK8),
  PH_HOLIDAY (PH_DATETIME_PARENT_POM, "ph-holiday", EProjectType.JAVA_LIBRARY),

  PH_DB_PARENT_POM ("ph-db-parent-pom", "ph-db", EHasPages.FALSE, EHasWiki.FALSE, "5.0.1", EJDK.JDK8),
  PH_DB_API (PH_DB_PARENT_POM, "ph-db-api", EProjectType.JAVA_LIBRARY),
  PH_DB_JDBC (PH_DB_PARENT_POM, "ph-db-jdbc", EProjectType.JAVA_LIBRARY),
  PH_DB_JPA (PH_DB_PARENT_POM, "ph-db-jpa", EProjectType.JAVA_LIBRARY),

  PH_SCHEDULE_PARENT_POM ("ph-schedule-parent-pom", "ph-schedule", EHasPages.FALSE, EHasWiki.FALSE, "3.6.1", EJDK.JDK8),
  PH_MINI_QUARTZ (PH_SCHEDULE_PARENT_POM, "ph-mini-quartz", EProjectType.JAVA_LIBRARY),
  PH_SCHEDULE (PH_SCHEDULE_PARENT_POM, "ph-schedule", EProjectType.JAVA_LIBRARY),

  PH_WEB_PARENT_POM ("ph-web-parent-pom", "ph-web", EHasPages.FALSE, EHasWiki.FALSE, "8.8.2", EJDK.JDK8),
  PH_NETWORK (PH_WEB_PARENT_POM, "ph-network", EProjectType.JAVA_LIBRARY),
  PH_HTTP (PH_WEB_PARENT_POM, "ph-http", EProjectType.JAVA_LIBRARY),
  PH_USERAGENT (PH_WEB_PARENT_POM, "ph-useragent", EProjectType.JAVA_LIBRARY),
  PH_SERVLET (PH_WEB_PARENT_POM, "ph-servlet", EProjectType.JAVA_LIBRARY),
  PH_MAIL (PH_WEB_PARENT_POM, "ph-mail", EProjectType.JAVA_LIBRARY),
  PH_SMTP (PH_WEB_PARENT_POM, "ph-smtp", EProjectType.JAVA_LIBRARY),
  PH_HTTPCLIENT (PH_WEB_PARENT_POM, "ph-httpclient", EProjectType.JAVA_LIBRARY),
  PH_SITEMAP (PH_WEB_PARENT_POM, "ph-sitemap", EProjectType.JAVA_LIBRARY, (String) null),
  PH_WEB (PH_WEB_PARENT_POM, "ph-web", EProjectType.JAVA_LIBRARY),
  PH_XSERVLET (PH_WEB_PARENT_POM, "ph-xservlet", EProjectType.JAVA_LIBRARY, (String) null),

  PH_CSS_PARENT_POM ("ph-css-parent-pom", "ph-css", EHasPages.FALSE, EHasWiki.FALSE, "5.0.4", EJDK.JDK8),
  PH_CSS (PH_CSS_PARENT_POM, "ph-css", EProjectType.JAVA_LIBRARY),
  PH_CSSCOMPRESS_MAVEN_PLUGIN (PH_CSS_PARENT_POM, "ph-csscompress-maven-plugin", EProjectType.MAVEN_PLUGIN),

  PH_MASTERDATA_PARENT_POM ("ph-masterdata-parent-pom",
                            "ph-masterdata",
                            EHasPages.FALSE,
                            EHasWiki.FALSE,
                            "5.0.5",
                            EJDK.JDK8),
  PH_MASTERDATA (PH_MASTERDATA_PARENT_POM, "ph-masterdata", EProjectType.JAVA_LIBRARY),
  PH_TENANCY (PH_MASTERDATA_PARENT_POM, "ph-tenancy", EProjectType.JAVA_LIBRARY, (String) null),

  PH_OTON_PARENT_POM ("ph-oton-parent-pom", "ph-oton", EHasPages.FALSE, EHasWiki.FALSE, "7.1.2", EJDK.JDK8),
  PH_OTON_HTML (PH_OTON_PARENT_POM, "ph-oton-html", EProjectType.JAVA_LIBRARY),
  PH_OTON_JSCODE (PH_OTON_PARENT_POM, "ph-oton-jscode", EProjectType.JAVA_LIBRARY),
  PH_OTON_JQUERY (PH_OTON_PARENT_POM, "ph-oton-jquery", EProjectType.JAVA_LIBRARY),
  PH_OTON_BASIC (PH_OTON_PARENT_POM, "ph-oton-basic", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP3 (PH_OTON_PARENT_POM, "ph-oton-bootstrap3", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP3_DEMO (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-demo", EProjectType.JAVA_WEB_APPLICATION),
  PH_OTON_BOOTSTRAP3_PAGES (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-pages", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP3_STUB (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-stub", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP3_UICTRLS (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-uictrls", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP4 (PH_OTON_PARENT_POM, "ph-oton-bootstrap4", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP4_STUB (PH_OTON_PARENT_POM, "ph-oton-bootstrap4-stub", EProjectType.JAVA_LIBRARY, (String) null),
  PH_OTON_CONNECT (PH_OTON_PARENT_POM, "ph-oton-connect", EProjectType.JAVA_LIBRARY),
  PH_OTON_CORE (PH_OTON_PARENT_POM, "ph-oton-core", EProjectType.JAVA_LIBRARY),
  PH_OTON_EXCHANGE (PH_OTON_PARENT_POM, "ph-oton-exchange", EProjectType.JAVA_LIBRARY),
  PH_OTON_JETTY (PH_OTON_PARENT_POM, "ph-oton-jetty", EProjectType.JAVA_LIBRARY),
  PH_OTON_SECURITY (PH_OTON_PARENT_POM, "ph-oton-security", EProjectType.JAVA_LIBRARY),
  PH_OTON_TINYMCE4 (PH_OTON_PARENT_POM, "ph-oton-tinymce4", EProjectType.JAVA_LIBRARY),
  PH_OTON_DATATABLES (PH_OTON_PARENT_POM, "ph-oton-datatables", EProjectType.JAVA_LIBRARY),
  PH_OTON_UICORE (PH_OTON_PARENT_POM, "ph-oton-uicore", EProjectType.JAVA_LIBRARY),
  PH_OTON_ICON (PH_OTON_PARENT_POM, "ph-oton-icon", EProjectType.JAVA_LIBRARY),
  PH_OTON_UICTRLS (PH_OTON_PARENT_POM, "ph-oton-uictrls", EProjectType.JAVA_LIBRARY),

  PH_SCHEMATRON_PARENT_POM ("ph-schematron-parent-pom",
                            "ph-schematron",
                            EHasPages.TRUE,
                            EHasWiki.FALSE,
                            "4.3.4",
                            EJDK.JDK8),
  PH_SCHEMATRON (PH_SCHEMATRON_PARENT_POM, "ph-schematron", EProjectType.JAVA_LIBRARY),
  PH_SCHEMATRON_TESTFILES (PH_SCHEMATRON_PARENT_POM, "ph-schematron-testfiles", EProjectType.JAVA_LIBRARY),
  PH_SCHEMATRON_VALIDATOR (PH_SCHEMATRON_PARENT_POM, "ph-schematron-validator", EProjectType.JAVA_LIBRARY),
  PH_SCH2XSLT_MAVEN_PLUGIN (PH_SCHEMATRON_PARENT_POM, "ph-sch2xslt-maven-plugin", EProjectType.MAVEN_PLUGIN),
  PH_SCHEMATRON_MAVEN_PLUGIN (PH_SCHEMATRON_PARENT_POM, "ph-schematron-maven-plugin", EProjectType.MAVEN_PLUGIN),
  PH_SCHEMATRON_ANT_TASK (PH_SCHEMATRON_PARENT_POM, "ph-schematron-ant-task", EProjectType.JAVA_LIBRARY),

  PH_UBL_PARENT_POM ("ph-ubl-parent-pom", "ph-ubl", EHasPages.FALSE, EHasWiki.FALSE, "5.1.0", EJDK.JDK8),
  PH_UBL_API (PH_UBL_PARENT_POM, "ph-ubl-api", EProjectType.JAVA_LIBRARY),
  PH_UBL_JAXB_PLUGIN (PH_UBL_PARENT_POM, "ph-ubl-jaxb-plugin", EProjectType.OTHER_PLUGIN),
  PH_UBL_TESTFILES (PH_UBL_PARENT_POM, "ph-ubl-testfiles", EProjectType.JAVA_LIBRARY),
  PH_UBL20 (PH_UBL_PARENT_POM, "ph-ubl20", EProjectType.JAVA_LIBRARY),
  PH_UBL20_CODELISTS (PH_UBL_PARENT_POM, "ph-ubl20-codelists", EProjectType.JAVA_LIBRARY),
  PH_UBL21 (PH_UBL_PARENT_POM, "ph-ubl21", EProjectType.JAVA_LIBRARY),
  PH_UBL21_CODELISTS (PH_UBL_PARENT_POM, "ph-ubl21-codelists", EProjectType.JAVA_LIBRARY),
  PH_UBL22 (PH_UBL_PARENT_POM, "ph-ubl22", EProjectType.JAVA_LIBRARY, (String) null),
  PH_UBL22_CODELISTS (PH_UBL_PARENT_POM, "ph-ubl22-codelists", EProjectType.JAVA_LIBRARY, (String) null),
  PH_UBLTR (PH_UBL_PARENT_POM, "ph-ubltr", EProjectType.JAVA_LIBRARY),

  PH_CII_PARENT_POM ("ph-cii-parent-pom", "ph-cii", EHasPages.FALSE, EHasWiki.FALSE, "1.0.0", EJDK.JDK8),
  PH_CII_TESTFILES (PH_CII_PARENT_POM, "ph-cii-testfiles", EProjectType.JAVA_LIBRARY),
  PH_CII_D16A1 (PH_CII_PARENT_POM, "ph-cii-d16a-1", EProjectType.JAVA_LIBRARY),
  PH_CII_D16B (PH_CII_PARENT_POM, "ph-cii-d16b", EProjectType.JAVA_LIBRARY),

  AS2_LIB_PARENT_POM ("as2-lib-parent-pom", "as2-lib", EHasPages.FALSE, EHasWiki.FALSE, "3.1.0", EJDK.JDK8),
  AS2_LIB (AS2_LIB_PARENT_POM, "as2-lib", EProjectType.JAVA_LIBRARY),
  AS2_PARTNERSHIP_MONGODB (AS2_LIB_PARENT_POM, "as2-partnership-mongodb", EProjectType.JAVA_LIBRARY),
  AS2_SERVLET (AS2_LIB_PARENT_POM, "as2-servlet", EProjectType.JAVA_LIBRARY),

  AS2_SERVER ("as2-server", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "3.1.0", EJDK.JDK8),

  PEPPOL_COMMONS_PARENT_POM ("peppol-commons-parent-pom",
                             "peppol-commons",
                             EHasPages.FALSE,
                             EHasWiki.FALSE,
                             "5.2.7",
                             EJDK.JDK8),
  PEPPOL_COMMONS (PEPPOL_COMMONS_PARENT_POM, "peppol-commons", EProjectType.JAVA_LIBRARY),
  PEPPOL_TESTFILES (PEPPOL_COMMONS_PARENT_POM, "peppol-testfiles", EProjectType.JAVA_LIBRARY),
  PEPPOL_SBDH (PEPPOL_COMMONS_PARENT_POM, "peppol-sbdh", EProjectType.JAVA_LIBRARY),
  PEPPOL_SML_CLIENT (PEPPOL_COMMONS_PARENT_POM, "peppol-sml-client", EProjectType.JAVA_LIBRARY),
  PEPPOL_SMP_CLIENT (PEPPOL_COMMONS_PARENT_POM, "peppol-smp-client", EProjectType.JAVA_LIBRARY),

  AS2_PEPPOL_CLIENT ("as2-peppol-client",
                     EProjectType.JAVA_LIBRARY,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     "2.0.7",
                     EJDK.JDK8),
  AS2_PEPPOL_SERVLET ("as2-peppol-servlet",
                      EProjectType.JAVA_LIBRARY,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "4.0.2",
                      EJDK.JDK8),
  AS2_PEPPOL_SERVER ("as2-peppol-server",
                     EProjectType.JAVA_WEB_APPLICATION,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     null,
                     EJDK.JDK8),

  PH_BDVE_PARENT_POM ("ph-bdve-parent-pom",
                      "peppol-validation-engine",
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "3.2.0",
                      EJDK.JDK8),
  PH_BDVE (PH_BDVE_PARENT_POM, "ph-bdve", EProjectType.JAVA_LIBRARY),
  PH_BDVE_PEPPOL (PH_BDVE_PARENT_POM, "ph-bdve-peppol", EProjectType.JAVA_LIBRARY),
  PH_BDVE_SIMPLERINVOICING (PH_BDVE_PARENT_POM, "ph-bdve-simplerinvoicing", EProjectType.JAVA_LIBRARY),
  PH_BDVE_EN16931 (PH_BDVE_PARENT_POM, "ph-bdve-en16931", EProjectType.JAVA_LIBRARY),

  PEPPOL_DIRECTORY_PARENT_POM ("peppol-directory-parent-pom",
                               "peppol-directory",
                               EHasPages.FALSE,
                               EHasWiki.FALSE,
                               "0.5.1",
                               EJDK.JDK8),
  PEPPOL_DIRECTORY_BUSINESSCARD (PEPPOL_DIRECTORY_PARENT_POM,
                                 "peppol-directory-businesscard",
                                 EProjectType.JAVA_LIBRARY),
  PEPPOL_DIRECTORY_API (PEPPOL_DIRECTORY_PARENT_POM, "peppol-directory-api", EProjectType.JAVA_LIBRARY),
  PEPPOL_DIRECTORY_INDEXER (PEPPOL_DIRECTORY_PARENT_POM, "peppol-directory-indexer", EProjectType.JAVA_LIBRARY),
  PEPPOL_DIRECTORY_PUBLISHER (PEPPOL_DIRECTORY_PARENT_POM,
                              "peppol-directory-publisher",
                              EProjectType.JAVA_WEB_APPLICATION),
  PEPPOL_DIRECTORY_CLIENT (PEPPOL_DIRECTORY_PARENT_POM, "peppol-directory-client", EProjectType.JAVA_LIBRARY),

  PEPPOL_SMP_SERVER_PARENT_POM ("peppol-smp-server-parent-pom",
                                "peppol-smp-server",
                                EHasPages.FALSE,
                                EHasWiki.TRUE,
                                "5.0.3",
                                EJDK.JDK8),
  PEPPOL_SMP_SERVER_LIBRARY (PEPPOL_SMP_SERVER_PARENT_POM, "peppol-smp-server-library", EProjectType.JAVA_LIBRARY),
  PEPPOL_SMP_SERVER_SQL (PEPPOL_SMP_SERVER_PARENT_POM, "peppol-smp-server-sql", EProjectType.JAVA_LIBRARY),
  PEPPOL_SMP_SERVER_XML (PEPPOL_SMP_SERVER_PARENT_POM, "peppol-smp-server-xml", EProjectType.JAVA_LIBRARY),
  PEPPOL_SMP_SERVER_WEBAPP (PEPPOL_SMP_SERVER_PARENT_POM, "peppol-smp-server-webapp", EProjectType.JAVA_LIBRARY),
  PEPPOL_SMP_SERVER_WEBAPP_SQL (PEPPOL_SMP_SERVER_PARENT_POM,
                                "peppol-smp-server-webapp-sql",
                                EProjectType.JAVA_WEB_APPLICATION),
  PEPPOL_SMP_SERVER_WEBAPP_XML (PEPPOL_SMP_SERVER_PARENT_POM,
                                "peppol-smp-server-webapp-xml",
                                EProjectType.JAVA_WEB_APPLICATION),

  EBINTERFACE_UBL_MAPPING ("ebinterface-ubl-mapping",
                           EProjectType.JAVA_LIBRARY,
                           EHasPages.FALSE,
                           EHasWiki.FALSE,
                           "2.2.0",
                           EJDK.JDK8),
  ERECHNUNG_WS_CLIENT (null,
                       "webservice-client",
                       "erechnung.gv.at-webservice-client",
                       EProjectType.JAVA_LIBRARY,
                       EHasPages.FALSE,
                       EHasWiki.FALSE,
                       "2.0.0",
                       EJDK.JDK8),

  PH_XPATH2 ("ph-xpath2", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null, EJDK.JDK8),

  PH_STX_PARENT_POM ("ph-stx-parent-pom", "ph-stx", EHasPages.FALSE, EHasWiki.FALSE, null, EJDK.JDK8),
  PH_STX_PARSER (PH_STX_PARENT_POM, "ph-stx-parser", EProjectType.JAVA_LIBRARY),
  PH_STX_ENGINE (PH_STX_PARENT_POM, "ph-stx-engine", EProjectType.JAVA_LIBRARY),

  PH_AS4_PARENT_POM ("ph-as4-parent-pom", "ph-as4", EHasPages.FALSE, EHasWiki.FALSE, "0.7.0", EJDK.JDK8),
  PH_AS4_LIB (PH_AS4_PARENT_POM, "ph-as4-lib", EProjectType.JAVA_LIBRARY),
  PH_AS4_ESENS (PH_AS4_PARENT_POM, "ph-as4-esens", EProjectType.JAVA_LIBRARY),
  PH_AS4_SERVLET (PH_AS4_PARENT_POM, "ph-as4-servlet", EProjectType.JAVA_LIBRARY),
  PH_AS4_SERVER_WEBAPP (PH_AS4_PARENT_POM, "ph-as4-server-webapp", EProjectType.JAVA_WEB_APPLICATION),
  PH_AS4_SERVER_WEBAPP_TEST (PH_AS4_PARENT_POM, "ph-as4-server-webapp-test", EProjectType.JAVA_WEB_APPLICATION),

  BOZOO ("bozoo", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null, EJDK.JDK8),
  PEPPOL_PRACTICAL ("peppol-practical",
                    EProjectType.JAVA_WEB_APPLICATION,
                    EHasPages.FALSE,
                    EHasWiki.FALSE,
                    null,
                    EJDK.JDK8),
  PHOSS_VALIDATOR ("phoss-validator", EProjectType.JAVA_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null, EJDK.JDK8);

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
  private EProject (@Nonnull @Nonempty final String sProjectName,
                    @Nonnull @Nonempty final String sProjectBaseDirName,
                    @Nonnull final EHasPages eHasPagesProject,
                    @Nonnull final EHasWiki eHasWikiProject,
                    @Nullable final String sLastPublishedVersion,
                    @Nonnull final EJDK eMinJDK)
  {
    this (null,
          sProjectName,
          sProjectBaseDirName,
          EProjectType.MAVEN_POM,
          eHasPagesProject,
          eHasWikiProject,
          sLastPublishedVersion,
          eMinJDK);
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
  private EProject (@Nonnull final EProject eParentProject,
                    @Nonnull @Nonempty final String sProjectName,
                    @Nonnull final EProjectType eProjectType)
  {
    // Project name equals project base directory name
    this (eParentProject,
          sProjectName,
          sProjectName,
          eProjectType,
          EHasPages.FALSE,
          EHasWiki.FALSE,
          eParentProject.getLastPublishedVersionString (),
          eParentProject.getMinimumJDKVersion ());
  }

  /**
   * Constructor for child projects where project name equals directory name and
   * the last published version is identical to the one of the parent project
   * but the child project uses a different JDK version
   *
   * @param eParentProject
   *        Parent project. May not be <code>null</code>.
   * @param sProjectName
   *        Project name
   * @param eProjectType
   *        Project type
   * @param eMinJDK
   *        Minimum JDK version to use
   */
  private EProject (@Nonnull final EProject eParentProject,
                    @Nonnull @Nonempty final String sProjectName,
                    @Nonnull final EProjectType eProjectType,
                    @Nonnull final EJDK eMinJDK)
  {
    // Project name equals project base directory name
    this (eParentProject,
          sProjectName,
          sProjectName,
          eProjectType,
          EHasPages.FALSE,
          EHasWiki.FALSE,
          eParentProject.getLastPublishedVersionString (),
          eMinJDK);
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
  private EProject (@Nonnull final EProject eParentProject,
                    @Nonnull @Nonempty final String sProjectName,
                    @Nonnull final EProjectType eProjectType,
                    @Nullable final String sLastPublishedVersion)
  {
    // Project name equals project base directory name
    this (eParentProject,
          sProjectName,
          sProjectName,
          eProjectType,
          EHasPages.FALSE,
          EHasWiki.FALSE,
          sLastPublishedVersion,
          eParentProject.getMinimumJDKVersion ());
  }

  /**
   * Constructor for standalone projects (without a parent POM) where project
   * name and directory name are identical.
   *
   * @param sProjectName
   *        Project name and directory name
   * @param eProjectType
   *        Project type
   * @param eHasPagesProject
   *        pages project present?
   * @param eHasWikiProject
   *        wiki project present?
   * @param sLastPublishedVersion
   *        Last published version
   * @param eMinJDK
   *        Minimum JDK version to use
   */
  private EProject (@Nonnull @Nonempty final String sProjectName,
                    @Nonnull final EProjectType eProjectType,
                    @Nonnull final EHasPages eHasPagesProject,
                    @Nonnull final EHasWiki eHasWikiProject,
                    @Nullable final String sLastPublishedVersion,
                    @Nonnull final EJDK eMinJDK)
  {
    // Project name equals project base directory name
    this (null,
          sProjectName,
          sProjectName,
          eProjectType,
          eHasPagesProject,
          eHasWikiProject,
          sLastPublishedVersion,
          eMinJDK);
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
  private EProject (@Nullable final EProject eParentProject,
                    @Nonnull @Nonempty final String sProjectName,
                    @Nonnull @Nonempty final String sProjectBaseDirName,
                    @Nonnull final EProjectType eProjectType,
                    @Nonnull final EHasPages eHasPagesProject,
                    @Nonnull final EHasWiki eHasWikiProject,
                    @Nullable final String sLastPublishedVersion,
                    @Nonnull final EJDK eMinJDK)
  {
    m_aProject = new SimpleProject (eParentProject,
                                    sProjectName,
                                    eProjectType,
                                    new File (eParentProject != null ? eParentProject.getBaseDir ()
                                                                     : CMeta.GIT_BASE_DIR,
                                              sProjectBaseDirName.equals ("ph-pdf-layout4") ? "ph-pdf-layout"
                                                                                            : sProjectBaseDirName),
                                    EIsDeprecated.FALSE,
                                    eHasPagesProject,
                                    eHasWikiProject,
                                    sLastPublishedVersion,
                                    eMinJDK);
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

  @Nonnull
  @Nonempty
  public String getGitHubOrganization ()
  {
    if (this == EProject.EBINTERFACE_UBL_MAPPING)
      return "austriapro";
    return "phax";
  }

  public int compareTo (@Nonnull final IProject aProject)
  {
    return m_aProject.compareTo (aProject);
  }
}
