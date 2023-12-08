/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
  API_GUARDIAN ("org.apiguardian", "apiguardian-api", "1.1.2", EJDK.JDK8),

  ANT ("org.apache.ant", "ant", "1.10.14", EJDK.JDK8),
  ANT_APACHE_RESOLVER ("org.apache.ant", "ant-apache-resolver", ANT),
  ANT_TESTUTIL ("org.apache.ant", "ant-testutil", ANT),

  ASM ("org.ow2.asm", "asm", "9.6", EJDK.JDK8),
  ASM_ANALYSIS ("org.ow2.asm", "asm-analysis", ASM),
  ASM_COMMONS ("org.ow2.asm", "asm-commons", ASM),
  ASM_TREE ("org.ow2.asm", "asm-tree", ASM),

  AWS_LAMBDA_CORE ("com.amazonaws", "aws-lambda-java-core", "1.2.3", EJDK.JDK8),

  AWS_S3 ("software.amazon.awssdk", "s3", "2.21.41", EJDK.JDK8),

  BATIK_BRIDGE ("org.apache.xmlgraphics", "batik-bridge", "1.17", EJDK.JDK8),

  BC_MAIL18 ("org.bouncycastle", "bcmail-jdk18on", "1.77", EJDK.JDK8),
  BC_PROV18 ("org.bouncycastle", "bcprov-jdk18on", BC_MAIL18),
  BC_PG18 ("org.bouncycastle", "bcpg-jdk18on", BC_MAIL18),
  BC_JMAIL18 ("org.bouncycastle", "bcjmail-jdk18on", BC_MAIL18),
  BC_PKIX18 ("org.bouncycastle", "bcpkix-jdk18on", BC_MAIL18),
  BC_PROV_EXT18 ("org.bouncycastle", "bcprov-ext-jdk18on", BC_MAIL18),
  BC_TLS18 ("org.bouncycastle", "bctls-jdk18on", BC_MAIL18),

  CLASSLOADER_LEAK_PROTECTION ("se.jiderhamn.classloader-leak-prevention",
                               "classloader-leak-prevention-core",
                               "2.7.0",
                               EJDK.JDK8),
  CODEMODEL ("com.sun.codemodel", "codemodel", "2.6", EJDK.JDK8),
  COMMONS_BEANUTILS ("commons-beanutils", "commons-beanutils", "1.9.4", EJDK.JDK8),
  COMMONS_CODEC ("commons-codec", "commons-codec", "1.16.0", EJDK.JDK8),
  COMMONS_COLLECTIONS_3 ("commons-collections", "commons-collections", "3.2.2", EJDK.JDK8),
  COMMONS_COLLECTIONS_4 ("org.apache.commons", "commons-collections4", "4.4", EJDK.JDK8),
  COMMONS_COMPRESS ("org.apache.commons", "commons-compress", "1.25.0", EJDK.JDK8),
  COMMONS_DBCP2 ("org.apache.commons", "commons-dbcp2", "2.11.0", EJDK.JDK8),
  COMMONS_EXEC ("org.apache.commons", "commons-exec", "1.3", EJDK.JDK8),
  COMMONS_MATH3 ("org.apache.commons", "commons-math3", "3.6.1", EJDK.JDK8),
  COMMONS_NET ("commons-net", "commons-net", "3.10.0", EJDK.JDK8),
  COMMONS_POOL2 ("org.apache.commons", "commons-pool2", "2.12.0", EJDK.JDK8),

  DNSJAVA ("dnsjava", "dnsjava", "3.5.3", EJDK.JDK8),
  DOCLET ("org.umlgraph", "doclet", "5.1", EJDK.JDK8),
  DOM4J ("org.dom4j", "dom4j", "2.1.4", EJDK.JDK8),
  EASYMOCK ("org.easymock", "easymock", "5.2.0", EJDK.JDK8),

  ECLIPSELINK_ASM ("org.eclipse.persistence", "org.eclipse.persistence.asm", "9.6.0", EJDK.JDK11),
  ECLIPSELINK4_CORE ("org.eclipse.persistence", "org.eclipse.persistence.core", "4.0.2", EJDK.JDK11),
  ECLIPSELINK4_JPA ("org.eclipse.persistence", "org.eclipse.persistence.jpa", ECLIPSELINK4_CORE),

  ECLIPSE_JDT_CORE ("org.eclipse.jdt", "org.eclipse.jdt.core", "3.26.0", EJDK.JDK8),
  ECLIPSE_JDT_CORE_11 ("org.eclipse.jdt", "org.eclipse.jdt.core", "3.36.0", EJDK.JDK11),

  ECLIPSE_CORE_CONTENTTYPE ("org.eclipse.platform", "org.eclipse.core.contenttype", "3.7.1000", EJDK.JDK8),
  ECLIPSE_CORE_CONTENTTYPE2 ("org.eclipse.platform", "org.eclipse.core.contenttype", "3.9.200", EJDK.JDK11),
  ECLIPSE_CORE_JOBS ("org.eclipse.platform", "org.eclipse.core.jobs", "3.10.1100", EJDK.JDK8),
  ECLIPSE_CORE_JOBS2 ("org.eclipse.platform", "org.eclipse.core.jobs", "3.15.100", EJDK.JDK11),
  ECLIPSE_CORE_RESOURCES ("org.eclipse.platform", "org.eclipse.core.resources", "3.13.900", EJDK.JDK8),
  ECLIPSE_CORE_RESOURCES2 ("org.eclipse.platform", "org.eclipse.core.resources", "3.20.0", EJDK.JDK11),
  ECLIPSE_CORE_RUNTIME ("org.eclipse.platform", "org.eclipse.core.runtime", "3.13.0", EJDK.JDK8),
  ECLIPSE_CORE_RUNTIME2 ("org.eclipse.platform", "org.eclipse.core.runtime", "3.30.0", EJDK.JDK11),
  ECLIPSE_EQUINOX_COMMON ("org.eclipse.platform", "org.eclipse.equinox.common", "3.8.0", EJDK.JDK8),
  ECLIPSE_EQUINOX_COMMON2 ("org.eclipse.platform", "org.eclipse.equinox.common", "3.18.200", EJDK.JDK11),

  EXPIRING_MAP ("net.jodah", "expiringmap", "0.5.11", EJDK.JDK8),
  FELIX ("org.apache.felix", "org.apache.felix.framework", "7.0.5", EJDK.JDK8),
  FINDBUGS_ANNOTATIONS_3 ("com.google.code.findbugs", "annotations", "3.0.1u2", EJDK.JDK8),

  @VersionMaxExcl ("4.0.0")
  FLAPDOODLE_3("de.flapdoodle.embed", "de.flapdoodle.embed.mongo", "3.5.4", EJDK.JDK8),
  FLAPDOODLE_4 ("de.flapdoodle.embed", "de.flapdoodle.embed.mongo", "4.11.1", EJDK.JDK8),

  FLYWAY10 ("org.flywaydb", "flyway-core", "10.2.0", EJDK.JDK8),
  FLYWAY_MYSQL10 ("org.flywaydb", "flyway-mysql", FLYWAY10),

  FOP ("org.apache.xmlgraphics", "fop", "2.9", EJDK.JDK8),
  FOP_HYPH ("net.sf.offo", "fop-hyph", "2.0", EJDK.JDK8),
  FORBIDDEN_APIS ("de.thetaphi", "forbiddenapis", "3.6", EJDK.JDK8),
  GMAVEN_PLUS ("org.codehaus.gmavenplus", "gmavenplus-plugin", "3.0.2", EJDK.JDK8),

  GOOGLE_CLOSURE_11 ("com.google.javascript", "closure-compiler", "v20231112", EJDK.JDK11),
  GOOGLE_PROTOBUF ("com.google.protobuf", "protobuf-java", "3.25.1", EJDK.JDK8),

  H2 ("com.h2database", "h2", "2.2.224", EJDK.JDK8),

  HAMCREST_LIBRARY ("org.hamcrest", "hamcrest-library", "2.2", EJDK.JDK8),

  HAZELCAST ("com.hazelcast", "hazelcast", "5.3.6", EJDK.JDK8),

  HTTP_CORE5 ("org.apache.httpcomponents.core5", "httpcore5", "5.2.4", EJDK.JDK8),
  HTTP_CLIENT5 ("org.apache.httpcomponents.client5", "httpclient5", "5.3", EJDK.JDK8),

  IBM_JCC ("com.ibm.db2", "jcc", "11.5.9.0", EJDK.JDK8),

  @IsBOM
  JACKSON_BOM ("com.fasterxml.jackson", "jackson-bom", "2.16.0", EJDK.JDK8),
  JACKSON_CORE ("com.fasterxml.jackson.core", "jackson-core", JACKSON_BOM),
  JACKSON_ANNOTATIONS ("com.fasterxml.jackson.core", "jackson-annotations", JACKSON_BOM),
  JACKSON_DATABIND ("com.fasterxml.jackson.core", "jackson-databind", JACKSON_BOM),
  JACKSON_MODULE_AFTERBURNER ("com.fasterxml.jackson.module", "jackson-module-afterburner", JACKSON_BOM),
  JACKSON_DATAFORMAT_CBOR ("com.fasterxml.jackson.dataformat", "jackson-dataformat-cbor", JACKSON_BOM),

  JACOCO ("org.jacoco", "jacoco-maven-plugin", "0.8.11", EJDK.JDK8),

  @IsLegacy (replacedWith = "org.eclipse.angus:angus-activation")
  JAKARTA_ACTIVATION_2("com.sun.activation", "jakarta.activation", "2.0.1", EJDK.JDK11),

  ANGUAS_ACTIVATION ("org.eclipse.angus", "angus-activation", "2.0.1", EJDK.JDK11),

  @VersionMaxExcl ("2.0.0")
  JAKARTA_ANNOTATION_API("jakarta.annotation", "jakarta.annotation-api", "1.3.5", EJDK.JDK8),
  JAKARTA_ANNOTATION_API_2 ("jakarta.annotation", "jakarta.annotation-api", "2.1.1", EJDK.JDK11),

  // @VersionMaxExcl ("2.0.0")
  // JAKARTA_MAIL("com.sun.mail", "jakarta.mail", "1.6.7", EJDK.JDK8),

  @IsLegacy (replacedWith = "org.eclipse.angus:angus-mail")
  JAKARTA_MAIL_2("com.sun.mail", "jakarta.mail", "2.0.1", EJDK.JDK11),

  ANGUAS_MAIL ("org.eclipse.angus", "angus-mail", "2.0.2", EJDK.JDK11),

  @Deprecated
  JAKARTA_PERSISTENCE_2 ("org.eclipse.persistence", "jakarta.persistence", "2.2.3", EJDK.JDK8),
  JAKARTA_PERSISTENCE_3 ("jakarta.persistence", "jakarta.persistence-api", "3.1.0", EJDK.JDK11),

  @VersionMaxExcl ("6.0.0")
  JAKARTA_SERVLET_API_5("jakarta.servlet", "jakarta.servlet-api", "5.0.0", EJDK.JDK11),

  JAKARTA_SERVLET_API_6 ("jakarta.servlet", "jakarta.servlet-api", "6.0.0", EJDK.JDK17),

  JAKARTA_JSP_API_2 ("jakarta.servlet.jsp", "jakarta.servlet.jsp-api", "2.3.6", EJDK.JDK8),
  // JakartaEE 9
  JAKARTA_JSP_API_30 ("jakarta.servlet.jsp", "jakarta.servlet.jsp-api", "3.0.0", EJDK.JDK11),
  // JakartaEE 10
  // JAKARTA_JSP_API_31 ("jakarta.servlet.jsp", "jakarta.servlet.jsp-api",
  // "3.1.1", EJDK.JDK11),

  JAVA_PARSER_CORE ("com.github.javaparser", "javaparser-core", "3.25.7", EJDK.JDK8),

  JAVACC ("net.java.dev.javacc", "javacc", "7.0.13", EJDK.JDK8),
  JAVAX_EL ("org.glassfish", "javax.el", "3.0.0", EJDK.JDK8),
  // @IsLegacy (replacedWith = "4.x")
  @VersionMaxExcl ("4.0.0")
  JAVAX_SERVLET_API_3("javax.servlet", "javax.servlet-api", "3.1.0", EJDK.JDK8),
  // @IsLegacy (replacedWith = "jakarta.servlet-api")
  JAVAX_SERVLET_API_4 ("javax.servlet", "javax.servlet-api", "4.0.1", EJDK.JDK11),

  @IsLegacy (replacedWith = "com.evolvedbinary.maven.jvnet:jaxb2-maven-plugin")
  JAXB2_PLUGIN_HIGHSOURCE("org.jvnet.jaxb2.maven2", "maven-jaxb2-plugin", "0.14.0", EJDK.JDK8),
  JAXB2_PLUGIN_EVOLVEDBINARY ("com.evolvedbinary.maven.jvnet", "jaxb2-maven-plugin", "0.15.0", EJDK.JDK8),
  JAXB2_BASICS ("org.jvnet.jaxb2_commons", "jaxb2-basics", "0.13.1", EJDK.JDK8),

  JAXB4_API ("jakarta.xml.bind", "jakarta.xml.bind-api", "4.0.1", EJDK.JDK11),
  @IsBOM
  JAXB4_BOM ("com.sun.xml.bind", "jaxb-bom-ext", "4.0.4", EJDK.JDK11),
  JAXB4_CODEMODEL ("com.sun.xml.bind", "jaxb-impl", JAXB4_BOM),

  JAXWS4_API ("jakarta.xml.ws", "jakarta.xml.ws-api", "4.0.1", EJDK.JDK11),
  @IsBOM
  JAXWS4_RI_BOM ("com.sun.xml.ws", "jaxws-ri-bom", "4.0.2", EJDK.JDK11),
  JAXWS4_RT ("com.sun.xml.ws", "jaxws-rt", JAXWS4_RI_BOM),
  JAXWS4_MAVEN_PLUGIN ("com.sun.xml.ws", "jaxws-maven-plugin", JAXWS4_RI_BOM),

  JBIG2_APACHE ("org.apache.pdfbox", "jbig2-imageio", "3.0.4", EJDK.JDK8),
  JEDIS ("redis.clients", "jedis", "5.1.0", EJDK.JDK8),
  JEROMQ ("org.zeromq", "jeromq", "0.5.4", EJDK.JDK8),

  // JDK 1.7 since 2.7
  // JDK 1.8 since 2.26
  @IsBOM
  @VersionMaxExcl ("3.0.0")
  JERSEY2_BOM("org.glassfish.jersey", "jersey-bom", "2.39.1", EJDK.JDK8),
  JERSEY2_SERVER ("org.glassfish.jersey.core", "jersey-server", JERSEY2_BOM),
  JERSEY2_COMMON ("org.glassfish.jersey.core", "jersey-common", JERSEY2_BOM),
  JERSEY2_CLIENT ("org.glassfish.jersey.core", "jersey-client", JERSEY2_BOM),
  JERSEY2_HK2 ("org.glassfish.jersey.inject", "jersey-hk2", JERSEY2_BOM),
  JERSEY2_SERVLET ("org.glassfish.jersey.containers", "jersey-container-servlet", JERSEY2_BOM),

  @IsBOM
  JERSEY3_BOM ("org.glassfish.jersey", "jersey-bom", "3.1.4", EJDK.JDK11),
  JERSEY3_SERVER ("org.glassfish.jersey.core", "jersey-server", JERSEY3_BOM),
  JERSEY3_COMMON ("org.glassfish.jersey.core", "jersey-common", JERSEY3_BOM),
  JERSEY3_CLIENT ("org.glassfish.jersey.core", "jersey-client", JERSEY3_BOM),
  JERSEY3_HK2 ("org.glassfish.jersey.inject", "jersey-hk2", JERSEY3_BOM),
  JERSEY3_SERVLET ("org.glassfish.jersey.containers", "jersey-container-servlet", JERSEY3_BOM),

  @IsBOM
  @VersionMaxExcl ("12.0.0")
  JETTY11_BOM("org.eclipse.jetty", "jetty-bom", "11.0.18", EJDK.JDK11),
  JETTY11_WEBAPP ("org.eclipse.jetty", "jetty-webapp", JETTY11_BOM),
  JETTY11_ANNOTATIONS ("org.eclipse.jetty", "jetty-annotations", JETTY11_BOM),
  JETTY11_PLUS ("org.eclipse.jetty", "jetty-plus", JETTY11_BOM),
  JETTY11_RUNNER ("org.eclipse.jetty", "jetty-runner", JETTY11_BOM),
  JETTY11_APACHE_JSP ("org.eclipse.jetty", "apache-jsp", JETTY11_BOM),
  JETTY11_SERVLET ("org.eclipse.jetty", "jetty-servlet", JETTY11_BOM),
  JETTY11_SERVER ("org.eclipse.jetty", "jetty-server", JETTY11_BOM),

  @IsBOM
  JETTY12_BOM ("org.eclipse.jetty", "jetty-bom", "12.0.4", EJDK.JDK17),
  JETTY12_SERVER ("org.eclipse.jetty", "jetty-server", JETTY12_BOM),

  JING ("org.relaxng", "jing", "20220510", EJDK.JDK8),
  TRANG ("org.relaxng", "trang", "20220510", EJDK.JDK8),

  JJWT_IMPL ("io.jsonwebtoken", "jjwt-impl", "0.12.3", EJDK.JDK8),
  JJWT_ORG_JSON ("io.jsonwebtoken", "jjwt-orgjson", JJWT_IMPL),

  @IsLegacy
  JDK ("JDK", "runtime", "1.8", EJDK.JDK8),
  JSCH ("com.jcraft", "jsch", "0.1.55", EJDK.JDK8),
  @IsLegacy (replacedWith = "jakarta.servlet.jsp-api")
  JAVAX_JSP_API("javax.servlet.jsp", "javax.servlet.jsp-api", "2.3.3", EJDK.JDK8),
  JSR305 ("com.google.code.findbugs", "jsr305", "3.0.2", EJDK.JDK8),
  JTB ("edu.ucla.cs.compilers", "jtb", "1.3.2", EJDK.JDK8),
  JUNIT ("junit", "junit", "4.13.2", EJDK.JDK8),

  JUNIT5_JUPITER ("org.junit.jupiter", "junit-jupiter", "5.10.1", EJDK.JDK8),
  JUNIT5_JUPITER_API ("org.junit.jupiter", "junit-jupiter-api", JUNIT5_JUPITER),
  JUNIT5_JUPITER_ENGINE ("org.junit.jupiter", "junit-jupiter-engine", JUNIT5_JUPITER),
  JUNIT5_VINTAGE_ENGINE ("org.junit.vintage", "junit-vintage-engine", JUNIT5_JUPITER),
  JUNIT5_PLATFORM_LAUNCHER ("org.junit.platform", "junit-platform-launcher", "1.10.1", EJDK.JDK8),
  JUNIT5_PLATFORM_SUREFIRE_PROVIDER ("org.junit.platform", "junit-platform-surefire-provider", "1.3.2", EJDK.JDK8),

  KAFKA_CLIENT ("org.apache.kafka", "kafka-clients", "3.6.1", EJDK.JDK8),

  LITTLEPROXY ("org.littleshoot", "littleproxy", "1.1.2", EJDK.JDK8),

  LOG4J2_CORE ("org.apache.logging.log4j", "log4j-core", "2.22.0", EJDK.JDK8),
  LOG4J2_API ("org.apache.logging.log4j", "log4j-api", LOG4J2_CORE),
  LOG4J2_SLF4J2_IMPL ("org.apache.logging.log4j", "log4j-slf4j2-impl", LOG4J2_CORE),
  LOG4J2_TO_SLF4J ("org.apache.logging.log4j", "log4j-to-slf4j", LOG4J2_CORE),
  LOG4J2_WEB ("org.apache.logging.log4j", "log4j-web", LOG4J2_CORE),

  LUCENE9_CORE ("org.apache.lucene", "lucene-core", "9.9.0", EJDK.JDK8),
  LUCENE9_BACKWARD_CODECS ("org.apache.lucene", "lucene-backward-codecs", LUCENE9_CORE),
  LUCENE9_DEMO ("org.apache.lucene", "lucene-demo", LUCENE9_CORE),
  LUCENE9_GROUPING ("org.apache.lucene", "lucene-grouping", LUCENE9_CORE),
  LUCENE9_QUERYPARSER ("org.apache.lucene", "lucene-queryparser", LUCENE9_CORE),

  MAVEN_PLUGIN_PLUGIN ("org.apache.maven.plugins", "maven-plugin-plugin", "3.10.2", EJDK.JDK8),
  @IsLegacy
  M2E ("org.eclipse.m2e", "lifecycle-mapping", "1.0.0", EJDK.JDK8),
  METRO3 ("org.glassfish.metro", "webservices-rt", "3.0.3", EJDK.JDK8),
  METRO4 ("org.glassfish.metro", "webservices-rt", "4.0.3", EJDK.JDK11),

  MONGO_DRIVER_REACTIVESTREAMS ("org.mongodb", "mongodb-driver-reactivestreams", "4.11.1", EJDK.JDK8),
  MONGO_DRIVER_SYNC ("org.mongodb", "mongodb-driver-sync", MONGO_DRIVER_REACTIVESTREAMS),

  MYSQL ("com.mysql", "mysql-connector-j", "8.2.0", EJDK.JDK8),

  @IsBOM
  ORACLE_JDBC ("com.oracle.database.jdbc", "ojdbc-bom", "23.3.0.23.09", EJDK.JDK8),

  OWASP_DEPENDENCY_CHECK ("org.owasp", "dependency-check-maven", "9.0.4", EJDK.JDK8),

  PDFBOX ("org.apache.pdfbox", "pdfbox", "3.0.1", EJDK.JDK8),
  PDFBOX_APP ("org.apache.pdfbox", "pdfbox-app", PDFBOX),
  PDFBOX_EXAMPLES ("org.apache.pdfbox", "pdfbox-examples", PDFBOX),
  PDFBOX_XMPBOX ("org.apache.pdfbox", "xmpbox", PDFBOX),

  PICOCLI ("info.picocli", "picocli", "4.7.5", EJDK.JDK8),

  POI ("org.apache.poi", "poi", "5.2.5", EJDK.JDK8),
  POI_OOXML ("org.apache.poi", "poi-ooxml", POI),
  POI_SCRATCHPAD ("org.apache.poi", "poi-scratchpad", POI),

  POSTGRESQL ("org.postgresql", "postgresql", "42.7.1", EJDK.JDK8),

  QUARTZ ("org.quartz-scheduler", "quartz", "2.3.2", EJDK.JDK8),
  RATELIMITJ_INMEMORY ("es.moki.ratelimitj", "ratelimitj-inmemory", "0.7.0", EJDK.JDK8),

  @VersionMaxExcl ("12.0.0")
  SAXON_11("net.sf.saxon", "Saxon-HE", "11.6", EJDK.JDK8),
  // Currently Saxon 11 is the stable one
  SAXON_12 ("net.sf.saxon", "Saxon-HE", "12.4", EJDK.JDK17),

  SCHXSLT ("name.dmaus.schxslt", "schxslt", "1.9.5", EJDK.JDK8),
  SELENIUM ("org.seleniumhq.selenium", "selenium-java", "4.16.1", EJDK.JDK8),
  SIMPLE_ODF ("org.odftoolkit", "simple-odf", "0.9.0", EJDK.JDK8),

  SLF4J_API ("org.slf4j", "slf4j-api", "2.0.9", EJDK.JDK8),
  SLF4J_SIMPLE ("org.slf4j", "slf4j-simple", SLF4J_API),
  JUL_TO_SLF4J ("org.slf4j", "jul-to-slf4j", SLF4J_API),
  JCL_OVER_SLF4J ("org.slf4j", "jcl-over-slf4j", SLF4J_API),
  LOG4J2_OVER_SLF4J ("org.slf4j", "log4j-over-slf4j", SLF4J_API),
  SLF4J_JDK_PLATFORM_LOGGING ("org.slf4j", "slf4j-jdk-platform-logging", SLF4J_API),

  SPOTBUGS_ANNOTATIONS ("com.github.spotbugs", "spotbugs-annotations", "4.8.2", EJDK.JDK8),

  @IsBOM
  SPRING6_FRAMEWORK_BOM ("org.springframework", "spring-framework-bom", "6.1.1", EJDK.JDK11),

  @IsBOM
  SPRING_BOOT3_DEPENDENCIES ("org.springframework.boot", "spring-boot-dependencies", "3.2.0", EJDK.JDK11),
  SPRING_BOOT3_STARTER_WEB ("org.springframework.boot", "spring-boot-starter-web", SPRING_BOOT3_DEPENDENCIES),
  SPRING_BOOT3_STARTER_TEST ("org.springframework.boot", "spring-boot-starter-test", SPRING_BOOT3_DEPENDENCIES),

  STAX_EX ("org.jvnet.staxex", "stax-ex", "2.1.0", EJDK.JDK8),

  THREE_TEN_EXTRA ("org.threeten", "threeten-extra", "1.7.2", EJDK.JDK8),
  TYPESAFE_CONFIG ("com.typesafe", "config", "1.4.3", EJDK.JDK8),

  @IsLegacy (replacedWith = "jakarta.validation:jakarta.validation-api")
  JAVAX_VALIDATION_API("javax.validation", "validation-api", "2.0.1.Final", EJDK.JDK8),
  JAKARTA_VALIDATION_API ("jakarta.validation", "jakarta.validation-api", "3.0.2", EJDK.JDK8),

  VERSIONS_MAVEN_PLUGIN ("org.codehaus.mojo", "versions-maven-plugin", "2.16.2", EJDK.JDK8),

  WSS4J_3 ("org.apache.wss4j", "wss4j-ws-security-dom", "3.0.2", EJDK.JDK11),

  XMLBEANS ("org.apache.xmlbeans", "xmlbeans", "5.2.0", EJDK.JDK8),

  @VersionMaxExcl ("4.0.0")
  XMLSEC_3("org.apache.santuario", "xmlsec", "3.0.3", EJDK.JDK11),
  // Java 11
  XMLSEC_4 ("org.apache.santuario", "xmlsec", "4.0.1", EJDK.JDK11),

  XSOM ("com.sun.xsom", "xsom", "20140925", EJDK.JDK8),
  ZXING_CORE ("com.google.zxing", "core", "3.5.2", EJDK.JDK8),
  ZXING_JAVASE ("com.google.zxing", "javase", ZXING_CORE),

  // parent POM dependencies
  PARENT_POM_0 ("com.mycila", "license-maven-plugin", "4.3", EJDK.JDK8),
  PARENT_POM_1 ("org.apache.felix", "maven-bundle-plugin", "5.1.9", EJDK.JDK8),
  PARENT_POM_2 ("org.apache.maven.plugins", "maven-acr-plugin", "3.1.0", EJDK.JDK8),
  PARENT_POM_3 ("org.apache.maven.plugins", "maven-antrun-plugin", "3.1.0", EJDK.JDK8),
  PARENT_POM_4 ("org.apache.maven.plugins", "maven-assembly-plugin", "3.6.0", EJDK.JDK8),
  PARENT_POM_5 ("org.apache.maven.plugins", "maven-changes-plugin", "2.12.1", EJDK.JDK8),
  PARENT_POM_6 ("org.apache.maven.plugins", "maven-checkstyle-plugin", "3.3.1", EJDK.JDK8),
  PARENT_POM_7 ("org.apache.maven.plugins", "maven-clean-plugin", "3.3.2", EJDK.JDK8),
  PARENT_POM_8 ("org.apache.maven.plugins", "maven-compiler-plugin", "3.11.0", EJDK.JDK8),
  PARENT_POM_9 ("org.apache.maven.plugins", "maven-dependency-plugin", "3.6.1", EJDK.JDK8),
  PARENT_POM_10 ("org.apache.maven.plugins", "maven-deploy-plugin", "3.1.1", EJDK.JDK8),
  PARENT_POM_11 ("org.apache.maven.plugins", "maven-ear-plugin", "3.3.0", EJDK.JDK8),
  PARENT_POM_12 ("org.apache.maven.plugins", "maven-ejb-plugin", "3.2.1", EJDK.JDK8),
  PARENT_POM_13 ("org.apache.maven.plugins", "maven-enforcer-plugin", "3.4.1", EJDK.JDK8),
  PARENT_POM_14 ("org.apache.maven.plugins", "maven-gpg-plugin", "3.1.0", EJDK.JDK8),
  PARENT_POM_15 ("org.apache.maven.plugins", "maven-idea-plugin", "2.2.1", EJDK.JDK8),
  PARENT_POM_16 ("org.apache.maven.plugins", "maven-install-plugin", "3.1.1", EJDK.JDK8),
  PARENT_POM_17 ("org.apache.maven.plugins", "maven-jar-plugin", "3.3.0", EJDK.JDK8),
  PARENT_POM_18 ("org.apache.maven.plugins", "maven-jarsigner-plugin", "3.0.0", EJDK.JDK8),
  PARENT_POM_19 ("org.apache.maven.plugins", "maven-javadoc-plugin", "3.6.3", EJDK.JDK8),
  PARENT_POM_20 ("org.apache.maven.plugins", "maven-jdeps-plugin", "3.1.2", EJDK.JDK8),
  PARENT_POM_21 ("org.apache.maven.plugins", "maven-jxr-plugin", "3.3.1", EJDK.JDK8),
  PARENT_POM_22 ("org.apache.maven.plugins", "maven-pmd-plugin", "3.21.2", EJDK.JDK8),
  PARENT_POM_23 ("org.apache.maven.plugins", "maven-project-info-reports-plugin", "3.5.0", EJDK.JDK8),
  PARENT_POM_24 ("org.apache.maven.plugins", "maven-rar-plugin", "3.0.0", EJDK.JDK8),
  PARENT_POM_25 ("org.apache.maven.plugins", "maven-release-plugin", "3.0.1", EJDK.JDK8),
  PARENT_POM_26 ("org.apache.maven.plugins", "maven-resources-plugin", "3.3.1", EJDK.JDK8),
  PARENT_POM_27 ("org.apache.maven.plugins", "maven-shade-plugin", "3.5.1", EJDK.JDK8),
  PARENT_POM_28 ("org.apache.maven.plugins", "maven-site-plugin", "3.12.1", EJDK.JDK8),
  PARENT_POM_29 ("org.apache.maven.plugins", "maven-source-plugin", "3.3.0", EJDK.JDK8),
  PARENT_POM_30 ("org.apache.maven.plugins", "maven-surefire-plugin", "3.2.2", EJDK.JDK8),
  PARENT_POM_31 ("org.apache.maven.plugins", "maven-surefire-report-plugin", "3.2.2", EJDK.JDK8),
  PARENT_POM_32 ("org.apache.maven.plugins", "maven-failsafe-plugin", "3.2.2", EJDK.JDK8),
  PARENT_POM_33 ("org.apache.maven.plugins", "maven-war-plugin", "3.4.0", EJDK.JDK8),
  PARENT_POM_34 ("org.codehaus.mojo", "clirr-maven-plugin", "2.8", EJDK.JDK8),
  PARENT_POM_36 ("org.codehaus.mojo", "findbugs-maven-plugin", "3.0.5", EJDK.JDK8),
  PARENT_POM_37 ("com.github.spotbugs", "spotbugs-maven-plugin", "4.8.2.0", EJDK.JDK8),
  PARENT_POM_38 ("org.codehaus.mojo", "jdepend-maven-plugin", "2.0", EJDK.JDK8),
  PARENT_POM_39 ("org.codehaus.mojo", "taglist-maven-plugin", "3.0.0", EJDK.JDK8);

  private final String m_sGroupID;
  private final String m_sArticfactID;
  private final String m_sVersion;
  private final Version m_aVersion;
  private final EJDK m_eMinJDK;
  private final boolean m_bIsBOM;
  private final boolean m_bIsLegacy;
  private final VersionMaxExcl m_aVersionMax;

  EExternalDependency (@Nonnull @Nonempty final String sGroupID,
                       @Nonnull @Nonempty final String sArticfactID,
                       @Nonnull final EExternalDependency eBase)
  {
    this (sGroupID,
          sArticfactID,
          eBase.getLastPublishedVersionString (),
          eBase.getMinimumJDKVersion (),
          eBase.getVersionMaxExcl ());
  }

  EExternalDependency (@Nonnull @Nonempty final String sGroupID,
                       @Nonnull @Nonempty final String sArticfactID,
                       @Nonnull @Nonempty final String sVersion,
                       @Nonnull final EJDK eMinJDK)
  {
    this (sGroupID, sArticfactID, sVersion, eMinJDK, null);
  }

  @Nullable
  private static <T> T _nonNull (@Nullable final T a, @Nullable final T b)
  {
    return a != null ? a : b;
  }

  EExternalDependency (@Nonnull @Nonempty final String sGroupID,
                       @Nonnull @Nonempty final String sArticfactID,
                       @Nonnull @Nonempty final String sVersion,
                       @Nonnull final EJDK eMinJDK,
                       @Nullable final VersionMaxExcl aVersionMaxParent)
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
      m_aVersionMax = _nonNull (aField.getAnnotation (VersionMaxExcl.class), aVersionMaxParent);
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

  @Nullable
  public VersionMaxExcl getVersionMaxExcl ()
  {
    return m_aVersionMax;
  }

  @Nullable
  public String getMaxVersionString ()
  {
    return m_aVersionMax == null ? null : m_aVersionMax.value ();
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
        return JAKARTA_JSP_API_2;
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
