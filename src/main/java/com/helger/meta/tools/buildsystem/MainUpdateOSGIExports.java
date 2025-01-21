/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import javax.xml.XMLConstants;

import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.IsSPIInterface;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsSortedMap;
import com.helger.commons.collection.impl.ICommonsSortedSet;
import com.helger.commons.io.file.FileSystemRecursiveIterator;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.mock.SPITestHelper;
import com.helger.commons.string.StringHelper;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.asm.ASMHelper;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLParserProperty;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.microdom.util.MicroHelper;
import com.helger.xml.microdom.util.MicroRecursiveIterator;
import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xml.sax.InputSourceFactory;
import com.helger.xml.serialize.read.SAXReaderSettings;
import com.helger.xml.serialize.write.XMLWriterSettings;

public final class MainUpdateOSGIExports extends AbstractProjectMain
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainUpdateOSGIExports.class);
  private static final String NS_MAVEN = "http://maven.apache.org/POM/4.0.0";
  private static final String AUTOMATIC_MODULE_NAME = "Automatic-Module-Name";
  private static final String EXPORT_PACKAGE = "Export-Package";
  private static final String IMPORT_PACKAGE = "Import-Package";
  private static final String REQUIRE_CAPABILITY = "Require-Capability";
  private static final String PROVIDE_CAPABILITY = "Provide-Capability";

  public static void main (final String [] args) throws Exception
  {
    final ClassPathResource aMavenXSD = new ClassPathResource ("external/maven-4.0.0.xsd");
    final MapBasedNamespaceContext aNSCtx = new MapBasedNamespaceContext ();
    aNSCtx.addMapping ("", NS_MAVEN);
    aNSCtx.addMapping ("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    final XMLWriterSettings aXWS = new XMLWriterSettings ().setNamespaceContext (aNSCtx);
    final ICommonsMap <String, IProject> aUsedAutomaticModuleNames = new CommonsHashMap <> ();
    final ICommonsMap <String, IProject> aUsedExportPackages = new CommonsHashMap <> ();

    for (final IProject aProject : ProjectList.getAllProjects (x -> x.isBuildInProject () &&
                                                                    !x.isDeprecated () &&
                                                                    x.getProjectType ().hasJavaCode ()))
    {
      if (false)
        _info (aProject, "Start project");

      final SAXReaderSettings aSRS = new SAXReaderSettings ();
      aSRS.setFeatureValue (EXMLParserFeature.VALIDATION, true);
      aSRS.setFeatureValue (EXMLParserFeature.SCHEMA, true);
      aSRS.setFeatureValue (EXMLParserFeature.NAMESPACES, true);
      aSRS.setPropertyValue (EXMLParserProperty.JAXP_SCHEMA_LANGUAGE, XMLConstants.W3C_XML_SCHEMA_NS_URI);
      if (false)
        aSRS.setPropertyValue (EXMLParserProperty.JAXP_SCHEMA_SOURCE, aMavenXSD.getAsFile ());
      aSRS.setEntityResolver ( (sPublicId, sSystemId) -> {
        // Public ID is unfortunately null
        if (sSystemId != null && (sSystemId.endsWith ("/maven-v4_0_0.xsd") || sSystemId.endsWith ("/maven-4.0.0.xsd")))
          return InputSourceFactory.create (aMavenXSD);
        return null;
      });

      final IMicroDocument aPOM = MicroReader.readMicroXML (aProject.getPOMFile (), aSRS);
      if (aPOM == null)
      {
        _warn (aProject, "Failed to read pom.xml!");
        continue;
      }
      final IMicroElement eRoot = aPOM.getDocumentElement ();
      if ("bundle".equals (MicroHelper.getChildTextContent (eRoot, "packaging")))
      {
        boolean bFoundInstructions = false;
        for (final IMicroNode aNode : new MicroRecursiveIterator (eRoot))
          if (aNode.isElement ())
          {
            final IMicroElement eInstructions = (IMicroElement) aNode;
            // groupId is optional e.g. for the defined artefact
            if (eInstructions.hasLocalName ("instructions"))
            {
              if (false)
                _info (aProject, "Found Bundle instructions");
              bFoundInstructions = true;
              final ICommonsOrderedMap <String, String> aInstructionMap = new CommonsLinkedHashMap <> ();
              eInstructions.forAllChildElements (x -> aInstructionMap.put (x.getLocalName (),
                                                                           x.getTextContentTrimmed ()));

              final String sAutomaticModuleName = aInstructionMap.get (AUTOMATIC_MODULE_NAME);
              if (StringHelper.hasNoText (sAutomaticModuleName))
                _warn (aProject, "No " + AUTOMATIC_MODULE_NAME + " present!");
              else
              {
                // Check if name is unique
                final IProject aOld = aUsedAutomaticModuleNames.put (sAutomaticModuleName, aProject);
                if (aOld != null)
                {
                  _warn (aProject,
                         "The automatic module name '" +
                                   sAutomaticModuleName +
                                   "' is already used in project " +
                                   aOld.getProjectName ());
                }
              }

              final String sExportPackage = aInstructionMap.get (EXPORT_PACKAGE);
              if (StringHelper.hasNoText (sExportPackage))
                _warn (aProject, "No " + EXPORT_PACKAGE + " present!");
              else
              {
                // Check if name is unique
                final IProject aOld = aUsedExportPackages.put (sExportPackage, aProject);
                if (aOld != null)
                {
                  _warn (aProject,
                         "The export package '" +
                                   sExportPackage +
                                   "' is already used in project " +
                                   aOld.getProjectName ());
                }
                if (StringHelper.hasText (sAutomaticModuleName) &&
                    !sExportPackage.contains (sAutomaticModuleName + ".*"))
                {
                  _warn (aProject,
                         "Weird " +
                                   EXPORT_PACKAGE +
                                   " '" +
                                   sExportPackage +
                                   "' vs automatic module name '" +
                                   sAutomaticModuleName +
                                   "'");
                }
              }

              final String sImportPackage = aInstructionMap.get (IMPORT_PACKAGE);
              if (!"!javax.annotation.*,*".equals (sImportPackage))
                _warn (aProject, IMPORT_PACKAGE + " is weird: " + sImportPackage);

              boolean bChanged = false;
              final ICommonsList <String> aRequireC = new CommonsArrayList <> ();
              final ICommonsList <String> aProvideC = new CommonsArrayList <> ();

              // ServiceLoader provider
              // Map from interface to Set of implementations
              final ICommonsSortedMap <String, ICommonsSortedSet <String>> aImpls = SPITestHelper.testIfAllSPIImplementationsAreValid (new File (aProject.getBaseDir (),
                                                                                                                                                 SPITestHelper.MAIN_SERVICES).getAbsolutePath (),
                                                                                                                                       SPITestHelper.EMode.NO_RESOLVE);
              if (aImpls.isNotEmpty ())
              {
                aRequireC.add ("osgi.extender; filter:=\"(osgi.extender=osgi.serviceloader.registrar)\"");
                for (final String sInterfaceSPI : aImpls.keySet ())
                  aProvideC.add ("osgi.serviceloader; osgi.serviceloader=" + sInterfaceSPI);
              }

              // Check all classes of this project for SPI interfaces
              {
                boolean bFirst = true;
                final File aClassDir = new File (aProject.getBaseDir (), "target/classes");
                for (final File aClassFile : new FileSystemRecursiveIterator (aClassDir))
                  if (aClassFile.isFile () && aClassFile.getName ().endsWith ("SPI.class"))
                  {
                    final ClassNode cn = ASMHelper.readClassFile (aClassFile);
                    if (ASMHelper.containsAnnotation (cn, IsSPIInterface.class))
                    {
                      final String sClassName = ClassHelper.getClassFromPath (cn.name);
                      if (bFirst)
                      {
                        aRequireC.add ("osgi.extender; filter:=\"(osgi.extender=osgi.serviceloader.processor)\"");
                        bFirst = false;
                      }
                      aRequireC.add ("osgi.serviceloader; filter:=\"(osgi.serviceloader=" +
                                     sClassName +
                                     ")\"; cardinality:=multiple; resolution:=optional");
                    }
                  }
              }

              // Build felix bundle string
              if (aRequireC.isNotEmpty ())
              {
                final StringBuilder aSB = new StringBuilder ();
                aRequireC.forEachByIndex ( (x, idx) -> {
                  if (idx > 0)
                    aSB.append (",\n");
                  aSB.append (x);
                });
                final String sNew = aSB.toString ();
                if (!sNew.equals (aInstructionMap.get (REQUIRE_CAPABILITY)))
                {
                  aInstructionMap.put (REQUIRE_CAPABILITY, sNew);
                  bChanged = true;
                }
              }
              if (aProvideC.isNotEmpty ())
              {
                final StringBuilder aSB = new StringBuilder ();
                aProvideC.forEachByIndex ( (x, idx) -> {
                  if (idx > 0)
                    aSB.append (",\n");
                  aSB.append (x);
                });
                final String sNew = aSB.toString ();
                if (!sNew.equals (aInstructionMap.get (PROVIDE_CAPABILITY)))
                {
                  aInstructionMap.put (PROVIDE_CAPABILITY, sNew);
                  bChanged = true;
                }
              }

              if (bChanged)
              {
                // Update pom.xml!
                eInstructions.removeAllChildren ();
                aInstructionMap.forEach ( (k, v) -> eInstructions.appendElement (NS_MAVEN, k).appendText (v));
                MicroWriter.writeToFile (aPOM, aProject.getPOMFile (), aXWS);
                _info (aProject, "Updated OSGI configuration!");
              }
              break;
            }
          }

        if (!bFoundInstructions)
          _warn (aProject, "OSGI bundle is missing plugin instructions!");
      }
    }
    LOGGER.info ("done");
  }
}
