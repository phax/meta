package com.helger.meta.project;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.Nonempty;

@Immutable
final class ProjectName
{
  private final String m_sProjectName;
  private final String m_sProjectBaseDirName;

  public ProjectName (@Nonnull @Nonempty final String sProjectName)
  {
    this (sProjectName, sProjectName);
  }

  public ProjectName (@Nonnull @Nonempty final String sProjectName, @Nonnull final String sProjectBaseDirName)
  {
    m_sProjectName = sProjectName;
    m_sProjectBaseDirName = sProjectBaseDirName;
  }

  @Nonnull
  @Nonempty
  public String getProjectName ()
  {
    return m_sProjectName;
  }

  @Nonnull
  @Nonempty
  public String getProjectBaseDirName ()
  {
    return m_sProjectBaseDirName;
  }
}
