/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

public enum EHostingPlatform implements IHasID <String>
{
  GITHUB ("github", "github.com"),
  GITLAB ("gitlab", "gitlab.com");

  private final String m_sID;
  private final String m_sDomain;

  EHostingPlatform (@Nonnull @Nonempty final String sID, @Nonnull @Nonempty final String sDomain)
  {
    m_sID = sID;
    m_sDomain = sDomain;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getDomain ()
  {
    return m_sDomain;
  }

  @Nullable
  public static EHostingPlatform getFromIDOrDefault (@Nullable final String sID, @Nullable final EHostingPlatform eDefault)
  {
    return EnumHelper.getFromIDOrDefault (EHostingPlatform.class, sID, eDefault);
  }
}
