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

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.meta.tools.wsdlgen.model.type.IWGType;

@NotThreadSafe
public class WGMethod
{
  private final String m_sName;
  private ICommonsOrderedMap <String, IWGType> m_aInput;
  private ICommonsOrderedMap <String, IWGType> m_aOutput;
  private ICommonsOrderedMap <String, IWGType> m_aFault;

  public WGMethod (@NonNull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    m_sName = sName;
  }

  @NonNull
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

  public void addInputParam (@NonNull @Nonempty final String sParamName, @NonNull final IWGType aParamType)
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

  @NonNull
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

  public void addOutputParam (@NonNull @Nonempty final String sReturnName, @NonNull final IWGType aReturnType)
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

  @NonNull
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

  public void addFaultParam (@NonNull @Nonempty final String sReturnName, @NonNull final IWGType aReturnType)
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

  @NonNull
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
