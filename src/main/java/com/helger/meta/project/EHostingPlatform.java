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

  private EHostingPlatform (@Nonnull @Nonempty final String sID, @Nonnull @Nonempty final String sDomain)
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
  public static EHostingPlatform getFromIDOrDefault (@Nullable final String sID,
                                                     @Nullable final EHostingPlatform eDefault)
  {
    return EnumHelper.getFromIDOrDefault (EHostingPlatform.class, sID, eDefault);
  }
}
