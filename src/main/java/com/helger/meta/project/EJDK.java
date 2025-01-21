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
package com.helger.meta.project;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.EnumHelper;

public enum EJDK
{
  JDK8 (8),
  JDK11 (11),
  JDK17 (17),
  JDK21 (21);

  private final int m_nMajor;

  EJDK (@Nonnegative final int nMajor)
  {
    m_nMajor = nMajor;
  }

  @Nonnegative
  public int getMajor ()
  {
    return m_nMajor;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    if (m_nMajor > 8)
      return "JDK " + m_nMajor;
    return "JDK 1." + m_nMajor;
  }

  public boolean isCompatibleToRuntimeVersion (@Nonnull final EJDK eRTVersion)
  {
    return m_nMajor <= eRTVersion.m_nMajor;
  }

  public boolean isAtLeast9 ()
  {
    return m_nMajor >= 9;
  }

  @Nullable
  public static EJDK getFromMajorOrNull (final int nMajor)
  {
    return EnumHelper.findFirst (EJDK.class, e -> e.m_nMajor == nMajor);
  }
}
