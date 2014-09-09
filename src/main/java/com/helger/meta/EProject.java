package com.helger.meta;

import java.io.File;

import javax.annotation.Nonnull;

import com.helger.commons.annotations.Nonempty;

/**
 * Defines all the available projects.
 *
 * @author Philip Helger
 */
public enum EProject
{
  AS2_LIB ("as2-lib"),
  AS2_SERVER ("as2-server"),
  BOTANIK_MANAGER ("botanik-manager"),
  PH_PDF_LAYOUT ("ph-pdf-layout"),
  PH_VALIDATION ("ph-validation"),
  PH_WEB ("ph-web"),
  PH_WEBBASICS ("ph-webbasics"),
  PH_WEBSCOPES ("ph-webscopes");

  private final String m_sProjectName;
  private final File m_aBaseDir;

  private EProject (@Nonnull @Nonempty final String sProjectName)
  {
    m_sProjectName = sProjectName;
    m_aBaseDir = new File (CMeta.BASE_PATH, sProjectName);
    if (!m_aBaseDir.exists ())
      throw new IllegalStateException ("Project base directory does not exist: " + m_aBaseDir);
  }

  @Nonnull
  @Nonempty
  public String getProjectName ()
  {
    return m_sProjectName;
  }

  @Nonnull
  public File getBaseDir ()
  {
    return m_aBaseDir;
  }
}
