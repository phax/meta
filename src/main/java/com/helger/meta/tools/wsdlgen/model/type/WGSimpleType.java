/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.ToStringGenerator;

public class WGSimpleType extends AbstractWGAssembledType
{
  private IWGType m_aExtension;
  private IWGType m_aRestriction;
  private ICommonsList <WGEnumEntry> m_aEnumEntries;
  private int m_nMaxLength = CGlobal.ILLEGAL_UINT;

  public WGSimpleType (@Nonnull final String sNamespace, @Nonnull @Nonempty final String sName)
  {
    super (sNamespace, sName);
  }

  public final boolean isSimple ()
  {
    return true;
  }

  public final boolean isComplex ()
  {
    return false;
  }

  @Override
  public void addChildAttribute (@Nonnull @Nonempty final String sName, @Nonnull final WGTypeDef aTypeDef)
  {
    if (m_aRestriction != null)
      throw new IllegalStateException ("Attributes cannot be present if a restriction is set!");
    super.addChildAttribute (sName, aTypeDef);
  }

  public void setExtension (@Nullable final IWGType aExtension)
  {
    if (aExtension != null && m_aRestriction != null)
      throw new IllegalStateException ("Cannot set the extension type " +
                                       aExtension.getName () +
                                       " because the restriction type " +
                                       m_aRestriction.getName () +
                                       " is already set");
    m_aExtension = aExtension;
  }

  @Nullable
  public IWGType getExtension ()
  {
    return m_aExtension;
  }

  public boolean isExtension ()
  {
    return m_aExtension != null;
  }

  public void setRestriction (@Nullable final IWGType aRestriction)
  {
    if (aRestriction != null)
    {
      if (m_aExtension != null)
        throw new IllegalStateException ("Cannot set the restriction type " +
                                         aRestriction.getName () +
                                         " because the extension type " +
                                         m_aExtension.getName () +
                                         " is already set");
      if (hasChildren ())
        throw new IllegalStateException ("Cannot set the restriction type " +
                                         aRestriction.getName () +
                                         " because attributes are already present");
    }
    m_aRestriction = aRestriction;
  }

  @Nullable
  public IWGType getRestriction ()
  {
    return m_aRestriction;
  }

  public boolean isRestriction ()
  {
    return m_aRestriction != null;
  }

  public void setEnumEntries (@Nonnull final ICommonsList <WGEnumEntry> aEnumEntries)
  {
    ValueEnforcer.notEmpty (aEnumEntries, "EnumEntries");

    if (m_aExtension != null)
      throw new IllegalArgumentException ("Enums can not be present when an extension is present!");
    if (m_aRestriction == null)
      m_aRestriction = WGTypeRegistry.getTypeOfName ("string");
    m_aEnumEntries = aEnumEntries.getClone ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <WGEnumEntry> getAllEnumEntries ()
  {
    return CollectionHelper.newList (m_aEnumEntries);
  }

  public boolean hasEnumEntries ()
  {
    return CollectionHelper.isNotEmpty (m_aEnumEntries);
  }

  public void setMaxLength (final int nMaxLength)
  {
    m_nMaxLength = nMaxLength;
  }

  public int getMaxLength ()
  {
    return m_nMaxLength;
  }

  public boolean hasMaxLength ()
  {
    return m_nMaxLength > 0;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("extension", m_aExtension)
                            .append ("restriction", m_aRestriction)
                            .appendIfNotNull ("enumEntries", m_aEnumEntries)
                            .append ("maxLength", m_nMaxLength)
                            .getToString ();
  }
}
