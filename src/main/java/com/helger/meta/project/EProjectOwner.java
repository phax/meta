package com.helger.meta.project;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.StringHelper;
import com.helger.meta.CMeta;

public enum EProjectOwner
{
  DEFAULT_PROJECT_OWNER ("phax", "git"),
  PROJECT_OWNER_AUSTRIAPRO ("austriapro", "git-austriapro"),
  PROJECT_OWNER_TOOP ("TOOP4EU", "git-toop"),
  PROJECT_OWNER_CENTC434 ("CenPc434", "git-en16931"),
  PROJECT_ECOSIO_PH ("ecosio-ph", "git-ecosio");

  private final String m_sGitOrgaName;
  private final String m_sLocalGitDir;

  EProjectOwner (@Nonnull @Nonempty final String sName, @Nonnull @Nonempty final String sGitDir)
  {
    m_sGitOrgaName = sName;
    m_sLocalGitDir = sGitDir;
  }

  @Nonnull
  @Nonempty
  public String getGitOrgaName ()
  {
    return m_sGitOrgaName;
  }

  @Nonnull
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
    if (StringHelper.hasText (s))
      for (final EProjectOwner e : values ())
        if (s.equals (e.m_sGitOrgaName))
          return e;
    return null;
  }
}
