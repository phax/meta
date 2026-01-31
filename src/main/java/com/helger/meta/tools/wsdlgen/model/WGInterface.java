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
package com.helger.meta.tools.wsdlgen.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.meta.tools.wsdlgen.model.type.IWGType;
import com.helger.meta.tools.wsdlgen.model.type.WGTypeDef;
import com.helger.meta.tools.wsdlgen.model.type.WGTypeRegistry;

@NotThreadSafe
public class WGInterface
{
  private final String m_sName;
  private final String m_sNamespace;
  private final String m_sEndpoint;
  private String m_sDoc;
  private final ICommonsOrderedMap <String, WGTypeDef> m_aTypes = new CommonsLinkedHashMap <> ();
  private final ICommonsOrderedMap <String, WGMethod> m_aMethods = new CommonsLinkedHashMap <> ();

  public WGInterface (@NonNull @Nonempty final String sName,
                      @NonNull @Nonempty final String sNamespace,
                      @Nullable final String sEndpoint)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notEmpty (sNamespace, "Namespace");
    m_sName = sName;
    m_sNamespace = sNamespace;
    m_sEndpoint = sEndpoint;
  }

  @NonNull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  @NonNull
  @Nonempty
  public String getNamespace ()
  {
    return m_sNamespace;
  }

  public boolean hasEndpoint ()
  {
    return StringHelper.isNotEmpty (m_sEndpoint);
  }

  @Nullable
  public String getEndpoint ()
  {
    return m_sEndpoint;
  }

  public void setDocumentation (@Nullable final String sDoc)
  {
    m_sDoc = sDoc;
  }

  public boolean hasDocumentation ()
  {
    return StringHelper.isNotEmpty (m_sDoc);
  }

  @Nullable
  public String getDocumentation ()
  {
    return m_sDoc;
  }

  @NonNull
  public final EChange addType (@NonNull final WGTypeDef aType)
  {
    ValueEnforcer.notNull (aType, "Type");

    final String sName = aType.getType ().getName ();
    if (m_aTypes.containsKey (sName))
      return EChange.UNCHANGED;
    m_aTypes.put (sName, aType);
    return EChange.CHANGED;
  }

  @Nullable
  public IWGType getTypeOfName (@Nullable final String sTypeName)
  {
    final WGTypeDef ret = m_aTypes.get (sTypeName);
    if (ret != null)
      return ret.getType ();
    // Global type?
    return WGTypeRegistry.getTypeOfName (sTypeName);
  }

  public boolean hasTypes ()
  {
    return m_aTypes.isNotEmpty ();
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, WGTypeDef> getAllTypes ()
  {
    return m_aTypes.getClone ();
  }

  @NonNull
  public final EChange addMethod (@NonNull final WGMethod aMethod)
  {
    ValueEnforcer.notNull (aMethod, "Method");

    final String sName = aMethod.getName ();
    if (m_aMethods.containsKey (sName))
      return EChange.UNCHANGED;
    m_aMethods.put (sName, aMethod);
    return EChange.CHANGED;
  }

  @Nullable
  public WGMethod getMethodOfName (@Nullable final String sMethodName)
  {
    return m_aMethods.get (sMethodName);
  }

  public boolean hasMethods ()
  {
    return m_aMethods.isNotEmpty ();
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <WGMethod> getAllMethods ()
  {
    return m_aMethods.copyOfValues ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("name", m_sName)
                                       .append ("namespace", m_sNamespace)
                                       .append ("endpoint", m_sEndpoint)
                                       .append ("types", m_aTypes)
                                       .append ("methods", m_aMethods)
                                       .getToString ();
  }
}
