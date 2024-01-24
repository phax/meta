/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.version.Version;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

public class SimpleProject implements IProject
{
  private static final Logger LOGGER = LoggerFactory.getLogger (SimpleProject.class);

  private final EHostingPlatform m_eHostingPlatform;
  private final IProject m_aParentProject;
  private final EProjectOwner m_eProjectOwner;
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
  private final boolean m_bIsGitHubPrivate;
  private final String m_sMavenGroupID;
  private final String m_sMavenArtifactID;

  public SimpleProject (@Nonnull final EHostingPlatform eHostingPlatform,
                        @Nullable final IProject aParentProject,
                        @Nonnull final EProjectOwner eProjectOwner,
                        @Nonnull @Nonempty final String sProjectName,
                        @Nonnull final EProjectType eProjectType,
                        @Nonnull final File aBaseDir,
                        @Nonnull final EIsDeprecated eIsDeprecated,
                        @Nonnull final EHasPages eHasPagesProject,
                        @Nonnull final EHasWiki eHasWikiProject,
                        @Nullable final String sLastPublishedVersion,
                        @Nonnull final EJDK eMinJDK,
                        final boolean bIsGitHubPrivate)
  {
    ValueEnforcer.notNull (eProjectOwner, "ProjectOwner");
    ValueEnforcer.notEmpty (sProjectName, "ProjectName");
    ValueEnforcer.notNull (eProjectType, "ProjectType");
    ValueEnforcer.notNull (aBaseDir, "BaseDir");
    ValueEnforcer.notNull (eIsDeprecated, "IsDeprecated");
    ValueEnforcer.notNull (eHasPagesProject, "HasPagesProject");
    ValueEnforcer.notNull (eHasWikiProject, "HasWikiProject");
    ValueEnforcer.notNull (eMinJDK, "MinJDK");

    m_eHostingPlatform = eHostingPlatform;
    m_aParentProject = aParentProject;
    m_eProjectOwner = eProjectOwner;
    m_sProjectName = sProjectName;
    m_eProjectType = eProjectType;
    m_aBaseDir = aBaseDir;
    if (!m_aBaseDir.exists () && eIsDeprecated.isFalse ())
      throw new IllegalStateException ("Project base directory does not exist: " + m_aBaseDir);
    m_sFullBaseDirName = (aParentProject != null ? aParentProject.getFullBaseDirName () + "/" : "") + aBaseDir.getName ();
    m_bIsDeprecated = eIsDeprecated.isTrue ();
    m_bHasPagesProject = eHasPagesProject.isTrue ();
    m_bHasWikiProject = eHasWikiProject.isTrue ();
    m_sLastPublishedVersion = sLastPublishedVersion;
    m_aLastPublishedVersion = sLastPublishedVersion == null ? null : Version.parse (sLastPublishedVersion);
    m_eMinJDK = eMinJDK;
    m_bIsGitHubPrivate = bIsGitHubPrivate;

    // Determine group and artifact from POM
    String sGroupID = null;
    String sArtifactID = null;
    final IMicroDocument aDoc = MicroReader.readMicroXML (getPOMFile ());
    if (aDoc != null)
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Read " + getPOMFile ().getAbsolutePath () + " for project '" + sProjectName + "'");
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
      if (!m_bIsDeprecated)
        LOGGER.warn ("Failed to read POM " + getPOMFile ());

      // For deprecated projects
      m_sMavenGroupID = eProjectType == EProjectType.MAVEN_PLUGIN ? "com.helger.maven" : "com.helger";
      m_sMavenArtifactID = m_sProjectName;
    }
  }

  public boolean isBuildInProject ()
  {
    return false;
  }

  @Nonnull
  public EHostingPlatform getHostingPlatform ()
  {
    return m_eHostingPlatform;
  }

  @Nullable
  public IProject getParentProject ()
  {
    return m_aParentProject;
  }

  @Nonnull
  public EProjectOwner getProjectOwner ()
  {
    return m_eProjectOwner;
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

  public boolean isGitHubPrivate ()
  {
    return m_bIsGitHubPrivate;
  }

  public int compareTo (@Nonnull final IProject aProject)
  {
    int ret = m_eProjectOwner.compareTo (aProject.getProjectOwner ());
    if (ret == 0)
      ret = m_sProjectName.compareTo (aProject.getProjectName ());
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("ProjectName", m_sProjectName).append ("ProjectType", m_eProjectType).getToString ();
  }
}
