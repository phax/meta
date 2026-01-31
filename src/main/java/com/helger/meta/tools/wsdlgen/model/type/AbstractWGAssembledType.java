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
package com.helger.meta.tools.wsdlgen.model.type;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsOrderedMap;

public abstract class AbstractWGAssembledType implements IWGType
{
  private final String m_sNamespace;
  private final String m_sName;
  private final ICommonsOrderedMap <String, WGTypeDef> m_aAttributes = new CommonsLinkedHashMap <> ();

  public AbstractWGAssembledType (@NonNull final String sNamespace, @NonNull @Nonempty final String sName)
  {
    ValueEnforcer.notNull (sNamespace, "Namespace");
    ValueEnforcer.notEmpty (sName, "Name");
    m_sNamespace = sNamespace;
    m_sName = sName;
  }

  @NonNull
  public String getNamespace ()
  {
    return m_sNamespace;
  }

  @NonNull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  public void addChildAttribute (@NonNull @Nonempty final String sName, @NonNull final WGTypeDef aTypeDef)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notNull (aTypeDef, "TypeDef");
    if (m_aAttributes.containsKey (sName))
      throw new IllegalArgumentException ("Child with name already contained: '" + sName + "'");
    m_aAttributes.put (sName, aTypeDef);
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, WGTypeDef> getAllChildAttributes ()
  {
    return m_aAttributes.getClone ();
  }

  public boolean hasChildren ()
  {
    return m_aAttributes.isNotEmpty ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("namespace", m_sNamespace)
                                       .append ("name", m_sName)
                                       .append ("attributes", m_aAttributes)
                                       .getToString ();
  }
}
