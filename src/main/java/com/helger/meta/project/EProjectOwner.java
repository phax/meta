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
package com.helger.meta.project;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.string.StringHelper;
import com.helger.meta.CMeta;

public enum EProjectOwner
{
  DEFAULT_PROJECT_OWNER ("phax", "git"),
  PROJECT_OWNER_HELGER_IT ("Helger-IT", "git"),
  PROJECT_OWNER_AUSTRIAPRO ("austriapro", "git-austriapro"),
  @Deprecated (forRemoval = false)
  PROJECT_OWNER_TOOP("TOOP4EU", "git-toop"),
  PROJECT_OWNER_CONNECTING_EUROPE ("ConnectingEurope", "git-en16931"),
  PROJECT_ECOSIO_PH ("ecosio-ph", "git-ecosio");

  private final String m_sGitOrgaName;
  private final String m_sLocalGitDir;

  EProjectOwner (@NonNull @Nonempty final String sName, @NonNull @Nonempty final String sGitDir)
  {
    m_sGitOrgaName = sName;
    m_sLocalGitDir = sGitDir;
  }

  @NonNull
  @Nonempty
  public String getGitOrgaName ()
  {
    return m_sGitOrgaName;
  }

  @NonNull
  public File getLocalGitDir ()
  {
    try
    {
      return new File (CMeta.GIT_BASE_DIR, "../" + m_sLocalGitDir).getCanonicalFile ();
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  @Nullable
  public static EProjectOwner getFromGitOrgaOrNull (@Nullable final String s)
  {
    if (StringHelper.isNotEmpty (s))
      for (final EProjectOwner e : values ())
        if (s.equals (e.m_sGitOrgaName))
          return e;
    return null;
  }
}
