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
package com.helger.meta.tools.wsdlgen.model.type;

import java.io.Serializable;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;

/**
 * Represents a single enum entry
 *
 * @author Philip Helger
 */
public class WGEnumEntry implements Serializable
{
  private final String m_sKey;
  private final String m_sDoc;

  public WGEnumEntry (@NonNull @Nonempty final String sKey)
  {
    this (sKey, null);
  }

  public WGEnumEntry (@NonNull @Nonempty final String sKey, @Nullable final String sDoc)
  {
    ValueEnforcer.notEmpty (sKey, "Key");
    m_sKey = sKey;
    m_sDoc = sDoc;
  }

  @NonNull
  @Nonempty
  public String getKey ()
  {
    return m_sKey;
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
}
