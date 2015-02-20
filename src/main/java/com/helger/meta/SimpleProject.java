package com.helger.meta;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.version.Version;

public class SimpleProject implements IProject
{
  private final String m_sProjectName;
  private final File m_aBaseDir;
  private final EProjectType m_eProjectType;
  private final boolean m_bIsDeprecated;
  private final boolean m_bHasPagesProject;
  private final boolean m_bHasWikiProject;
  private final String m_sLastPublishedVersion;
  private final Version m_aLastPublishedVersion;

  public SimpleProject (@Nonnull @Nonempty final String sProjectName,
                        @Nonnull final EProjectType eProjectType,
                        @Nonnull final EIsDeprecated eIsDeprecated,
                        @Nonnull final EHasPages eHasPagesProject,
                        @Nonnull final EHasWiki eHasWikiProject,
                        @Nullable final String sLastPublishedVersion)
  {
    m_sProjectName = sProjectName;
    m_eProjectType = eProjectType;
    m_aBaseDir = new File (CMeta.GIT_BASE_DIR, sProjectName);
    if (!m_aBaseDir.exists ())
      throw new IllegalStateException ("Project base directory does not exist: " + m_aBaseDir);
    m_bIsDeprecated = eIsDeprecated.isTrue ();
    m_bHasPagesProject = eHasPagesProject.isTrue ();
    m_bHasWikiProject = eHasWikiProject.isTrue ();
    m_sLastPublishedVersion = sLastPublishedVersion;
    m_aLastPublishedVersion = sLastPublishedVersion == null ? null : new Version (sLastPublishedVersion);
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

  @Nonnull
  public File getPOMFile ()
  {
    return new File (m_aBaseDir, "pom.xml");
  }

  @Nonnull
  public EProjectType getProjectType ()
  {
    return m_eProjectType;
  }

  public boolean isDeprecated ()
  {
    return m_bIsDeprecated;
  }

  /**
   * @return <code>true</code> if this project has the auto-generated
   *         <code>gh-pages</code> branch.
   */
  public boolean hasPagesProject ()
  {
    return m_bHasPagesProject;
  }

  @Nonnull
  @Nonempty
  public String getPagesProjectName ()
  {
    return m_sProjectName + CMeta.EXTENSION_PAGES_PROJECT;
  }

  /**
   * @return <code>true</code> if this project has a special Wiki project.
   */
  public boolean hasWikiProject ()
  {
    return m_bHasWikiProject;
  }

  @Nonnull
  @Nonempty
  public String getWikiProjectName ()
  {
    return m_sProjectName + CMeta.EXTENSION_WIKI_PROJECT;
  }

  /**
   * @return <code>true</code> if this project had at least one release,
   *         <code>false</code> if not.
   */
  public boolean isPublished ()
  {
    return m_sLastPublishedVersion != null;
  }

  @Nullable
  public String getLastPublishedVersionString ()
  {
    return m_sLastPublishedVersion;
  }

  @Nullable
  public Version getLastPublishedVersion ()
  {
    return m_aLastPublishedVersion;
  }

  public int compareTo (@Nonnull final IProject aProject)
  {
    return m_sProjectName.compareTo (aProject.getProjectName ());
  }
}
