package com.helger.meta;

import javax.annotation.Nonnull;

import com.helger.commons.annotations.Nonempty;

public enum EProject
{
  PH_VALIDATION ("ph-validation"),
  PH_WEBSCOPES ("ph-webscopes");

  private final String m_sProjectName;

  private EProject (@Nonnull @Nonempty final String sProjectName)
  {
    m_sProjectName = sProjectName;
  }

  @Nonnull
  @Nonempty
  public String getProjectName ()
  {
    return m_sProjectName;
  }
}
