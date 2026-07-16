package com.helger.meta.tools.buildsystem;

import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.cache.regex.RegExHelper;

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
}
