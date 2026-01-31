/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.meta.tools.buildsystem;

import java.io.File;
import java.time.format.DateTimeFormatter;

import javax.xml.XMLConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.string.StringHelper;
import com.helger.datetime.helper.PDTFactory;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EExternalDependency;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xml.serialize.write.XMLWriterSettings;

/**
 * Create a POM that contains all external dependencies defined in {@link EExternalDependency} so
 * that default maven checks can be performed.
 *
 * @author Philip Helger
 */
public final class MainCreateKnownDependencyPOM extends AbstractProjectMain
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCreateKnownDependencyPOM.class);
  private static final String NS = "http://maven.apache.org/POM/4.0.0";

  public static void main (final String [] args)
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eProject = aDoc.addElementNS (NS, "project");
    eProject.setAttributeNS (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
                             "schemaLocation",
                             NS + " http://maven.apache.org/maven-v4_0_0.xsd");
    eProject.addElementNS (NS, "modelVersion").addText ("4.0.0");
    eProject.addElementNS (NS, "groupId").addText ("com.helger");
    eProject.addElementNS (NS, "artifactId").addText ("external-dependencies");
    eProject.addElementNS (NS, "version")
            .addText ("1.0.0-" + DateTimeFormatter.BASIC_ISO_DATE.format (PDTFactory.getCurrentLocalDateTime ()));

    final IMicroElement eDeps = eProject.addElementNS (NS, "dependencies");
    eDeps.addComment ("External dependencies:");
    for (final EExternalDependency e : EExternalDependency.values ())
      if (!e.isLegacy ())
      {
        final IMicroElement eDep = eDeps.addElementNS (NS, "dependency");
        eDep.addElementNS (NS, "groupId").addText (e.getGroupID ());
        eDep.addElementNS (NS, "artifactId").addText (e.getArtifactID ());

        final String sMaxVersion = e.getMaxVersionString ();
        if (StringHelper.isEmpty (sMaxVersion))
          eDep.addElementNS (NS, "version").addText (e.getLastPublishedVersionString ());
        else
        {
          // User a version range
          eDep.addElementNS (NS, "version")
              .addText ("[" + e.getLastPublishedVersionString () + "," + sMaxVersion + ")");
        }

        if (e.isBOM ())
          eDep.addElementNS (NS, "type").addText ("pom");
      }

    eDeps.addComment ("Internal projects:");
    for (final IProject aProject : ProjectList.getAllProjects (x -> x.isBuildInProject () &&
                                                                    x.isPublished () &&
                                                                    !x.isDeprecated ()))
    {
      final IMicroElement eDep = eDeps.addElementNS (NS, "dependency");
      eDep.addElementNS (NS, "groupId").addText (aProject.getMavenGroupID ());
      eDep.addElementNS (NS, "artifactId").addText (aProject.getMavenArtifactID ());
      eDep.addElementNS (NS, "version").addText (aProject.getLastPublishedVersionString ());
      switch (aProject.getProjectType ())
      {
        case MAVEN_POM:
          eDep.addElementNS (NS, "type").addText ("pom");
          break;
        case JAVA_WEB_APPLICATION:
          eDep.addElementNS (NS, "type").addText ("war");
          break;
      }
    }

    {
      final IMicroElement eBuild = eProject.addElementNS (NS, "build");
      final IMicroElement ePlugins = eBuild.addElementNS (NS, "plugins");
      final IMicroElement ePlugin = ePlugins.addElementNS (NS, "plugin");
      ePlugin.addElementNS (NS, "groupId").addText ("org.codehaus.mojo");
      ePlugin.addElementNS (NS, "artifactId").addText ("versions-maven-plugin");
      ePlugin.addElementNS (NS, "version")
             .addText (EExternalDependency.VERSIONS_MAVEN_PLUGIN.getLastPublishedVersionString ());
      final IMicroElement eConfig = ePlugin.addElementNS (NS, "configuration");
      eConfig.addElementNS (NS, "allowSnapshots").addText ("false");
      eConfig.addElementNS (NS, "rulesUri").addText ("file:versions-maven-plugin-rules.xml");
    }

    final File f = new File ("deps/pom.xml");
    final MapBasedNamespaceContext aNSCtx = new MapBasedNamespaceContext ();
    aNSCtx.setDefaultNamespaceURI (NS);
    aNSCtx.addMapping ("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
    MicroWriter.writeToFile (aDoc, f, new XMLWriterSettings ().setNamespaceContext (aNSCtx));
    LOGGER.info ("Done");
    LOGGER.info ("Run the following now on " + f.toString () + ": mvn versions:display-dependency-updates");
  }
}
