/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.meta.tools.buildsystem;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.system.SystemProperties;
import com.helger.commons.version.Version;
import com.helger.commons.version.VersionRange;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EExternalDependency;
import com.helger.meta.project.EJDK;
import com.helger.meta.project.EProject;
import com.helger.meta.project.EProjectOwner;
import com.helger.meta.project.EProjectType;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.util.MicroHelper;
import com.helger.xml.microdom.util.MicroRecursiveIterator;

/**
 * Check whether the Maven pom.xml of a project is consistent to the
 * requirements
 *
 * @author Philip Helger
 */
public final class MainCheckPOMArtifactVersions extends AbstractProjectMain
{
  private static final boolean DEBUG_LOG = false;

  // Parent POM requirements
  private static final String PARENT_POM_GROUPID = "com.helger";
  private static final String PARENT_POM_ARTIFACTID = "parent-pom";
  private static final String SUFFIX_SNAPSHOT = "-SNAPSHOT";

  @Nonnull
  @Nonempty
  private static String [] _getDesiredPackagings (@Nonnull final IProject eProject)
  {
    switch (eProject.getProjectType ())
    {
      case JAVA_LIBRARY:
        return new String [] { "bundle", "jar" };
      case JAVA_APPLICATION:
        return new String [] { "jar" };
      case JAVA_WEB_APPLICATION:
        return new String [] { "war" };
      case MAVEN_PLUGIN:
        return new String [] { "maven-plugin" };
      case MAVEN_POM:
        return new String [] { "pom" };
      case OTHER_PLUGIN:
        return new String [] { "jar" };
      case RESOURCES_ONLY:
        return new String [] { "jar" };
      default:
        throw new IllegalArgumentException ("Unsupported project type in " + eProject);
    }
  }

  private static boolean _isSupportedGroupID (@Nullable final String sGroupID)
  {
    if (sGroupID == null)
      return false;
    return PARENT_POM_GROUPID.equals (sGroupID) ||
           sGroupID.startsWith ("com.helger.") ||
           "at.austriapro".equals (sGroupID) ||
           "at.winenet".equals (sGroupID) ||
           "eu.toop".equals (sGroupID) ||
           "eu.de4a".equals (sGroupID);
  }

  private static boolean _isSnapshotVersion (final String sVersion)
  {
    return sVersion.endsWith (SUFFIX_SNAPSHOT) ||
           RegExHelper.stringMatchesPattern (".+[-_\\.](alpha|Alpha|ALPHA|b|beta|Beta|BETA|rc|RC|M|EA|SNAOSHOT)[-_\\.]?[0-9]+",
                                             Pattern.CASE_INSENSITIVE,
                                             sVersion);
  }

  @Nonnull
  private static String _getParentPOMVersion (@Nonnull final IProject aProject)
  {
    assert aProject != null;
    return EProject.PH_PARENT_POM.getLastPublishedVersionString ();
  }

  @Nonnull
  private static String _getResolvedVar (@Nonnull final String sText, @Nonnull final ICommonsMap <String, String> aProps)
  {
    String ret = sText;
    while (true)
    {
      final int nIndex = ret.indexOf ("${");
      if (nIndex < 0)
        break;

      final int nIndex2 = ret.indexOf ("}");
      if (nIndex2 < 0)
        break;

      // Variable name
      final String sVar = ret.substring (nIndex, nIndex2 + 1);

      // Resolved value
      final String sNewValue = aProps.get (sVar);
      if (sNewValue == null)
        throw new IllegalStateException ("Failed to resolve variable " + sVar + " in " + aProps);

      // Replace
      ret = ret.substring (0, nIndex) + sNewValue + ret.substring (nIndex2 + 1);
    }
    return ret;
  }

  private static void _addParentPOMProperties (@Nonnull final File aThisPOMFile,
                                               @Nonnull final IMicroElement eProject,
                                               @Nonnull final ICommonsMap <String, String> aProperties)
  {
    final IMicroElement eParent = eProject.getFirstChildElement ("parent");
    if (eParent != null)
    {
      String sParentPath = MicroHelper.getChildTextContent (eParent, "relativePath");
      if (StringHelper.hasNoText (sParentPath))
        sParentPath = "../";
      else
        sParentPath = FilenameHelper.ensurePathEndingWithSeparator (sParentPath);
      final File fParentPOM = new File (aThisPOMFile.getParentFile (), sParentPath + "pom.xml").getAbsoluteFile ();
      final IMicroDocument aParentPOM = MicroReader.readMicroXML (fParentPOM);
      if (aParentPOM != null)
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Sucessfully resolved parent POM " + fParentPOM.getAbsolutePath ());

        // First read parent of parent properties
        _addParentPOMProperties (fParentPOM, aParentPOM.getDocumentElement (), aProperties);

        // Than read the properties
        final IMicroElement eProperties = aParentPOM.getDocumentElement ().getFirstChildElement ("properties");
        if (eProperties != null)
          eProperties.forAllChildElements (eProperty -> {
            String sValue = eProperty.getTextContentTrimmed ();
            sValue = _getResolvedVar (sValue, aProperties);
            aProperties.put ("${" + eProperty.getTagName () + "}", sValue);
          });
      }
    }
  }

  private static void _validatePOM (@Nonnull final IProject aProject, @Nonnull final IMicroDocument aDoc)
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug (aProject.getProjectName ());

    final IMicroElement eRoot = aDoc.getDocumentElement ();
    final EJDK eProjectJDK = aProject.getMinimumJDKVersion ();
    final EProjectOwner eProjectOwner = aProject.getProjectOwner ();

    final ICommonsMap <String, String> aProperties = new CommonsLinkedHashMap <> ();
    // Put in predefined properties
    aProperties.put ("${maven.build.timestamp}", PDTFactory.getCurrentLocalDateTime ().toString ());
    aProperties.put ("${project.build.directory}", aProject.getFullBaseDirName () + "/target");

    // Try read the parent POM properties
    _addParentPOMProperties (aProject.getPOMFile (), eRoot, aProperties);

    // Read all unconditional properties
    {
      final IMicroElement eProperties = eRoot.getFirstChildElement ("properties");
      if (eProperties != null)
        eProperties.forAllChildElements (eProperty -> {
          String sValue = eProperty.getTextContentTrimmed ();
          sValue = _getResolvedVar (sValue, aProperties);
          aProperties.put ("${" + eProperty.getTagName () + "}", sValue);
        });
    }

    // Read all profile properties
    {
      final Version aCurrentVersion = Version.parse (SystemProperties.getJavaVersion ());

      final IMicroElement eProfiles = eRoot.getFirstChildElement ("profiles");
      if (eProfiles != null)
        for (final IMicroElement eProfile : eProfiles.getAllChildElements ("profile"))
        {
          boolean bCanUseProfile = false;
          final IMicroElement eActivation = eProfile.getFirstChildElement ("activation");
          if (eActivation != null)
          {
            final IMicroElement eJdk = eActivation.getFirstChildElement ("jdk");
            if (eJdk != null)
            {
              final String sValue = eJdk.getTextContentTrimmed ();
              if (sValue.indexOf (',') >= 0)
              {
                // Version range
                final VersionRange aRange = VersionRange.parse (sValue);
                bCanUseProfile = aRange.versionMatches (aCurrentVersion);
              }
              else
              {
                // Single version
                final Version aVersion = Version.parse (sValue);
                bCanUseProfile = aVersion.equals (aCurrentVersion);
              }
            }
            else
            {
              if (DEBUG_LOG)
                _warn (aProject, "Only profile activations with JDK version are supported");
            }
          }
          else
          {
            // Happens in parent POM
            if (DEBUG_LOG)
              _warn (aProject, "Found profile without activation");
          }

          if (bCanUseProfile)
          {
            // Add all properties from profile
            final IMicroElement eProperties = eProfile.getFirstChildElement ("properties");
            if (eProperties != null)
            {
              if (DEBUG_LOG)
                _info (aProject, "Using properties from profile '" + MicroHelper.getChildTextContent (eProfile, "id") + "'");
              eProperties.forAllChildElements (eProperty -> {
                String sValue = eProperty.getTextContentTrimmed ();
                sValue = _getResolvedVar (sValue, aProperties);
                aProperties.put ("${" + eProperty.getTagName () + "}", sValue);
              });
            }
          }
        }
    }

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Having the following POM properties: " + aProperties.toString ());

    // Check parent POM
    String sParentPOMGroupId = null;
    String sParentPOMArtifactId = null;
    String sParentPOMVersion = null;
    {
      final IMicroElement eParent = eRoot.getFirstChildElement ("parent");
      if (eParent == null)
      {
        if (!aProject.isParentPOM ())
          _warn (aProject, "No parent element found");
      }
      else
      {
        // Found a "<parent>" element
        final String sGroupId = MicroHelper.getChildTextContent (eParent, "groupId");
        sParentPOMGroupId = sGroupId;
        if (!_isSupportedGroupID (sGroupId))
        {
          if (aProject.isBuildInProject ())
            _warn (aProject, "Parent POM uses non-standard groupId '" + sGroupId + "'");
        }
        else
        {
          // Check only if groupId matches
          final String sArtifactId = MicroHelper.getChildTextContent (eParent, "artifactId");
          sParentPOMArtifactId = sArtifactId;
          boolean bCheckVersion = true;
          if (!PARENT_POM_ARTIFACTID.equals (sArtifactId))
          {
            bCheckVersion = false;
            if (aProject.isNestedProject () && aProject.getParentProject ().getProjectName ().equals (sArtifactId))
            {
              // It's ok
            }
            else
            {
              if (eProjectOwner.equals (EProjectOwner.DEFAULT_PROJECT_OWNER))
                _warn (aProject, "Parent POM uses non-standard artifactId '" + sArtifactId + "'");
            }
          }

          {
            // Check version only if group and artifact match
            final String sVersion = MicroHelper.getChildTextContent (eParent, "version");
            if (bCheckVersion && !_getParentPOMVersion (aProject).equals (sVersion))
              _warn (aProject, "Parent POM uses non-standard version '" + sVersion + "'");
            sParentPOMVersion = sVersion;
          }
        }
      }
    }

    // Check Packaging
    {
      String sPackaging = MicroHelper.getChildTextContent (eRoot, "packaging");
      if (sPackaging == null)
      {
        // This is the default
        sPackaging = "jar";
      }

      final String [] aExpectedPackagings = _getDesiredPackagings (aProject);
      if (!ArrayHelper.contains (aExpectedPackagings, sPackaging))
        _warn (aProject, "Unexpected packaging '" + sPackaging + "' used. Expected one of " + Arrays.toString (aExpectedPackagings) + ".");
    }

    // Group ID for properties
    {
      String sGroupId = MicroHelper.getChildTextContent (eRoot, "groupId");
      if (sGroupId == null)
        sGroupId = sParentPOMGroupId;
      if (sGroupId != null)
        aProperties.put ("${project.groupId}", sGroupId);
    }

    // Artifact ID for properties
    {
      String sArtifactId = MicroHelper.getChildTextContent (eRoot, "artifactId");
      if (sArtifactId == null)
        sArtifactId = sParentPOMArtifactId;
      if (sArtifactId != null)
        aProperties.put ("${project.artifactId}", sArtifactId);
    }

    // Version for properties
    {
      String sVersion = MicroHelper.getChildTextContent (eRoot, "version");
      if (sVersion == null)
        sVersion = sParentPOMVersion;
      if (sVersion != null)
        aProperties.put ("${project.version}", sVersion);
    }

    // Check URL
    if (aProject.isBuildInProject ())
    {
      final String sURL = MicroHelper.getChildTextContent (eRoot, "url");
      final String sExpectedURL = "https://" +
                                  aProject.getHostingPlatform ().getDomain () +
                                  "/" +
                                  eProjectOwner.getGitOrgaName () +
                                  "/" +
                                  aProject.getFullBaseDirName ();
      if (!sExpectedURL.equalsIgnoreCase (sURL))
        _warn (aProject, "Unexpected URL '" + sURL + "'. Expected '" + sExpectedURL + "'");
    }

    // Check for inception year
    {
      final String sInceptionYear = MicroHelper.getChildTextContent (eRoot, "inceptionYear");
      if (StringHelper.hasNoText (sInceptionYear))
        _warn (aProject, "inceptionYear element is missing or empty");
      else
        if (!StringParser.isUnsignedInt (sInceptionYear))
          _warn (aProject, "Inception year '" + sInceptionYear + "' is not numeric");
    }

    // Check for license element
    if (aProject.isBuildInProject ())
    {
      if (!eRoot.hasChildElements ("licenses"))
        _warn (aProject, "licenses element is missing");
    }

    // Check SCM
    if (aProject.isBuildInProject ())
    {
      final IMicroElement eSCM = eRoot.getFirstChildElement ("scm");
      if (eSCM == null)
      {
        if (!aProject.isNestedProject ())
        {
          // Nested projects might not use it
          _warn (aProject, "scm element is missing");
        }
      }
      else
      {
        final String sConnection = MicroHelper.getChildTextContent (eSCM, "connection");
        final String sExpectedConnection = "scm:git:git@" +
                                           aProject.getHostingPlatform ().getDomain () +
                                           ":" +
                                           eProjectOwner.getGitOrgaName () +
                                           "/" +
                                           aProject.getFullBaseDirName () +
                                           ".git";
        // Alternatively:
        // "scm:git:https://github.com/"+org+"/"+eProject.getProjectName ()
        if (!sExpectedConnection.equalsIgnoreCase (sConnection))
          _warn (aProject, "Unexpected SCM connection '" + sConnection + "'. Expected '" + sExpectedConnection + "'");

        final String sDeveloperConnection = MicroHelper.getChildTextContent (eSCM, "developerConnection");
        final String sExpectedDeveloperConnection = sExpectedConnection;
        if (!sExpectedDeveloperConnection.equalsIgnoreCase (sDeveloperConnection))
          _warn (aProject,
                 "Unexpected SCM developer connection '" + sDeveloperConnection + "'. Expected '" + sExpectedDeveloperConnection + "'");

        final String sURL = MicroHelper.getChildTextContent (eSCM, "url");
        final String sExpectedURL = "http://" +
                                    aProject.getHostingPlatform ().getDomain () +
                                    "/" +
                                    eProjectOwner.getGitOrgaName () +
                                    "/" +
                                    aProject.getFullBaseDirName ();
        if (!sExpectedURL.equalsIgnoreCase (sURL))
          _warn (aProject, "Unexpected SCM URL '" + sURL + "'. Expected '" + sExpectedURL + "'");

        final String sTag = MicroHelper.getChildTextContent (eSCM, "tag");
        final String sExpectedTag = "HEAD";
        if (!sExpectedTag.equals (sTag))
          _warn (aProject, "Unexpected SCM tag '" + sTag + "'. Expected '" + sExpectedTag + "'");
      }
    }

    // Check all relevant dependencies or the like
    for (final IMicroNode aNode : new MicroRecursiveIterator (eRoot))
      if (aNode.isElement ())
      {
        final IMicroElement aElement = (IMicroElement) aNode;
        // groupId is optional e.g. for the defined artefact
        if (aElement.getLocalName ().equals ("artifactId"))
        {
          // Check if the current artefact is in the "com.helger" group
          String sGroupID = MicroHelper.getChildTextContentTrimmed ((IMicroElement) aElement.getParent (), "groupId");
          if (sGroupID != null && sGroupID.contains ("$"))
          {
            // Try to resolve through properties. May be null if properties
            // are in the parent POM
            sGroupID = _getResolvedVar (sGroupID, aProperties);
          }
          String sArtifactID = aElement.getTextContentTrimmed ();
          if (sArtifactID != null && sArtifactID.contains ("$"))
          {
            // Try to resolve through properties. May be null if properties
            // are in the parent POM
            sArtifactID = _getResolvedVar (sArtifactID, aProperties);
          }
          // Version is optional e.g. when dependencyManagement is used
          String sVersion = MicroHelper.getChildTextContentTrimmed ((IMicroElement) aElement.getParent (), "version");
          if (sVersion != null && sVersion.contains ("$"))
          {
            // Try to resolve through properties. May be null if properties
            // are in the parent POM
            sVersion = _getResolvedVar (sVersion, aProperties);
          }

          if (_isSupportedGroupID (sGroupID))
          {
            // Match!
            final IProject aReferencedProject = ProjectList.getProjectOfName (sArtifactID);
            if (aReferencedProject == null)
            {
              _warn (aProject, "Referenced unknown project '" + sArtifactID + "'");
            }
            else
            {
              if (aReferencedProject.isDeprecated ())
                _warn (aProject, aReferencedProject.getMavenID () + " is deprecated!");

              // Avoid warnings for components that require a later JDK
              if (!aReferencedProject.getMinimumJDKVersion ().isCompatibleToRuntimeVersion (eProjectJDK) &&
                  aReferencedProject.getProjectType () != EProjectType.MAVEN_POM)
              {
                final boolean bIsSpecialCase1 = false;

                if (!bIsSpecialCase1)
                {
                  _info (aProject,
                         "Incompatible artifact " +
                                   sGroupID +
                                   "::" +
                                   sArtifactID +
                                   "::" +
                                   sVersion +
                                   " (" +
                                   aReferencedProject.getMinimumJDKVersion ().getDisplayName () +
                                   ") for this project requiring " +
                                   eProjectJDK.getDisplayName ());
                }
                continue;
              }

              if (sVersion != null)
              {
                final boolean bIsSnapshot = _isSnapshotVersion (sVersion);
                if (aReferencedProject.isPublished ())
                {
                  // Referenced project published at least once
                  final boolean bPublishedIsSnapshot = _isSnapshotVersion (aReferencedProject.getLastPublishedVersionString ());
                  final Version aLastPublishedVersion = bPublishedIsSnapshot ? Version.parse (StringHelper.trimEnd (aReferencedProject.getLastPublishedVersionString (),
                                                                                                                    SUFFIX_SNAPSHOT))
                                                                             : aReferencedProject.getLastPublishedVersion ();

                  final Version aVersionInFile = Version.parse (bIsSnapshot ? StringHelper.trimEnd (sVersion, SUFFIX_SNAPSHOT) : sVersion);
                  if (aVersionInFile.isLT (aLastPublishedVersion))
                  {
                    // Version in file lower than known
                    _warn (aProject,
                           sArtifactID +
                                     ": " +
                                     sVersion +
                                     " is out of date. The latest version is " +
                                     aReferencedProject.getLastPublishedVersionString ());
                  }
                  else
                    if (aVersionInFile.equals (aLastPublishedVersion))
                    {
                      // Version matches - check for SNAPSHOT differences
                      if (bIsSnapshot && !bPublishedIsSnapshot)
                        _warn (aProject,
                               sArtifactID +
                                         ": " +
                                         sVersion +
                                         " is out of date. The latest version is " +
                                         aReferencedProject.getLastPublishedVersionString ());
                    }
                    else
                      if (aVersionInFile.isGT (aLastPublishedVersion))
                      {
                        // Version in file greater than in referenced project
                        if (!bIsSnapshot)
                          _warn (aProject,
                                 "Referenced version " +
                                           sVersion +
                                           " of project '" +
                                           aReferencedProject +
                                           "' is newer than the latest known version " +
                                           aReferencedProject.getLastPublishedVersionString ());
                      }
                      else
                        _warn (aProject, "Houston we have a problem: " + aVersionInFile + " vs. " + aLastPublishedVersion);
                }
                else
                {
                  // Referenced project not yet published
                  if (!bIsSnapshot)
                    _warn (aProject,
                           "Referenced project " +
                                     aReferencedProject +
                                     " is marked as not published, but the non-SNAPSHOT version '" +
                                     sVersion +
                                     "' is referenced!");
                }
              }
            }
          }
          else
            if (sGroupID != null && sArtifactID != null && sVersion != null)
            {
              // Check for known external deps
              final ICommonsList <EExternalDependency> aExternalDeps = EExternalDependency.findAll (sGroupID, sArtifactID);

              final String sSuffix = aExternalDeps.size () <= 1 ? "" : " for " + eProjectJDK.getDisplayName ();

              for (final EExternalDependency eExternalDep : aExternalDeps)
              {
                // Avoid warnings for components that require a later JDK
                if (!eExternalDep.getMinimumJDKVersion ().isCompatibleToRuntimeVersion (eProjectJDK))
                {
                  if (DEBUG_LOG)
                    _info (aProject, "Incompatible artifact " + sGroupID + "::" + sArtifactID + "::" + sVersion);
                  continue;
                }

                if (eExternalDep.isDeprecatedForJDK (eProjectJDK))
                {
                  _warn (aProject,
                         sGroupID +
                                   "::" +
                                   sArtifactID +
                                   " is deprecated - use " +
                                   eExternalDep.getReplacement (eProjectJDK).getDisplayNameWithVersion () +
                                   " instead");
                }
                else
                {
                  // if (DEBUG_LOG)
                  if (eExternalDep.isLegacy ())
                    _warn (aProject, sGroupID + "::" + sArtifactID + " is legacy - there is something better");

                  // Referenced project published at least once
                  final Version aVersionInFile = Version.parse (sVersion);
                  if (aVersionInFile.isLT (eExternalDep.getLastPublishedVersion ()))
                  {
                    // Version in file lower than known
                    _warn (aProject,
                           sArtifactID +
                                     ": " +
                                     sVersion +
                                     " is out of date. The latest version is " +
                                     eExternalDep.getLastPublishedVersionString () +
                                     sSuffix);
                  }
                  else
                    if (aVersionInFile.isGT (eExternalDep.getLastPublishedVersion ()))
                    {
                      // Version in file greater than in referenced project
                      _warn (aProject,
                             "Referenced version " +
                                       sVersion +
                                       " of '" +
                                       eExternalDep.getDisplayName () +
                                       "' is newer than the latest known version " +
                                       eExternalDep.getLastPublishedVersionString () +
                                       sSuffix);
                    }
                }

                break;
              }

              if (aExternalDeps.isEmpty ())
              {
                // Neither my project nor a known external
                if (true)
                  if (!sGroupID.startsWith ("org.apache.maven") &&
                      !sGroupID.startsWith ("org.codehaus.mojo") &&
                      !sArtifactID.equals ("rt") &&
                      !sArtifactID.contains ("-maven-") &&
                      !sArtifactID.startsWith ("maven-") &&
                      !sArtifactID.startsWith ("plexus-") &&
                      !sArtifactID.startsWith ("aether-"))
                    _warn (aProject, "Unsuported artifact " + sGroupID + "::" + sArtifactID + "::" + sVersion);
              }
            }
            else
            {
              // Group ID, Artifact ID or Version is null
              if (DEBUG_LOG)
                _warn (aProject, "Unchecked artifact " + sGroupID + "::" + sArtifactID + "::" + sVersion);
            }
        }
      }
  }

  public static void main (final String [] args)
  {
    for (final IProject aProject : ProjectList.getAllProjects (p -> !p.isDeprecated ()))
      if (aProject != EProject.PH_JAXWS_MAVEN_PLUGIN)
      {
        if (DEBUG_LOG)
          _info (aProject, "Scanning POM " + aProject.getPOMFile ().getAbsolutePath ());

        final IMicroDocument aDoc = MicroReader.readMicroXML (aProject.getPOMFile ());
        if (aDoc == null)
          throw new IllegalStateException ("Failed to read " + aProject.getPOMFile ());
        try
        {
          _validatePOM (aProject, aDoc);
        }
        catch (final RuntimeException ex)
        {
          LOGGER.error ("Error interpreting " + aProject.getPOMFile ().getAbsolutePath ());
          throw ex;
        }
      }
    LOGGER.info ("Done - " + getWarnCount () + " warning(s) for " + ProjectList.size () + " projects");
  }
}
