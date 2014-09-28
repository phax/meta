package com.helger.meta.asm.translation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ICloneable;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.annotations.ReturnsMutableObject;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a single string table, mapping and ID to the different texts.
 *
 * @author philip
 */
@NotThreadSafe
public final class StringTable implements ICloneable <StringTable>
{
  public static final boolean DEFAULT_WARN_ON_DUPLICATED_IDS = false;
  private static final Logger s_aLogger = LoggerFactory.getLogger (StringTable.class);

  // Map <StringID, Map <Locale-as-String, Text>>
  private final Map <String, Map <String, String>> m_aMap = new TreeMap <String, Map <String, String>> ();

  // State-only
  private final boolean m_bWarnOnDuplicateIDs;

  public StringTable ()
  {
    this (DEFAULT_WARN_ON_DUPLICATED_IDS);
  }

  public StringTable (final boolean bWarnOnDuplicateIDs)
  {
    m_bWarnOnDuplicateIDs = bWarnOnDuplicateIDs;
  }

  private StringTable (@Nonnull final StringTable rhs)
  {
    this (rhs.m_bWarnOnDuplicateIDs);
    m_aMap.putAll (rhs.m_aMap);
  }

  public void addAll (@Nonnull final StringTable aST)
  {
    for (final Map.Entry <String, Map <String, String>> aEntry : aST.m_aMap.entrySet ())
      setTexts (aEntry.getKey (), aEntry.getValue ());
  }

  @Nullable
  public Map <String, String> getTexts (@Nullable final String sID)
  {
    return m_aMap.get (sID);
  }

  @Nullable
  public String getText (@Nullable final String sID, @Nullable final String sLocale)
  {
    final Map <String, String> aMap = m_aMap.get (sID);
    return aMap == null ? null : aMap.get (sLocale);
  }

  public void setTexts (@Nonnull final String sID, @Nonnull final Map <String, String> aTexts)
  {
    for (final Map.Entry <String, String> aText : aTexts.entrySet ())
      setText (sID, aText.getKey (), aText.getValue ());
  }

  public void setText (@Nonnull final String sID, @Nonnull final TextInLocale aTIL)
  {
    setText (sID, aTIL.getLocale (), aTIL.getText ());
  }

  @Nonnull
  public EChange setText (@Nonnull final String sID, @Nonnull final String sLocale, @Nonnull final String sNewText)
  {
    final Map <String, String> aMap = m_aMap.get (sID);
    if (m_bWarnOnDuplicateIDs && aMap != null && aMap.containsKey (sLocale))
    {
      final String sExistingText = aMap.get (sLocale);
      if (!sExistingText.equals (sNewText))
        throw new IllegalArgumentException ("A text for ID '" +
                                            sID +
                                            "' in locale '" +
                                            sLocale +
                                            "' is already present and differs: '" +
                                            sExistingText +
                                            "' new: '" +
                                            sNewText +
                                            "'");
      s_aLogger.warn ("A text for ID '" + sID + "' in locale '" + sLocale + "' is already present!");
    }

    return overwriteText (sID, sLocale, sNewText);
  }

  @Nonnull
  public EChange overwriteText (@Nonnull final String sID, @Nonnull final String sLocale, @Nonnull final String sText)
  {
    if (sID == null)
      throw new NullPointerException ("ID");
    if (sLocale == null)
      throw new NullPointerException ("locale");
    if (sText == null)
      throw new NullPointerException ("text");

    Map <String, String> aMap = m_aMap.get (sID);
    if (aMap == null)
    {
      aMap = new TreeMap <String, String> ();
      m_aMap.put (sID, aMap);
    }
    return EChange.valueOf (!EqualsUtils.equals (aMap.put (sLocale, sText), sText));
  }

  public boolean containsID (@Nullable final String sID)
  {
    return m_aMap.containsKey (sID);
  }

  @Nonnull
  @ReturnsMutableObject (reason = "Performance")
  public Set <String> getAllIDs ()
  {
    return m_aMap.keySet ();
  }

  @Nonnegative
  public int size ()
  {
    return m_aMap.size ();
  }

  public boolean isEmpty ()
  {
    return m_aMap.isEmpty ();
  }

  @Nonnull
  @ReturnsMutableObject (reason = "Performance")
  public Map <String, Map <String, String>> directGetMap ()
  {
    return m_aMap;
  }

  public void findAllIDsContainingText (@Nonnull final TextInLocale aSearchText, @Nonnull final Set <String> aIDCont)
  {
    for (final Map.Entry <String, Map <String, String>> aEntry : m_aMap.entrySet ())
    {
      // Get current text in the search languages
      final String sText = aEntry.getValue ().get (aSearchText.getLocale ());
      if (sText != null && sText.contains (aSearchText.getText ()))
        aIDCont.add (aEntry.getKey ());
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> findAllIDsContainingText (@Nonnull final TextInLocale aSearchText)
  {
    final Set <String> ret = new HashSet <String> ();
    findAllIDsContainingText (aSearchText, ret);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllUsedLocales ()
  {
    final Set <String> ret = new HashSet <String> ();
    for (final Map <String, String> aPerIDMap : m_aMap.values ())
      ret.addAll (aPerIDMap.keySet ());
    return ret;
  }

  /**
   * Returns a map from ID to text in the specified locale
   *
   * @param sLocale
   *        The locale to use
   * @return A non-<code>null</code> map
   */
  @Nonnull
  @ReturnsMutableCopy
  public Map <String, String> getAllTextsInLocale (final String sLocale)
  {
    // ID to text map
    final Map <String, String> ret = new HashMap <String, String> ();
    for (final Map.Entry <String, Map <String, String>> aEntry : m_aMap.entrySet ())
    {
      final String sText = aEntry.getValue ().get (sLocale);
      if (sText != null)
        ret.put (aEntry.getKey (), sText);
    }
    return ret;
  }

  @Nonnull
  public EChange removeID (final String sID)
  {
    return EChange.valueOf (m_aMap.remove (sID) != null);
  }

  @Nonnull
  public StringTable getClone ()
  {
    return new StringTable (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof StringTable))
      return false;
    final StringTable rhs = (StringTable) o;
    return m_aMap.equals (rhs.m_aMap);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aMap).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("map", m_aMap).toString ();
  }
}
