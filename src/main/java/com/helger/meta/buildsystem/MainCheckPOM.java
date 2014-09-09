package com.helger.meta.buildsystem;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.microdom.utils.MicroUtils;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.meta.EProject;
import com.helger.meta.EProjectType;

/**
 * Check whether the Maven pom.xml of a project is consistent to the
 * requirements
 *
 * @author Philip Helger
 */
public final class MainCheckPOM
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MainCheckPOM.class);
  private static int s_nWarnCount = 0;

  // Parent POM requirements
  private static final String PARENT_POM_ARTIFACTID = "parent-pom";
  private static final String PARENT_POM_GROUPID = "com.helger";
  private static final String PARENT_POM_VERSION = "1.3";

  private static void _warn (@Nonnull final EProject eProject, @Nonnull final String sMsg)
  {
    s_aLogger.warn ("Project " + eProject.getProjectName () + ": " + sMsg);
    s_nWarnCount++;
  }

  @Nonnull
  @Nonempty
  private static String _getDesiredPackaging (@Nonnull final EProject eProject)
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
      case JAXB_PLUGIN:
        return "jar";
      default:
        throw new IllegalArgumentException ("Unsupported project type in " + eProject);
    }
  }

  private static void _validatePOM (@Nonnull final EProject eProject, @Nonnull final IMicroDocument aDoc)
  {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug (eProject.getProjectName ());

    final IMicroElement eRoot = aDoc.getDocumentElement ();

    // Check parent POM
    {
      final IMicroElement eParent = eRoot.getFirstChildElement ("parent");
      if (eParent == null)
        _warn (eProject, "No parent element found");
      else
      {
        final String sGroupId = MicroUtils.getChildTextContent (eParent, "groupId");
        if (!PARENT_POM_GROUPID.equals (sGroupId))
          _warn (eProject, "Parent POM uses non-standard groupId '" + sGroupId + "'");
        else
        {
          // Check only if groupId matches
          final String sArtifactId = MicroUtils.getChildTextContent (eParent, "artifactId");
          if (!PARENT_POM_ARTIFACTID.equals (sArtifactId))
            _warn (eProject, "Parent POM uses non-standard artifactId '" + sArtifactId + "'");
          else
          {
            // Check version only if group and artifact match
            final String sVersion = MicroUtils.getChildTextContent (eParent, "version");
            if (!PARENT_POM_VERSION.equals (sVersion))
              _warn (eProject, "Parent POM uses non-standard version '" + sVersion + "'");
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

      final String sExpectedPackaging = _getDesiredPackaging (eProject);
      if (!sPackaging.equals (sExpectedPackaging))
        _warn (eProject, "Unexpected packaging '" + sPackaging + "' used. Expected '" + sExpectedPackaging + "'");
    }

    // Check URL
    {
      final String sURL = MicroUtils.getChildTextContent (eRoot, "url");
      final String sExpectedURL = "https://github.com/phax/" + eProject.getProjectName ();
      if (!sExpectedURL.equals (sURL))
        _warn (eProject, "Unexpected URL '" + sURL + "'. Expected '" + sExpectedURL + "'");
    }

    // Check for inception year
    {
      final String sInceptionYear = MicroUtils.getChildTextContent (eRoot, "inceptionYear");
      if (StringHelper.hasNoText (sInceptionYear))
        _warn (eProject, "inceptionYear element is missing or empty");
      else
        if (!StringParser.isUnsignedInt (sInceptionYear))
          _warn (eProject, "Inception year '" + sInceptionYear + "' is not numeric");
    }

    // Check for license element
    {
      if (!eRoot.hasChildElements ("licenses"))
        _warn (eProject, "licenses element is missing");
    }

    // Check SCM
    {
      final IMicroElement eSCM = eRoot.getFirstChildElement ("scm");
      if (eSCM == null)
        _warn (eProject, "scm element is missing");
      else
      {
        final String sConnection = MicroUtils.getChildTextContent (eSCM, "connection");
        final String sExpectedConnection = "scm:git:git@github.com:phax/" + eProject.getProjectName () + ".git";
        // Alternatively:
        // "scm:git:https://github.com/phax/"+eProject.getProjectName ()
        if (!sExpectedConnection.equals (sConnection))
          _warn (eProject, "Unexpected SCM connection '" + sConnection + "'. Expected '" + sExpectedConnection + "'");

        final String sDeveloperConnection = MicroUtils.getChildTextContent (eSCM, "developerConnection");
        final String sExpectedDeveloperConnection = sExpectedConnection;
        if (!sExpectedDeveloperConnection.equals (sDeveloperConnection))
          _warn (eProject, "Unexpected SCM developer connection '" +
                           sDeveloperConnection +
                           "'. Expected '" +
                           sExpectedDeveloperConnection +
                           "'");

        final String sURL = MicroUtils.getChildTextContent (eSCM, "url");
        final String sExpectedURL = "http://github.com/phax/" + eProject.getProjectName ();
        if (!sExpectedURL.equals (sURL))
          _warn (eProject, "Unexpected SCM URL '" + sURL + "'. Expected '" + sExpectedURL + "'");

        final String sTag = MicroUtils.getChildTextContent (eSCM, "tag");
        final String sExpectedTag = "HEAD";
        if (!sExpectedTag.equals (sTag))
          _warn (eProject, "Unexpected SCM tag '" + sTag + "'. Expected '" + sExpectedTag + "'");
      }
    }
  }

  public static void main (final String [] args)
  {
    for (final EProject e : EProject.values ())
      if (e.getProjectType () != EProjectType.MAVEN_POM)
      {
        final IMicroDocument aDoc = MicroReader.readMicroXML (e.getPOMFile ());
        if (aDoc == null)
          throw new IllegalStateException ("Failed to read " + e.getPOMFile ());
        _validatePOM (e, aDoc);
      }
    s_aLogger.info ("Done - " + s_nWarnCount + " warning(s)");
  }
}
