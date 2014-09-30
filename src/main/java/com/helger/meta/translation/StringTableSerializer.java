package com.helger.meta.translation;

import java.io.File;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hash.HashCodeCalculator;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.microdom.impl.MicroElement;
import com.helger.commons.microdom.serialize.MicroWriter;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;

public final class StringTableSerializer
{
  private StringTableSerializer ()
  {}

  @Nonnull
  public static IMicroElement getStringTableAsXML (@Nonnull final StringTable aST)
  {
    ValueEnforcer.notNull (aST, "ST");
    final IMicroElement eRoot = new MicroElement ("translationitems");
    eRoot.setAttribute ("count", aST.size ());

    int nHashCode = HashCodeGenerator.INITIAL_HASHCODE;

    for (final Map.Entry <String, Map <String, String>> aEntry : aST.directGetMap ().entrySet ())
    {
      final IMicroElement eItem = eRoot.appendElement ("item");

      final String sID = aEntry.getKey ();
      eItem.setAttribute ("id", sID);
      nHashCode = HashCodeCalculator.append (nHashCode, sID);

      for (final Map.Entry <String, String> aEntry2 : aEntry.getValue ().entrySet ())
      {
        // Locale name as element name
        final String sLocale = aEntry2.getKey ();
        final IMicroElement eText = eItem.appendElement (sLocale);
        nHashCode = HashCodeCalculator.append (nHashCode, sLocale);

        // Main text
        final String sText = aEntry2.getValue ();
        eText.appendText (sText);
        nHashCode = HashCodeCalculator.append (nHashCode, sText);
      }
    }

    // Small consistency check
    eRoot.setAttribute ("hashcode", StringHelper.getHexStringLeadingZero (nHashCode & 0xffffffffl, 8));
    return eRoot;
  }

  @Nonnull
  public static ESuccess writeStringTableAsXML (@Nonnull final File aFile, @Nonnull final StringTable aST)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (aST, "ST");

    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.appendChild (getStringTableAsXML (aST));

    return MicroWriter.writeToFile (aDoc, aFile);
  }
}
