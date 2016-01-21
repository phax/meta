/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.version.Version;

public class SimpleProject implements IProject
{
  public static final String EXTENSION_PAGES_PROJECT = ".pages";
  public static final String EXTENSION_WIKI_PROJECT = ".wiki";

  private final IProject m_aParentProject;
  private final String m_sProjectName;
  private final EProjectType m_eProjectType;
  private final File m_aBaseDir;
  private final String m_sFullBaseDirName;
  private final boolean m_bIsDeprecated;
  private final boolean m_bHasPagesProject;
  private final boolean m_bHasWikiProject;
  private final String m_sLastPublishedVersion;
  private final Version m_aLastPublishedVersion;
  private final EJDK m_eMinJDK;
  private final String m_sMavenGroupID;
  private final String m_sMavenArtifactID;

  public SimpleProject (@Nullable final IProject aParentProject,
                        @Nonnull @Nonempty final String sProjectName,
                        @Nonnull final EProjectType eProjectType,
                        @Nonnull final File aBaseDir,
                        @Nonnull final EIsDeprecated eIsDeprecated,
                        @Nonnull final EHasPages eHasPagesProject,
                        @Nonnull final EHasWiki eHasWikiProject,
                        @Nullable final String sLastPublishedVersion,
                        @Nonnull final EJDK eMinJDK)
  {
    ValueEnforcer.notEmpty (sProjectName, "ProjectName");
    ValueEnforcer.notNull (eProjectType, "ProjectType");
    ValueEnforcer.notNull (aBaseDir, "BaseDir");
    ValueEnforcer.notNull (eIsDeprecated, "IsDeprecated");
    ValueEnforcer.notNull (eHasPagesProject, "HasPagesProject");
    ValueEnforcer.notNull (eHasWikiProject, "HasWikiProject");
    ValueEnforcer.notNull (eMinJDK, "MinJDK");

    m_aParentProject = aParentProject;
    m_sProjectName = sProjectName;
    m_eProjectType = eProjectType;
    m_aBaseDir = aBaseDir;
    if (!m_aBaseDir.exists () && eIsDeprecated.isFalse ())
      throw new IllegalStateException ("Project base directory does not exist: " + m_aBaseDir);
    m_sFullBaseDirName = (aParentProject != null ? aParentProject.getFullBaseDirName () + "/" : "") +
                         aBaseDir.getName ();
    m_bIsDeprecated = eIsDeprecated.isTrue ();
    m_bHasPagesProject = eHasPagesProject.isTrue ();
    m_bHasWikiProject = eHasWikiProject.isTrue ();
    m_sLastPublishedVersion = sLastPublishedVersion;
    m_aLastPublishedVersion = sLastPublishedVersion == null ? null : new Version (sLastPublishedVersion);
    m_eMinJDK = eMinJDK;

    String sGroupID = null;
    String sArtifactID = null;
    final IMicroDocument aDoc = MicroReader.readMicroXML (getPOMFile ());
    if (aDoc != null)
    {
      final IMicroElement eParent = aDoc.getDocumentElement ().getFirstChildElement ("parent");
      IMicroElement eGroupID = aDoc.getDocumentElement ().getFirstChildElement ("groupId");
      if (eGroupID == null && eParent != null)
        eGroupID = eParent.getFirstChildElement ("groupId");
      if (eGroupID != null)
        sGroupID = eGroupID.getTextContentTrimmed ();
      if (sGroupID == null)
        throw new IllegalStateException ("Failed to resolve Maven groupId in " + sProjectName);

      IMicroElement eArtifactID = aDoc.getDocumentElement ().getFirstChildElement ("artifactId");
      if (eArtifactID == null && eParent != null)
        eArtifactID = eParent.getFirstChildElement ("artifactId");
      if (eArtifactID != null)
        sArtifactID = eArtifactID.getTextContentTrimmed ();
      if (sArtifactID == null)
        throw new IllegalStateException ("Failed to resolve Maven artifactId in " + sProjectName);
      m_sMavenGroupID = sGroupID;
      m_sMavenArtifactID = sArtifactID;
    }
    else
    {
      // For deprecated projects
      m_sMavenGroupID = eProjectType == EProjectType.MAVEN_PLUGIN ? "com.helger.maven" : "com.helger";
      m_sMavenArtifactID = m_sProjectName;
    }
  }

  public boolean isBuildInProject ()
  {
    return false;
  }

  @Nullable
  public IProject getParentProject ()
  {
    return m_aParentProject;
  }

  public boolean isNestedProject ()
  {
    return m_aParentProject != null;
  }

  @Nonnull
  @Nonempty
  public String getProjectName ()
  {
    return m_sProjectName;
  }

  @Nonnull
  public EProjectType getProjectType ()
  {
    return m_eProjectType;
  }

  @Nonnull
  public File getBaseDir ()
  {
    return m_aBaseDir;
  }

  @Nonnull
  @Nonempty
  public String getFullBaseDirName ()
  {
    return m_sFullBaseDirName;
  }

  @Nonnull
  public final File getPOMFile ()
  {
    return new File (m_aBaseDir, "pom.xml");
  }

  @Nonnull
  public EJDK getMinimumJDKVersion ()
  {
    return m_eMinJDK;
  }

  @Nonempty
  public String getMavenGroupID ()
  {
    return m_sMavenGroupID;
  }

  @Nonnull
  @Nonempty
  public String getMavenArtifactID ()
  {
    return m_sMavenArtifactID;
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
    return m_sProjectName + EXTENSION_PAGES_PROJECT;
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
    return m_sProjectName + EXTENSION_WIKI_PROJECT;
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
