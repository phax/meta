/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
  API_GUARDIAN ("org.apiguardian", "apiguardian-api", "1.1.1", EJDK.JDK8),
  ARCHAIS_CORE ("com.netflix.archaius", "archaius-core", "0.7.7", EJDK.JDK8),
  ANT ("org.apache.ant", "ant", "1.10.9", EJDK.JDK8),
  ANT_APACHE_RESOLVER ("org.apache.ant", "ant-apache-resolver", ANT),
  ANT_TESTUTIL ("org.apache.ant", "ant-testutil", ANT),
  ASM ("org.ow2.asm", "asm", "9.1", EJDK.JDK8),
  ASM_ANALYSIS ("org.ow2.asm", "asm-analysis", ASM),
  ASM_COMMONS ("org.ow2.asm", "asm-commons", ASM),
  ASM_TREE ("org.ow2.asm", "asm-tree", ASM),
  AWS_LAMBDA_CORE ("com.amazonaws", "aws-lambda-java-core", "1.2.1", EJDK.JDK8),
  BATIK_BRIDGE ("org.apache.xmlgraphics", "batik-bridge", "1.14", EJDK.JDK8),

  BC_MAIL ("org.bouncycastle", "bcmail-jdk15on", "1.68", EJDK.JDK8),
  BC_PG ("org.bouncycastle", "bcpg-jdk15on", BC_MAIL),
  BC_PROV ("org.bouncycastle", "bcprov-jdk15on", BC_MAIL),
  BC_PKIX ("org.bouncycastle", "bcpkix-jdk15on", BC_MAIL),
  BC_PROV_EXT ("org.bouncycastle", "bcprov-ext-jdk15on", BC_MAIL),

  CLASSLOADER_LEAK_PROTECTION ("se.jiderhamn.classloader-leak-prevention", "classloader-leak-prevention-core", "2.7.0", EJDK.JDK8),
  CODEMODEL ("com.sun.codemodel", "codemodel", "2.6", EJDK.JDK8),
  COMMONS_BEANUTILS ("commons-beanutils", "commons-beanutils", "1.9.4", EJDK.JDK8),
  COMMONS_CODEC ("commons-codec", "commons-codec", "1.15", EJDK.JDK8),
  COMMONS_COLLECTIONS_3 ("commons-collections", "commons-collections", "3.2.2", EJDK.JDK8),
  COMMONS_COLLECTIONS_4 ("org.apache.commons", "commons-collections4", "4.4", EJDK.JDK8),
  COMMONS_COMPRESS ("org.apache.commons", "commons-compress", "1.20", EJDK.JDK8),
  COMMONS_DBCP2 ("org.apache.commons", "commons-dbcp2", "2.8.0", EJDK.JDK8),
  COMMONS_EXEC ("org.apache.commons", "commons-exec", "1.3", EJDK.JDK8),
  COMMONS_MATH3 ("org.apache.commons", "commons-math3", "3.6.1", EJDK.JDK8),
  COMMONS_NET ("commons-net", "commons-net", "3.8.0", EJDK.JDK8),
  COMMONS_POOL2 ("org.apache.commons", "commons-pool2", "2.9.0", EJDK.JDK8),

  DNSJAVA ("dnsjava", "dnsjava", "3.3.1", EJDK.JDK8),
  DOCLET ("org.umlgraph", "doclet", "5.1", EJDK.JDK8),
  @IsLegacy (replacedWith = "org.dom4j:dom4j")
  DOM4J_OLD("dom4j", "dom4j", "1.6.1", EJDK.JDK8),
  DOM4J ("org.dom4j", "dom4j", "2.1.3", EJDK.JDK8),
  EASYMOCK ("org.easymock", "easymock", "4.2", EJDK.JDK8),

  ECLIPSELINK2_CORE ("org.eclipse.persistence", "org.eclipse.persistence.core", "2.7.8", EJDK.JDK8),
  ECLIPSELINK2_JPA ("org.eclipse.persistence", "org.eclipse.persistence.jpa", ECLIPSELINK2_CORE),
  ECLIPSELINK2_ANTLR ("org.eclipse.persistence", "org.eclipse.persistence.antlr", ECLIPSELINK2_CORE),
  ECLIPSELINK2_ASM ("org.eclipse.persistence", "org.eclipse.persistence.asm", ECLIPSELINK2_CORE),

  ECLIPSELINK3_CORE ("org.eclipse.persistence", "org.eclipse.persistence.core", "3.0.0", EJDK.JDK11),
  ECLIPSELINK3_JPA ("org.eclipse.persistence", "org.eclipse.persistence.jpa", ECLIPSELINK3_CORE),
  ECLIPSELINK3_ASM ("org.eclipse.persistence", "org.eclipse.persistence.asm", ECLIPSELINK3_CORE),

  EXPIRING_MAP ("net.jodah", "expiringmap", "0.5.9", EJDK.JDK8),
  FELIX ("org.apache.felix", "org.apache.felix.framework", "7.0.0", EJDK.JDK8),
  FINDBUGS_ANNOTATIONS_3 ("com.google.code.findbugs", "annotations", "3.0.1u2", EJDK.JDK8),
  FLAPDOODLE ("de.flapdoodle.embed", "de.flapdoodle.embed.mongo", "3.0.0", EJDK.JDK8),
  FLYWAY ("org.flywaydb", "flyway-core", "7.5.4", EJDK.JDK8),
  FOP0 ("fop", "fop", "0.20.5", EJDK.JDK8),
  FOP ("org.apache.xmlgraphics", "fop", "2.6", EJDK.JDK8),
  FOP_HYPH ("net.sf.offo", "fop-hyph", "2.0", EJDK.JDK8),
  FORBIDDEN_APIS ("de.thetaphi", "forbiddenapis", "3.1", EJDK.JDK8),
  GMAVEN_PLUS ("org.codehaus.gmavenplus", "gmavenplus-plugin", "1.12.1", EJDK.JDK8),

  GOOGLE_CLOSURE ("com.google.javascript", "closure-compiler", "v20210202", EJDK.JDK8),
  GOOGLE_PROTOBUF ("com.google.protobuf", "protobuf-java", "3.15.3", EJDK.JDK8),

  @IsLegacy (replacedWith = "2.5.x")
  GROOVY_ALL_24("org.codehaus.groovy", "groovy-all", "2.4.15", EJDK.LEGACY),
  @IsBOM
  GROOVY_ALL ("org.codehaus.groovy", "groovy-all", "3.0.7", EJDK.JDK8),
  GROOVY ("org.codehaus.groovy", "groovy", GROOVY_ALL),

  H2 ("com.h2database", "h2", "1.4.200", EJDK.JDK8),

  HAMCREST_LIBRARY ("org.hamcrest", "hamcrest-library", "2.2", EJDK.JDK8),

  HAZELCAST ("com.hazelcast", "hazelcast", "4.1.1", EJDK.JDK8),

  HTTP_CORE ("org.apache.httpcomponents", "httpcore", "4.4.14", EJDK.JDK8),
  HTTP_CLIENT ("org.apache.httpcomponents", "httpclient", "4.5.13", EJDK.JDK8),
  HTTP_CLIENT_FLUENT_HC ("org.apache.httpcomponents", "fluent-hc", "4.5.13", EJDK.JDK8),

  HYSTRIX_CORE ("com.netflix.hystrix", "hystrix-core", "1.5.18", EJDK.JDK8),
  HYSTRIX_METRICS_EVENT_STREAM ("com.netflix.hystrix", "hystrix-metrics-event-stream", HYSTRIX_CORE),

  JACKSON_CORE ("com.fasterxml.jackson.core", "jackson-core", "2.12.1", EJDK.JDK8),
  JACKSON_ANNOTATIONS ("com.fasterxml.jackson.core", "jackson-annotations", JACKSON_CORE),
  JACKSON_DATABIND ("com.fasterxml.jackson.core", "jackson-databind", JACKSON_CORE),
  JACKSON_MODULE_AFTERBURNER ("com.fasterxml.jackson.module", "jackson-module-afterburner", JACKSON_CORE),
  JACKSON_DATAFORMAT_CBOR ("com.fasterxml.jackson.dataformat", "jackson-dataformat-cbor", JACKSON_CORE),

  JACOCO ("org.jacoco", "jacoco-maven-plugin", "0.8.6", EJDK.JDK8),

  JAKARTA_ACTIVATION ("com.sun.activation", "jakarta.activation", "1.2.2", EJDK.JDK8),
  // We need to wait for BouncyCastle before we can update
  JAKARTA_ACTIVATION_2 ("com.sun.activation", "jakarta.activation", "2.0.0", EJDK.JDK11),
  JAKARTA_MAIL ("com.sun.mail", "jakarta.mail", "1.6.5", EJDK.JDK8),
  // We need to wait for BouncyCastle before we can update
  JAKARTA_MAIL_2 ("com.sun.mail", "jakarta.mail", "2.0.0", EJDK.JDK11),
  JAKARTA_PERSISTENCE ("org.eclipse.persistence", "jakarta.persistence", "2.2.3", EJDK.JDK8),
  JAKARTA_SERVLET_API ("jakarta.servlet", "jakarta.servlet-api", "5.0.0", EJDK.JDK8),

  JAKARTA_JSP_API ("jakarta.servlet.jsp", "jakarta.servlet.jsp-api", "2.3.6", EJDK.JDK8),
  // We need to wait for implementations
  JAKARTA_JSP_API_3 ("jakarta.servlet.jsp", "jakarta.servlet.jsp-api", "3.0.0", EJDK.JDK11),

  JAVA_PARSER_CORE ("com.github.javaparser", "javaparser-core", "3.19.0", EJDK.JDK8),

  JAVACC ("net.java.dev.javacc", "javacc", "7.0.10", EJDK.JDK8),
  JAVAX_EL ("org.glassfish", "javax.el", "3.0.0", EJDK.JDK8),
  // @IsLegacy (replacedWith = "4.x")
  JAVAX_SERVLET_API_310 ("javax.servlet", "javax.servlet-api", "3.1.0", EJDK.JDK8),
  // @IsLegacy (replacedWith = "jakarta.servlet-api")
  JAVAX_SERVLET_API_401 ("javax.servlet", "javax.servlet-api", "4.0.1", EJDK.JDK11),

  JAXB2_API ("jakarta.xml.bind", "jakarta.xml.bind-api", "2.3.3", EJDK.JDK8),
  @IsBOM
  JAXB2_BOM ("com.sun.xml.bind", "jaxb-bom-ext", "2.3.3", EJDK.JDK8),
  JAXB2_CODEMODEL ("com.sun.xml.bind", "jaxb-impl", JAXB2_BOM),

  JAXB2_PLUGIN ("org.jvnet.jaxb2.maven2", "maven-jaxb2-plugin", "0.14.0", EJDK.JDK8),
  JAXB2_BASICS ("org.jvnet.jaxb2_commons", "jaxb2-basics", "0.12.0", EJDK.JDK8),

  JAXB3_API ("jakarta.xml.bind", "jakarta.xml.bind-api", "3.0.0", EJDK.JDK11),
  @IsBOM
  JAXB3_BOM ("com.sun.xml.bind", "jaxb-bom-ext", "3.0.0", EJDK.JDK11),
  JAXB3_CODEMODEL ("com.sun.xml.bind", "jaxb-impl", JAXB3_BOM),

  JAXWS2_API ("jakarta.xml.ws", "jakarta.xml.ws-api", "2.3.3", EJDK.JDK8),
  @IsBOM
  JAXWS2_RI_BOM ("com.sun.xml.ws", "jaxws-ri-bom", "2.3.3", EJDK.JDK8),
  JAXWS2_RT ("com.sun.xml.ws", "jaxws-rt", JAXWS2_RI_BOM),
  JAXWS2_MAVEN_PLUGIN ("com.sun.xml.ws", "jaxws-maven-plugin", JAXWS2_RI_BOM),

  JAXWS3_API ("jakarta.xml.ws", "jakarta.xml.ws-api", "3.0.0", EJDK.JDK11),
  @IsBOM
  JAXWS3_RI_BOM ("com.sun.xml.ws", "jaxws-ri-bom", "3.0.0", EJDK.JDK11),
  JAXWS3_RT ("com.sun.xml.ws", "jaxws-rt", JAXWS3_RI_BOM),
  JAXWS3_MAVEN_PLUGIN ("com.sun.xml.ws", "jaxws-maven-plugin", JAXWS3_RI_BOM),

  JBIG2_APACHE ("org.apache.pdfbox", "jbig2-imageio", "3.0.3", EJDK.JDK8),
  JEROMQ ("org.zeromq", "jeromq", "0.5.2", EJDK.JDK8),

  JDT_CORE ("org.eclipse.jdt", "org.eclipse.jdt.core", "3.24.0", EJDK.JDK8),

  JERSEY1_SERVLET ("com.sun.jersey", "jersey-servlet", "1.19.4", EJDK.JDK8),
  JERSEY1_CLIENT ("com.sun.jersey", "jersey-client", JERSEY1_SERVLET),

  // JDK 1.7 since 2.7
  // JDK 1.8 since 2.26
  @IsBOM
  JERSEY2_BOM ("org.glassfish.jersey", "jersey-bom", "2.32", EJDK.JDK8),
  JERSEY2_SERVER ("org.glassfish.jersey.core", "jersey-server", JERSEY2_BOM),
  JERSEY2_COMMON ("org.glassfish.jersey.core", "jersey-common", JERSEY2_BOM),
  JERSEY2_CLIENT ("org.glassfish.jersey.core", "jersey-client", JERSEY2_BOM),
  JERSEY2_HK2 ("org.glassfish.jersey.inject", "jersey-hk2", JERSEY2_BOM),
  JERSEY2_SERVLET ("org.glassfish.jersey.containers", "jersey-container-servlet", JERSEY2_BOM),

  @IsBOM
  JERSEY3_BOM ("org.glassfish.jersey", "jersey-bom", "3.0.1", EJDK.JDK11),
  JERSEY3_SERVER ("org.glassfish.jersey.core", "jersey-server", JERSEY3_BOM),
  JERSEY3_COMMON ("org.glassfish.jersey.core", "jersey-common", JERSEY3_BOM),
  JERSEY3_CLIENT ("org.glassfish.jersey.core", "jersey-client", JERSEY3_BOM),
  JERSEY3_HK2 ("org.glassfish.jersey.inject", "jersey-hk2", JERSEY3_BOM),
  JERSEY3_SERVLET ("org.glassfish.jersey.containers", "jersey-container-servlet", JERSEY3_BOM),

  @IsBOM
  JETTY9_BOM ("org.eclipse.jetty", "jetty-bom", "9.4.36.v20210114", EJDK.JDK8),
  JETTY9_WEBAPP ("org.eclipse.jetty", "jetty-webapp", JETTY9_BOM),
  JETTY9_ANNOTATIONS ("org.eclipse.jetty", "jetty-annotations", JETTY9_BOM),
  JETTY9_PLUS ("org.eclipse.jetty", "jetty-plus", JETTY9_BOM),
  JETTY9_RUNNER ("org.eclipse.jetty", "jetty-runner", JETTY9_BOM),
  JETTY9_APACHE_JSP ("org.eclipse.jetty", "apache-jsp", JETTY9_BOM),

  @IsBOM
  JETTY11_BOM ("org.eclipse.jetty", "jetty-bom", "11.0.1", EJDK.JDK11),
  JETTY11_WEBAPP ("org.eclipse.jetty", "jetty-webapp", JETTY11_BOM),
  JETTY11_ANNOTATIONS ("org.eclipse.jetty", "jetty-annotations", JETTY11_BOM),
  JETTY11_PLUS ("org.eclipse.jetty", "jetty-plus", JETTY11_BOM),
  JETTY11_RUNNER ("org.eclipse.jetty", "jetty-runner", JETTY11_BOM),
  JETTY11_APACHE_JSP ("org.eclipse.jetty", "apache-jsp", JETTY11_BOM),

  JING ("com.thaiopensource", "jing", "20091111", EJDK.JDK8),
  JJWT_IMPL ("io.jsonwebtoken", "jjwt-impl", "0.11.2", EJDK.JDK8),
  JJWT_ORG_JSON ("io.jsonwebtoken", "jjwt-orgjson", "0.11.2", EJDK.JDK8),
  @IsLegacy
  JDK ("JDK", "runtime", "1.8", EJDK.JDK8),
  JSCH ("com.jcraft", "jsch", "0.1.55", EJDK.JDK8),
  @IsLegacy (replacedWith = "jakarta.servlet.jsp-api")
  JAVAX_JSP_API("javax.servlet.jsp", "javax.servlet.jsp-api", "2.3.3", EJDK.JDK8),
  JSR305 ("com.google.code.findbugs", "jsr305", "3.0.2", EJDK.JDK8),
  JTB ("edu.ucla.cs.compilers", "jtb", "1.3.2", EJDK.JDK8),
  JUNIT ("junit", "junit", "4.13.2", EJDK.JDK8),

  JUNIT5_JUPITER ("org.junit.jupiter", "junit-jupiter", "5.7.1", EJDK.JDK8),
  JUNIT5_JUPITER_API ("org.junit.jupiter", "junit-jupiter-api", JUNIT5_JUPITER),
  JUNIT5_JUPITER_ENGINE ("org.junit.jupiter", "junit-jupiter-engine", JUNIT5_JUPITER),
  JUNIT5_VINTAGE_ENGINE ("org.junit.vintage", "junit-vintage-engine", JUNIT5_JUPITER),
  JUNIT5_PLATFORM_LAUNCHER ("org.junit.platform", "junit-platform-launcher", "1.7.1", EJDK.JDK8),
  JUNIT5_PLATFORM_SUREFIRE_PROVIDER ("org.junit.platform", "junit-platform-surefire-provider", "1.3.2", EJDK.JDK8),

  KAFKA_CLIENT ("org.apache.kafka", "kafka-clients", "2.7.0", EJDK.JDK8),
  LITTLEPROXY ("org.littleshoot", "littleproxy", "1.1.2", EJDK.JDK8),

  LOG4J2_CORE ("org.apache.logging.log4j", "log4j-core", "2.14.0", EJDK.JDK8),
  LOG4J2_SLF4J ("org.apache.logging.log4j", "log4j-slf4j-impl", LOG4J2_CORE),
  LOG4J2_WEB ("org.apache.logging.log4j", "log4j-web", LOG4J2_CORE),

  LUCENE_CORE ("org.apache.lucene", "lucene-core", "7.7.3", EJDK.JDK8),
  LUCENE_ANALYZER_COMMON ("org.apache.lucene", "lucene-analyzers-common", LUCENE_CORE),
  LUCENE_BACKWARD_CODECS ("org.apache.lucene", "lucene-backward-codecs", LUCENE_CORE),
  LUCENE_DEMO ("org.apache.lucene", "lucene-demo", LUCENE_CORE),
  LUCENE_GROUPING ("org.apache.lucene", "lucene-grouping", LUCENE_CORE),
  LUCENE_QUERYPARSER ("org.apache.lucene", "lucene-queryparser", LUCENE_CORE),

  // JDK 11 is just fake, because I'm stuck with Lucene 7 in Directory
  LUCENE8_CORE ("org.apache.lucene", "lucene-core", "8.8.1", EJDK.JDK11),
  LUCENE8_ANALYZER_COMMON ("org.apache.lucene", "lucene-analyzers-common", LUCENE8_CORE),
  LUCENE8_BACKWARD_CODECS ("org.apache.lucene", "lucene-backward-codecs", LUCENE8_CORE),
  LUCENE8_DEMO ("org.apache.lucene", "lucene-demo", LUCENE8_CORE),
  LUCENE8_GROUPING ("org.apache.lucene", "lucene-grouping", LUCENE8_CORE),
  LUCENE8_QUERYPARSER ("org.apache.lucene", "lucene-queryparser", LUCENE8_CORE),

  MAVEN_PLUGIN_PLUGIN ("org.apache.maven.plugins", "maven-plugin-plugin", "3.6.0", EJDK.JDK8),
  @IsLegacy
  M2E ("org.eclipse.m2e", "lifecycle-mapping", "1.0.0", EJDK.JDK8),
  METRO ("org.glassfish.metro", "webservices-rt", "3.0.0", EJDK.JDK8),
  MIGLAYOUT ("com.miglayout", "miglayout-swing", "5.2", EJDK.JDK8),

  @IsLegacy
  MONGO_DRIVER_ASYNC ("org.mongodb", "mongodb-driver-async", "3.12.1", EJDK.JDK8),
  MONGO_DRIVER_REACTIVESTREAMS ("org.mongodb", "mongodb-driver-reactivestreams", "4.2.2", EJDK.JDK8),
  MONGO_DRIVER_SYNC ("org.mongodb", "mongodb-driver-sync", "4.2.2", EJDK.JDK8),

  MYSQL ("mysql", "mysql-connector-java", "8.0.23", EJDK.JDK8),

  OWASP_DEPENDENCY_CHECK ("org.owasp", "dependency-check-maven", "6.1.1", EJDK.JDK8),

  PDFBOX ("org.apache.pdfbox", "pdfbox", "2.0.22", EJDK.JDK8),
  PDFBOX_APP ("org.apache.pdfbox", "pdfbox-app", PDFBOX),
  PDFBOX_EXAMPLES ("org.apache.pdfbox", "pdfbox-examples", PDFBOX),

  PICOCLI ("info.picocli", "picocli", "4.6.1", EJDK.JDK8),
  POI ("org.apache.poi", "poi", "5.0.0", EJDK.JDK8),
  POI_OOXML ("org.apache.poi", "poi-ooxml", POI),
  POI_SCRATCHPAD ("org.apache.poi", "poi-scratchpad", POI),
  POSTGRESQL ("org.postgresql", "postgresql", "42.2.19", EJDK.JDK8),

  QUARTZ ("org.quartz-scheduler", "quartz", "2.3.2", EJDK.JDK8),
  R7INSIGHT ("com.rapid7", "r7insight_java", "3.0.7", EJDK.JDK8),
  RATELIMITJ_INMEMORY ("es.moki.ratelimitj", "ratelimitj-inmemory", "0.6.0", EJDK.JDK8),
  RXJAVA ("io.reactivex", "rxjava", "1.3.8", EJDK.JDK8),
  SAXON ("net.sf.saxon", "Saxon-HE", "10.3", EJDK.JDK8),
  SCHXSLT ("name.dmaus.schxslt", "schxslt", "1.6.2", EJDK.JDK8),
  SELENIUM ("org.seleniumhq.selenium", "selenium-java", "3.141.59", EJDK.JDK8),
  SIMPLE_ODF ("org.odftoolkit", "simple-odf", "0.9.0-RC1", EJDK.JDK8),

  SLF4J_API ("org.slf4j", "slf4j-api", "1.7.30", EJDK.JDK8),
  SLF4J_SIMPLE ("org.slf4j", "slf4j-simple", SLF4J_API),
  SLF4J_LOG4J12 ("org.slf4j", "slf4j-log4j12", SLF4J_API),
  JUL_TO_SLF4J ("org.slf4j", "jul-to-slf4j", SLF4J_API),
  JCL_OVER_SLF4J ("org.slf4j", "jcl-over-slf4j", SLF4J_API),
  LOG4J2_OVER_SLF4J ("org.slf4j", "log4j-over-slf4j", SLF4J_API),

  SPOCK_CORE ("org.spockframework", "spock-core", "2.0-M4-groovy-3.0", EJDK.JDK8),
  @IsBOM
  SPRING_BOOT_DEPENDENCIES ("org.springframework.boot", "spring-boot-dependencies", "2.4.3", EJDK.JDK8),
  SPRING_BOOT_STARTER_WEB ("org.springframework.boot", "spring-boot-starter-web", SPRING_BOOT_DEPENDENCIES),
  SPRING_BOOT_STARTER_TEST ("org.springframework.boot", "spring-boot-starter-test", SPRING_BOOT_DEPENDENCIES),
  STAX_EX ("org.jvnet.staxex", "stax-ex", "2.0.0", EJDK.JDK8),

  TESTNG ("org.testng", "testng", "7.4.0", EJDK.JDK8),
  THREE_TEN_EXTRA ("org.threeten", "threeten-extra", "1.6.0", EJDK.JDK8),
  TRANG ("com.thaiopensource", "trang", "20091111", EJDK.JDK8),
  TYPESAFE_CONFIG ("com.typesafe", "config", "1.4.1", EJDK.JDK8),
  UNDERTOW ("io.undertow", "undertow-servlet", "2.2.4.Final", EJDK.JDK8),
  VALIDATION_API ("javax.validation", "validation-api", "2.0.1.Final", EJDK.JDK8),
  VERSIONS_MAVEN_PLUGIN ("org.codehaus.mojo", "versions-maven-plugin", "2.8.1", EJDK.JDK8),
  WSS4J ("org.apache.wss4j", "wss4j-ws-security-dom", "2.3.1", EJDK.JDK8),
  XMLBEANS ("org.apache.xmlbeans", "xmlbeans", "4.0.0", EJDK.JDK8),
  XMLSEC ("org.apache.santuario", "xmlsec", "2.2.1", EJDK.JDK8),
  XSOM ("com.sun.xsom", "xsom", "20140925", EJDK.JDK8),
  ZXING_CORE ("com.google.zxing", "core", "3.4.1", EJDK.JDK8),
  ZXING_JAVASE ("com.google.zxing", "javase", ZXING_CORE),
  // parent POM dependencies
  PARENT_POM_0 ("com.mycila", "license-maven-plugin", "3.0", EJDK.JDK8),
  PARENT_POM_1 ("org.apache.felix", "maven-bundle-plugin", "5.1.1", EJDK.JDK8),
  PARENT_POM_2 ("org.apache.maven.plugins", "maven-acr-plugin", "3.1.0", EJDK.JDK8),
  PARENT_POM_3 ("org.apache.maven.plugins", "maven-antrun-plugin", "3.0.0", EJDK.JDK8),
  PARENT_POM_4 ("org.apache.maven.plugins", "maven-assembly-plugin", "3.3.0", EJDK.JDK8),
  PARENT_POM_5 ("org.apache.maven.plugins", "maven-changes-plugin", "2.12.1", EJDK.JDK8),
  PARENT_POM_6 ("org.apache.maven.plugins", "maven-checkstyle-plugin", "3.1.2", EJDK.JDK8),
  PARENT_POM_7 ("org.apache.maven.plugins", "maven-clean-plugin", "3.1.0", EJDK.JDK8),
  PARENT_POM_8 ("org.apache.maven.plugins", "maven-compiler-plugin", "3.8.1", EJDK.JDK8),
  PARENT_POM_9 ("org.apache.maven.plugins", "maven-dependency-plugin", "3.1.2", EJDK.JDK8),
  PARENT_POM_10 ("org.apache.maven.plugins", "maven-deploy-plugin", "2.8.2", EJDK.JDK8),
  PARENT_POM_11 ("org.apache.maven.plugins", "maven-ear-plugin", "3.2.0", EJDK.JDK8),
  PARENT_POM_12 ("org.apache.maven.plugins", "maven-ejb-plugin", "3.1.0", EJDK.JDK8),
  PARENT_POM_13 ("org.apache.maven.plugins", "maven-enforcer-plugin", "1.4.1", EJDK.JDK8),
  PARENT_POM_14 ("org.apache.maven.plugins", "maven-gpg-plugin", "1.6", EJDK.JDK8),
  PARENT_POM_15 ("org.apache.maven.plugins", "maven-idea-plugin", "2.2.1", EJDK.JDK8),
  PARENT_POM_16 ("org.apache.maven.plugins", "maven-install-plugin", "2.5.2", EJDK.JDK8),
  PARENT_POM_17 ("org.apache.maven.plugins", "maven-jar-plugin", "3.2.0", EJDK.JDK8),
  PARENT_POM_18 ("org.apache.maven.plugins", "maven-jarsigner-plugin", "3.0.0", EJDK.JDK8),
  PARENT_POM_19 ("org.apache.maven.plugins", "maven-javadoc-plugin", "3.2.0", EJDK.JDK8),
  PARENT_POM_20 ("org.apache.maven.plugins", "maven-jdeps-plugin", "3.1.2", EJDK.JDK8),
  PARENT_POM_21 ("org.apache.maven.plugins", "maven-jxr-plugin", "3.0.0", EJDK.JDK8),
  PARENT_POM_22 ("org.apache.maven.plugins", "maven-pmd-plugin", "3.14.0", EJDK.JDK8),
  PARENT_POM_23 ("org.apache.maven.plugins", "maven-project-info-reports-plugin", "3.1.1", EJDK.JDK8),
  PARENT_POM_24 ("org.apache.maven.plugins", "maven-rar-plugin", "2.4", EJDK.JDK8),
  PARENT_POM_25 ("org.apache.maven.plugins", "maven-release-plugin", "2.5.3", EJDK.JDK8),
  PARENT_POM_26 ("org.apache.maven.plugins", "maven-resources-plugin", "3.2.0", EJDK.JDK8),
  PARENT_POM_27 ("org.apache.maven.plugins", "maven-shade-plugin", "3.2.4", EJDK.JDK8),
  PARENT_POM_28 ("org.apache.maven.plugins", "maven-site-plugin", "3.9.1", EJDK.JDK8),
  PARENT_POM_29 ("org.apache.maven.plugins", "maven-source-plugin", "3.2.1", EJDK.JDK8),
  PARENT_POM_30 ("org.apache.maven.plugins", "maven-surefire-plugin", "2.22.2", EJDK.JDK8),
  PARENT_POM_31 ("org.apache.maven.plugins", "maven-surefire-report-plugin", "2.22.2", EJDK.JDK8),
  PARENT_POM_32 ("org.apache.maven.plugins", "maven-failsafe-plugin", "2.22.2", EJDK.JDK8),
  PARENT_POM_33 ("org.apache.maven.plugins", "maven-war-plugin", "3.3.1", EJDK.JDK8),
  PARENT_POM_34 ("org.codehaus.mojo", "clirr-maven-plugin", "2.8", EJDK.JDK8),
  PARENT_POM_35 ("org.codehaus.mojo", "cobertura-maven-plugin", "2.7", EJDK.JDK8),
  PARENT_POM_36 ("org.codehaus.mojo", "findbugs-maven-plugin", "3.0.5", EJDK.JDK8),
  PARENT_POM_37 ("com.github.spotbugs", "spotbugs-maven-plugin", "4.2.0", EJDK.JDK8),
  PARENT_POM_38 ("org.codehaus.mojo", "jdepend-maven-plugin", "2.0", EJDK.JDK8),
  PARENT_POM_39 ("org.codehaus.mojo", "taglist-maven-plugin", "2.4", EJDK.JDK8);

  private final String m_sGroupID;
  private final String m_sArticfactID;
  private final String m_sVersion;
  private final Version m_aVersion;
  private final EJDK m_eMinJDK;
  private final boolean m_bIsBOM;
  private final boolean m_bIsLegacy;

  EExternalDependency (@Nonnull @Nonempty final String sGroupID,
                       @Nonnull @Nonempty final String sArticfactID,
                       @Nonnull final EExternalDependency eBase)
  {
    this (sGroupID, sArticfactID, eBase.getLastPublishedVersionString (), eBase.getMinimumJDKVersion ());
  }

  EExternalDependency (@Nonnull @Nonempty final String sGroupID,
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
      case JAVAX_JSP_API:
        return JAKARTA_JSP_API;
      // case JAXWS_MAVEN_PLUGIN_OLD:
      // return JAXWS_MAVEN_PLUGIN;
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
  public static ICommonsList <EExternalDependency> findAll (@Nullable final String sGroupID, @Nullable final String sArtifactID)
  {
    final ICommonsList <EExternalDependency> ret = findAll (x -> x.hasGroupID (sGroupID) && x.hasArtifactID (sArtifactID));
    // Sort by JDK descending
    ret.sort (Comparator.comparingInt ( (final EExternalDependency e) -> e.getMinimumJDKVersion ().getMajor ()).reversed ());
    return ret;
  }

  @Nullable
  public static ICommonsList <EExternalDependency> findAll (@Nonnull final Predicate <EExternalDependency> aFilter)
  {
    return EnumHelper.getAll (EExternalDependency.class, aFilter);
  }
}
