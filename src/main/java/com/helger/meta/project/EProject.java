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
import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.version.Version;
import com.helger.meta.CMeta;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Defines all the active projects.
 *
 * @author Philip Helger
 */
public enum EProject implements IProject
{
  PH_PARENT_POM (null,
                 IProject.DEFAULT_PROJECT_OWNER,
                 "parent-pom",
                 "ph-parent-pom",
                 EProjectType.MAVEN_POM,
                 EHasPages.FALSE,
                 EHasWiki.FALSE,
                 "1.11.1",
                 EJDK.JDK8),
  PH_FORBIDDEN_APIS (null,
                     IProject.DEFAULT_PROJECT_OWNER,
                     "ph-forbidden-apis",
                     "ph-forbidden-apis",
                     EProjectType.RESOURCES_ONLY,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     "1.1.1",
                     EJDK.JDK8),
  PH_JAXB_POM (null,
               IProject.DEFAULT_PROJECT_OWNER,
               "ph-jaxb-pom",
               "ph-jaxb-pom",
               EProjectType.MAVEN_POM,
               EHasPages.FALSE,
               EHasWiki.FALSE,
               "1.0.3",
               EJDK.JDK8),
  PH_JAXWS_POM (null,
                IProject.DEFAULT_PROJECT_OWNER,
                "ph-jaxws-pom",
                "ph-jaxws-pom",
                EProjectType.MAVEN_POM,
                EHasPages.FALSE,
                EHasWiki.FALSE,
                "1.1.3",
                EJDK.JDK8),
  JCODEMODEL (null,
              IProject.DEFAULT_PROJECT_OWNER,
              "jcodemodel",
              "jcodemodel",
              EProjectType.JAVA_LIBRARY,
              EHasPages.TRUE,
              EHasWiki.FALSE,
              "3.3.0",
              EJDK.JDK8),

  PH_COMMONS_PARENT_POM (null,
                         IProject.DEFAULT_PROJECT_OWNER,
                         "ph-commons-parent-pom",
                         "ph-commons",
                         EProjectType.MAVEN_POM,
                         EHasPages.FALSE,
                         EHasWiki.FALSE,
                         "9.3.9",
                         EJDK.JDK8),
  PH_COMMONS (PH_COMMONS_PARENT_POM, "ph-commons", EProjectType.JAVA_LIBRARY),
  PH_WSCLIENT (PH_COMMONS_PARENT_POM, "ph-wsclient", EProjectType.JAVA_LIBRARY),
  PH_BC (PH_COMMONS_PARENT_POM, "ph-bc", EProjectType.JAVA_LIBRARY),
  PH_SECURITY (PH_COMMONS_PARENT_POM, "ph-security", EProjectType.JAVA_LIBRARY),
  PH_SCOPES (PH_COMMONS_PARENT_POM, "ph-scopes", EProjectType.JAVA_LIBRARY),
  PH_COLLECTION (PH_COMMONS_PARENT_POM, "ph-collection", EProjectType.JAVA_LIBRARY),
  PH_CLI (PH_COMMONS_PARENT_POM, "ph-cli", EProjectType.JAVA_LIBRARY),
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
  PH_DAO (PH_COMMONS_PARENT_POM, "ph-dao", EProjectType.JAVA_LIBRARY),

  PH_JAXB22_PLUGIN (null,
                    IProject.DEFAULT_PROJECT_OWNER,
                    "ph-jaxb22-plugin",
                    "ph-jaxb22-plugin",
                    EProjectType.OTHER_PLUGIN,
                    EHasPages.FALSE,
                    EHasWiki.FALSE,
                    "2.3.2.4",
                    EJDK.JDK8),
  PH_WSIMPORT_PLUGIN (null,
                      IProject.DEFAULT_PROJECT_OWNER,
                      "ph-wsimport-plugin",
                      "ph-wsimport-plugin",
                      EProjectType.OTHER_PLUGIN,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "2.3.1.3",
                      EJDK.JDK8),

  META (null,
        IProject.DEFAULT_PROJECT_OWNER,
        "meta",
        "meta",
        EProjectType.JAVA_APPLICATION,
        EHasPages.FALSE,
        EHasWiki.FALSE,
        null,
        EJDK.JDK8),
  PGCC (null,
        IProject.DEFAULT_PROJECT_OWNER,
        "parser-generator-cc",
        "ParserGeneratorCC",
        EProjectType.JAVA_LIBRARY,
        EHasPages.FALSE,
        EHasWiki.FALSE,
        "1.1.2",
        EJDK.JDK8),

  PH_JDMC_PARENT_POM (null,
                      IProject.DEFAULT_PROJECT_OWNER,
                      "ph-jdmc-parent-pom",
                      "ph-jdmc",
                      EProjectType.MAVEN_POM,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "0.0.4",
                      EJDK.JDK8),
  PH_JDMC_CORE (PH_JDMC_PARENT_POM, "ph-jdmc-core", EProjectType.JAVA_LIBRARY),
  PH_JDMC_MAVEN_PLUGIN (PH_JDMC_PARENT_POM, "ph-jdmc-maven-plugin", EProjectType.MAVEN_PLUGIN),

  PH_XSDS_PARENT_POM (null,
                      IProject.DEFAULT_PROJECT_OWNER,
                      "ph-xsds-parent-pom",
                      "ph-xsds",
                      EProjectType.MAVEN_POM,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "2.2.3",
                      EJDK.JDK8),
  PH_XSDS_XMLDSIG (PH_XSDS_PARENT_POM, "ph-xsds-xmldsig", EProjectType.JAVA_LIBRARY),
  PH_XSDS_XMLDSIG11 (PH_XSDS_PARENT_POM, "ph-xsds-xmldsig11", EProjectType.JAVA_LIBRARY),
  PH_XSDS_XADES132 (PH_XSDS_PARENT_POM, "ph-xsds-xades132", EProjectType.JAVA_LIBRARY),
  PH_XSDS_XADES141 (PH_XSDS_PARENT_POM, "ph-xsds-xades141", EProjectType.JAVA_LIBRARY),
  PH_XSDS_CCTS_CCT_SCHEMAMODULE (PH_XSDS_PARENT_POM, "ph-xsds-ccts-cct-schemamodule", EProjectType.JAVA_LIBRARY),
  PH_XSDS_BDXR_SMP1 (PH_XSDS_PARENT_POM, "ph-xsds-bdxr-smp1", EProjectType.JAVA_LIBRARY),
  PH_XSDS_BDXR_SMP2 (PH_XSDS_PARENT_POM, "ph-xsds-bdxr-smp2", EProjectType.JAVA_LIBRARY),

  PH_ASIC (null,
           IProject.DEFAULT_PROJECT_OWNER,
           "ph-asic",
           "ph-asic",
           EProjectType.JAVA_LIBRARY,
           EHasPages.FALSE,
           EHasWiki.FALSE,
           "1.5.0",
           EJDK.JDK8),
  PH_BDE (null,
          IProject.DEFAULT_PROJECT_OWNER,
          "ph-bde",
          "ph-bde",
          EProjectType.JAVA_LIBRARY,
          EHasPages.FALSE,
          EHasWiki.FALSE,
          "2.1.1",
          EJDK.JDK8),
  PH_XHE (null,
          IProject.DEFAULT_PROJECT_OWNER,
          "ph-xhe",
          "ph-xhe",
          EProjectType.JAVA_LIBRARY,
          EHasPages.FALSE,
          EHasWiki.FALSE,
          "1.0.1",
          EJDK.JDK8),
  PH_EBINTERFACE (null,
                  IProject.DEFAULT_PROJECT_OWNER,
                  "ph-ebinterface",
                  "ph-ebinterface",
                  EProjectType.JAVA_LIBRARY,
                  EHasPages.FALSE,
                  EHasWiki.TRUE,
                  "6.1.1",
                  EJDK.JDK8),
  PH_ISORELAX (null,
               IProject.DEFAULT_PROJECT_OWNER,
               "ph-isorelax",
               "ph-isorelax",
               EProjectType.JAVA_LIBRARY,
               EHasPages.FALSE,
               EHasWiki.FALSE,
               "1.1.1",
               EJDK.JDK8),
  PH_PDF_LAYOUT4 (null,
                  IProject.DEFAULT_PROJECT_OWNER,
                  "ph-pdf-layout4",
                  "ph-pdf-layout4",
                  EProjectType.JAVA_LIBRARY,
                  EHasPages.FALSE,
                  EHasWiki.FALSE,
                  "5.0.9",
                  EJDK.JDK8),
  PH_POI (null,
          IProject.DEFAULT_PROJECT_OWNER,
          "ph-poi",
          "ph-poi",
          EProjectType.JAVA_LIBRARY,
          EHasPages.FALSE,
          EHasWiki.FALSE,
          "5.0.7",
          EJDK.JDK8),
  PH_GENERICODE (null,
                 IProject.DEFAULT_PROJECT_OWNER,
                 "ph-genericode",
                 "ph-genericode",
                 EProjectType.JAVA_LIBRARY,
                 EHasPages.FALSE,
                 EHasWiki.FALSE,
                 "6.1.0",
                 EJDK.JDK8),
  PH_SBDH (null,
           IProject.DEFAULT_PROJECT_OWNER,
           "ph-sbdh",
           "ph-sbdh",
           EProjectType.JAVA_LIBRARY,
           EHasPages.FALSE,
           EHasWiki.FALSE,
           "4.1.0",
           EJDK.JDK8),
  PH_XMLDSIG (null,
              IProject.DEFAULT_PROJECT_OWNER,
              "ph-xmldsig",
              "ph-xmldsig",
              EProjectType.JAVA_LIBRARY,
              EHasPages.FALSE,
              EHasWiki.FALSE,
              "4.3.1",
              EJDK.JDK8),

  PH_MATH_PARENT_POM (null,
                      IProject.DEFAULT_PROJECT_OWNER,
                      "ph-math-parent-pom",
                      "ph-math",
                      EProjectType.MAVEN_POM,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "4.0.0",
                      EJDK.JDK8),
  PH_MATH (PH_MATH_PARENT_POM, "ph-math", EProjectType.JAVA_LIBRARY),

  PH_FONTS_PARENT_POM (null,
                       IProject.DEFAULT_PROJECT_OWNER,
                       "ph-fonts-parent-pom",
                       "ph-fonts",
                       EProjectType.MAVEN_POM,
                       EHasPages.FALSE,
                       EHasWiki.FALSE,
                       "4.0.1",
                       EJDK.JDK8),
  PH_FONTS_API (PH_FONTS_PARENT_POM, "ph-fonts-api", EProjectType.JAVA_LIBRARY),
  PH_FONTS_ALEGREYA_SANS (PH_FONTS_PARENT_POM, "ph-fonts-alegreya-sans", EProjectType.JAVA_LIBRARY),
  PH_FONTS_ANAHEIM (PH_FONTS_PARENT_POM, "ph-fonts-anaheim", EProjectType.JAVA_LIBRARY),
  PH_FONTS_EXO2 (PH_FONTS_PARENT_POM, "ph-fonts-exo2", EProjectType.JAVA_LIBRARY),
  PH_FONTS_LATO2 (PH_FONTS_PARENT_POM, "ph-fonts-lato2", EProjectType.JAVA_LIBRARY),
  PH_FONTS_OPEN_SANS (PH_FONTS_PARENT_POM, "ph-fonts-open-sans", EProjectType.JAVA_LIBRARY),
  PH_FONTS_ROBOTO (PH_FONTS_PARENT_POM, "ph-fonts-roboto", EProjectType.JAVA_LIBRARY),
  PH_FONTS_SOURCE_SANS_PRO (PH_FONTS_PARENT_POM, "ph-fonts-source-sans-pro", EProjectType.JAVA_LIBRARY),

  PH_JAVACC_MAVEN_PLUGIN (null,
                          IProject.DEFAULT_PROJECT_OWNER,
                          "ph-javacc-maven-plugin",
                          "ph-javacc-maven-plugin",
                          EProjectType.MAVEN_PLUGIN,
                          EHasPages.FALSE,
                          EHasWiki.FALSE,
                          "4.1.3",
                          EJDK.JDK8),
  PH_BUILDINFO_MAVEN_PLUGIN (null,
                             IProject.DEFAULT_PROJECT_OWNER,
                             "ph-buildinfo-maven-plugin",
                             "ph-buildinfo-maven-plugin",
                             EProjectType.MAVEN_PLUGIN,
                             EHasPages.FALSE,
                             EHasWiki.FALSE,
                             "3.0.1",
                             EJDK.JDK8),
  PH_DIRINDEX_MAVEN_PLUGIN (null,
                            IProject.DEFAULT_PROJECT_OWNER,
                            "ph-dirindex-maven-plugin",
                            "ph-dirindex-maven-plugin",
                            EProjectType.MAVEN_PLUGIN,
                            EHasPages.FALSE,
                            EHasWiki.FALSE,
                            "3.0.2",
                            EJDK.JDK8),
  PH_JSCOMPRESS_MAVEN_PLUGIN (null,
                              IProject.DEFAULT_PROJECT_OWNER,
                              "ph-jscompress-maven-plugin",
                              "ph-jscompress-maven-plugin",
                              EProjectType.MAVEN_PLUGIN,
                              EHasPages.FALSE,
                              EHasWiki.FALSE,
                              "2.2.3",
                              EJDK.JDK8),
  @IsLegacy (replacedWith = "Use com.sun.xml.ws::jaxws-maven-plugin::2.3.2")
  PH_JAXWS_MAVEN_PLUGIN(null,
                        IProject.DEFAULT_PROJECT_OWNER,
                        "jaxws-maven-plugin",
                        "jaxws-maven-plugin",
                        EProjectType.MAVEN_PLUGIN,
                        EHasPages.FALSE,
                        EHasWiki.FALSE,
                        "2.6.2",
                        EJDK.JDK8),

  PH_DATETIME_PARENT_POM (null,
                          IProject.DEFAULT_PROJECT_OWNER,
                          "ph-datetime-parent-pom",
                          "ph-datetime",
                          EProjectType.MAVEN_POM,
                          EHasPages.FALSE,
                          EHasWiki.FALSE,
                          "6.0.1",
                          EJDK.JDK8),
  PH_HOLIDAY (PH_DATETIME_PARENT_POM, "ph-holiday", EProjectType.JAVA_LIBRARY),

  PH_DB_PARENT_POM (null,
                    IProject.DEFAULT_PROJECT_OWNER,
                    "ph-db-parent-pom",
                    "ph-db",
                    EProjectType.MAVEN_POM,
                    EHasPages.FALSE,
                    EHasWiki.FALSE,
                    "6.1.5",
                    EJDK.JDK8),
  PH_DB_API (PH_DB_PARENT_POM, "ph-db-api", EProjectType.JAVA_LIBRARY),
  PH_DB_JDBC (PH_DB_PARENT_POM, "ph-db-jdbc", EProjectType.JAVA_LIBRARY),
  PH_DB_JPA (PH_DB_PARENT_POM, "ph-db-jpa", EProjectType.JAVA_LIBRARY),

  PH_SCHEDULE_PARENT_POM (null,
                          IProject.DEFAULT_PROJECT_OWNER,
                          "ph-schedule-parent-pom",
                          "ph-schedule",
                          EProjectType.MAVEN_POM,
                          EHasPages.FALSE,
                          EHasWiki.FALSE,
                          "4.0.1",
                          EJDK.JDK8),
  PH_MINI_QUARTZ (PH_SCHEDULE_PARENT_POM, "ph-mini-quartz", EProjectType.JAVA_LIBRARY),
  PH_SCHEDULE (PH_SCHEDULE_PARENT_POM, "ph-schedule", EProjectType.JAVA_LIBRARY),

  PH_WEB_PARENT_POM (null,
                     IProject.DEFAULT_PROJECT_OWNER,
                     "ph-web-parent-pom",
                     "ph-web",
                     EProjectType.MAVEN_POM,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     "9.1.10",
                     EJDK.JDK8),
  PH_NETWORK (PH_WEB_PARENT_POM, "ph-network", EProjectType.JAVA_LIBRARY),
  PH_HTTP (PH_WEB_PARENT_POM, "ph-http", EProjectType.JAVA_LIBRARY),
  PH_USERAGENT (PH_WEB_PARENT_POM, "ph-useragent", EProjectType.JAVA_LIBRARY),
  PH_SERVLET (PH_WEB_PARENT_POM, "ph-servlet", EProjectType.JAVA_LIBRARY),
  PH_MAIL (PH_WEB_PARENT_POM, "ph-mail", EProjectType.JAVA_LIBRARY),
  PH_SMTP (PH_WEB_PARENT_POM, "ph-smtp", EProjectType.JAVA_LIBRARY),
  PH_HTTPCLIENT (PH_WEB_PARENT_POM, "ph-httpclient", EProjectType.JAVA_LIBRARY),
  PH_SITEMAP (PH_WEB_PARENT_POM, "ph-sitemap", EProjectType.JAVA_LIBRARY),
  PH_WEB (PH_WEB_PARENT_POM, "ph-web", EProjectType.JAVA_LIBRARY),
  PH_XSERVLET (PH_WEB_PARENT_POM, "ph-xservlet", EProjectType.JAVA_LIBRARY),

  PH_CSS_PARENT_POM (null,
                     IProject.DEFAULT_PROJECT_OWNER,
                     "ph-css-parent-pom",
                     "ph-css",
                     EProjectType.MAVEN_POM,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     "6.2.1",
                     EJDK.JDK8),
  PH_CSS (PH_CSS_PARENT_POM, "ph-css", EProjectType.JAVA_LIBRARY),
  PH_CSSCOMPRESS_MAVEN_PLUGIN (PH_CSS_PARENT_POM, "ph-csscompress-maven-plugin", EProjectType.MAVEN_PLUGIN),

  PH_MASTERDATA_PARENT_POM (null,
                            IProject.DEFAULT_PROJECT_OWNER,
                            "ph-masterdata-parent-pom",
                            "ph-masterdata",
                            EProjectType.MAVEN_POM,
                            EHasPages.FALSE,
                            EHasWiki.FALSE,
                            "6.1.4",
                            EJDK.JDK8),
  PH_MASTERDATA (PH_MASTERDATA_PARENT_POM, "ph-masterdata", EProjectType.JAVA_LIBRARY),
  PH_TENANCY (PH_MASTERDATA_PARENT_POM, "ph-tenancy", EProjectType.JAVA_LIBRARY),

  PH_OTON_PARENT_POM (null,
                      IProject.DEFAULT_PROJECT_OWNER,
                      "ph-oton-parent-pom",
                      "ph-oton",
                      EProjectType.MAVEN_POM,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "8.2.3",
                      EJDK.JDK8),
  PH_OTON_HTML (PH_OTON_PARENT_POM, "ph-oton-html", EProjectType.JAVA_LIBRARY),
  PH_OTON_JSCODE (PH_OTON_PARENT_POM, "ph-oton-jscode", EProjectType.JAVA_LIBRARY),
  PH_OTON_JQUERY (PH_OTON_PARENT_POM, "ph-oton-jquery", EProjectType.JAVA_LIBRARY),
  PH_OTON_ATOM (PH_OTON_PARENT_POM, "ph-oton-atom", EProjectType.JAVA_LIBRARY),
  PH_OTON_APP (PH_OTON_PARENT_POM, "ph-oton-app", EProjectType.JAVA_LIBRARY),
  PH_OTON_AUDIT (PH_OTON_PARENT_POM, "ph-oton-audit", EProjectType.JAVA_LIBRARY),
  PH_OTON_AJAX (PH_OTON_PARENT_POM, "ph-oton-ajax", EProjectType.JAVA_LIBRARY),
  PH_OTON_API (PH_OTON_PARENT_POM, "ph-oton-api", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP3 (PH_OTON_PARENT_POM, "ph-oton-bootstrap3", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP3_DEMO (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-demo", EProjectType.JAVA_WEB_APPLICATION),
  PH_OTON_BOOTSTRAP3_PAGES (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-pages", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP3_STUB (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-stub", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP3_UICTRLS (PH_OTON_PARENT_POM, "ph-oton-bootstrap3-uictrls", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP4 (PH_OTON_PARENT_POM, "ph-oton-bootstrap4", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP4_DEMO (PH_OTON_PARENT_POM, "ph-oton-bootstrap4-demo", EProjectType.JAVA_WEB_APPLICATION),
  PH_OTON_BOOTSTRAP4_PAGES (PH_OTON_PARENT_POM, "ph-oton-bootstrap4-pages", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP4_STUB (PH_OTON_PARENT_POM, "ph-oton-bootstrap4-stub", EProjectType.JAVA_LIBRARY),
  PH_OTON_BOOTSTRAP4_UICTRLS (PH_OTON_PARENT_POM, "ph-oton-bootstrap4-uictrls", EProjectType.JAVA_LIBRARY),
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

  PH_SCHEMATRON_PARENT_POM (null,
                            IProject.DEFAULT_PROJECT_OWNER,
                            "ph-schematron-parent-pom",
                            "ph-schematron",
                            EProjectType.MAVEN_POM,
                            EHasPages.TRUE,
                            EHasWiki.FALSE,
                            "5.5.0",
                            EJDK.JDK8),
  PH_SCHEMATRON (PH_SCHEMATRON_PARENT_POM, "ph-schematron", EProjectType.JAVA_LIBRARY),
  PH_SCHEMATRON_TESTFILES (PH_SCHEMATRON_PARENT_POM, "ph-schematron-testfiles", EProjectType.JAVA_LIBRARY),
  PH_SCHEMATRON_VALIDATOR (PH_SCHEMATRON_PARENT_POM, "ph-schematron-validator", EProjectType.JAVA_LIBRARY),
  PH_SCHEMATRON_MAVEN_PLUGIN (PH_SCHEMATRON_PARENT_POM, "ph-schematron-maven-plugin", EProjectType.MAVEN_PLUGIN),
  PH_SCHEMATRON_ANT_TASK (PH_SCHEMATRON_PARENT_POM, "ph-schematron-ant-task", EProjectType.JAVA_LIBRARY),

  PH_UBL_PARENT_POM (null,
                     IProject.DEFAULT_PROJECT_OWNER,
                     "ph-ubl-parent-pom",
                     "ph-ubl",
                     EProjectType.MAVEN_POM,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     "6.2.1",
                     EJDK.JDK8),
  PH_UBL_API (PH_UBL_PARENT_POM, "ph-ubl-api", EProjectType.JAVA_LIBRARY),
  PH_UBL_TESTFILES (PH_UBL_PARENT_POM, "ph-ubl-testfiles", EProjectType.JAVA_LIBRARY),
  PH_UBL20 (PH_UBL_PARENT_POM, "ph-ubl20", EProjectType.JAVA_LIBRARY),
  PH_UBL20_CODELISTS (PH_UBL_PARENT_POM, "ph-ubl20-codelists", EProjectType.JAVA_LIBRARY),
  PH_UBL21 (PH_UBL_PARENT_POM, "ph-ubl21", EProjectType.JAVA_LIBRARY),
  PH_UBL21_CODELISTS (PH_UBL_PARENT_POM, "ph-ubl21-codelists", EProjectType.JAVA_LIBRARY),
  PH_UBL22 (PH_UBL_PARENT_POM, "ph-ubl22", EProjectType.JAVA_LIBRARY),
  PH_UBL22_CODELISTS (PH_UBL_PARENT_POM, "ph-ubl22-codelists", EProjectType.JAVA_LIBRARY),
  PH_UBL23 (PH_UBL_PARENT_POM, "ph-ubl23", EProjectType.JAVA_LIBRARY),
  PH_UBL23_CODELISTS (PH_UBL_PARENT_POM, "ph-ubl23-codelists", EProjectType.JAVA_LIBRARY),
  PH_UBLPE (PH_UBL_PARENT_POM, "ph-ublpe", EProjectType.JAVA_LIBRARY),
  PH_UBLTR (PH_UBL_PARENT_POM, "ph-ubltr", EProjectType.JAVA_LIBRARY),
  PH_UBL_DIAN (PH_UBL_PARENT_POM, "ph-ubl-dian", EProjectType.JAVA_LIBRARY),

  PH_CII_PARENT_POM (null,
                     IProject.DEFAULT_PROJECT_OWNER,
                     "ph-cii-parent-pom",
                     "ph-cii",
                     EProjectType.MAVEN_POM,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     "2.1.1",
                     EJDK.JDK8),
  PH_CII_TESTFILES (PH_CII_PARENT_POM, "ph-cii-testfiles", EProjectType.JAVA_LIBRARY),
  PH_CII_D16A1 (PH_CII_PARENT_POM, "ph-cii-d16a-1", EProjectType.JAVA_LIBRARY),
  PH_CII_D16B (PH_CII_PARENT_POM, "ph-cii-d16b", EProjectType.JAVA_LIBRARY),

  AS2_LIB_PARENT_POM (null,
                      IProject.DEFAULT_PROJECT_OWNER,
                      "as2-lib-parent-pom",
                      "as2-lib",
                      EProjectType.MAVEN_POM,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "4.5.5",
                      EJDK.JDK8),
  AS2_LIB (AS2_LIB_PARENT_POM, "as2-lib", EProjectType.JAVA_LIBRARY),
  AS2_PARTNERSHIP_MONGODB (AS2_LIB_PARENT_POM, "as2-partnership-mongodb", EProjectType.JAVA_LIBRARY),
  AS2_SERVLET (AS2_LIB_PARENT_POM, "as2-servlet", EProjectType.JAVA_LIBRARY),
  AS2_SERVER (AS2_LIB_PARENT_POM, "as2-server", EProjectType.JAVA_LIBRARY),
  AS2_DEMO_WEBAPP (AS2_LIB_PARENT_POM, "as2-demo-webapp", EProjectType.JAVA_WEB_APPLICATION),
  AS2_DEMO_SPRING_BOOT (AS2_LIB_PARENT_POM, "as2-demo-spring-boot", EProjectType.JAVA_APPLICATION),

  PEPPOL_COMMONS_PARENT_POM (null,
                             IProject.DEFAULT_PROJECT_OWNER,
                             "peppol-commons-parent-pom",
                             "peppol-commons",
                             EProjectType.MAVEN_POM,
                             EHasPages.FALSE,
                             EHasWiki.FALSE,
                             "8.0.2",
                             EJDK.JDK8),
  PEPPOL_ID (PEPPOL_COMMONS_PARENT_POM, "peppol-id", EProjectType.JAVA_LIBRARY),
  PEPPOL_COMMONS (PEPPOL_COMMONS_PARENT_POM, "peppol-commons", EProjectType.JAVA_LIBRARY),
  PEPPOL_TESTFILES (PEPPOL_COMMONS_PARENT_POM, "peppol-testfiles", EProjectType.JAVA_LIBRARY),
  PEPPOL_SBDH (PEPPOL_COMMONS_PARENT_POM, "peppol-sbdh", EProjectType.JAVA_LIBRARY),
  PEPPOL_SML_CLIENT (PEPPOL_COMMONS_PARENT_POM, "peppol-sml-client", EProjectType.JAVA_LIBRARY),
  PEPPOL_SMP_CLIENT (PEPPOL_COMMONS_PARENT_POM, "peppol-smp-client", EProjectType.JAVA_LIBRARY),

  AS2_PEPPOL_CLIENT (null,
                     IProject.DEFAULT_PROJECT_OWNER,
                     "as2-peppol-client",
                     "as2-peppol-client",
                     EProjectType.JAVA_LIBRARY,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     "3.3.2",
                     EJDK.JDK8),
  AS2_PEPPOL_SERVLET (null,
                      IProject.DEFAULT_PROJECT_OWNER,
                      "as2-peppol-servlet",
                      "as2-peppol-servlet",
                      EProjectType.JAVA_LIBRARY,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "5.3.1",
                      EJDK.JDK8),
  AS2_PEPPOL_SERVER (null,
                     IProject.DEFAULT_PROJECT_OWNER,
                     "as2-peppol-server",
                     "as2-peppol-server",
                     EProjectType.JAVA_WEB_APPLICATION,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     null,
                     EJDK.JDK8),

  PH_BDVE_PARENT_POM (null,
                      IProject.DEFAULT_PROJECT_OWNER,
                      "ph-bdve-parent-pom",
                      "ph-bdve",
                      EProjectType.MAVEN_POM,
                      EHasPages.FALSE,
                      EHasWiki.FALSE,
                      "5.2.9",
                      EJDK.JDK8),
  PH_BDVE (PH_BDVE_PARENT_POM, "ph-bdve", EProjectType.JAVA_LIBRARY),
  PH_BDVE_CII (PH_BDVE_PARENT_POM, "ph-bdve-cii", EProjectType.JAVA_LIBRARY),
  PH_BDVE_EBINTERFACE (PH_BDVE_PARENT_POM, "ph-bdve-ebinterface", EProjectType.JAVA_LIBRARY),
  PH_BDVE_EHF (PH_BDVE_PARENT_POM, "ph-bdve-ehf", EProjectType.JAVA_LIBRARY),
  PH_BDVE_EN16931 (PH_BDVE_PARENT_POM, "ph-bdve-en16931", EProjectType.JAVA_LIBRARY),
  PH_BDVE_ENERGIEEFACTUUR (PH_BDVE_PARENT_POM, "ph-bdve-energieefactuur", EProjectType.JAVA_LIBRARY),
  PH_BDVE_OIOUBL (PH_BDVE_PARENT_POM, "ph-bdve-oioubl", EProjectType.JAVA_LIBRARY),
  PH_BDVE_PEPPOL (PH_BDVE_PARENT_POM, "ph-bdve-peppol", EProjectType.JAVA_LIBRARY),
  PH_BDVE_SIMPLERINVOICING (PH_BDVE_PARENT_POM, "ph-bdve-simplerinvoicing", EProjectType.JAVA_LIBRARY),
  PH_BDVE_TEAPPS (PH_BDVE_PARENT_POM, "ph-bdve-teapps", EProjectType.JAVA_LIBRARY),
  PH_BDVE_UBL (PH_BDVE_PARENT_POM, "ph-bdve-ubl", EProjectType.JAVA_LIBRARY),
  PH_BDVE_UBLBE (PH_BDVE_PARENT_POM, "ph-bdve-ublbe", EProjectType.JAVA_LIBRARY),
  PH_BDVE_XRECHNUNG (PH_BDVE_PARENT_POM, "ph-bdve-xrechnung", EProjectType.JAVA_LIBRARY),

  PHOSS_DIRECTORY_PARENT_POM (null,
                              IProject.DEFAULT_PROJECT_OWNER,
                              "phoss-directory-parent-pom",
                              "phoss-directory",
                              EProjectType.MAVEN_POM,
                              EHasPages.FALSE,
                              EHasWiki.FALSE,
                              "0.8.6",
                              EJDK.JDK8),
  PHOSS_DIRECTORY_BUSINESSCARD (PHOSS_DIRECTORY_PARENT_POM, "phoss-directory-businesscard", EProjectType.JAVA_LIBRARY),
  PHOSS_DIRECTORY_INDEXER (PHOSS_DIRECTORY_PARENT_POM, "phoss-directory-indexer", EProjectType.JAVA_LIBRARY),
  PHOSS_DIRECTORY_PUBLISHER (PHOSS_DIRECTORY_PARENT_POM,
                             "phoss-directory-publisher",
                             EProjectType.JAVA_WEB_APPLICATION),
  PHOSS_DIRECTORY_CLIENT (PHOSS_DIRECTORY_PARENT_POM, "phoss-directory-client", EProjectType.JAVA_LIBRARY),
  PHOSS_DIRECTORY_SEARCHAPI (PHOSS_DIRECTORY_PARENT_POM, "phoss-directory-searchapi", EProjectType.JAVA_LIBRARY),

  PHOSS_SMP_PARENT_POM (null,
                        IProject.DEFAULT_PROJECT_OWNER,
                        "phoss-smp-parent-pom",
                        "phoss-smp",
                        EProjectType.MAVEN_POM,
                        EHasPages.FALSE,
                        EHasWiki.TRUE,
                        "5.2.3",
                        EJDK.JDK8),
  PHOSS_SMP_BACKEND (PHOSS_SMP_PARENT_POM, "phoss-smp-backend", EProjectType.JAVA_LIBRARY),
  PHOSS_SMP_BACKEND_MONGODB (PHOSS_SMP_PARENT_POM, "phoss-smp-backend-mongodb", EProjectType.JAVA_LIBRARY),
  PHOSS_SMP_BACKEND_SQL (PHOSS_SMP_PARENT_POM, "phoss-smp-backend-sql", EProjectType.JAVA_LIBRARY),
  PHOSS_SMP_BACKEND_XML (PHOSS_SMP_PARENT_POM, "phoss-smp-backend-xml", EProjectType.JAVA_LIBRARY),
  PHOSS_SMP_WEBAPP (PHOSS_SMP_PARENT_POM, "phoss-smp-webapp", EProjectType.JAVA_LIBRARY),
  PHOSS_SMP_WEBAPP_MONGODB (PHOSS_SMP_PARENT_POM, "phoss-smp-webapp-mongodb", EProjectType.JAVA_WEB_APPLICATION),
  PHOSS_SMP_WEBAPP_SQL (PHOSS_SMP_PARENT_POM, "phoss-smp-webapp-sql", EProjectType.JAVA_WEB_APPLICATION),
  PHOSS_SMP_WEBAPP_XML (PHOSS_SMP_PARENT_POM, "phoss-smp-webapp-xml", EProjectType.JAVA_WEB_APPLICATION),

  EN16931_CII2UBL_PARENT_POM (null,
                              IProject.DEFAULT_PROJECT_OWNER,
                              "en16931-cii2ubl-parent-pom",
                              "en16931-cii2ubl",
                              EProjectType.MAVEN_POM,
                              EHasPages.FALSE,
                              EHasWiki.FALSE,
                              "1.2.0",
                              EJDK.JDK8),
  EN16931_CII2UBL (EN16931_CII2UBL_PARENT_POM, "en16931-cii2ubl", EProjectType.JAVA_LIBRARY),
  EN16931_CII2UBL_CLI (EN16931_CII2UBL_PARENT_POM, "en16931-cii2ubl-cli", EProjectType.JAVA_LIBRARY),

  EBINTERFACE_RENDERING (null,
                         IProject.PROJECT_OWNER_AUSTRIAPRO,
                         "ebinterface-rendering",
                         "ebinterface-rendering",
                         EProjectType.JAVA_LIBRARY,
                         EHasPages.FALSE,
                         EHasWiki.FALSE,
                         "1.0.0",
                         EJDK.JDK8),
  EBINTERFACE_UBL_MAPPING (null,
                           IProject.PROJECT_OWNER_AUSTRIAPRO,
                           "ebinterface-ubl-mapping",
                           "ebinterface-ubl-mapping",
                           EProjectType.JAVA_LIBRARY,
                           EHasPages.FALSE,
                           EHasWiki.FALSE,
                           "4.5.3",
                           EJDK.JDK8),
  EBINTERFACE_XRECHNUNG_MAPPING (null,
                                 IProject.PROJECT_OWNER_AUSTRIAPRO,
                                 "ebinterface-xrechnung-mapping",
                                 "ebinterface-xrechnung-mapping",
                                 EProjectType.JAVA_LIBRARY,
                                 EHasPages.FALSE,
                                 EHasWiki.FALSE,
                                 "1.0.3",
                                 EJDK.JDK8),
  ERECHNUNG_WS_CLIENT (null,
                       IProject.DEFAULT_PROJECT_OWNER,
                       "webservice-client",
                       "erechnung.gv.at-webservice-client",
                       EProjectType.JAVA_LIBRARY,
                       EHasPages.FALSE,
                       EHasWiki.FALSE,
                       "3.1.0",
                       EJDK.JDK8),

  PHASE4_PARENT_POM (null,
                     IProject.DEFAULT_PROJECT_OWNER,
                     "phase4-parent-pom",
                     "phase4",
                     EProjectType.MAVEN_POM,
                     EHasPages.FALSE,
                     EHasWiki.FALSE,
                     "0.9.13",
                     EJDK.JDK8),
  PHASE4_LIB (PHASE4_PARENT_POM, "phase4-lib", EProjectType.JAVA_LIBRARY),
  PHASE4_PROFILE_CEF (PHASE4_PARENT_POM, "phase4-profile-cef", EProjectType.JAVA_LIBRARY),
  PHASE4_PROFILE_PEPPOL (PHASE4_PARENT_POM, "phase4-profile-peppol", EProjectType.JAVA_LIBRARY),
  PHASE4_SERVLET (PHASE4_PARENT_POM, "phase4-servlet", EProjectType.JAVA_LIBRARY),
  PHASE4_SERVER_WEBAPP (PHASE4_PARENT_POM, "phase4-server-webapp", EProjectType.JAVA_WEB_APPLICATION),
  PHASE4_TEST (PHASE4_PARENT_POM, "phase4-test", EProjectType.JAVA_WEB_APPLICATION),
  PHASE4_PEPPOL_CLIENT (PHASE4_PARENT_POM, "phase4-peppol-client", EProjectType.JAVA_LIBRARY),
  PHASE4_PEPPOL_SERVLET (PHASE4_PARENT_POM, "phase4-peppol-servlet", EProjectType.JAVA_LIBRARY),
  PHASE4_PEPPOL_SERVER_WEBAPP (PHASE4_PARENT_POM, "phase4-peppol-server-webapp", EProjectType.JAVA_WEB_APPLICATION),

  @IsPrivateRepo
  TOTHOLZ (null,
           IProject.DEFAULT_PROJECT_OWNER,
           "totholz",
           "totholz",
           EProjectType.JAVA_WEB_APPLICATION,
           EHasPages.FALSE,
           EHasWiki.FALSE,
           null,
           EJDK.JDK8),
  @IsPrivateRepo
  BOZOO (null,
         IProject.DEFAULT_PROJECT_OWNER,
         "bozoo",
         "bozoo",
         EProjectType.JAVA_WEB_APPLICATION,
         EHasPages.FALSE,
         EHasWiki.FALSE,
         null,
         EJDK.JDK8),

  @IsGitLab
  @IsPrivateRepo
  ENTWERTER (null,
             IProject.PROJECT_ECOSIO_PH,
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
  ENTWERTER_JAXWS (ENTWERTER, "entwerter-jaxws", EProjectType.JAVA_LIBRARY),
  @IsGitLab
  @IsPrivateRepo
  ENTWERTER_WEBAPP (ENTWERTER, "entwerter-webapp", EProjectType.JAVA_WEB_APPLICATION),
  @IsGitLab
  @IsPrivateRepo
  ENTWERTER_STANDALONE (ENTWERTER, "entwerter-standalone", EProjectType.JAVA_APPLICATION),

  PEPPOL_PRACTICAL (null,
                    IProject.DEFAULT_PROJECT_OWNER,
                    "peppol-practical",
                    "peppol-practical",
                    EProjectType.JAVA_WEB_APPLICATION,
                    EHasPages.FALSE,
                    EHasWiki.FALSE,
                    null,
                    EJDK.JDK8),
  REGISTRY434 (null,
               IProject.DEFAULT_PROJECT_OWNER,
               "registry434",
               "registry434",
               EProjectType.JAVA_WEB_APPLICATION,
               EHasPages.FALSE,
               EHasWiki.FALSE,
               null,
               EJDK.JDK8),

  // TOOP stuff
  TOOP_COMMONS_PARENT_POM (null,
                           IProject.PROJECT_OWNER_TOOP,
                           "toop-commons-parent-pom",
                           "toop-commons",
                           EProjectType.MAVEN_POM,
                           EHasPages.FALSE,
                           EHasWiki.FALSE,
                           "0.10.8",
                           EJDK.JDK8),
  TOOP_COMMONS (TOOP_COMMONS_PARENT_POM, "toop-commons", EProjectType.JAVA_LIBRARY),
  TOOP_SCHEMATRON (TOOP_COMMONS_PARENT_POM, "toop-schematron", EProjectType.JAVA_LIBRARY),
  TOOP_KAFKA_CLIENT (TOOP_COMMONS_PARENT_POM, "toop-kafka-client", EProjectType.JAVA_LIBRARY),

  TOOP_INTERFACE (null,
                  IProject.PROJECT_OWNER_TOOP,
                  "toop-interface",
                  "toop-interface",
                  EProjectType.JAVA_LIBRARY,
                  EHasPages.FALSE,
                  EHasWiki.FALSE,
                  "0.10.8",
                  EJDK.JDK8),

  TOOP_CONNECTOR_PARENT_POM (null,
                             IProject.PROJECT_OWNER_TOOP,
                             "toop-connector-parent-pom",
                             "toop-connector",
                             EProjectType.MAVEN_POM,
                             EHasPages.FALSE,
                             EHasWiki.FALSE,
                             "0.10.8",
                             EJDK.JDK8),
  TOOP_CONNECTOR_API (TOOP_CONNECTOR_PARENT_POM, "toop-connector-api", EProjectType.JAVA_LIBRARY),
  TOOP_MEM_DEFAULT (TOOP_CONNECTOR_PARENT_POM, "toop-mem-default", EProjectType.JAVA_LIBRARY),
  TOOP_MEM_PHASE4 (TOOP_CONNECTOR_PARENT_POM, "toop-mem-phase4", EProjectType.JAVA_LIBRARY),
  TOOP_CONNECTOR (TOOP_CONNECTOR_PARENT_POM, "toop-connector", EProjectType.JAVA_LIBRARY),
  TOOP_CONNECTOR_WEBAPP (TOOP_CONNECTOR_PARENT_POM, "toop-connector-webapp", EProjectType.JAVA_WEB_APPLICATION);

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
  private EProject (@Nonnull final EProject eParentProject,
                    @Nonnull @Nonempty final String sProjectName,
                    @Nonnull final EProjectType eProjectType)
  {
    this (eParentProject, sProjectName, sProjectName, eProjectType);
  }

  private EProject (@Nonnull final EProject eParentProject,
                    @Nonnull @Nonempty final String sProjectName,
                    @Nonnull @Nonempty final String sProjectBaseDirName,
                    @Nonnull final EProjectType eProjectType)
  {
    this (eParentProject,
          sProjectName,
          sProjectBaseDirName,
          eProjectType,
          eParentProject.getLastPublishedVersionString ());
  }

  private EProject (@Nonnull final EProject eParentProject,
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
  private EProject (@Nonnull final EProject eParentProject,
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
  private EProject (@Nullable final EProject eParentProject,
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
      final Field aField = EProject.class.getField (name ());
      bIsGitLab = aField.isAnnotationPresent (IsGitLab.class);
    }
    catch (final Exception ex)
    {
      throw new IllegalStateException (ex);
    }
    final boolean bIsPrivateRepo;
    try
    {
      final Field aField = EProject.class.getField (name ());
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
                                    new File (eParentProject != null ? eParentProject.getBaseDir ()
                                                                     : CMeta.GIT_BASE_DIR,
                                              sProjectBaseDirName.equals ("ph-pdf-layout4") ? "ph-pdf-layout"
                                                                                            : sProjectBaseDirName),
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
