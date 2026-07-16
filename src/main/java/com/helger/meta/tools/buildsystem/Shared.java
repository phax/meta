package com.helger.meta.tools.buildsystem;

import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.system.SystemProperties;
import com.helger.base.version.Version;
import com.helger.base.version.VersionRange;
import com.helger.cache.regex.RegExHelper;
import com.helger.meta.project.EProject;

final class Shared
{
  // Parent POM requirements
  static final String PARENT_POM_GROUPID = "com.helger";
  static final String PARENT_POM_ARTIFACTID = "parent-pom";
  static final String SUFFIX_SNAPSHOT = "-SNAPSHOT";

  private Shared ()
  {}

  static boolean isSupportedGroupID (@Nullable final String sGroupID)
  {
    if (sGroupID == null)
      return false;
    return PARENT_POM_GROUPID.equals (sGroupID) ||
           sGroupID.startsWith ("com.helger.") ||
           "at.austriapro".equals (sGroupID) ||
           "eu.de4a".equals (sGroupID);
  }

  static boolean isSnapshotVersion (@NonNull final String sVersion)
  {
    return sVersion.endsWith (SUFFIX_SNAPSHOT) ||
           RegExHelper.stringMatchesPattern (".+[-_\\.](alpha|b|beta|rc|m|ea|snaoshot)[-_\\.]?[0-9]+",
                                             Pattern.CASE_INSENSITIVE,
                                             sVersion);
  }

  /**
   * Check if a Maven profile <code>&lt;activation&gt;&lt;jdk&gt;</code> value matches the JDK version
   * this tool is currently running on.
   *
   * @param sJdkActivation
   *        The content of the <code>jdk</code> activation element. Either a single version (e.g.
   *        <code>17</code>) or a version range (e.g. <code>[11,17)</code>).
   * @return <code>true</code> if the current runtime JDK matches the activation.
   */
  static boolean matchesCurrentJDK (@NonNull final String sJdkActivation)
  {
    final Version aCurrentJavaVersion = Version.parse (SystemProperties.getJavaVersion ());
    if (sJdkActivation.indexOf (',') >= 0)
      return VersionRange.parse (sJdkActivation).versionMatches (aCurrentJavaVersion);
    return Version.parse (sJdkActivation).equals (aCurrentJavaVersion);
  }

  @NonNull
  static String getParentPOMVersionString ()
  {
    return EProject.PH_PARENT_POM.getLastPublishedVersionString ();
  }

  @NonNull
  static Version getParentPOMVersion ()
  {
    return EProject.PH_PARENT_POM.getLastPublishedVersion ();
  }
}
