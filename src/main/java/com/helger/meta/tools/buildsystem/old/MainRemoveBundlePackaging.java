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
package com.helger.meta.tools.buildsystem.old;

import javax.xml.XMLConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.io.resource.ClassPathResource;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLParserProperty;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.serialize.MicroReaderSettings;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.microdom.util.MicroHelper;
import com.helger.xml.microdom.util.MicroRecursiveIterator;
import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xml.sax.InputSourceFactory;
import com.helger.xml.serialize.write.XMLWriterSettings;

/**
 * One-time tool to remove OSGi bundle packaging from all projects.
 * <ul>
 * <li>Changes &lt;packaging&gt;bundle&lt;/packaging&gt; to
 * &lt;packaging&gt;jar&lt;/packaging&gt;</li>
 * <li>Removes the maven-bundle-plugin &lt;plugin&gt; element entirely</li>
 * <li>Cleans up empty &lt;plugins&gt; and &lt;build&gt; elements</li>
 * </ul>
 */
public final class MainRemoveBundlePackaging extends AbstractProjectMain
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainRemoveBundlePackaging.class);
  private static final String NS_MAVEN = "http://maven.apache.org/POM/4.0.0";

  public static void main (final String [] args) throws Exception
  {
    final ClassPathResource aMavenXSD = new ClassPathResource ("external/maven-4.0.0.xsd");
    final MapBasedNamespaceContext aNSCtx = new MapBasedNamespaceContext ();
    aNSCtx.addMapping ("", NS_MAVEN);
    aNSCtx.addMapping ("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    final XMLWriterSettings aXWS = new XMLWriterSettings ().setNamespaceContext (aNSCtx);

    for (final IProject aProject : ProjectList.getAllProjects (x -> x.isBuildInProject () &&
                                                                    !x.isDeprecated () &&
                                                                    x.getProjectType ().hasJavaCode ()))
    {
      final MicroReaderSettings aSRS = new MicroReaderSettings ();
      aSRS.setFeatureValue (EXMLParserFeature.VALIDATION, true);
      aSRS.setFeatureValue (EXMLParserFeature.SCHEMA, true);
      aSRS.setFeatureValue (EXMLParserFeature.NAMESPACES, true);
      aSRS.setPropertyValue (EXMLParserProperty.JAXP_SCHEMA_LANGUAGE, XMLConstants.W3C_XML_SCHEMA_NS_URI);
      aSRS.setPropertyValue (EXMLParserProperty.ACCESS_EXTERNAL_SCHEMA, "all");
      aSRS.setEntityResolver ( (sPublicId, sSystemId) -> {
        if (sSystemId != null && (sSystemId.endsWith ("/maven-v4_0_0.xsd") || sSystemId.endsWith ("/maven-4.0.0.xsd")))
          return InputSourceFactory.create (aMavenXSD);
        return null;
      });
      aSRS.setSaveNamespaceDeclarations (true);

      final IMicroDocument aPOM = MicroReader.readMicroXML (aProject.getPOMFile (), aSRS);
      if (aPOM == null)
      {
        _warn (aProject, "Failed to read pom.xml!");
        continue;
      }

      final IMicroElement eRoot = aPOM.getDocumentElement ();
      boolean bChanged = false;

      // 1. Change <packaging>bundle</packaging> to <packaging>jar</packaging>
      final IMicroElement ePackaging = eRoot.getFirstChildElement ("packaging");
      if (ePackaging != null && "bundle".equals (ePackaging.getTextContentTrimmed ()))
      {
        ePackaging.removeAllChildren ();
        ePackaging.addText ("jar");
        bChanged = true;
      }

      // 2. Find and remove the maven-bundle-plugin <plugin> element
      IMicroElement eBundlePlugin = null;
      for (final IMicroNode aNode : new MicroRecursiveIterator (eRoot))
        if (aNode.isElement ())
        {
          final IMicroElement eElement = (IMicroElement) aNode;
          if (eElement.hasLocalName ("plugin"))
          {
            final String sArtifactId = MicroHelper.getChildTextContentTrimmed (eElement, "artifactId");
            if ("maven-bundle-plugin".equals (sArtifactId))
            {
              eBundlePlugin = eElement;
              break;
            }
          }
        }

      if (eBundlePlugin != null)
      {
        final IMicroElement ePlugins = (IMicroElement) eBundlePlugin.getParent ();
        ePlugins.removeChild (eBundlePlugin);
        bChanged = true;

        // If <plugins> is now empty, remove it
        if (!ePlugins.hasChildElements ())
        {
          final IMicroElement eBuild = (IMicroElement) ePlugins.getParent ();
          eBuild.removeChild (ePlugins);

          // If <build> is now empty, remove it
          if (!eBuild.hasChildElements ())
          {
            eRoot.removeChild (eBuild);
          }
        }
      }

      if (bChanged)
      {
        MicroWriter.writeToFile (aPOM, aProject.getPOMFile (), aXWS);
        _info (aProject, "Removed bundle packaging and maven-bundle-plugin");
      }
    }
    LOGGER.info ("Done - removed bundle packaging from all projects");
  }
}
