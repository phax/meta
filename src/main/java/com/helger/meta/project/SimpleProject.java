/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
import com.helger.commons.version.Version;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

public class SimpleProject implements IProject
{
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
    m_aLastPublishedVersion = sLastPublishedVersion == null ? null : Version.parse (sLastPublishedVersion);
    m_eMinJDK = eMinJDK;

    // Determine group and artifact from POM
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

  public boolean hasPagesProject ()
  {
    return m_bHasPagesProject;
  }

  public boolean hasWikiProject ()
  {
    return m_bHasWikiProject;
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
