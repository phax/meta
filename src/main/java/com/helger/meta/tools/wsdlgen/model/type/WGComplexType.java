/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.string.ToStringGenerator;

public class WGComplexType extends AbstractWGAssembledType
{
  private EComplexTypeType m_eType = EComplexTypeType.SEQUENCE;
  private final ICommonsOrderedMap <String, WGTypeDef> m_aElements = new CommonsLinkedHashMap <> ();

  public WGComplexType (@Nonnull final String sNamespace, @Nonnull @Nonempty final String sName)
  {
    super (sNamespace, sName);
  }

  public final boolean isSimple ()
  {
    return false;
  }

  public final boolean isComplex ()
  {
    return true;
  }

  public void setType (@Nonnull final EComplexTypeType eType)
  {
    m_eType = ValueEnforcer.notNull (eType, "Type");
  }

  @Nonnull
  public EComplexTypeType getType ()
  {
    return m_eType;
  }

  @Override
  public boolean hasChildren ()
  {
    return super.hasChildren () || m_aElements.isNotEmpty ();
  }

  public void addChildElement (@Nonnull @Nonempty final String sName, @Nonnull final WGTypeDef aTypeDef)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notNull (aTypeDef, "TypeDef");

    if (m_aElements.containsKey (sName))
      throw new IllegalArgumentException ("Child with name already contained: '" + sName + "'");
    m_aElements.put (sName, aTypeDef);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, WGTypeDef> getAllChildElements ()
  {
    return m_aElements.getClone ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("type", m_eType).append ("elements", m_aElements).getToString ();
  }
}
