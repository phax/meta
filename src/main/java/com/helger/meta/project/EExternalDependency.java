/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.version.Version;

/**
 * Externally used dependencies in the latest applicable version per JDK.
 *
 * @author Philip Helger
 */
public enum EExternalDependency
{
  API_GUARDIAN ("org.apiguardian", "apiguardian-api", "1.0.0", EJDK.JDK8),
  ARCHAIS_CORE ("com.netflix.archaius", "archaius-core", "0.7.6", EJDK.JDK8),
  ANT ("org.apache.ant", "ant", "1.10.5", EJDK.JDK8),
  ANT_APACHE_RESOLVER ("org.apache.ant", "ant-apache-resolver", ANT),
  ANT_TESTUTIL ("org.apache.ant", "ant-testutil", ANT),
  ASM ("org.ow2.asm", "asm", "7.0", EJDK.JDK8),
  ASM_ANALYSIS ("org.ow2.asm", "asm-analysis", ASM),
  ASM_COMMONS ("org.ow2.asm", "asm-commons", ASM),
  ASM_TREE ("org.ow2.asm", "asm-tree", ASM),

  BC_MAIL ("org.bouncycastle", "bcmail-jdk15on", "1.60", EJDK.JDK8),
  BC_PG ("org.bouncycastle", "bcpg-jdk15on", BC_MAIL),
  BC_PROV ("org.bouncycastle", "bcprov-jdk15on", BC_MAIL),
  BC_PROV_EXT ("org.bouncycastle", "bcprov-ext-jdk15on", BC_MAIL),
  BC_PKIX ("org.bouncycastle", "bcpkix-jdk15on", BC_MAIL),

  CLOSURE ("com.google.javascript", "closure-compiler", "v20181028", EJDK.JDK8),
  CODEMODEL ("com.sun.codemodel", "codemodel", "2.6", EJDK.JDK8),
  COMMONS_BEANUTILS ("commons-beanutils", "commons-beanutils", "1.9.3", EJDK.JDK8),
  COMMONS_COLLECTIONS_3 ("commons-collections", "commons-collections", "3.2.2", EJDK.JDK8),
  COMMONS_COLLECTIONS_4 ("org.apache.commons", "commons-collections4", "4.2", EJDK.JDK8),
  COMMONS_DBCP2 ("org.apache.commons", "commons-dbcp2", "2.5.0", EJDK.JDK8),
  COMMONS_EXEC ("org.apache.commons", "commons-exec", "1.3", EJDK.JDK8),
  COMMONS_NET ("commons-net", "commons-net", "3.6", EJDK.JDK8),
  COMMONS_POOL2 ("org.apache.commons", "commons-pool2", "2.6.0", EJDK.JDK8),
  DNSJAVA ("dnsjava", "dnsjava", "2.1.8", EJDK.JDK8),
  DOCLET ("org.umlgraph", "doclet", "5.1", EJDK.JDK8),
  DOM4J ("dom4j", "dom4j", "1.6.1", EJDK.JDK8),
  EASYMOCK ("org.easymock", "easymock", "4.0.1", EJDK.JDK8),

  ECLIPSELINK_CORE ("org.eclipse.persistence", "org.eclipse.persistence.core", "2.7.3", EJDK.JDK8),
  ECLIPSELINK_JPA ("org.eclipse.persistence", "org.eclipse.persistence.jpa", ECLIPSELINK_CORE),
  ECLIPSELINK_ANTLR ("org.eclipse.persistence", "org.eclipse.persistence.antlr", ECLIPSELINK_CORE),
  ECLIPSELINK_ASM ("org.eclipse.persistence", "org.eclipse.persistence.asm", ECLIPSELINK_CORE),

  FELIX ("org.apache.felix", "org.apache.felix.framework", "6.0.1", EJDK.JDK8),
  FINDBUGS_ANNOTATIONS_3 ("com.google.code.findbugs", "annotations", "3.0.1u2", EJDK.JDK8),
  FLAPDOODLE ("de.flapdoodle.embed", "de.flapdoodle.embed.mongo", "2.1.1", EJDK.JDK8),
  FLUENT_HC ("org.apache.httpcomponents", "fluent-hc", "4.5.6", EJDK.JDK8),
  FOP0 ("fop", "fop", "0.20.5", EJDK.JDK8),
  FOP ("org.apache.xmlgraphics", "fop", "2.3", EJDK.JDK8),
  FOP_HYPH ("net.sf.offo", "fop-hyph", "2.0", EJDK.JDK8),
  FORBIDDEN_APIS ("de.thetaphi", "forbiddenapis", "2.6", EJDK.JDK8),
  GMAVEN_PLUS ("org.codehaus.gmavenplus", "gmavenplus-plugin", "1.6.2", EJDK.JDK8),
  @IsLegacy
  GROOVY_ALL_24 ("org.codehaus.groovy", "groovy-all", "2.4.15", EJDK.LEGACY),
  @IsBOM
  GROOVY_ALL ("org.codehaus.groovy", "groovy-all", "2.5.3", EJDK.JDK8),
  GROOVY ("org.codehaus.groovy", "groovy", GROOVY_ALL),
  H2 ("com.h2database", "h2", "1.4.197", EJDK.JDK8),
  HAMCREST_LIBRARY ("org.hamcrest", "hamcrest-library", "1.3", EJDK.JDK8),
  HAZELCAST ("com.hazelcast", "hazelcast", "3.11", EJDK.JDK8),
  HTTP_CORE ("org.apache.httpcomponents", "httpcore", "4.4.10", EJDK.JDK8),
  HTTP_CLIENT ("org.apache.httpcomponents", "httpclient", "4.5.6", EJDK.JDK8),
  HYSTRIX_CORE ("com.netflix.hystrix", "hystrix-core", "1.5.12", EJDK.JDK8),
  HYSTRIX_METRICS_EVENT_STREAM ("com.netflix.hystrix", "hystrix-metrics-event-stream", HYSTRIX_CORE),

  JACKSON_CORE ("com.fasterxml.jackson.core", "jackson-core", "2.9.7", EJDK.JDK8),
  JACKSON_ANNOTATIONS ("com.fasterxml.jackson.core", "jackson-annotations", JACKSON_CORE),
  JACKSON_DATABIND ("com.fasterxml.jackson.core", "jackson-databind", JACKSON_CORE),
  JACKSON_MODULE_AFTERBURNER ("com.fasterxml.jackson.module", "jackson-module-afterburner", JACKSON_CORE),
  JACKSON_DATAFORMAT_CBOR ("com.fasterxml.jackson.dataformat", "jackson-dataformat-cbor", JACKSON_CORE),

  JACOCO ("org.jacoco", "jacoco-maven-plugin", "0.8.2", EJDK.JDK8),
  JAVA_PARSER ("com.github.javaparser", "javaparser-core", "3.7.0", EJDK.JDK8),
  JAVACC ("net.java.dev.javacc", "javacc", "7.0.4", EJDK.JDK8),
  JAVAX_ACTIVATION ("javax.activation", "activation", "1.1.1", EJDK.JDK8),
  JAVAX_EL ("org.glassfish", "javax.el", "3.0.0", EJDK.JDK8),
  JAVAX_MAIL ("com.sun.mail", "javax.mail", "1.6.2", EJDK.JDK8),
  JAVAX_PERSISTENCE ("org.eclipse.persistence", "javax.persistence", "2.2.1", EJDK.JDK8),

  @IsBOM
  @IsLegacy
  JAXB_BOM ("org.glassfish.jaxb", "jaxb-bom", "2.2.11", EJDK.JDK8),
  @IsLegacy
  JAXB_CODEMODEL ("org.glassfish.jaxb", "codemodel", JAXB_BOM),
  @IsLegacy
  JAXB_CORE ("org.glassfish.jaxb", "jaxb-core", JAXB_BOM),
  @IsLegacy
  JAXB_JXC ("org.glassfish.jaxb", "jaxb-jxc", JAXB_BOM),
  @IsLegacy
  JAXB_RUNTIME ("org.glassfish.jaxb", "jaxb-runtime", JAXB_BOM),
  @IsLegacy
  JAXB_TXW2 ("org.glassfish.jaxb", "txw2", JAXB_BOM),
  @IsLegacy
  JAXB_XJC ("org.glassfish.jaxb", "jaxb-xjc", JAXB_BOM),

  @IsBOM
  JAXB9_BOM ("org.glassfish.jaxb", "jaxb-bom", "2.3.1", EJDK.JDK9),
  JAXB9_CODEMODEL ("org.glassfish.jaxb", "codemodel", JAXB9_BOM),
  JAXB9_JXC ("org.glassfish.jaxb", "jaxb-jxc", JAXB9_BOM),
  JAXB9_RUNTIME ("org.glassfish.jaxb", "jaxb-runtime", JAXB9_BOM),
  JAXB9_TXW2 ("org.glassfish.jaxb", "txw2", JAXB9_BOM),
  JAXB9_XJC ("org.glassfish.jaxb", "jaxb-xjc", JAXB9_BOM),

  // Not in 2.3.1...
  JAXB9_CORE ("org.glassfish.jaxb", "jaxb-core", "2.3.0.1", EJDK.JDK9),

  // JAXB_IMPL_SUN ("com.sun.xml.bind", "jaxb-impl", "2.2.11", EJDK.JDK8),
  // JAXB_JXC_SUN ("com.sun.xml.bind", "jaxb-jxc", JAXB_IMPL_SUN),
  // JAXB_XJC_SUN ("com.sun.xml.bind", "jaxb-xjc", JAXB_IMPL_SUN),
  @IsLegacy
  JAXB_RNGOM_SUN ("com.sun.xml.bind.external", "rngom", "2.2.11", EJDK.JDK8),

  // JAXB9_IMPL_SUN ("com.sun.xml.bind", "jaxb-impl", "2.3.0", EJDK.JDK9),
  // JAXB9_JXC_SUN ("com.sun.xml.bind", "jaxb-jxc", JAXB9_IMPL_SUN),
  // JAXB9_XJC_SUN ("com.sun.xml.bind", "jaxb-xjc", JAXB9_IMPL_SUN),
  JAXB9_RNGOM_SUN ("com.sun.xml.bind.external", "rngom", "2.3.1", EJDK.JDK9),

  JAXB2_PLUGIN ("org.jvnet.jaxb2.maven2", "maven-jaxb2-plugin", "0.14.0", EJDK.JDK8),
  JAXB2_BASICS ("org.jvnet.jaxb2_commons", "jaxb2-basics", "0.12.0", EJDK.JDK8),

  @IsBOM
  @IsLegacy
  JAXWS_RI_BOM ("com.sun.xml.ws", "jaxws-ri-bom", "2.2.10", EJDK.JDK8),
  @IsLegacy
  JAXWS_RT ("com.sun.xml.ws", "jaxws-rt", JAXWS_RI_BOM),
  @IsLegacy
  JAXWS_TOOLS ("com.sun.xml.ws", "jaxws-tools", JAXWS_RI_BOM),

  @IsBOM
  JAXWS9_RI_BOM ("com.sun.xml.ws", "jaxws-ri-bom", "2.3.1", EJDK.JDK9),
  JAXWS9_RT ("com.sun.xml.ws", "jaxws-rt", JAXWS9_RI_BOM),
  JAXWS9_TOOLS ("com.sun.xml.ws", "jaxws-tools", JAXWS9_RI_BOM),

  JAXWS_MAVEN_PLUGIN ("org.codehaus.mojo", "jaxws-maven-plugin", "2.5", EJDK.JDK8),
  @IsLegacy
  JBIG2 ("com.levigo.jbig2", "levigo-jbig2-imageio", "2.0", EJDK.JDK8),
  JBIG2_APACHE ("org.apache.pdfbox", "jbig2-imageio", "3.0.2", EJDK.JDK8),
  JEROMQ ("org.zeromq", "jeromq", "0.4.3", EJDK.JDK8),

  JERSEY1_SERVLET ("com.sun.jersey", "jersey-servlet", "1.19.4", EJDK.JDK8),
  JERSEY1_CLIENT ("com.sun.jersey", "jersey-client", JERSEY1_SERVLET),

  // JDK 1.7 since 2.7
  // JDK 1.8 since 2.26
  @IsBOM
  JERSEY2_BOM ("org.glassfish.jersey", "jersey-bom", "2.27", EJDK.JDK8),
  JERSEY2_SERVER ("org.glassfish.jersey.core", "jersey-server", JERSEY2_BOM),
  JERSEY2_COMMON ("org.glassfish.jersey.core", "jersey-common", JERSEY2_BOM),
  JERSEY2_CLIENT ("org.glassfish.jersey.core", "jersey-client", JERSEY2_BOM),
  JERSEY2_HK2 ("org.glassfish.jersey.inject", "jersey-hk2", JERSEY2_BOM),
  JERSEY2_SERVLET ("org.glassfish.jersey.containers", "jersey-container-servlet", JERSEY2_BOM),

  @IsBOM
  JETTY_BOM ("org.eclipse.jetty", "jetty-bom", "9.4.12.v20180830", EJDK.JDK8),
  JETTY_WEBAPP ("org.eclipse.jetty", "jetty-webapp", JETTY_BOM),
  JETTY_ANNOTATIONS ("org.eclipse.jetty", "jetty-annotations", JETTY_BOM),
  JETTY_PLUS ("org.eclipse.jetty", "jetty-plus", JETTY_BOM),
  JETTY_RUNNER ("org.eclipse.jetty", "jetty-runner", JETTY_BOM),
  JETTY_APACHE_JSP ("org.eclipse.jetty", "apache-jsp", JETTY_BOM),

  JING ("com.thaiopensource", "jing", "20091111", EJDK.JDK8),
  JJWT ("io.jsonwebtoken", "jjwt-impl", "0.10.5", EJDK.JDK8),
  @IsLegacy
  JDK ("JDK", "runtime", "1.8", EJDK.JDK8),
  JSCH ("com.jcraft", "jsch", "0.1.54", EJDK.JDK8),
  JSP_API_OLD ("javax.servlet.jsp", "jsp-api", "2.2", EJDK.JDK8),
  JSP_API ("javax.servlet.jsp", "javax.servlet.jsp-api", "2.3.3", EJDK.JDK8),
  JSR305 ("com.google.code.findbugs", "jsr305", "3.0.2", EJDK.JDK8),
  JTB ("edu.ucla.cs.compilers", "jtb", "1.3.2", EJDK.JDK8),
  JUNIT ("junit", "junit", "4.12", EJDK.JDK8),

  JUNIT5_JUPITER_API ("org.junit.jupiter", "junit-jupiter-api", "5.3.1", EJDK.JDK8),
  JUNIT5_JUPITER_ENGINE ("org.junit.jupiter", "junit-jupiter-engine", JUNIT5_JUPITER_API),
  JUNIT5_PLATFORM_LAUNCHER ("org.junit.platform", "junit-platform-launcher", "1.3.1", EJDK.JDK8),
  JUNIT5_PLATFORM_SUREFIRE_PROVIDER ("org.junit.platform",
                                     "junit-platform-surefire-provider",
                                     JUNIT5_PLATFORM_LAUNCHER),
  JUNIT5_VINTAGE_ENGINE ("org.junit.vintage", "junit-vintage-engine", JUNIT5_JUPITER_API),

  KAFKA_CLIENT ("org.apache.kafka", "kafka-clients", "2.0.0", EJDK.JDK8),
  LITTLEPROXY ("org.littleshoot", "littleproxy", "1.1.2", EJDK.JDK8),
  LOG4J2_CORE ("org.apache.logging.log4j", "log4j-core", "2.11.1", EJDK.JDK8),
  LOG4J2_SLF4J ("org.apache.logging.log4j", "log4j-slf4j-impl", LOG4J2_CORE),
  LOG4J2_WEB ("org.apache.logging.log4j", "log4j-web", LOG4J2_CORE),
  LUCENE_CORE ("org.apache.lucene", "lucene-core", "7.5.0", EJDK.JDK8),
  LUCENE_ANALYZER_COMMON ("org.apache.lucene", "lucene-analyzers-common", LUCENE_CORE),
  LUCENE_BACKWARD_CODECS ("org.apache.lucene", "lucene-backward-codecs", LUCENE_CORE),
  LUCENE_DEMO ("org.apache.lucene", "lucene-demo", LUCENE_CORE),
  LUCENE_GROUPING ("org.apache.lucene", "lucene-grouping", LUCENE_CORE),
  LUCENE_QUERYPARSER ("org.apache.lucene", "lucene-queryparser", LUCENE_CORE),
  MAVEN_PLUGIN_PLUGIN ("org.apache.maven.plugins", "maven-plugin-plugin", "3.6.0", EJDK.JDK8),
  @IsLegacy
  M2E ("org.eclipse.m2e", "lifecycle-mapping", "1.0.0", EJDK.JDK8),
  METRO ("org.glassfish.metro", "webservices-rt", "2.4.2", EJDK.JDK8),
  MIGLAYOUT ("com.miglayout", "miglayout-swing", "5.2", EJDK.JDK8),
  MONGO_DB ("org.mongodb", "mongodb-driver", "3.9.0", EJDK.JDK8),
  MYSQL ("mysql", "mysql-connector-java", "8.0.13", EJDK.JDK8),
  PDFBOX ("org.apache.pdfbox", "pdfbox", "2.0.12", EJDK.JDK8),
  PDFBOX_APP ("org.apache.pdfbox", "pdfbox-app", PDFBOX),
  PDFBOX_EXAMPLES ("org.apache.pdfbox", "pdfbox-examples", PDFBOX),
  POI ("org.apache.poi", "poi", "4.0.0", EJDK.JDK8),
  POI_OOXML ("org.apache.poi", "poi-ooxml", POI),
  POI_SCRATCHPAD ("org.apache.poi", "poi-scratchpad", POI),
  QUARTZ ("org.quartz-scheduler", "quartz", "2.3.0", EJDK.JDK8),
  RXJAVA ("io.reactivex", "rxjava", "1.3.8", EJDK.JDK8),
  SAXON ("net.sf.saxon", "Saxon-HE", "9.9.0-2", EJDK.JDK8),
  SELENIUM ("org.seleniumhq.selenium", "selenium-java", "3.141.5", EJDK.JDK8),
  @IsLegacy
  SERVLET_API_310 ("javax.servlet", "javax.servlet-api", "3.1.0", EJDK.LEGACY),
  SERVLET_API_400 ("javax.servlet", "javax.servlet-api", "4.0.1", EJDK.JDK8),
  SIMPLE_ODF ("org.apache.odftoolkit", "simple-odf", "0.8.2-incubating", EJDK.JDK8),
  SLF4J_API ("org.slf4j", "slf4j-api", "1.7.25", EJDK.JDK8),
  SLF4J_SIMPLE ("org.slf4j", "slf4j-simple", SLF4J_API),
  SLF4J_LOG4J12 ("org.slf4j", "slf4j-log4j12", SLF4J_API),
  SPOCK_CORE ("org.spockframework", "spock-core", "1.2-groovy-2.5", EJDK.JDK8),
  STAX_EX ("org.jvnet.staxex", "stax-ex", "1.8", EJDK.JDK8),
  JUL_TO_SLF4J ("org.slf4j", "jul-to-slf4j", SLF4J_API),
  JCL_OVER_SLF4J ("org.slf4j", "jcl-over-slf4j", SLF4J_API),
  THREE_TEN_EXTRA ("org.threeten", "threeten-extra", "1.4", EJDK.JDK8),
  TRANG ("com.thaiopensource", "trang", "20091111", EJDK.JDK8),
  UNDERTOW ("io.undertow", "undertow-servlet", "2.0.15.Final", EJDK.JDK8),
  VALIDATION_API ("javax.validation", "validation-api", "2.0.1.Final", EJDK.JDK8),
  VERSIONS_MAVEN_PLUGIN ("org.codehaus.mojo", "versions-maven-plugin", "2.7", EJDK.JDK8),
  WSS4J ("org.apache.wss4j", "wss4j-ws-security-dom", "2.2.2", EJDK.JDK8),
  XMLBEANS ("org.apache.xmlbeans", "xmlbeans", "3.0.2", EJDK.JDK8),
  XMLSEC ("org.apache.santuario", "xmlsec", "2.1.2", EJDK.JDK8),
  XSOM ("com.sun.xsom", "xsom", "20140925", EJDK.JDK8),
  ZXING_CORE ("com.google.zxing", "core", "3.3.3", EJDK.JDK8),
  ZXING_JAVASE ("com.google.zxing", "javase", ZXING_CORE),
  // parent POM dependencies
  PARENT_POM_0 ("com.mycila", "license-maven-plugin", "3.0", EJDK.JDK8),
  PARENT_POM_1 ("org.apache.felix", "maven-bundle-plugin", "4.1.0", EJDK.JDK8),
  PARENT_POM_2 ("org.apache.maven.plugins", "maven-acr-plugin", "3.1.0", EJDK.JDK8),
  PARENT_POM_3 ("org.apache.maven.plugins", "maven-antrun-plugin", "1.8", EJDK.JDK8),
  PARENT_POM_4 ("org.apache.maven.plugins", "maven-assembly-plugin", "3.1.0", EJDK.JDK8),
  PARENT_POM_5 ("org.apache.maven.plugins", "maven-changes-plugin", "2.12.1", EJDK.JDK8),
  PARENT_POM_6 ("org.apache.maven.plugins", "maven-checkstyle-plugin", "3.0.0", EJDK.JDK8),
  PARENT_POM_7 ("org.apache.maven.plugins", "maven-clean-plugin", "3.1.0", EJDK.JDK8),
  PARENT_POM_8 ("org.apache.maven.plugins", "maven-compiler-plugin", "3.8.0", EJDK.JDK8),
  PARENT_POM_9 ("org.apache.maven.plugins", "maven-dependency-plugin", "3.1.1", EJDK.JDK8),
  PARENT_POM_10 ("org.apache.maven.plugins", "maven-deploy-plugin", "2.8.2", EJDK.JDK8),
  PARENT_POM_11 ("org.apache.maven.plugins", "maven-ear-plugin", "3.0.1", EJDK.JDK8),
  PARENT_POM_12 ("org.apache.maven.plugins", "maven-ejb-plugin", "3.0.1", EJDK.JDK8),
  PARENT_POM_13 ("org.apache.maven.plugins", "maven-enforcer-plugin", "1.4.1", EJDK.JDK8),
  PARENT_POM_14 ("org.apache.maven.plugins", "maven-gpg-plugin", "1.6", EJDK.JDK8),
  PARENT_POM_15 ("org.apache.maven.plugins", "maven-idea-plugin", "2.2.1", EJDK.JDK8),
  PARENT_POM_16 ("org.apache.maven.plugins", "maven-install-plugin", "2.5.2", EJDK.JDK8),
  PARENT_POM_17 ("org.apache.maven.plugins", "maven-jar-plugin", "3.1.0", EJDK.JDK8),
  PARENT_POM_18 ("org.apache.maven.plugins", "maven-jarsigner-plugin", "3.0.0", EJDK.JDK8),
  PARENT_POM_19 ("org.apache.maven.plugins", "maven-javadoc-plugin", "3.0.1", EJDK.JDK8),
  PARENT_POM_20 ("org.apache.maven.plugins", "maven-jdeps-plugin", "3.1.1", EJDK.JDK8),
  PARENT_POM_21 ("org.apache.maven.plugins", "maven-jxr-plugin", "3.0.0", EJDK.JDK8),
  PARENT_POM_22 ("org.apache.maven.plugins", "maven-pmd-plugin", "3.11.0", EJDK.JDK8),
  PARENT_POM_23 ("org.apache.maven.plugins", "maven-project-info-reports-plugin", "3.0.0", EJDK.JDK8),
  PARENT_POM_24 ("org.apache.maven.plugins", "maven-rar-plugin", "2.4", EJDK.JDK8),
  PARENT_POM_25 ("org.apache.maven.plugins", "maven-release-plugin", "2.5.3", EJDK.JDK8),
  PARENT_POM_26 ("org.apache.maven.plugins", "maven-resources-plugin", "3.1.0", EJDK.JDK8),
  PARENT_POM_27 ("org.apache.maven.plugins", "maven-shade-plugin", "3.2.0", EJDK.JDK8),
  PARENT_POM_28 ("org.apache.maven.plugins", "maven-site-plugin", "3.7.1", EJDK.JDK8),
  PARENT_POM_29 ("org.apache.maven.plugins", "maven-source-plugin", "3.0.1", EJDK.JDK8),
  PARENT_POM_30 ("org.apache.maven.plugins", "maven-surefire-plugin", "2.22.1", EJDK.JDK8),
  PARENT_POM_31 ("org.apache.maven.plugins", "maven-surefire-report-plugin", "2.22.1", EJDK.JDK8),
  PARENT_POM_32 ("org.apache.maven.plugins", "maven-failsafe-plugin", "2.22.1", EJDK.JDK8),
  PARENT_POM_33 ("org.apache.maven.plugins", "maven-war-plugin", "3.2.2", EJDK.JDK8),
  PARENT_POM_34 ("org.codehaus.mojo", "clirr-maven-plugin", "2.8", EJDK.JDK8),
  PARENT_POM_35 ("org.codehaus.mojo", "cobertura-maven-plugin", "2.7", EJDK.JDK8),
  PARENT_POM_36 ("org.codehaus.mojo", "findbugs-maven-plugin", "3.0.5", EJDK.JDK8),
  PARENT_POM_37 ("org.codehaus.mojo", "jdepend-maven-plugin", "2.0", EJDK.JDK8),
  PARENT_POM_38 ("org.codehaus.mojo", "taglist-maven-plugin", "2.4", EJDK.JDK8);

  private final String m_sGroupID;
  private final String m_sArticfactID;
  private final String m_sVersion;
  private final Version m_aVersion;
  private final EJDK m_eMinJDK;
  private final boolean m_bIsBOM;
  private final boolean m_bIsLegacy;

  private EExternalDependency (@Nonnull @Nonempty final String sGroupID,
                               @Nonnull @Nonempty final String sArticfactID,
                               @Nonnull final EExternalDependency eBase)
  {
    this (sGroupID, sArticfactID, eBase.getLastPublishedVersionString (), eBase.getMinimumJDKVersion ());
  }

  private EExternalDependency (@Nonnull @Nonempty final String sGroupID,
                               @Nonnull @Nonempty final String sArticfactID,
                               @Nonnull @Nonempty final String sVersion,
                               @Nonnull final EJDK eMinJDK)
  {
    m_sGroupID = sGroupID;
    m_sArticfactID = sArticfactID;
    m_sVersion = sVersion;
    m_aVersion = Version.parse (sVersion);
    m_eMinJDK = eMinJDK;
    try
    {
      final Field aField = EExternalDependency.class.getField (name ());
      m_bIsBOM = aField.isAnnotationPresent (IsBOM.class);
      m_bIsLegacy = aField.isAnnotationPresent (IsLegacy.class);
    }
    catch (final Exception ex)
    {
      throw new IllegalStateException (ex);
    }
  }

  @Nonnull
  @Nonempty
  public String getGroupID ()
  {
    return m_sGroupID;
  }

  public boolean hasGroupID (@Nullable final String sGroupID)
  {
    return getGroupID ().equals (sGroupID);
  }

  @Nonnull
  @Nonempty
  public String getArtifactID ()
  {
    return m_sArticfactID;
  }

  public boolean hasArtifactID (@Nullable final String sGroupID)
  {
    return getArtifactID ().equals (sGroupID);
  }

  @Nonnull
  public Version getLastPublishedVersion ()
  {
    return m_aVersion;
  }

  @Nonnull
  @Nonempty
  public String getLastPublishedVersionString ()
  {
    return m_sVersion;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sGroupID + "::" + m_sArticfactID;
  }

  @Nonnull
  @Nonempty
  public String getDisplayNameWithVersion ()
  {
    return getDisplayName () + "::" + m_sVersion;
  }

  @Nonnull
  public EJDK getMinimumJDKVersion ()
  {
    return m_eMinJDK;
  }

  @Nullable
  public EExternalDependency getReplacement (@Nonnull final EJDK eForJDK)
  {
    ValueEnforcer.notNull (eForJDK, "ForJDK");
    switch (this)
    {
      // case JAXB_IMPL_SUN:
      // return JAXB_CORE;
      // case JAXB_JXC_SUN:
      // return JAXB_JXC;
      // case JAXB_XJC_SUN:
      // return JAXB_XJC;
      case JSP_API_OLD:
        return JSP_API;
    }
    return null;
  }

  public boolean isDeprecatedForJDK (@Nonnull final EJDK eForJDK)
  {
    return getReplacement (eForJDK) != null;
  }

  public boolean isBOM ()
  {
    return m_bIsBOM;
  }

  public boolean isLegacy ()
  {
    return m_bIsLegacy;
  }

  @Nullable
  public static ICommonsList <EExternalDependency> findAll (@Nullable final String sGroupID,
                                                            @Nullable final String sArtifactID)
  {
    final ICommonsList <EExternalDependency> ret = findAll (x -> x.hasGroupID (sGroupID) &&
                                                                 x.hasArtifactID (sArtifactID));
    // Sort by JDK descending
    ret.sort (Comparator.comparingInt ( (final EExternalDependency e) -> e.getMinimumJDKVersion ().getMajor ())
                        .reversed ());
    return ret;
  }

  @Nullable
  public static ICommonsList <EExternalDependency> findAll (@Nonnull final Predicate <EExternalDependency> aFilter)
  {
    return EnumHelper.getAll (EExternalDependency.class, aFilter);
  }
}
