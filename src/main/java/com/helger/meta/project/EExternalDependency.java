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

import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.version.Version;

public enum EExternalDependency
{
  ASM_TREE ("org.ow2.asm", "asm-tree", "5.1"),
  BC_MAIL ("org.bouncycastle", "bcmail-jdk15on", "1.54"),
  BC_PROV ("org.bouncycastle", "bcprov-jdk15on", BC_MAIL),
  BC_PKIX ("org.bouncycastle", "bcpkix-jdk15on", BC_MAIL),
  CLOSURE ("com.google.javascript", "closure-compiler", "v20160315"),
  COMMONS_DBCP2 ("org.apache.commons", "commons-dbcp2", "2.1.1", EJDK.JDK7),
  COMMONS_NET ("commons-net", "commons-net", "3.4"),
  COMMONS_POOL2 ("org.apache.commons", "commons-pool2", "2.4.2", EJDK.JDK7),
  DNSJAVA ("dnsjava", "dnsjava", "2.1.7"),
  DOCLET ("org.umlgraph", "doclet", "5.1"),
  DOM4J ("dom4j", "dom4j", "1.6.1"),
  ECLIPSELINK_CORE ("org.eclipse.persistence", "org.eclipse.persistence.core", "2.6.2", EJDK.JDK7),
  ECLIPSELINK_JPA ("org.eclipse.persistence", "org.eclipse.persistence.jpa", ECLIPSELINK_CORE),
  ECLIPSELINK_ANTLR ("org.eclipse.persistence", "org.eclipse.persistence.antlr", ECLIPSELINK_CORE),
  ECLIPSELINK_ASM ("org.eclipse.persistence", "org.eclipse.persistence.asm", ECLIPSELINK_CORE),
  FELIX ("org.apache.felix", "org.apache.felix.framework", "5.4.0"),
  FINDBUGS_ANNOTATIONS_2 ("com.google.code.findbugs", "annotations", "2.0.3", EJDK.JDK6),
  FINDBUGS_ANNOTATIONS_3 ("com.google.code.findbugs", "annotations", "3.0.1u2", EJDK.JDK7),
  FLUENT_HC ("org.apache.httpcomponents", "fluent-hc", "4.5.2"),
  FORBIDDEN_APIS ("de.thetaphi", "forbiddenapis", "2.0"),
  H2 ("com.h2database", "h2", "1.4.191"),
  HAZELCAST ("com.hazelcast", "hazelcast", "3.6.2"),
  HTTP_CORE ("org.apache.httpcomponents", "httpcore", "4.4.4"),
  HTTP_CLIENT ("org.apache.httpcomponents", "httpclient", "4.5.2"),
  JACKSON_CORE ("com.fasterxml.jackson.core", "jackson-core", "2.7.4"),
  JACKSON_DATABIND ("com.fasterxml.jackson.core", "jackson-databind", JACKSON_CORE),
  JACOCO ("org.jacoco", "jacoco-maven-plugin", "0.7.6.201602180812"),
  JAVA_PARSER ("com.github.javaparser", "javaparser-core", "2.4.0"),
  JAVAX_EL ("org.glassfish", "javax.el", "3.0.0"),
  JAVAX_MAIL ("com.sun.mail", "javax.mail", "1.5.5"),
  JAVAX_PERSISTENCE ("org.eclipse.persistence", "javax.persistence", "2.1.1", EJDK.JDK7),
  JAXB_BOM ("org.glassfish.jaxb", "jaxb-bom", "2.2.11"),
  JAXB_CODEMODEL ("org.glassfish.jaxb", "codemodel", JAXB_BOM),
  JAXB_TXW2 ("org.glassfish.jaxb", "txw2", JAXB_BOM),
  JAXB_IMPL_SUN ("com.sun.xml.bind", "jaxb-impl", "2.2.11"),
  JAXB_JXC_SUN ("com.sun.xml.bind", "jaxb-jxc", JAXB_IMPL_SUN),
  JAXB_XJC_SUN ("com.sun.xml.bind", "jaxb-xjc", JAXB_IMPL_SUN),
  JAXB2_PLUGIN ("org.jvnet.jaxb2.maven2", "maven-jaxb2-plugin", "0.13.1"),
  JAXB2_BASICS ("org.jvnet.jaxb2_commons", "jaxb2-basics", "0.11.0"),
  JAXWS_RI_COM ("com.sun.xml.ws", "jaxws-ri-bom", "2.2.10"),
  JAXWS_RT ("com.sun.xml.ws", "jaxws-rt", JAXWS_RI_COM),
  JAXWS_TOOLS ("com.sun.xml.ws", "jaxws-tools", JAXWS_RI_COM),
  JAXWS_MAVEN_PLUGIN_OLD ("org.jvnet.jax-ws-commons", "jaxws-maven-plugin", "2.3.1-b20150201.1248"),
  JAXWS_MAVEN_PLUGIN ("org.codehaus.mojo", "jaxws-maven-plugin", "2.4.1"),
  JERSEY1_SERVLET ("com.sun.jersey", "jersey-servlet", "1.19.1"),
  JERSEY1_CLIENT ("com.sun.jersey", "jersey-client", JERSEY1_SERVLET),
  // JDK 1.7 since 2.7
  JERSEY2_BOM ("org.glassfish.jersey", "jersey-bom", "2.22.2", EJDK.JDK7),
  JERSEY2_SERVER ("org.glassfish.jersey.core", "jersey-server", JERSEY2_BOM),
  JERSEY2_COMMON ("org.glassfish.jersey.core", "jersey-common", JERSEY2_BOM),
  JERSEY2_CLIENT ("org.glassfish.jersey.core", "jersey-client", JERSEY2_BOM),
  JETTY_92_WEBAPP ("org.eclipse.jetty", "jetty-webapp", "9.2.16.v20160414", EJDK.JDK7),
  JETTY_92_ANNOTATIONS ("org.eclipse.jetty", "jetty-annotations", JETTY_92_WEBAPP),
  JETTY_92_JSP ("org.eclipse.jetty", "jetty-jsp", JETTY_92_WEBAPP),
  JETTY_93_WEBAPP ("org.eclipse.jetty", "jetty-webapp", "9.3.8.v20160314", EJDK.JDK8),
  JETTY_93_ANNOTATIONS ("org.eclipse.jetty", "jetty-annotations", JETTY_93_WEBAPP),
  JETTY_93_PLUS ("org.eclipse.jetty", "jetty-plus", JETTY_93_WEBAPP),
  JETTY_93_APACHE_JSP ("org.eclipse.jetty", "apache-jsp", JETTY_93_WEBAPP),
  JODA_TIME ("joda-time", "joda-time", "2.9.3"),
  JDK ("JDK", "runtime", "1.8", EJDK.JDK8),
  JSCH ("com.jcraft", "jsch", "0.1.53"),
  JSP_API ("javax.servlet.jsp", "jsp-api", "2.2"),
  JUNIT ("junit", "junit", "4.12"),
  LOG4J2_23_CORE ("org.apache.logging.log4j", "log4j-core", "2.3", EJDK.JDK6),
  LOG4J2_23_SLF4J ("org.apache.logging.log4j", "log4j-slf4j-impl", LOG4J2_23_CORE),
  LOG4J2_23_WEB ("org.apache.logging.log4j", "log4j-web", LOG4J2_23_CORE),
  LOG4J2_24_CORE ("org.apache.logging.log4j", "log4j-core", "2.5", EJDK.JDK7),
  LOG4J2_24_SLF4J ("org.apache.logging.log4j", "log4j-slf4j-impl", LOG4J2_24_CORE),
  LOG4J2_24_WEB ("org.apache.logging.log4j", "log4j-web", LOG4J2_24_CORE),
  LUCENE_CORE ("org.apache.lucene", "lucene-core", "6.0.0", EJDK.JDK7),
  LUCENE_ANALYZER_COMMON ("org.apache.lucene", "lucene-analyzers-common", LUCENE_CORE),
  LUCENE_QUERYPARSER ("org.apache.lucene", "lucene-queryparser", LUCENE_CORE),
  LUCENE_GROUPING ("org.apache.lucene", "lucene-grouping", LUCENE_CORE),
  M2E ("org.eclipse.m2e", "lifecycle-mapping", "1.0.0"),
  METRO ("org.glassfish.metro", "webservices-rt", "2.2.1-1"),
  MYSQL ("mysql", "mysql-connector-java", "5.1.38"),
  PDFBOX ("org.apache.pdfbox", "pdfbox", "2.0.1"),
  PDFBOX_EXAMPLES ("org.apache.pdfbox", "pdfbox-examples", PDFBOX),
  POI ("org.apache.poi", "poi", "3.14"),
  POI_OOXML ("org.apache.poi", "poi-ooxml", POI),
  QUARTZ ("org.quartz-scheduler", "quartz", "2.2.3"),
  RHINO ("org.mozilla", "rhino", "1.7.7.1"),
  SAXON ("net.sf.saxon", "Saxon-HE", "9.7.0-4"),
  SERVLET_API_301 ("javax.servlet", "javax.servlet-api", "3.0.1", EJDK.JDK6),
  SERVLET_API_310 ("javax.servlet", "javax.servlet-api", "3.1.0", EJDK.JDK7),
  SIMPLE_ODF ("org.apache.odftoolkit", "simple-odf", "0.8.1-incubating"),
  SLF4J_API ("org.slf4j", "slf4j-api", "1.7.21"),
  SLF4J_SIMPLE ("org.slf4j", "slf4j-simple", SLF4J_API),
  SLF4J_LOG4J12 ("org.slf4j", "slf4j-log4j12", SLF4J_API),
  JUL_TO_SLF4J ("org.slf4j", "jul-to-slf4j", SLF4J_API),
  JCL_OVER_SLF4J ("org.slf4j", "jcl-over-slf4j", SLF4J_API),
  THREE_TEN_EXTRA ("org.threeten", "threeten-extra", "0.9", EJDK.JDK8),
  UNDERTOW ("io.undertow", "undertow-servlet", "1.3.21.Final"),
  VALIDATION_API ("javax.validation", "validation-api", "1.1.0.Final"),
  XERCES ("xerces", "xercesImpl", "2.11.0"),
  XMLSEC ("org.apache.santuario", "xmlsec", "2.0.6"),
  // Special dependencies :)
  _PH_PARENT_POM6 ("com.helger", "ph-parent-pom", "1.4.1", EJDK.JDK6),
  _PH_PARENT_POM7 ("com.helger", "ph-parent-pom", "1.4.2", EJDK.JDK7);

  private final String m_sGroupID;
  private final String m_sArticfactID;
  private final String m_sVersion;
  private final Version m_aVersion;
  private final EJDK m_eMinJDK;

  private EExternalDependency (@Nonnull @Nonempty final String sGroupID,
                               @Nonnull @Nonempty final String sArticfactID,
                               @Nonnull @Nonempty final String sVersion)
  {
    this (sGroupID, sArticfactID, sVersion, EJDK.JDK6);
  }

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
    switch (this)
    {
      case JAXWS_MAVEN_PLUGIN_OLD:
        return JAXWS_MAVEN_PLUGIN;
      case JODA_TIME:
      case RHINO:
      case XERCES:
        if (eForJDK.isAtLeast8 ())
          return JDK;
        break;
    }
    return null;
  }

  public boolean isDeprecatedForJDK (@Nonnull final EJDK eForJDK)
  {
    return getReplacement (eForJDK) != null;
  }

  @Nullable
  public static List <EExternalDependency> findAll (@Nullable final String sGroupID, @Nullable final String sArtifactID)
  {
    final List <EExternalDependency> ret = EnumHelper.getAll (EExternalDependency.class,
                                                              e -> e.m_sGroupID.equals (sGroupID) &&
                                                                   e.m_sArticfactID.equals (sArtifactID));
    // Sort by JDK decsending
    ret.sort (Comparator.comparingInt ( (final EExternalDependency e) -> e.getMinimumJDKVersion ().getMajor ())
                        .reversed ());
    return ret;
  }
}
