/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.string.StringHelper;
import com.helger.base.system.SystemProperties;
import com.helger.base.version.Version;
import com.helger.base.version.VersionRange;
import com.helger.cache.regex.RegExHelper;
import com.helger.io.file.FileHelper;
import com.helger.io.file.FilenameHelper;
import com.helger.meta.project.EProject;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.util.MicroHelper;

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

  /**
   * Invoke the passed consumer for every property that is effective for the JDK this tool runs on:
   * all unconditional <code>&lt;properties&gt;</code> plus the <code>&lt;properties&gt;</code> of all
   * JDK-active profiles. The raw (unresolved, but trimmed) value is passed - the caller decides
   * whether and how to resolve and store it.
   *
   * @param eRoot
   *        The root element of a pom.xml.
   * @param aConsumer
   *        The consumer invoked with (property name, raw trimmed value) for every property.
   */
  static void forEachActiveProperty (@NonNull final IMicroElement eRoot,
                                     @NonNull final BiConsumer <String, String> aConsumer)
  {
    // Unconditional properties
    {
      final IMicroElement eProperties = eRoot.getFirstChildElement ("properties");
      if (eProperties != null)
        eProperties.forAllChildElements (eProperty -> aConsumer.accept (eProperty.getTagName (),
                                                                        eProperty.getTextContentTrimmed ()));
    }

    // Properties of JDK-active profiles
    {
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
              bCanUseProfile = matchesCurrentJDK (eJdk.getTextContentTrimmed ());
          }

          if (bCanUseProfile)
          {
            final IMicroElement eProperties = eProfile.getFirstChildElement ("properties");
            if (eProperties != null)
              eProperties.forAllChildElements (eProperty -> aConsumer.accept (eProperty.getTagName (),
                                                                              eProperty.getTextContentTrimmed ()));
          }
        }
    }
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

  /**
   * Resolve the parent pom.xml of the passed pom.xml via its <code>&lt;parent&gt;&lt;relativePath&gt;</code>
   * (defaulting to <code>../</code>). The result is returned in canonical form.
   *
   * @param aThisPOMFile
   *        The pom.xml file to resolve the parent of.
   * @param eRoot
   *        The already parsed root element of that pom.xml.
   * @return The parent pom.xml file, or <code>null</code> if there is no <code>&lt;parent&gt;</code>
   *         element or the resolved parent pom.xml does not exist (e.g. the shared external parent
   *         POM, which is not reachable via a relative path).
   */
  @Nullable
  static File getParentPOMFileOrNull (@NonNull final File aThisPOMFile, @NonNull final IMicroElement eRoot)
  {
    final IMicroElement eParent = eRoot.getFirstChildElement ("parent");
    if (eParent == null)
      return null;

    String sRelativePath = MicroHelper.getChildTextContent (eParent, "relativePath");
    if (StringHelper.isEmpty (sRelativePath))
      sRelativePath = "../";
    else
      sRelativePath = FilenameHelper.ensurePathEndingWithSeparator (sRelativePath);

    final File aParentFile = new File (aThisPOMFile.getParentFile (), sRelativePath + "pom.xml");
    final File aCanonical = FileHelper.getCanonicalFileOrNull (aParentFile);
    final File aResult = aCanonical != null ? aCanonical : aParentFile.getAbsoluteFile ();
    return aResult.exists () ? aResult : null;
  }
}
