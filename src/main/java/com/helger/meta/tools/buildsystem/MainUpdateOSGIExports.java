package com.helger.meta.tools.buildsystem;

import java.io.File;

import org.objectweb.asm.tree.ClassNode;

import com.helger.commons.annotation.IsSPIInterface;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.CommonsLinkedHashMap;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.collection.ext.ICommonsOrderedMap;
import com.helger.commons.collection.multimap.MultiTreeMapTreeSetBased;
import com.helger.commons.io.file.iterate.FileSystemRecursiveIterator;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.mock.SPITestHelper;
import com.helger.commons.string.StringHelper;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.asm.ASMHelper;
import com.helger.meta.project.EProject;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;
import com.helger.xml.CXML;
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
  private static final String NS_MAVEN = "http://maven.apache.org/POM/4.0.0";
  private static final String EXPORT_PACKAGE = "Export-Package";
  private static final String IMPORT_PACKAGE = "Import-Package";
  private static final String REQUIRE_CAPABILITY = "Require-Capability";
  private static final String PROVIDE_CAPABILITY = "Provide-Capability";

  public static void main (final String [] args) throws Exception
  {
    final ClassPathResource aXSD = new ClassPathResource ("maven-4.0.0.xsd");
    final MapBasedNamespaceContext aNSCtx = new MapBasedNamespaceContext ();
    aNSCtx.addMapping ("", NS_MAVEN);
    aNSCtx.addMapping ("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    final XMLWriterSettings aXWS = new XMLWriterSettings ().setNamespaceContext (aNSCtx);

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
      aSRS.setPropertyValue (EXMLParserProperty.JAXP_SCHEMA_LANGUAGE, CXML.XML_NS_XSD);
      aSRS.setPropertyValue (EXMLParserProperty.JAXP_SCHEMA_SORUCE, aXSD.getAsFile ());
      aSRS.setEntityResolver ( (sPublicId, sSystemId) -> {
        if (sSystemId != null && sSystemId.endsWith ("/maven-v4_0_0.xsd"))
          return InputSourceFactory.create (aXSD);
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
              final ICommonsOrderedMap <String, String> aInstructionMap = new CommonsLinkedHashMap<> ();
              eInstructions.forAllChildElements (x -> aInstructionMap.put (x.getLocalName (),
                                                                           x.getTextContentTrimmed ()));

              final String sExportPackage = aInstructionMap.get (EXPORT_PACKAGE);
              if (StringHelper.hasNoText (sExportPackage))
                _warn (aProject, "No Export-Package present!");

              final String sImportPackage = aInstructionMap.get (IMPORT_PACKAGE);
              if (!"!javax.annotation.*,*".equals (sImportPackage))
                if (aProject != EProject.PH_JAXB)
                  _warn (aProject, "Import-Package is weird: " + sImportPackage);

              boolean bChanged = false;
              final ICommonsList <String> aRequireC = new CommonsArrayList<> ();
              final ICommonsList <String> aProvideC = new CommonsArrayList<> ();

              // ServiceLoader provider
              final MultiTreeMapTreeSetBased <String, String> aImpls = SPITestHelper.testIfAllSPIImplementationsAreValid (new File (aProject.getBaseDir (),
                                                                                                                                    SPITestHelper.MAIN_SERVICES).getAbsolutePath (),
                                                                                                                          SPITestHelper.EMode.NO_RESOLVE);
              if (aImpls.isNotEmpty ())
              {
                aRequireC.add ("osgi.extender; filter:=\"(osgi.extender=osgi.serviceloader.registrar)\"");
                aImpls.forEachSingleValue (x -> aProvideC.add ("osgi.serviceloader; osgi.serviceloader=" + x));
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
                                     ")\"; cardinality:=multiple");
                    }
                  }
              }

              // Build felix bundle string
              if (aRequireC.isNotEmpty ())
              {
                final StringBuilder aSB = new StringBuilder ();
                aRequireC.forEach ( (x, idx) -> {
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
                aProvideC.forEach ( (x, idx) -> {
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
    s_aLogger.info ("done");
  }
}
