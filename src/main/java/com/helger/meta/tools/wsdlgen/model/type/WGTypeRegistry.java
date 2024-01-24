/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.meta.tools.wsdlgen.model.type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;

public final class WGTypeRegistry
{
  private static final ICommonsMap <String, IWGType> s_aTypes = new CommonsHashMap <> ();

  static
  {
    // string
    registerType (new WGXSDType ("ENTITIES"));
    registerType (new WGXSDType ("ENTITY"));
    registerType (new WGXSDType ("ID"));
    registerType (new WGXSDType ("IDREF"));
    registerType (new WGXSDType ("IDREFS"));
    registerType (new WGXSDType ("language"));
    registerType (new WGXSDType ("Name"));
    registerType (new WGXSDType ("NCName"));
    registerType (new WGXSDType ("NMTOKEN"));
    registerType (new WGXSDType ("NMTOKENS"));
    registerType (new WGXSDType ("normalizedString"));
    registerType (new WGXSDType ("QName"));
    registerType (new WGXSDType ("string"));
    registerType (new WGXSDType ("token"));
    // date and time
    registerType (new WGXSDType ("date"));
    registerType (new WGXSDType ("dateTime"));
    registerType (new WGXSDType ("duration"));
    registerType (new WGXSDType ("gDay"));
    registerType (new WGXSDType ("gMonth"));
    registerType (new WGXSDType ("gMonthDay"));
    registerType (new WGXSDType ("gYear"));
    registerType (new WGXSDType ("gYearMonth"));
    registerType (new WGXSDType ("time"));
    // numeric
    registerType (new WGXSDType ("byte"));
    registerType (new WGXSDType ("decimal"));
    registerType (new WGXSDType ("int"));
    registerType (new WGXSDType ("integer"));
    registerType (new WGXSDType ("long"));
    registerType (new WGXSDType ("negativeInteger"));
    registerType (new WGXSDType ("nonNegativeInteger"));
    registerType (new WGXSDType ("nonPositiveInteger"));
    registerType (new WGXSDType ("positiveInteger"));
    registerType (new WGXSDType ("short"));
    registerType (new WGXSDType ("unsignedLong"));
    registerType (new WGXSDType ("unsignedInt"));
    registerType (new WGXSDType ("unsignedShort"));
    registerType (new WGXSDType ("unsignedByte"));
    // misc
    registerType (new WGXSDType ("anyURI"));
    registerType (new WGXSDType ("base64Binary"));
    registerType (new WGXSDType ("boolean"));
    registerType (new WGXSDType ("double"));
    registerType (new WGXSDType ("float"));
    registerType (new WGXSDType ("hexBinary"));
    registerType (new WGXSDType ("NOTATION"));
  }

  private WGTypeRegistry ()
  {}

  public static void registerType (@Nonnull final IWGType aType)
  {
    ValueEnforcer.notNull (aType, "Type");

    final String sName = aType.getName ();
    if (s_aTypes.containsKey (sName))
      throw new IllegalArgumentException ("A type with name '" + sName + "' is already registered!");
    s_aTypes.put (sName, aType);
  }

  @Nullable
  public static IWGType getTypeOfName (@Nullable final String sTypeName)
  {
    return s_aTypes.get (sTypeName);
  }
}
