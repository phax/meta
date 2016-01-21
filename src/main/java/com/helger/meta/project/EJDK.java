package com.helger.meta.project;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.lang.EnumHelper;

public enum EJDK
{
  JDK6 (6),
  JDK7 (7),
  JDK8 (8);

  private final int m_nMajor;

  private EJDK (final int nMajor)
  {
    m_nMajor = nMajor;
  }

  public int getMajor ()
  {
    return m_nMajor;
  }

  public String getDisplayName ()
  {
    return "JDK 1." + m_nMajor;
  }

  public boolean isCompatibleToRuntimeVersion (@Nonnull final EJDK eRTVersion)
  {
    return m_nMajor <= eRTVersion.m_nMajor;
  }

  @Nullable
  public static EJDK getFromMajorOrNull (final int nMajor)
  {
    return EnumHelper.findFirst (EJDK.class, e -> e.m_nMajor == nMajor);
  }
}
