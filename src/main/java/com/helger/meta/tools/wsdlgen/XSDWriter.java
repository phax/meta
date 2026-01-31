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
package com.helger.meta.tools.wsdlgen;

import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

import javax.xml.XMLConstants;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.meta.tools.wsdlgen.model.WGInterface;
import com.helger.meta.tools.wsdlgen.model.WGMethod;
import com.helger.meta.tools.wsdlgen.model.type.IWGType;
import com.helger.meta.tools.wsdlgen.model.type.WGComplexType;
import com.helger.meta.tools.wsdlgen.model.type.WGEnumEntry;
import com.helger.meta.tools.wsdlgen.model.type.WGSimpleType;
import com.helger.meta.tools.wsdlgen.model.type.WGTypeDef;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xml.serialize.write.XMLWriterSettings;

/**
 * Create an XSD file with only the types from an existing {@link WGInterface} object. This is used
 * within the WSDL generation.
 *
 * @author Philip Helger
 */
public class XSDWriter
{
  public static final String XSD_NS = XMLConstants.W3C_XML_SCHEMA_NS_URI;

  public static final String PREFIX_XSD = "xs";
  public static final String PREFIX_TNS = "tns";

  private static final Logger LOGGER = LoggerFactory.getLogger (XSDWriter.class);

  private final WGInterface m_aInterface;
  private boolean m_bCreateDocumentation = true;
  private boolean m_bDocumentLiteral = false;

  public XSDWriter (@NonNull final WGInterface aInterface)
  {
    m_aInterface = ValueEnforcer.notNull (aInterface, "Interface");
  }

  @NonNull
  public XSDWriter setCreateDocumentation (final boolean bCreateDocumentation)
  {
    m_bCreateDocumentation = bCreateDocumentation;
    return this;
  }

  @NonNull
  public XSDWriter setDocumentLiteral (final boolean bDocumentLiteral)
  {
    m_bDocumentLiteral = bDocumentLiteral;
    return this;
  }

  @NonNull
  @Nonempty
  private static String _getTypeRef (@NonNull final MapBasedNamespaceContext aNSC, @NonNull final IWGType aType)
  {
    return aNSC.getPrefix (aType.getNamespace ()) + ":" + aType.getName ();
  }

  private void _appendXSDDocumentation (@NonNull final IMicroElement eParent, @NonNull final String sDocumentation)
  {
    if (m_bCreateDocumentation)
    {
      final IMicroElement aAnnotation = eParent.addElementNS (XSD_NS, "annotation");
      final IMicroElement aDocumentation = aAnnotation.addElementNS (XSD_NS, "documentation");
      aDocumentation.addText (sDocumentation);
    }
  }

  private void _appendXSDAttribute (@NonNull final MapBasedNamespaceContext aNSC,
                                    @NonNull final IMicroElement eParent,
                                    @NonNull final String sChildName,
                                    @NonNull final WGTypeDef aChildTypeDef)
  {
    final IMicroElement eAttribute = eParent.addElementNS (XSD_NS, "attribute");
    eAttribute.setAttribute ("name", sChildName);
    eAttribute.setAttribute ("type", _getTypeRef (aNSC, aChildTypeDef.getType ()));
    if (aChildTypeDef.hasDefault ())
      eAttribute.setAttribute ("default", aChildTypeDef.getDefault ());
    eAttribute.setAttribute ("use", aChildTypeDef.isOptional () ? "optional" : "required");

    if (aChildTypeDef.hasDocumentation ())
      _appendXSDDocumentation (eAttribute, aChildTypeDef.getDocumentation ());
  }

  @NonNull
  @Nonempty
  public static String getElementName (@NonNull final String sMethodName,
                                       @NonNull final String sPartName,
                                       @NonNull final EElementType eType)
  {
    String sRealPartName = sPartName;
    if (!Character.isUpperCase (sRealPartName.charAt (0)))
    {
      sRealPartName = sRealPartName.substring (0, 1).toUpperCase (Locale.US) + sRealPartName.substring (1);
    }
    return sMethodName + sRealPartName + eType.getID ();
  }

  private void _appendDocumentLiteralElements (@NonNull final MapBasedNamespaceContext aNSC,
                                               @NonNull final IMicroElement eSchema)
  {
    /*
     * Note: when using document/literal the 3 strings created for the "name" attributes must match
     * the names created in WSDLWriter.generatedWSDL!!!
     */

    for (final WGMethod aMethod : m_aInterface.getAllMethods ())
    {
      final String sMethodName = aMethod.getName ();
      if (aMethod.isInput ())
        for (final Map.Entry <String, IWGType> aEntry : aMethod.getAllInputs ().entrySet ())
        {
          final IMicroElement ePredefinedElement = eSchema.addElementNS (XSD_NS, "element");
          ePredefinedElement.setAttribute ("name", getElementName (sMethodName, aEntry.getKey (), EElementType.INPUT));
          ePredefinedElement.setAttribute ("type", _getTypeRef (aNSC, aEntry.getValue ()));
        }

      if (aMethod.isOutput ())
        for (final Map.Entry <String, IWGType> aEntry : aMethod.getAllOutputs ().entrySet ())
        {
          final IMicroElement ePredefinedElement = eSchema.addElementNS (XSD_NS, "element");
          ePredefinedElement.setAttribute ("name", getElementName (sMethodName, aEntry.getKey (), EElementType.OUTPUT));
          ePredefinedElement.setAttribute ("type", _getTypeRef (aNSC, aEntry.getValue ()));
        }

      if (aMethod.isFault ())
        for (final Map.Entry <String, IWGType> aEntry : aMethod.getAllFaults ().entrySet ())
        {
          final IMicroElement ePredefinedElement = eSchema.addElementNS (XSD_NS, "element");
          ePredefinedElement.setAttribute ("name", getElementName (sMethodName, aEntry.getKey (), EElementType.FAULT));
          ePredefinedElement.setAttribute ("type", _getTypeRef (aNSC, aEntry.getValue ()));
        }
    }
  }

  @NonNull
  public IMicroElement getXSDSchemaElement (@NonNull final MapBasedNamespaceContext aNSC)
  {
    aNSC.addMapping (PREFIX_XSD, XSD_NS);

    final IMicroElement eSchema = new MicroElement (XSD_NS, "schema");
    eSchema.setAttribute ("targetNamespace", m_aInterface.getNamespace ());
    eSchema.setAttribute ("elementFormDefault", "qualified");

    for (final Map.Entry <String, WGTypeDef> aEntry : m_aInterface.getAllTypes ().entrySet ())
    {
      final String sName = aEntry.getKey ();
      final WGTypeDef aTypeDef = aEntry.getValue ();
      final IWGType aType = aTypeDef.getType ();
      if (aType instanceof WGSimpleType)
      {
        final WGSimpleType aSimpleType = (WGSimpleType) aType;
        if (aSimpleType.isExtension ())
        {
          // complexType + simpleContent
          final IMicroElement eComplexType = eSchema.addElementNS (XSD_NS, "complexType");
          eComplexType.setAttribute ("name", sName);

          if (aTypeDef.hasDocumentation ())
            _appendXSDDocumentation (eComplexType, aTypeDef.getDocumentation ());

          final IMicroElement eSimpleContent = eComplexType.addElementNS (XSD_NS, "simpleContent");
          final IMicroElement eExtension = eSimpleContent.addElementNS (XSD_NS, "extension");
          eExtension.setAttribute ("base", _getTypeRef (aNSC, aSimpleType.getExtension ()));

          // Child attributes
          for (final Map.Entry <String, WGTypeDef> aChildEntry : aSimpleType.getAllChildAttributes ().entrySet ())
            _appendXSDAttribute (aNSC, eExtension, aChildEntry.getKey (), aChildEntry.getValue ());
        }
        else
          if (aSimpleType.isRestriction ())
          {
            // simpleType
            final IMicroElement eSimpleType = eSchema.addElementNS (XSD_NS, "simpleType");
            eSimpleType.setAttribute ("name", sName);

            if (aTypeDef.hasDocumentation ())
              _appendXSDDocumentation (eSimpleType, aTypeDef.getDocumentation ());

            final IMicroElement eRestriction = eSimpleType.addElementNS (XSD_NS, "restriction");
            eRestriction.setAttribute ("base", _getTypeRef (aNSC, aSimpleType.getRestriction ()));

            boolean bHasAnything = false;
            if (aSimpleType.hasEnumEntries ())
            {
              for (final WGEnumEntry aEnumEntry : aSimpleType.getAllEnumEntries ())
              {
                final IMicroElement eEnumeration = eRestriction.addElementNS (XSD_NS, "enumeration")
                                                               .setAttribute ("value", aEnumEntry.getKey ());
                if (aEnumEntry.hasDocumentation ())
                  _appendXSDDocumentation (eEnumeration, aEnumEntry.getDocumentation ());
              }
              bHasAnything = true;
            }
            if (aSimpleType.hasMaxLength ())
            {
              eRestriction.addElementNS (XSD_NS, "maxLength").setAttribute ("value", aSimpleType.getMaxLength ());
              bHasAnything = true;
            }

            if (!bHasAnything)
              throw new UnsupportedOperationException ("No restriction supplied!");

          }
          else
            throw new UnsupportedOperationException ("Neither extension nor restriction is present!");
      }
      else
        if (aType instanceof WGComplexType)
        {
          final WGComplexType aComplexType = (WGComplexType) aType;
          final IMicroElement eComplexType = eSchema.addElementNS (XSD_NS, "complexType");
          eComplexType.setAttribute ("name", sName);

          if (aTypeDef.hasDocumentation ())
            _appendXSDDocumentation (eComplexType, aTypeDef.getDocumentation ());

          if (aComplexType.hasChildren ())
          {
            // Child elements
            final IMicroElement eParent = eComplexType.addElementNS (XSD_NS, aComplexType.getType ().getTagName ());
            for (final Map.Entry <String, WGTypeDef> aChildEntry : aComplexType.getAllChildElements ().entrySet ())
            {
              final String sChildName = aChildEntry.getKey ();
              final WGTypeDef aChildTypeDef = aChildEntry.getValue ();
              final IMicroElement eElement = eParent.addElementNS (XSD_NS, "element");
              eElement.setAttribute ("name", sChildName);
              eElement.setAttribute ("type", _getTypeRef (aNSC, aChildTypeDef.getType ()));
              if (aChildTypeDef.hasDefault ())
              {
                // Default value only for simple types!
                if (aChildTypeDef.getType ().isSimple ())
                  eElement.setAttribute ("default", aChildTypeDef.getDefault ());
                else
                  LOGGER.warn ("Element " +
                               sChildName +
                               " of complex type " +
                               aChildTypeDef.getType ().getName () +
                               " cannot have a default value!");
              }
              if (aChildTypeDef.hasMin ())
                eElement.setAttribute ("minOccurs", aChildTypeDef.getMin ());
              if (aChildTypeDef.hasMax ())
                eElement.setAttribute ("maxOccurs", aChildTypeDef.getMax ());
              if (aChildTypeDef.hasDocumentation ())
                _appendXSDDocumentation (eElement, aChildTypeDef.getDocumentation ());
            }
          }

          // Child attributes
          for (final Map.Entry <String, WGTypeDef> aChildEntry : aComplexType.getAllChildAttributes ().entrySet ())
            _appendXSDAttribute (aNSC, eComplexType, aChildEntry.getKey (), aChildEntry.getValue ());
        }
        else
          throw new IllegalStateException ("Unhandled type: " + aType);
    }

    if (m_bDocumentLiteral)
      _appendDocumentLiteralElements (aNSC, eSchema);

    return eSchema;
  }

  public void generatedXSD (@NonNull final OutputStream aOS)
  {
    final MapBasedNamespaceContext aNSC = new MapBasedNamespaceContext ();
    aNSC.addMapping (PREFIX_TNS, m_aInterface.getNamespace ());
    final IMicroElement eSchema = getXSDSchemaElement (aNSC);

    // Wrap in document
    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.addComment ("\nThis file was automatically generated by ph-wsdl-gen\n");
    aDoc.addChild (eSchema);

    // Write to stream
    final XMLWriterSettings aSettings = new XMLWriterSettings ();
    aSettings.setNamespaceContext (aNSC);
    aSettings.setPutNamespaceContextPrefixesInRoot (true);
    MicroWriter.writeToStream (aDoc, aOS, aSettings);
  }
}
