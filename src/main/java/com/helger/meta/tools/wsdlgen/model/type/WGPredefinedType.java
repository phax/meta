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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base class for simple types that are predefined (like XSD types).
 *
 * @author Philip Helger
 */
public class WGPredefinedType implements IWGType
{
  private final String m_sNamespace;
  private final String m_sName;
  private final boolean m_bSimple;

  public WGPredefinedType (@Nonnull final String sNamespace,
                           @Nonnull @Nonempty final String sName,
                           final boolean bSimple)
  {
    ValueEnforcer.notNull (sNamespace, "Namespace");
    ValueEnforcer.notEmpty (sName, "Name");
    m_sNamespace = sNamespace;
    m_sName = sName;
    m_bSimple = bSimple;
  }

  @Nonnull
  @Nonempty
  public final String getNamespace ()
  {
    return m_sNamespace;
  }

  @Nonnull
  @Nonempty
  public final String getName ()
  {
    return m_sName;
  }

  public final boolean isSimple ()
  {
    return m_bSimple;
  }

  public final boolean isComplex ()
  {
    return !m_bSimple;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("namespace", m_sNamespace)
                                       .append ("name", m_sName)
                                       .append ("simple", m_bSimple)
                                       .getToString ();
  }
}
