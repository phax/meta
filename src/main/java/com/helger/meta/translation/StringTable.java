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
package com.helger.meta.translation;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.collection.impl.ICommonsSortedMap;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a string table. This is a mapping from ID to texts in different
 * locales.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class StringTable implements ICloneable <StringTable>
{
  public static final boolean DEFAULT_WARN_ON_DUPLICATED_IDS = false;
  private static final Logger LOGGER = LoggerFactory.getLogger (StringTable.class);

  // Map <StringID, Map <Locale-as-String, Text>>
  private final ICommonsSortedMap <String, ICommonsSortedMap <String, String>> m_aMap = new CommonsTreeMap <> ();

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
    for (final Map.Entry <String, ICommonsSortedMap <String, String>> aEntry : aST.m_aMap.entrySet ())
      setTexts (aEntry.getKey (), aEntry.getValue ());
  }

  @Nullable
  public ICommonsSortedMap <String, String> getTexts (@Nullable final String sID)
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
  public EChange setText (@Nonnull final String sID, @Nonnull final Locale aLocale, @Nonnull final String sNewText)
  {
    return setText (sID, aLocale.getLanguage (), sNewText);
  }

  @Nonnull
  public EChange setText (@Nonnull final String sID, @Nonnull @Nonempty final String sLocale, @Nonnull final String sNewText)
  {
    ValueEnforcer.notNull (sID, "ID");
    ValueEnforcer.notEmpty (sLocale, "Locale");
    ValueEnforcer.notNull (sNewText, "NewText");

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
      LOGGER.warn ("A text for ID '" + sID + "' in locale '" + sLocale + "' is already present!");
    }

    return overwriteText (sID, sLocale, sNewText);
  }

  @Nonnull
  public EChange overwriteText (@Nonnull final String sID, @Nonnull @Nonempty final String sLocale, @Nonnull final String sText)
  {
    ValueEnforcer.notNull (sID, "ID");
    ValueEnforcer.notEmpty (sLocale, "Locale");
    ValueEnforcer.notNull (sText, "Text");

    ICommonsSortedMap <String, String> aMap = m_aMap.get (sID);
    if (aMap == null)
    {
      // Tree map for defined order
      aMap = new CommonsTreeMap <> ();
      m_aMap.put (sID, aMap);
    }
    return EChange.valueOf (!EqualsHelper.equals (aMap.put (sLocale, sText), sText));
  }

  public boolean containsID (@Nullable final String sID)
  {
    return m_aMap.containsKey (sID);
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
  @ReturnsMutableObject ("Performance")
  public ICommonsSortedMap <String, ICommonsSortedMap <String, String>> directGetMap ()
  {
    return m_aMap;
  }

  public void findAllIDsContainingText (@Nonnull final TextInLocale aSearchText, @Nonnull final Collection <String> aIDCont)
  {
    for (final Map.Entry <String, ICommonsSortedMap <String, String>> aEntry : m_aMap.entrySet ())
    {
      // Get current text in the search languages
      final String sText = aEntry.getValue ().get (aSearchText.getLocale ());
      if (sText != null && sText.contains (aSearchText.getText ()))
        aIDCont.add (aEntry.getKey ());
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <String> findAllIDsContainingText (@Nonnull final TextInLocale aSearchText)
  {
    final ICommonsSet <String> ret = new CommonsHashSet <> ();
    findAllIDsContainingText (aSearchText, ret);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <String> getAllUsedLocales ()
  {
    final ICommonsSet <String> ret = new CommonsHashSet <> ();
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
  public ICommonsMap <String, String> getAllTextsInLocale (final String sLocale)
  {
    // ID to text map
    final ICommonsMap <String, String> ret = new CommonsHashMap <> ();
    for (final Map.Entry <String, ICommonsSortedMap <String, String>> aEntry : m_aMap.entrySet ())
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
    if (o == null || !getClass ().equals (o.getClass ()))
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
    return new ToStringGenerator (this).append ("map", m_aMap).getToString ();
  }
}
