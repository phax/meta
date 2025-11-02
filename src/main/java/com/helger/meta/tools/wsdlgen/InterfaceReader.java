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
package com.helger.meta.tools.wsdlgen;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.state.ETriState;
import com.helger.base.string.StringImplode;
import com.helger.base.string.StringRemove;
import com.helger.base.system.ENewLineMode;
import com.helger.cache.regex.RegExHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.resource.IReadableResource;
import com.helger.io.stream.StreamHelperExt;
import com.helger.json.IJson;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.parser.JsonParserSettings;
import com.helger.json.parser.handler.CollectingJsonParserHandler;
import com.helger.json.serialize.JsonReader;
import com.helger.meta.tools.wsdlgen.model.WGInterface;
import com.helger.meta.tools.wsdlgen.model.WGMethod;
import com.helger.meta.tools.wsdlgen.model.type.EComplexTypeType;
import com.helger.meta.tools.wsdlgen.model.type.IWGType;
import com.helger.meta.tools.wsdlgen.model.type.WGComplexType;
import com.helger.meta.tools.wsdlgen.model.type.WGEnumEntry;
import com.helger.meta.tools.wsdlgen.model.type.WGSimpleType;
import com.helger.meta.tools.wsdlgen.model.type.WGTypeDef;
import com.helger.meta.tools.wsdlgen.model.type.WGTypeRegistry;

/**
 * Class for reading the DSL and populating a {@link WGInterface}.
 *
 * @author Philip Helger
 */
public class InterfaceReader
{
  private static final Logger LOGGER = LoggerFactory.getLogger (InterfaceReader.class);

  @NonNull
  private static String _preprocess (@NonNull final ICommonsList <String> aContent)
  {
    ValueEnforcer.notNull (aContent, "Content");
    // Replace comment lines with empty line
    return StringImplode.getImplodedMapped (ENewLineMode.DEFAULT.getText (),
                                            aContent,
                                            sLine -> RegExHelper.stringMatchesPattern ("\\s*//.*", sLine) ? "" : sLine);
  }

  @Nullable
  private static IJsonObject _readAsJSON (@NonNull final IReadableResource aRes)
  {
    // Read line by line
    final ICommonsList <String> aContent = StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8);
    // Preprocess content
    final String sPreprocessedContent = _preprocess (aContent);
    // Convert to JSON
    final CollectingJsonParserHandler aHandler = new CollectingJsonParserHandler ();
    if (JsonReader.parseJson (new NonBlockingStringReader (sPreprocessedContent),
                              aHandler,
                              new JsonParserSettings ().setTrackPosition (true)
                                                       .setRequireStringQuotes (false)
                                                       .setAllowSpecialCharsInStrings (true),
                              ex -> LOGGER.error ("Failed to parse JSON", ex)).isSuccess ())
    {
      final IJson aJson = aHandler.getJson ();
      if (aJson.isObject ())
        return aJson.getAsObject ();

      LOGGER.error ("Parsed JSON is not an object!");
    }
    return null;
  }

  @Nullable
  private static String _cleanupText (@Nullable final String sText)
  {
    return sText == null ? null : StringRemove.removeAll (sText, '\r');
  }

  @Nullable
  private static String _getChildAsText (@NonNull final IJsonObject aNode, final String sFieldName)
  {
    return _cleanupText (aNode.getAsString (sFieldName));
  }

  @Nullable
  private static String _getDocumentation (@Nullable final String sDoc)
  {
    if (sDoc == null)
      return null;
    // Replace leading whitespaces after a line break
    return RegExHelper.stringReplacePattern ("\\n\\s+", _cleanupText (sDoc), "\n");
  }

  @NonNull
  private static ETriState _getTriState (@NonNull final IJsonObject aType, final String sProperty)
  {
    return ETriState.valueOf (aType.getAsBooleanObj (sProperty));
  }

  @NonNull
  private static WGTypeDef _readTypeDef (final WGInterface aInterface,
                                         final String sTypeChildName,
                                         @NonNull final IJson aTypeChildNode)
  {
    if (aTypeChildNode.isValue ())
    {
      // type only - no details
      final String sChildTypeName = aTypeChildNode.getAsValue ().getAsString ();
      final IWGType aChildType = aInterface.getTypeOfName (sChildTypeName);
      if (aChildType == null)
        throw new IllegalArgumentException ("Property '" +
                                            sTypeChildName +
                                            "' has invalid type '" +
                                            sChildTypeName +
                                            "'");
      return new WGTypeDef (aChildType);
    }

    // All details
    final IJsonObject aType = aTypeChildNode.getAsObject ();

    final String sChildTypeName = _getChildAsText (aType, "$type");
    final IWGType aChildType = aInterface.getTypeOfName (sChildTypeName);
    if (aChildType == null)
      throw new IllegalArgumentException ("Property '" +
                                          sTypeChildName +
                                          "' has invalid type '" +
                                          sChildTypeName +
                                          "'");
    final WGTypeDef aTypeDef = new WGTypeDef (aChildType);
    aTypeDef.setDocumentation (_getDocumentation (_getChildAsText (aType, "$doc")));
    aTypeDef.setMin (_getChildAsText (aType, "$min"));
    aTypeDef.setMax (_getChildAsText (aType, "$max"));
    aTypeDef.setDefault (_getChildAsText (aType, "$default"));
    aTypeDef.setOptional (_getTriState (aType, "$optional"));
    return aTypeDef;
  }

  @Nullable
  public static WGInterface readInterface (@NonNull final IReadableResource aRes)
  {
    final IJsonObject aInterfaceNode = _readAsJSON (aRes);
    if (aInterfaceNode == null)
      return null;

    final String sInterfaceName = _getChildAsText (aInterfaceNode, "$name");
    final String sInterfaceNamespace = _getChildAsText (aInterfaceNode, "$namespace");
    final String sInterfaceEndpoint = _getChildAsText (aInterfaceNode, "$endpoint");

    final WGInterface aInterface = new WGInterface (sInterfaceName, sInterfaceNamespace, sInterfaceEndpoint);
    // Dont't format global interface comment
    aInterface.setDocumentation (_getChildAsText (aInterfaceNode, "$doc"));

    // Add all types
    final IJsonObject aTypesNode = (IJsonObject) aInterfaceNode.get ("$types");
    if (aTypesNode != null)
    {
      // Read all contained types
      for (final Map.Entry <String, IJson> aTypeEntry : aTypesNode)
      {
        final String sTypeName = aTypeEntry.getKey ();
        final IJson aTypeNode = aTypeEntry.getValue ();

        if (!sTypeName.startsWith ("$"))
        {
          final boolean bIsSimpleType = sTypeName.startsWith ("#");
          if (bIsSimpleType)
          {
            // Simple type
            final WGSimpleType aSimpleType = new WGSimpleType (sInterfaceNamespace, sTypeName.substring (1));
            final WGTypeDef aSimpleTypeDef = new WGTypeDef (aSimpleType);
            for (final Map.Entry <String, IJson> aChildTypeEntry : aTypeNode.getAsObject ().getAll ().entrySet ())
            {
              final String sTypeChildName = aChildTypeEntry.getKey ();
              final IJson aTypeChildNode = aChildTypeEntry.getValue ();

              if (sTypeChildName.equals ("$doc"))
                aSimpleTypeDef.setDocumentation (_getDocumentation (aTypeChildNode.getAsValue ().getAsString ()));
              else
                if (sTypeChildName.equals ("$extension"))
                {
                  final String sExtension = aTypeChildNode.getAsValue ().getAsString ();
                  final IWGType aExtensionType = aInterface.getTypeOfName (sExtension);
                  if (aExtensionType == null)
                    throw new IllegalArgumentException ("Simple type '" +
                                                        aSimpleType.getName () +
                                                        "' has invalid extension type '" +
                                                        sExtension +
                                                        "'");
                  aSimpleType.setExtension (aExtensionType);
                }
                else
                  if (sTypeChildName.equals ("$restriction"))
                  {
                    final String sRestriction = aTypeChildNode.getAsValue ().getAsString ();
                    final IWGType aRestrictionType = aInterface.getTypeOfName (sRestriction);
                    if (aRestrictionType == null)
                      throw new IllegalArgumentException ("Simple type '" +
                                                          aSimpleType.getName () +
                                                          "' has invalid restriction type '" +
                                                          sRestriction +
                                                          "'");
                    aSimpleType.setRestriction (aRestrictionType);
                  }
                  else
                    if (sTypeChildName.equals ("$enum"))
                    {
                      final IJsonArray aEntries = aTypeChildNode.getAsArray ();
                      if (aEntries == null)
                        throw new IllegalArgumentException ("Simple type '" +
                                                            aSimpleType.getName () +
                                                            "' has invalid enum entries");

                      // Convert all to string
                      final ICommonsList <WGEnumEntry> aEnumEntries = new CommonsArrayList <> ();
                      for (final IJson aPropValue : aEntries)
                      {
                        if (aPropValue.isArray ())
                        {
                          // [key, documentation]
                          final IJsonArray aProvValueList = aPropValue.getAsArray ();
                          aEnumEntries.add (new WGEnumEntry (aProvValueList.getAsString (0),
                                                             aProvValueList.getAsString (1)));
                        }
                        else
                        {
                          // Just the key, without documentation
                          aEnumEntries.add (new WGEnumEntry (aPropValue.getAsValue ().getAsString ()));
                        }
                      }
                      aSimpleType.setEnumEntries (aEnumEntries);
                    }
                    else
                      if (sTypeChildName.equals ("$maxlength"))
                      {
                        final int nMaxLength = aTypeChildNode.getAsValue ().getAsInt (-1);
                        if (nMaxLength <= 0)
                          throw new IllegalArgumentException ("Simple type '" +
                                                              aSimpleType.getName () +
                                                              "' has invalid maxLength definition '" +
                                                              aTypeChildNode.getAsValue ().getAsString () +
                                                              "'");

                        aSimpleType.setMaxLength (nMaxLength);
                      }
                      else
                        if (!sTypeChildName.startsWith ("$"))
                        {
                          // Only attributes allowed!
                          if (!sTypeChildName.startsWith ("@"))
                            throw new IllegalArgumentException ("Simple type '" +
                                                                aSimpleType.getName () +
                                                                "' may only have attributes and not '" +
                                                                sTypeChildName +
                                                                "'");

                          final WGTypeDef aTypeDef = _readTypeDef (aInterface, sTypeChildName, aTypeChildNode);
                          aSimpleType.addChildAttribute (sTypeChildName.substring (1), aTypeDef);
                        }
                        else
                          throw new IllegalStateException ("Unhandled simple type child: " + sTypeChildName);
            }
            aInterface.addType (aSimpleTypeDef);
          }
          else
          {
            // Complex type
            final WGComplexType aComplexType = new WGComplexType (sInterfaceNamespace, sTypeName);
            final WGTypeDef aComplexTypeDef = new WGTypeDef (aComplexType);
            for (final Map.Entry <String, IJson> aChildTypeEntry : aTypeNode.getAsObject ().getAll ().entrySet ())
            {
              final String sTypeChildName = aChildTypeEntry.getKey ();
              final IJson aTypeChildNode = aChildTypeEntry.getValue ();

              if (sTypeChildName.equals ("$doc"))
                aComplexTypeDef.setDocumentation (_getDocumentation (aTypeChildNode.getAsValue ().getAsString ()));
              else
                if (sTypeChildName.equals ("$type"))
                  aComplexType.setType (EComplexTypeType.getFromTagNameOrThrow (aTypeChildNode.getAsValue ()
                                                                                              .getAsString ()));
                else
                  if (!sTypeChildName.startsWith ("$"))
                  {
                    final WGTypeDef aChildTypeDef = _readTypeDef (aInterface, sTypeChildName, aTypeChildNode);

                    // Attribute or element?
                    if (sTypeChildName.startsWith ("@"))
                      aComplexType.addChildAttribute (sTypeChildName.substring (1), aChildTypeDef);
                    else
                      aComplexType.addChildElement (sTypeChildName, aChildTypeDef);
                  }
                  else
                    throw new IllegalStateException ("Unhandled complex type child: " + sTypeChildName);
            }
            aInterface.addType (aComplexTypeDef);
          }
        }
        else
          throw new IllegalStateException ("Unhandled special type: " + sTypeName);
      }
    }

    // Add all methods
    for (final Map.Entry <String, IJson> aMethodEntry : aInterfaceNode.getAll ().entrySet ())
    {
      final String sMethodName = aMethodEntry.getKey ();

      if (!sMethodName.startsWith ("$"))
      {
        final IJsonObject aMethodNode = aMethodEntry.getValue ().getAsObject ();
        final WGMethod aMethod = new WGMethod (sMethodName);

        // Input
        final IJson aInputNode = aMethodNode.get ("$input");
        if (aInputNode != null && aInputNode.isObject ())
        {
          aMethod.markHavingInput ();
          for (final Map.Entry <String, IJson> aEntry : aInputNode.getAsObject ().getAll ().entrySet ())
          {
            final String sParamName = aEntry.getKey ();
            final String sParamType = aEntry.getValue ().getAsValue ().getAsString ();
            IWGType aParamType = aInterface.getTypeOfName (sParamType);
            if (aParamType == null)
            {
              // Try predefined type
              aParamType = WGTypeRegistry.getTypeOfName (sParamType);
            }
            if (aParamType == null)
            {
              throw new IllegalArgumentException ("Input type '" +
                                                  sParamType +
                                                  "' of method '" +
                                                  sMethodName +
                                                  "' for element '" +
                                                  sParamName +
                                                  "' not found");
            }
            aMethod.addInputParam (sParamName, aParamType);
          }
        }

        // Output
        final IJson aOutputNode = aMethodNode.get ("$output");
        if (aOutputNode != null && aOutputNode.isObject ())
        {
          aMethod.markHavingOutput ();
          for (final Map.Entry <String, IJson> aEntry : aOutputNode.getAsObject ().getAll ().entrySet ())
          {
            final String sParamName = aEntry.getKey ();
            final String sParamType = aEntry.getValue ().getAsValue ().getAsString ();
            IWGType aParamType = aInterface.getTypeOfName (sParamType);
            if (aParamType == null)
            {
              // Try predefined type
              aParamType = WGTypeRegistry.getTypeOfName (sParamType);
            }
            if (aParamType == null)
              throw new IllegalArgumentException ("Output type '" +
                                                  sParamType +
                                                  "' of method '" +
                                                  sMethodName +
                                                  "' for element '" +
                                                  sParamName +
                                                  "' not found");
            aMethod.addOutputParam (sParamName, aParamType);
          }
        }

        // Fault
        final IJson aFaultNode = aMethodNode.get ("$fault");
        if (aFaultNode != null && aFaultNode.isObject ())
        {
          aMethod.markHavingFault ();
          for (final Map.Entry <String, IJson> aEntry : aFaultNode.getAsObject ())
          {
            final String sParamName = aEntry.getKey ();
            final String sParamType = aEntry.getValue ().getAsValue ().getAsString ();
            IWGType aParamType = aInterface.getTypeOfName (sParamType);
            if (aParamType == null)
            {
              // Try predefined type
              aParamType = WGTypeRegistry.getTypeOfName (sParamType);
            }
            if (aParamType == null)
              throw new IllegalArgumentException ("Fault type '" +
                                                  sParamType +
                                                  "' of method '" +
                                                  sMethodName +
                                                  "' for element '" +
                                                  sParamName +
                                                  "' not found");
            aMethod.addFaultParam (sParamName, aParamType);
          }
        }

        aInterface.addMethod (aMethod);
      }
    }

    return aInterface;
  }
}
