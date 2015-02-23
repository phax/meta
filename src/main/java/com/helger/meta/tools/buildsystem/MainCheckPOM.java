/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.microdom.utils.MicroRecursiveIterator;
import com.helger.commons.microdom.utils.MicroUtils;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.version.Version;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EProject;
import com.helger.meta.project.EProjectType;
import com.helger.meta.project.IProject;

/**
 * Check whether the Maven pom.xml of a project is consistent to the
 * requirements
 *
 * @author Philip Helger
 */
public final class MainCheckPOM extends AbstractProjectMain
{
  // Parent POM requirements
  private static final String PARENT_POM_ARTIFACTID = "parent-pom";
  private static final String PARENT_POM_GROUPID = "com.helger";
  private static final String PARENT_POM_VERSION = EProject.PH_PARENT_POM.getLastPublishedVersionString ();

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
      default:
        throw new IllegalArgumentException ("Unsupported project type in " + eProject);
    }
  }

  private static boolean _isSupportedGroupID (@Nullable final String sGroupID)
  {
    return "com.helger".equals (sGroupID) || "com.helger.maven".equals (sGroupID);
  }

  private static void _validatePOM (@Nonnull final IProject aProject, @Nonnull final IMicroDocument aDoc)
  {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug (aProject.getProjectName ());

    final IMicroElement eRoot = aDoc.getDocumentElement ();

    // Check parent POM
    {
      final IMicroElement eParent = eRoot.getFirstChildElement ("parent");
      if (eParent == null)
        _warn (aProject, "No parent element found");
      else
      {
        final String sGroupId = MicroUtils.getChildTextContent (eParent, "groupId");
        if (!PARENT_POM_GROUPID.equals (sGroupId))
        {
          if (aProject.isBuildInProject ())
            _warn (aProject, "Parent POM uses non-standard groupId '" + sGroupId + "'");
        }
        else
        {
          // Check only if groupId matches
          final String sArtifactId = MicroUtils.getChildTextContent (eParent, "artifactId");
          if (!PARENT_POM_ARTIFACTID.equals (sArtifactId))
            _warn (aProject, "Parent POM uses non-standard artifactId '" + sArtifactId + "'");
          else
          {
            // Check version only if group and artifact match
            final String sVersion = MicroUtils.getChildTextContent (eParent, "version");
            if (!PARENT_POM_VERSION.equals (sVersion))
              _warn (aProject, "Parent POM uses non-standard version '" + sVersion + "'");
          }
        }
      }
    }

    // Check Packaging
    {
      String sPackaging = MicroUtils.getChildTextContent (eRoot, "packaging");
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
      final String sURL = MicroUtils.getChildTextContent (eRoot, "url");
      final String sExpectedURL = "https://github.com/phax/" + aProject.getProjectName ();
      if (!sExpectedURL.equals (sURL))
        _warn (aProject, "Unexpected URL '" + sURL + "'. Expected '" + sExpectedURL + "'");
    }

    // Check for inception year
    {
      final String sInceptionYear = MicroUtils.getChildTextContent (eRoot, "inceptionYear");
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
        _warn (aProject, "scm element is missing");
      else
      {
        final String sConnection = MicroUtils.getChildTextContent (eSCM, "connection");
        final String sExpectedConnection = "scm:git:git@github.com:phax/" + aProject.getProjectName () + ".git";
        // Alternatively:
        // "scm:git:https://github.com/phax/"+eProject.getProjectName ()
        if (!sExpectedConnection.equals (sConnection))
          _warn (aProject, "Unexpected SCM connection '" + sConnection + "'. Expected '" + sExpectedConnection + "'");

        final String sDeveloperConnection = MicroUtils.getChildTextContent (eSCM, "developerConnection");
        final String sExpectedDeveloperConnection = sExpectedConnection;
        if (!sExpectedDeveloperConnection.equals (sDeveloperConnection))
          _warn (aProject, "Unexpected SCM developer connection '" +
                           sDeveloperConnection +
                           "'. Expected '" +
                           sExpectedDeveloperConnection +
                           "'");

        final String sURL = MicroUtils.getChildTextContent (eSCM, "url");
        final String sExpectedURL = "http://github.com/phax/" + aProject.getProjectName ();
        if (!sExpectedURL.equals (sURL))
          _warn (aProject, "Unexpected SCM URL '" + sURL + "'. Expected '" + sExpectedURL + "'");

        final String sTag = MicroUtils.getChildTextContent (eSCM, "tag");
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
          final String sGroupID = MicroUtils.getChildTextContentTrimmed ((IMicroElement) aElement.getParent (),
                                                                         "groupId");
          if (_isSupportedGroupID (sGroupID))
          {
            // Match!
            final String sArtifactID = aElement.getTextContentTrimmed ();
            final IProject eReferencedProject = EProject.getFromProjectNameOrNull (sArtifactID);
            if (eReferencedProject == null)
            {
              _warn (aProject, "Referenced unknown project '" + sArtifactID + "'");
            }
            else
            {
              if (eReferencedProject.isDeprecated ())
                _warn (aProject, sArtifactID + ": is deprecated!");

              // Version is optional e.g. when dependencyManagement is used
              final String sVersion = MicroUtils.getChildTextContentTrimmed ((IMicroElement) aElement.getParent (),
                                                                             "version");
              if (sVersion != null)
              {
                final boolean bIsSnapshot = sVersion.endsWith ("-SNAPSHOT");
                if (eReferencedProject.isPublished ())
                {
                  // Referenced project published at least once
                  final Version aVersionInFile = new Version (bIsSnapshot ? StringHelper.trimEnd (sVersion, "-SNAPSHOT")
                                                                         : sVersion);
                  if (aVersionInFile.isLowerThan (eReferencedProject.getLastPublishedVersion ()))
                  {
                    // Version in file lower than known
                    _warn (aProject, sArtifactID +
                                     ":" +
                                     sVersion +
                                     " is out of date. The latest version is " +
                                     eReferencedProject.getLastPublishedVersionString ());
                  }
                  else
                    if (aVersionInFile.equals (eReferencedProject.getLastPublishedVersion ()))
                    {
                      // Version matches - check for SNAPSHOT differences
                      if (bIsSnapshot)
                        _warn (aProject, sArtifactID +
                                         ":" +
                                         sVersion +
                                         " is out of date. The latest version is " +
                                         eReferencedProject.getLastPublishedVersionString ());
                    }
                    else
                      if (aVersionInFile.isGreaterThan (eReferencedProject.getLastPublishedVersion ()))
                      {
                        // Version in file greater than in referenced project
                        if (!bIsSnapshot)
                          _warn (aProject, "Referenced version " +
                                           sVersion +
                                           " of project '" +
                                           eReferencedProject +
                                           "' is newer than the latest known version " +
                                           eReferencedProject.getLastPublishedVersionString ());
                      }
                }
                else
                {
                  // Referenced project not yet published
                  if (!bIsSnapshot)
                    _warn (aProject, "Referenced project " +
                                     eReferencedProject +
                                     " is marked as not published, but a non-SNAPSHOT version is referenced!");
                }
              }
            }
          }
          else
          {
            if (false)
              _warn (aProject, "Unsuported group " + sGroupID);
          }
        }
      }
  }

  public static void main (final String [] args)
  {
    final List <IProject> aAllProjects = getAllProjects ();
    for (final IProject e : aAllProjects)
      if (e.getProjectType () != EProjectType.MAVEN_POM && !e.isDeprecated ())
      {
        final IMicroDocument aDoc = MicroReader.readMicroXML (e.getPOMFile ());
        if (aDoc == null)
          throw new IllegalStateException ("Failed to read " + e.getPOMFile ());
        _validatePOM (e, aDoc);
      }
    s_aLogger.info ("Done - " + getWarnCount () + " warning(s) for " + aAllProjects.size () + " projects");
  }
}
