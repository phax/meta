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
package com.helger.meta.tools.wsdlgen.model.type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.StringHelper;

public enum EComplexTypeType
{
  SEQUENCE ("sequence"),
  CHOICE ("choice");

  private final String m_sTagName;

  private EComplexTypeType (@Nonnull @Nonempty final String sTagName)
  {
    m_sTagName = sTagName;
  }

  @Nonnull
  @Nonempty
  public String getTagName ()
  {
    return m_sTagName;
  }

  @Nonnull
  public static EComplexTypeType getFromTagNameOrThrow (@Nullable final String sTagName)
  {
    if (StringHelper.hasText (sTagName))
      for (final EComplexTypeType eType : values ())
        if (eType.getTagName ().equalsIgnoreCase (sTagName))
          return eType;
    throw new IllegalArgumentException ("Unsupported complex type '" + sTagName + "'");
  }
}
