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
package com.helger.meta.translation;

import java.io.File;
import java.util.Map;

import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeCalculator;
import com.helger.base.state.ESuccess;
import com.helger.base.string.StringHex;
import com.helger.collection.commons.ICommonsSortedMap;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.serialize.MicroWriter;

public final class StringTableSerializer
{
  private StringTableSerializer ()
  {}

  @NonNull
  public static IMicroElement getStringTableAsXML (@NonNull final StringTable aST)
  {
    ValueEnforcer.notNull (aST, "ST");
    final IMicroElement eRoot = new MicroElement ("translationitems");
    eRoot.setAttribute ("count", aST.size ());

    int nHashCode = HashCodeCalculator.INITIAL_HASHCODE;

    for (final Map.Entry <String, ICommonsSortedMap <String, String>> aEntry : aST.directGetMap ().entrySet ())
    {
      final IMicroElement eItem = eRoot.addElement ("item");

      final String sID = aEntry.getKey ();
      eItem.setAttribute ("id", sID);
      nHashCode = HashCodeCalculator.append (nHashCode, sID);

      for (final Map.Entry <String, String> aEntry2 : aEntry.getValue ().entrySet ())
      {
        // Locale name as element name
        final String sLocale = aEntry2.getKey ();
        final IMicroElement eText = eItem.addElement (sLocale);
        nHashCode = HashCodeCalculator.append (nHashCode, sLocale);

        // Main text
        final String sText = aEntry2.getValue ();
        eText.addText (sText);
        nHashCode = HashCodeCalculator.append (nHashCode, sText);
      }
    }

    // Small consistency check
    eRoot.setAttribute ("hashcode", StringHex.getHexStringLeadingZero (nHashCode & 0xffffffffL, 8));
    return eRoot;
  }

  @NonNull
  public static ESuccess writeStringTableAsXML (@NonNull final File aFile, @NonNull final StringTable aST)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (aST, "ST");

    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.addChild (getStringTableAsXML (aST));

    return MicroWriter.writeToFile (aDoc, aFile);
  }
}
