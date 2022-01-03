/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.ETriState;
import com.helger.commons.string.StringHelper;

public class WGTypeDef implements Serializable
{
  private final IWGType m_aType;
  private String m_sDoc;
  private String m_sMin;
  private String m_sMax;
  private String m_sDefault;
  private boolean m_bOptional = false;

  public WGTypeDef (@Nonnull final IWGType aType)
  {
    m_aType = ValueEnforcer.notNull (aType, "Type");
  }

  @Nonnull
  public IWGType getType ()
  {
    return m_aType;
  }

  public void setDocumentation (@Nullable final String sDoc)
  {
    m_sDoc = sDoc;
  }

  public boolean hasDocumentation ()
  {
    return StringHelper.hasText (m_sDoc);
  }

  @Nullable
  public String getDocumentation ()
  {
    return m_sDoc;
  }

  public void setMin (@Nullable final String sMin)
  {
    m_sMin = sMin;
  }

  public boolean hasMin ()
  {
    return StringHelper.hasText (m_sMin);
  }

  @Nullable
  public String getMin ()
  {
    return m_sMin;
  }

  public void setMax (@Nullable final String sMax)
  {
    m_sMax = sMax;
  }

  public boolean hasMax ()
  {
    return StringHelper.hasText (m_sMax);
  }

  @Nullable
  public String getMax ()
  {
    return m_sMax;
  }

  public void setDefault (@Nullable final String sDefault)
  {
    m_sDefault = sDefault;
    if (StringHelper.hasText (sDefault))
      m_bOptional = true;
  }

  public boolean hasDefault ()
  {
    return StringHelper.hasText (m_sDefault);
  }

  @Nullable
  public String getDefault ()
  {
    return m_sDefault;
  }

  public void setOptional (@Nonnull final ETriState eOptional)
  {
    ValueEnforcer.notNull (eOptional, "Optional");

    if (eOptional.isDefined ())
    {
      if (eOptional.isFalse () && StringHelper.hasText (m_sDefault))
        throw new IllegalArgumentException ("Attributes with a default value must be optional");
      m_bOptional = eOptional.isTrue ();
    }
  }

  public boolean isOptional ()
  {
    return m_bOptional;
  }
}
