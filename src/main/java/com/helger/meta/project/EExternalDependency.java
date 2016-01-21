package com.helger.meta.project;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.StringHelper;
import com.helger.commons.version.Version;

public enum EExternalDependency
{
  ASM_TREE ("org.ow2.asm", "asm-tree", "5.0.4"),
  BC_MAIL ("org.bouncycastle", "bcmail-jdk15on", "1.54"),
  BC_PROV ("org.bouncycastle", "bcprov-jdk15on", BC_MAIL.getLastPublishedVersionString ()),
  BC_PKIX ("org.bouncycastle", "bcpkix-jdk15on", BC_MAIL.getLastPublishedVersionString ()),
  COMMONS_DBCP2 ("org.apache.commons", "commons-dbcp2", "2.1.1"),
  COMMONS_NET ("commons-net", "commons-net", "3.3"),
  COMMONS_POOL2 ("org.apache.commons", "commons-pool2", "2.4.2"),
  DNSJAVA ("dnsjava", "dnsjava", "2.1.7"),
  DOCLET ("org.umlgraph", "doclet", "5.1"),
  ECLIPSELINK_CORE ("org.eclipse.persistence", "org.eclipse.persistence.core", "2.6.2"),
  ECLIPSELINK_JPA ("org.eclipse.persistence", "org.eclipse.persistence.jpa", ECLIPSELINK_CORE.getLastPublishedVersionString ()),
  ECLIPSELINK_ANTLR ("org.eclipse.persistence", "org.eclipse.persistence.antlr", ECLIPSELINK_CORE.getLastPublishedVersionString ()),
  ECLIPSELINK_ASM ("org.eclipse.persistence", "org.eclipse.persistence.asm", ECLIPSELINK_CORE.getLastPublishedVersionString ()),
  FELIX ("org.apache.felix", "org.apache.felix.framework", "5.4.0"),
  FINDBUGS_ANNOTATIONS ("com.google.code.findbugs", "annotations", "3.0.1u2"),
  FLUENT_HC ("org.apache.httpcomponents", "fluent-hc", "4.5.1"),
  H2 ("com.h2database", "h2", "1.4.190"),
  HAZELCAST ("com.hazelcast", "hazelcast", "3.5.4"),
  HTTP_CORE ("org.apache.httpcomponents", "httpcore", "4.4.4"),
  HTTP_CLIENT ("org.apache.httpcomponents", "httpclient", "4.5.1"),
  JACKSON_CORE ("com.fasterxml.jackson.core", "jackson-core", "2.7.0"),
  JACKSON_DATABIND ("com.fasterxml.jackson.core", "jackson-databind", JACKSON_CORE.getLastPublishedVersionString ()),
  JAVA_PARSER ("com.github.javaparser", "javaparser-core", "2.3.0"),
  JAVAX_EL ("org.glassfish", "javax.el", "3.0.0"),
  JAVAX_MAIL ("com.sun.mail", "javax.mail", "1.5.5"),
  JAVAX_PERSISTENCE ("org.eclipse.persistence", "javax.persistence", "2.1.1"),
  JAXB_IMPL ("com.sun.xml.bind", "jaxb-impl", "2.2.11"),
  JAXB_XJC ("com.sun.xml.bind", "jaxb-xjc", JAXB_IMPL.getLastPublishedVersionString ()),
  JAXB2_PLUGIN ("org.jvnet.jaxb2.maven2", "maven-jaxb2-plugin", "0.13.1"),
  JAXB2_BASICS ("org.jvnet.jaxb2_commons", "jaxb2-basics", "0.11.0"),
  JERSEY1_SERVLET ("com.sun.jersey", "jersey-servlet", "1.19"),
  JERSEY1_CLIENT ("com.sun.jersey", "jersey-client", JERSEY1_SERVLET.getLastPublishedVersionString ()),
  JERSEY2_BOM ("org.glassfish.jersey", "jersey-bom", "2.22.1"),
  JERSEY2_SERVER ("org.glassfish.jersey.core", "jersey-server", JERSEY2_BOM.getLastPublishedVersionString ()),
  JERSEY2_COMMON ("org.glassfish.jersey.core", "jersey-common", JERSEY2_BOM.getLastPublishedVersionString ()),
  JERSEY2_CLIENT ("org.glassfish.jersey.core", "jersey-client", JERSEY2_BOM.getLastPublishedVersionString ()),
  JETTY_WEBAPP ("org.eclipse.jetty", "jetty-webapp", "9.3.7.v20160115"),
  JETTY_ANNOTATIONS ("org.eclipse.jetty", "jetty-annotations", JETTY_WEBAPP.getLastPublishedVersionString ()),
  JETTY_PLUS ("org.eclipse.jetty", "jetty-plus", JETTY_WEBAPP.getLastPublishedVersionString ()),
  // New artifact in 9.3!
  JETTY_JSP ("org.eclipse.jetty", "jetty-jsp", "9.2.14.v20151106"),
  JSCH ("com.jcraft", "jsch", "0.1.53"),
  JSP_API ("javax.servlet.jsp", "jsp-api", "2.2"),
  JUNIT ("junit", "junit", "4.12"),
  LOG4J2_CORE ("org.apache.logging.log4j", "log4j-core", "2.5"),
  LOG4J2_SLF4J ("org.apache.logging.log4j", "log4j-slf4j-impl", LOG4J2_CORE.getLastPublishedVersionString ()),
  LOG4J2_WEB ("org.apache.logging.log4j", "log4j-web", LOG4J2_CORE.getLastPublishedVersionString ()),
  LUCENE_CORE ("org.apache.lucene", "lucene-core", "5.4.0"),
  LUCENE_ANALYZER_COMMON ("org.apache.lucene", "lucene-analyzers-common", LUCENE_CORE.getLastPublishedVersionString ()),
  LUCENE_QUERYPARSER ("org.apache.lucene", "lucene-queryparser", LUCENE_CORE.getLastPublishedVersionString ()),
  LUCENE_GROUPING ("org.apache.lucene", "lucene-grouping", LUCENE_CORE.getLastPublishedVersionString ()),
  MYSQL ("mysql", "mysql-connector-java", "5.1.38"),
  PDFBOX ("org.apache.pdfbox", "pdfbox", "2.0.0-RC3"),
  PDFBOX_EXAMPLES ("org.apache.pdfbox", "pdfbox-examples", PDFBOX.getLastPublishedVersionString ()),
  POI ("org.apache.poi", "poi", "3.13"),
  POI_OOXML ("org.apache.poi", "poi-ooxml", POI.getLastPublishedVersionString ()),
  RHINO ("org.mozilla", "rhino", "1.7.7"),
  SAXON ("net.sf.saxon", "Saxon-HE", "9.7.0-2"),
  SERVLET_API ("javax.servlet", "javax.servlet-api", "3.1.0"),
  SIMPLE_ODF ("org.apache.odftoolkit", "simple-odf", "0.8.1-incubating"),
  SLF4J_API ("org.slf4j", "slf4j-api", "1.7.13"),
  SLF4J_SIMPLE ("org.slf4j", "slf4j-simple", SLF4J_API.getLastPublishedVersionString ()),
  SLF4J_LOG4J12 ("org.slf4j", "slf4j-log4j12", SLF4J_API.getLastPublishedVersionString ()),
  JUL_TO_SLF4J ("org.slf4j", "jul-to-slf4j", SLF4J_API.getLastPublishedVersionString ()),
  JCL_OVER_SLF4J ("org.slf4j", "jcl-over-slf4j", SLF4J_API.getLastPublishedVersionString ()),
  THREE_TEN_EXTRA ("org.threeten", "threeten-extra", "0.9"),
  VALIDATION_API ("javax.validation", "validation-api", "1.1.0.Final"),
  XERCES ("xerces", "xercesImpl", "2.11.0"),
  XMLSEC ("org.apache.santuario", "xmlsec", "2.0.6"),;

  private final String m_sGroupID;
  private final String m_sArticfactID;
  private final String m_sVersion;
  private final Version m_aVersion;

  private EExternalDependency (@Nonnull @Nonempty final String sGroupID,
                               @Nonnull @Nonempty final String sArticfactID,
                               @Nonnull @Nonempty final String sVersion)
  {
    m_sGroupID = sGroupID;
    m_sArticfactID = sArticfactID;
    m_sVersion = sVersion;
    m_aVersion = new Version (sVersion);
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

  @Nullable
  public static EExternalDependency find (@Nullable final String sGroupID, @Nullable final String sArtifactID)
  {
    if (StringHelper.hasText (sGroupID) && StringHelper.hasText (sArtifactID))
      for (final EExternalDependency e : values ())
        if (e.m_sGroupID.equals (sGroupID) && e.m_sArticfactID.equals (sArtifactID))
          return e;
    return null;
  }
}
