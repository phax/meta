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
package com.helger.meta.tools.buildsystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.microdom.util.MicroHelper;
import com.helger.commons.microdom.util.MicroRecursiveIterator;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.version.Version;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EExternalDependency;
import com.helger.meta.project.EJDK;
import com.helger.meta.project.EProject;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Check whether the Maven pom.xml of a project is consistent to the
 * requirements
 *
 * @author Philip Helger
 */
public final class MainCheckPOMArtifactVersions extends AbstractProjectMain
{
  // Parent POM requirements
  private static final String PARENT_POM_ARTIFACTID = "parent-pom";
  private static final String PARENT_POM_GROUPID = "com.helger";
  private static final String SUFFIX_SNAPSHOT = "-SNAPSHOT";

  @Nonnull
  @Nonempty
  private static String _getDesiredPackaging (@Nonnull final IProject eProject)
  {
    switch (eProject.getProjectType ())
    {
      case JAVA_LIBRARY:
        return "bundle";
      case JAVA_APPLICATION:
        return "jar";
      case JAVA_WEB_APPLICATION:
        return "war";
      case MAVEN_PLUGIN:
        return "maven-plugin";
      case MAVEN_POM:
        return "pom";
      case OTHER_PLUGIN:
        return "jar";
      case RESOURCES_ONLY:
        return "jar";
      default:
        throw new IllegalArgumentException ("Unsupported project type in " + eProject);
    }
  }

  private static boolean _isSupportedGroupID (@Nullable final String sGroupID)
  {
    return PARENT_POM_GROUPID.equals (sGroupID) || "com.helger.maven".equals (sGroupID);
  }

  private static boolean _isSnapshot (final String sVersion)
  {
    return sVersion.endsWith (SUFFIX_SNAPSHOT) ||
           RegExHelper.stringMatchesPattern (".+\\-beta[0-9]+", Pattern.CASE_INSENSITIVE, sVersion);
  }

  @Nonnull
  private static String _getParentPOMVersion (@Nonnull final IProject aProject)
  {
    assert aProject != null;
    return EProject.PH_PARENT_POM.getLastPublishedVersionString ();
  }

  private static void _validatePOM (@Nonnull final IProject aProject, @Nonnull final IMicroDocument aDoc)
  {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug (aProject.getProjectName ());

    final IMicroElement eRoot = aDoc.getDocumentElement ();
    final EJDK eProjectJDK = aProject.getMinimumJDKVersion ();

    // Read all properties
    final Map <String, String> aProperties = new HashMap<> ();
    {
      final IMicroElement eProperties = eRoot.getFirstChildElement ("properties");
      if (eProperties != null)
        for (final IMicroElement eProperty : eProperties.getAllChildElements ())
          aProperties.put ("${" + eProperty.getTagName () + "}", eProperty.getTextContentTrimmed ());
    }

    // Check parent POM
    {
      final IMicroElement eParent = eRoot.getFirstChildElement ("parent");
      if (eParent == null)
      {
        if (!aProject.isParentPOM ())
          _warn (aProject, "No parent element found");
      }
      else
      {
        final String sGroupId = MicroHelper.getChildTextContent (eParent, "groupId");
        if (!PARENT_POM_GROUPID.equals (sGroupId))
        {
          if (aProject.isBuildInProject ())
            _warn (aProject, "Parent POM uses non-standard groupId '" + sGroupId + "'");
        }
        else
        {
          // Check only if groupId matches
          final String sArtifactId = MicroHelper.getChildTextContent (eParent, "artifactId");
          if (!PARENT_POM_ARTIFACTID.equals (sArtifactId))
          {
            if (aProject.isNestedProject () && aProject.getParentProject ().getProjectName ().equals (sArtifactId))
            {
              // It's ok
            }
            else
              _warn (aProject, "Parent POM uses non-standard artifactId '" + sArtifactId + "'");
          }
          else
          {
            // Check version only if group and artifact match
            final String sVersion = MicroHelper.getChildTextContent (eParent, "version");
            if (!_getParentPOMVersion (aProject).equals (sVersion))
              _warn (aProject, "Parent POM uses non-standard version '" + sVersion + "'");
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

      final String sExpectedPackaging = _getDesiredPackaging (aProject);
      if (!sPackaging.equals (sExpectedPackaging) && !aProject.isBuildInProject ())
        _warn (aProject, "Unexpected packaging '" + sPackaging + "' used. Expected '" + sExpectedPackaging + "'");
    }

    // Check URL
    if (aProject.isBuildInProject ())
    {
      final String sURL = MicroHelper.getChildTextContent (eRoot, "url");
      final String sExpectedURL = "https://github.com/phax/" + aProject.getFullBaseDirName ();
      if (!sExpectedURL.equals (sURL))
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
        final String sExpectedConnection = "scm:git:git@github.com:phax/" + aProject.getFullBaseDirName () + ".git";
        // Alternatively:
        // "scm:git:https://github.com/phax/"+eProject.getProjectName ()
        if (!sExpectedConnection.equals (sConnection))
          _warn (aProject, "Unexpected SCM connection '" + sConnection + "'. Expected '" + sExpectedConnection + "'");

        final String sDeveloperConnection = MicroHelper.getChildTextContent (eSCM, "developerConnection");
        final String sExpectedDeveloperConnection = sExpectedConnection;
        if (!sExpectedDeveloperConnection.equals (sDeveloperConnection))
          _warn (aProject,
                 "Unexpected SCM developer connection '" +
                           sDeveloperConnection +
                           "'. Expected '" +
                           sExpectedDeveloperConnection +
                           "'");

        final String sURL = MicroHelper.getChildTextContent (eSCM, "url");
        final String sExpectedURL = "http://github.com/phax/" + aProject.getFullBaseDirName ();
        if (!sExpectedURL.equals (sURL))
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
            sGroupID = aProperties.get (sGroupID);
          }
          String sArtifactID = aElement.getTextContentTrimmed ();
          if (sArtifactID != null && sArtifactID.contains ("$"))
          {
            // Try to resolve through properties. May be null if properties
            // are in the parent POM
            sArtifactID = aProperties.get (sArtifactID);
          }
          // Version is optional e.g. when dependencyManagement is used
          String sVersion = MicroHelper.getChildTextContentTrimmed ((IMicroElement) aElement.getParent (), "version");
          if (sVersion != null && sVersion.contains ("$"))
          {
            // Try to resolve through properties. May be null if properties
            // are in the parent POM
            sVersion = aProperties.get (sVersion);
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
              if (!aReferencedProject.getMinimumJDKVersion ().isCompatibleToRuntimeVersion (eProjectJDK))
              {
                if (false)
                  _info (aProject,
                         "Incompatible artifact " +
                                   sGroupID +
                                   "::" +
                                   sArtifactID +
                                   "::" +
                                   sVersion +
                                   " for " +
                                   eProjectJDK.getDisplayName ());
                continue;
              }

              if (sVersion != null)
              {
                final boolean bIsSnapshot = _isSnapshot (sVersion);
                if (aReferencedProject.isPublished ())
                {
                  // Referenced project published at least once
                  final boolean bPublishedIsSnapshot = _isSnapshot (aReferencedProject.getLastPublishedVersionString ());
                  final Version aLastPublishedVersion = bPublishedIsSnapshot ? Version.parse (StringHelper.trimEnd (aReferencedProject.getLastPublishedVersionString (),
                                                                                                                    SUFFIX_SNAPSHOT))
                                                                             : aReferencedProject.getLastPublishedVersion ();

                  final Version aVersionInFile = Version.parse (bIsSnapshot ? StringHelper.trimEnd (sVersion,
                                                                                                    SUFFIX_SNAPSHOT)
                                                                            : sVersion);
                  if (aVersionInFile.isLowerThan (aLastPublishedVersion))
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
                      if (aVersionInFile.isGreaterThan (aLastPublishedVersion))
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
                        _warn (aProject,
                               "Houston we have a problem: " + aVersionInFile + " vs. " + aLastPublishedVersion);
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
              final List <EExternalDependency> aExternalDeps = EExternalDependency.findAll (sGroupID, sArtifactID);

              final String sSuffix = aExternalDeps.size () <= 1 ? "" : " for " + eProjectJDK.getDisplayName ();

              for (final EExternalDependency eExternalDep : aExternalDeps)
              {
                // Avoid warnings for components that require a later JDK
                if (!eExternalDep.getMinimumJDKVersion ().isCompatibleToRuntimeVersion (eProjectJDK))
                {
                  if (false)
                    _info (aProject, "Incompatible artifact " + sGroupID + "::" + sArtifactID + "::" + sVersion);
                  continue;
                }

                if (eExternalDep.isDeprecatedForJDK (eProjectJDK))
                {
                  _warn (aProject,
                         sArtifactID +
                                   " is deprecated - use " +
                                   eExternalDep.getReplacement (eProjectJDK).getDisplayNameWithVersion () +
                                   " instead");
                }
                else
                {
                  // Referenced project published at least once
                  final Version aVersionInFile = Version.parse (sVersion);
                  if (aVersionInFile.isLowerThan (eExternalDep.getLastPublishedVersion ()))
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
                    if (aVersionInFile.isGreaterThan (eExternalDep.getLastPublishedVersion ()))
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
                if (false)
                  if (!sGroupID.startsWith ("org.apache.maven") &&
                      !sGroupID.startsWith ("org.codehaus.mojo") &&
                      !sArtifactID.contains ("-maven-") &&
                      !sArtifactID.startsWith ("maven-"))
                    _warn (aProject, "Unsuported artifact " + sGroupID + "::" + sArtifactID + "::" + sVersion);
              }
            }
            else
            {
              // Group ID, Artifact ID or Version is null
              if (false)
                _warn (aProject, "Unchecked artifact " + sGroupID + "::" + sArtifactID + "::" + sVersion);
            }
        }
      }
  }

  public static void main (final String [] args)
  {
    for (final IProject aProject : ProjectList.getAllProjects ())
      if (!aProject.isDeprecated ())
      {
        final IMicroDocument aDoc = MicroReader.readMicroXML (aProject.getPOMFile ());
        if (aDoc == null)
          throw new IllegalStateException ("Failed to read " + aProject.getPOMFile ());
        _validatePOM (aProject, aDoc);
      }
    s_aLogger.info ("Done - " + getWarnCount () + " warning(s) for " + ProjectList.size () + " projects");
  }
}
