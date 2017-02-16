/**
 * Copyright (C) 2013-2017 Philip Helger
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

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsLinkedHashMap;
import com.helger.commons.collection.ext.ICommonsOrderedMap;
import com.helger.commons.string.ToStringGenerator;
import com.helger.meta.tools.wsdlgen.model.type.IWGType;

public class WGMethod
{
  private final String m_sName;
  private ICommonsOrderedMap <String, IWGType> m_aInput;
  private ICommonsOrderedMap <String, IWGType> m_aOutput;
  private ICommonsOrderedMap <String, IWGType> m_aFault;

  public WGMethod (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    m_sName = sName;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  public void markHavingInput ()
  {
    m_aInput = new CommonsLinkedHashMap <> ();
  }

  public boolean isInput ()
  {
    return m_aInput != null;
  }

  public void addInputParam (@Nonnull @Nonempty final String sParamName, @Nonnull final IWGType aParamType)
  {
    if (m_aInput == null)
      throw new IllegalStateException ("No input!");
    if (m_aInput.containsKey (sParamName))
      throw new IllegalArgumentException ("Input with name '" + sParamName + "' already contained");
    m_aInput.put (sParamName, aParamType);
  }

  public boolean hasInputParams ()
  {
    return m_aInput != null && m_aInput.isNotEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, IWGType> getAllInputs ()
  {
    return new CommonsLinkedHashMap <> (m_aInput);
  }

  public void markHavingOutput ()
  {
    m_aOutput = new CommonsLinkedHashMap <> ();
  }

  public boolean isOutput ()
  {
    return m_aOutput != null;
  }

  public void addOutputParam (@Nonnull @Nonempty final String sReturnName, @Nonnull final IWGType aReturnType)
  {
    if (m_aOutput == null)
      throw new IllegalStateException ("No output!");
    if (m_aOutput.containsKey (sReturnName))
      throw new IllegalArgumentException ("Output with name '" + sReturnName + "' already contained");
    m_aOutput.put (sReturnName, aReturnType);
  }

  public boolean hasOutputParams ()
  {
    return m_aOutput != null && m_aOutput.isNotEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, IWGType> getAllOutputs ()
  {
    return new CommonsLinkedHashMap <> (m_aOutput);
  }

  public void markHavingFault ()
  {
    m_aFault = new CommonsLinkedHashMap <> ();
  }

  public boolean isFault ()
  {
    return m_aFault != null;
  }

  public void addFaultParam (@Nonnull @Nonempty final String sReturnName, @Nonnull final IWGType aReturnType)
  {
    if (m_aFault == null)
      throw new IllegalStateException ("No output!");
    if (m_aFault.containsKey (sReturnName))
      throw new IllegalArgumentException ("Fault with name '" + sReturnName + "' already contained");
    m_aFault.put (sReturnName, aReturnType);
  }

  public boolean hasFaultParams ()
  {
    return m_aFault != null && m_aFault.isNotEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, IWGType> getAllFaults ()
  {
    return new CommonsLinkedHashMap <> (m_aFault);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("name", m_sName)
                                       .append ("input", m_aInput)
                                       .append ("output", m_aOutput)
                                       .append ("fault", m_aFault)
                                       .getToString ();
  }
}
