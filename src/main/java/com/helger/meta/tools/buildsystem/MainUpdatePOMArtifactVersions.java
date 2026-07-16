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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.string.StringHelper;
import com.helger.base.version.Version;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EExternalDependency;
import com.helger.meta.project.EJDK;
import com.helger.meta.project.EProject;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.util.MicroHelper;
import com.helger.xml.microdom.util.MicroRecursiveIterator;

/**
 * Update the Maven pom.xml files of all non-deprecated projects so that the latest known versions
 * from {@link EProject} (referenced helger projects) and {@link EExternalDependency} (known
 * external dependencies) are used. This is the writing counterpart to
 * {@link MainCheckPOMArtifactVersions}.
 * <p>
 * The update is performed purely on the text of the file (only the affected version substring is
 * replaced), so the existing formatting of the pom.xml is preserved. A version is only rewritten if
 * the version in the file is <b>strictly lower</b> than the known latest version; equal, newer and
 * SNAPSHOT/pre-release versions are left untouched.
 * <p>
 * Versions are rewritten in three places:
 * <ul>
 * <li>literal <code>&lt;version&gt;</code> elements of a dependency/plugin,</li>
 * <li><code>&lt;properties&gt;</code> entries that a dependency/plugin version resolves through
 * (and that are defined in the same pom.xml), and</li>
 * <li>the <code>com.helger:parent-pom</code> version of the <code>&lt;parent&gt;</code>
 * element.</li>
 * </ul>
 * For external dependencies that exist in multiple variants sharing the same groupId/artifactId
 * (e.g. different JDK levels or {@link com.helger.meta.project.VersionMaxExcl major lines}), the
 * variant of the currently used major line is picked, and an info is emitted if a newer major line
 * exists.
 *
 * @author Philip Helger
 */
public final class MainUpdatePOMArtifactVersions extends AbstractProjectMain
{
  /**
   * If <code>true</code> the changes are only logged but the files are not written.
   */
  private static final boolean DRY_RUN = false;

  private static final Logger LOGGER = LoggerFactory.getLogger (MainUpdatePOMArtifactVersions.class);
  private static final int MAX_DEPTH = 50;

  private static int s_nModifiedFiles = 0;

  private enum EReplaceKind
  {
    LITERAL,
    PROPERTY,
    PARENT
  }

  /**
   * A single pending version replacement in the raw pom.xml text.
   *
   * @author Philip Helger
   */
  private static final class Replacement
  {
    private final EReplaceKind m_eKind;
    // artifactId for LITERAL, property name for PROPERTY, unused for PARENT
    private final String m_sAnchor;
    private final String m_sOldVersion;
    private final String m_sNewVersion;

    Replacement (@NonNull final EReplaceKind eKind,
                 @Nullable final String sAnchor,
                 @NonNull final String sOldVersion,
                 @NonNull final String sNewVersion)
    {
      m_eKind = eKind;
      m_sAnchor = sAnchor;
      m_sOldVersion = sOldVersion;
      m_sNewVersion = sNewVersion;
    }

    @NonNull
    String getKey ()
    {
      return m_eKind + "|" + StringHelper.getNotNull (m_sAnchor) + "|" + m_sOldVersion;
    }

    /**
     * @return The regular expression matching the region to replace. The old version is wrapped in
     *         two capturing groups that must be kept as-is.
     */
    @NonNull
    private Pattern _getPattern ()
    {
      return switch (m_eKind)
      {
        case LITERAL -> Pattern.compile ("(<artifactId>\\s*" +
                                         Pattern.quote (m_sAnchor) +
                                         "\\s*</artifactId>(?:(?!</dependency>|</plugin>|</extension>|<artifactId>).)*?<version>\\s*)" +
                                         Pattern.quote (m_sOldVersion) +
                                         "(\\s*</version>)",
                                         Pattern.DOTALL);
        case PROPERTY -> Pattern.compile ("(<" +
                                          Pattern.quote (m_sAnchor) +
                                          ">\\s*)" +
                                          Pattern.quote (m_sOldVersion) +
                                          "(\\s*</" +
                                          Pattern.quote (m_sAnchor) +
                                          ">)");
        case PARENT -> Pattern.compile ("(<parent>.*?<version>\\s*)" +
                                        Pattern.quote (m_sOldVersion) +
                                        "(\\s*</version>.*?</parent>)",
                                        Pattern.DOTALL);
      };
    }

    /**
     * Apply this replacement to the passed content.
     *
     * @param sContent
     *        Source content
     * @return The modified content or <code>null</code> if nothing was replaced.
     */
    @Nullable
    String apply (@NonNull final String sContent)
    {
      final Matcher aMatcher = _getPattern ().matcher (sContent);
      final StringBuilder aSB = new StringBuilder ();
      boolean bFound = false;
      while (aMatcher.find ())
      {
        bFound = true;
        aMatcher.appendReplacement (aSB,
                                    Matcher.quoteReplacement (aMatcher.group (1) + m_sNewVersion + aMatcher.group (2)));
      }
      if (!bFound)
        return null;
      aMatcher.appendTail (aSB);
      return aSB.toString ();
    }

    @NonNull
    String getLogText ()
    {
      final String sWhat = switch (m_eKind)
      {
        case LITERAL -> "dependency '" + m_sAnchor + "'";
        case PROPERTY -> "property '" + m_sAnchor + "'";
        case PARENT -> "parent POM";
      };
      return sWhat + ": " + m_sOldVersion + " -> " + m_sNewVersion;
    }
  }

  /**
   * Resolve all <code>${...}</code> variables in the passed text against the passed raw variable
   * map.
   *
   * @param sText
   *        The text to resolve
   * @param aRawVars
   *        Map from bare variable name to its raw (possibly itself unresolved) value
   * @return The fully resolved text or <code>null</code> if any variable could not be resolved.
   */
  @Nullable
  private static String _resolveVars (@Nullable final String sText,
                                      @NonNull final ICommonsMap <String, String> aRawVars)
  {
    if (sText == null)
      return null;

    String ret = sText;
    // Simple safety net against cyclic variables
    for (int nDepth = 0; nDepth < MAX_DEPTH; ++nDepth)
    {
      final int nIndex = ret.indexOf ("${");
      if (nIndex < 0)
        return ret;

      final int nIndex2 = ret.indexOf ('}', nIndex);
      if (nIndex2 < 0)
        return null;

      final String sVarName = ret.substring (nIndex + 2, nIndex2);
      final String sValue = aRawVars.get (sVarName);
      if (sValue == null)
        return null;

      ret = ret.substring (0, nIndex) + sValue + ret.substring (nIndex2 + 1);
    }
    return null;
  }

  /**
   * For a version reference of the form <code>${name}</code>, follow potential property chains and
   * return the property that actually holds the literal version value, but only as long as all
   * involved properties are defined in the current pom.xml.
   *
   * @param sVarName
   *        The initial property name
   * @param aOwnPropRaw
   *        Raw properties defined in this pom.xml (name to raw value)
   * @return A two element array (property name, literal value) or <code>null</code> if the terminal
   *         property is not defined in this pom.xml or the chain is not a simple property
   *         reference.
   */
  @Nullable
  private static String [] _findTerminalOwnProperty (@NonNull final String sVarName,
                                                     @NonNull final ICommonsMap <String, String> aOwnPropRaw)
  {
    String sCur = sVarName;
    for (int nDepth = 0; nDepth < MAX_DEPTH; ++nDepth)
    {
      final String sRaw = aOwnPropRaw.get (sCur);
      if (sRaw == null)
      {
        // Not defined in this pom.xml (e.g. defined in parent POM)
        return null;
      }
      if (!sRaw.contains ("${"))
      {
        // Found the literal
        return new String [] { sCur, sRaw };
      }
      // Only follow simple "${next}" references
      if (sRaw.startsWith ("${") && sRaw.endsWith ("}") && sRaw.indexOf ("${", 2) < 0)
      {
        sCur = sRaw.substring (2, sRaw.length () - 1);
        continue;
      }
      // Complex expression like "${a}.${b}" - do not touch
      return null;
    }
    return null;
  }

  /**
   * Among the passed JDK compatible external dependency variants, pick the one belonging to the
   * same major line as the current version. A major boundary is never crossed automatically; if the
   * current major has no matching variant, <code>null</code> is returned (and the caller emits an
   * info about the available newer major). If several variants share the current major, the one
   * with the tightest {@code @VersionMaxExcl} bracket still containing the current version is
   * preferred.
   *
   * @param aCandidates
   *        JDK compatible variants sharing groupId/artifactId. Never empty.
   * @param aCurVersion
   *        The version currently used in the pom.xml
   * @return The best matching variant of the current major line or <code>null</code> if none
   *         exists.
   */
  @Nullable
  private static EExternalDependency _selectSameMajorLine (@NonNull final ICommonsList <EExternalDependency> aCandidates,
                                                           @NonNull final Version aCurVersion)
  {
    final int nCurMajor = aCurVersion.getMajor ();
    EExternalDependency aBest = null;
    Version aBestMax = null;
    for (final EExternalDependency aCand : aCandidates)
    {
      // Never cross a major boundary automatically
      if (aCand.getLastPublishedVersion ().getMajor () != nCurMajor)
        continue;

      final String sMax = aCand.getMaxVersionString ();
      final Version aMax = sMax == null ? null : Version.parse (sMax);
      // The current version must be within this variant's upper bound
      if (aMax != null && !aCurVersion.isLT (aMax))
        continue;

      if (aBest == null)
      {
        aBest = aCand;
        aBestMax = aMax;
      }
      else
        if (aBestMax == null && aMax != null)
        {
          // A finite upper bound is tighter than an unbounded one
          aBest = aCand;
          aBestMax = aMax;
        }
        else
          if (aBestMax != null && aMax != null && aMax.isLT (aBestMax))
          {
            // A smaller upper bound is tighter
            aBest = aCand;
            aBestMax = aMax;
          }
    }
    return aBest;
  }

  private static void _collectOwnProperties (@NonNull final IMicroElement eRoot,
                                             @NonNull final ICommonsMap <String, String> aOwnPropRaw)
  {
    // Unconditional properties
    {
      final IMicroElement eProperties = eRoot.getFirstChildElement ("properties");
      if (eProperties != null)
        eProperties.forAllChildElements (eProperty -> aOwnPropRaw.put (eProperty.getTagName (),
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
              bCanUseProfile = Shared.matchesCurrentJDK (eJdk.getTextContentTrimmed ());
          }

          if (bCanUseProfile)
          {
            final IMicroElement eProperties = eProfile.getFirstChildElement ("properties");
            if (eProperties != null)
              eProperties.forAllChildElements (eProperty -> aOwnPropRaw.put (eProperty.getTagName (),
                                                                             eProperty.getTextContentTrimmed ()));
          }
        }
    }
  }

  private static void _collectDependencyUpdates (@NonNull final IProject aProject,
                                                 @NonNull final IMicroElement eRoot,
                                                 @NonNull final ICommonsMap <String, String> aOwnPropRaw,
                                                 @NonNull final ICommonsMap <String, String> aUnionRaw,
                                                 @NonNull final ICommonsMap <String, Replacement> aReplacements)
  {
    final EJDK eProjectJDK = aProject.getMinimumJDKVersion ();

    for (final IMicroNode aNode : new MicroRecursiveIterator (eRoot))
      if (aNode.isElement ())
      {
        final IMicroElement aElement = (IMicroElement) aNode;
        if (!"artifactId".equals (aElement.getLocalName ()))
          continue;

        final IMicroElement aParentEl = (IMicroElement) aElement.getParent ();
        final String sParentLocal = aParentEl.getLocalName ();

        // The <parent> version is handled separately; the project's own <artifactId> is not a
        // dependency
        if ("parent".equals (sParentLocal) || "project".equals (sParentLocal))
          continue;

        final String sVersionRaw = MicroHelper.getChildTextContentTrimmed (aParentEl, "version");
        if (sVersionRaw == null)
        {
          // Version is managed elsewhere (e.g. dependencyManagement / parent POM)
          continue;
        }

        final String sArtifactRaw = aElement.getTextContentTrimmed ();
        final String sGroupID = _resolveVars (MicroHelper.getChildTextContentTrimmed (aParentEl, "groupId"), aUnionRaw);
        final String sArtifactID = _resolveVars (sArtifactRaw, aUnionRaw);
        if (sGroupID == null || sArtifactID == null)
          continue;

        // Determine the literal version currently in use and where to replace it
        final EReplaceKind eKind;
        final String sAnchor;
        final String sCurVerStr;
        if (!sVersionRaw.contains ("$"))
        {
          // Literal version directly on the dependency/plugin
          if (sArtifactRaw.contains ("$"))
          {
            // Cannot anchor the text replacement on a non-literal artifactId
            continue;
          }
          eKind = EReplaceKind.LITERAL;
          sAnchor = sArtifactRaw;
          sCurVerStr = sVersionRaw;
        }
        else
          if (sVersionRaw.startsWith ("${") && sVersionRaw.endsWith ("}") && sVersionRaw.indexOf ("${", 2) < 0)
          {
            // Version defined via a single property
            final String sVarName = sVersionRaw.substring (2, sVersionRaw.length () - 1);
            final String [] aTerminal = _findTerminalOwnProperty (sVarName, aOwnPropRaw);
            if (aTerminal == null)
            {
              // Not defined in this pom.xml (e.g. parent POM or ${project.version})
              continue;
            }
            eKind = EReplaceKind.PROPERTY;
            sAnchor = aTerminal[0];
            sCurVerStr = aTerminal[1];
          }
          else
          {
            // Complex expression - do not touch
            continue;
          }

        if (Shared.isSnapshotVersion (sCurVerStr))
          continue;

        final Version aCurVer = Version.parse (sCurVerStr);

        // Determine the desired latest version
        String sNewVerStr = null;
        if (Shared.isSupportedGroupID (sGroupID))
        {
          // A helger (or partner) project
          final IProject aRef = ProjectList.getProjectOfName (sArtifactID);
          if (aRef == null || aRef.isDeprecated () || !aRef.isPublished ())
            continue;
          // Avoid bumping to a version requiring a newer JDK than this project supports
          if (!aRef.getMinimumJDKVersion ().isCompatibleToRuntimeVersion (eProjectJDK))
            continue;
          final String sLastPublished = aRef.getLastPublishedVersionString ();
          if (Shared.isSnapshotVersion (sLastPublished))
            continue;
          if (aCurVer.isLT (aRef.getLastPublishedVersion ()))
            sNewVerStr = sLastPublished;
        }
        else
        {
          // A known external dependency
          final List <EExternalDependency> aAll = EExternalDependency.findAll (sGroupID, sArtifactID);
          final ICommonsList <EExternalDependency> aJdkOk = new CommonsArrayList <> ();
          for (final EExternalDependency aDep : aAll)
            if (aDep.getMinimumJDKVersion ().isCompatibleToRuntimeVersion (eProjectJDK))
              aJdkOk.add (aDep);
          if (aJdkOk.isEmpty ())
            continue;

          // Only ever bump within the current major line
          final EExternalDependency aSel = _selectSameMajorLine (aJdkOk, aCurVer);

          // Inform about a newer major line being available (independent of any bump)
          EExternalDependency aNewerMajor = null;
          for (final EExternalDependency aDep : aJdkOk)
            if (aDep.getLastPublishedVersion ().getMajor () > aCurVer.getMajor ())
              if (aNewerMajor == null || aDep.getLastPublishedVersion ().isLT (aNewerMajor.getLastPublishedVersion ()))
                aNewerMajor = aDep;

          // Skip for now
          if (false)
            if (aNewerMajor != null)
              _info (aProject,
                     sGroupID +
                               "::" +
                               sArtifactID +
                               ": staying on major line " +
                               aCurVer.getMajor () +
                               " (" +
                               sCurVerStr +
                               "), but newer major version " +
                               aNewerMajor.getDisplayNameWithVersion () +
                               " is available");

          if (aSel != null && aCurVer.isLT (aSel.getLastPublishedVersion ()))
            sNewVerStr = aSel.getLastPublishedVersionString ();
        }

        if (sNewVerStr == null || sNewVerStr.equals (sCurVerStr))
          continue;

        final Replacement aReplacement = new Replacement (eKind, sAnchor, sCurVerStr, sNewVerStr);
        aReplacements.putIfAbsent (aReplacement.getKey (), aReplacement);
      }
  }

  private static void _collectParentUpdate (@NonNull final IMicroElement eRoot,
                                            @NonNull final ICommonsMap <String, Replacement> aReplacements)
  {
    // parent POM present?
    final IMicroElement eParent = eRoot.getFirstChildElement ("parent");
    if (eParent != null)
    {
      final String sGroupID = MicroHelper.getChildTextContentTrimmed (eParent, "groupId");
      final String sArtifactID = MicroHelper.getChildTextContentTrimmed (eParent, "artifactId");
      final String sVersion = MicroHelper.getChildTextContentTrimmed (eParent, "version");

      // Use only our own parent POM
      if (Shared.PARENT_POM_GROUPID.equals (sGroupID) && Shared.PARENT_POM_ARTIFACTID.equals (sArtifactID))
      {
        if (sVersion == null || sVersion.contains ("$") || Shared.isSnapshotVersion (sVersion))
          return;

        final String sLastPublished = Shared.getParentPOMVersionString ();
        if (Version.parse (sVersion).isLT (Shared.getParentPOMVersion ()))
        {
          final Replacement aReplacement = new Replacement (EReplaceKind.PARENT, null, sVersion, sLastPublished);
          aReplacements.putIfAbsent (aReplacement.getKey (), aReplacement);
        }
      }
    }
  }

  private static void _updateProject (@NonNull final IProject aProject)
  {
    // pom.xml exists?
    final File aPOMFile = aProject.getPOMFile ();
    if (!aPOMFile.exists ())
    {
      _warn (aProject, "POM file " + aPOMFile.getAbsolutePath () + " does not exist");
      return;
    }

    // pom.xml is XML?
    final IMicroDocument aDoc = MicroReader.readMicroXML (aPOMFile);
    if (aDoc == null)
    {
      _warn (aProject, "Failed to read POM file " + aPOMFile.getAbsolutePath ());
      return;
    }
    final IMicroElement eRoot = aDoc.getDocumentElement ();

    // Predefined and own properties (raw)
    final ICommonsMap <String, String> aOwnPropRaw = new CommonsLinkedHashMap <> ();
    _collectOwnProperties (eRoot, aOwnPropRaw);

    // Get own coordinates
    final ICommonsMap <String, String> aUnionRaw = new CommonsLinkedHashMap <> (aOwnPropRaw);
    {
      String sGroupID = MicroHelper.getChildTextContentTrimmed (eRoot, "groupId");
      if (sGroupID == null)
        sGroupID = MicroHelper.getChildTextContent (eRoot.getFirstChildElement ("parent"), "groupId");
      if (sGroupID != null)
        aUnionRaw.put ("project.groupId", sGroupID);

      final String sArtifactID = MicroHelper.getChildTextContentTrimmed (eRoot, "artifactId");
      if (sArtifactID != null)
        aUnionRaw.put ("project.artifactId", sArtifactID);

      String sVersion = MicroHelper.getChildTextContentTrimmed (eRoot, "version");
      if (sVersion == null)
        sVersion = MicroHelper.getChildTextContent (eRoot.getFirstChildElement ("parent"), "version");
      if (sVersion != null)
        aUnionRaw.put ("project.version", sVersion);
    }

    // Collect all required replacements
    final ICommonsMap <String, Replacement> aReplacements = new CommonsLinkedHashMap <> ();
    _collectParentUpdate (eRoot, aReplacements);
    _collectDependencyUpdates (aProject, eRoot, aOwnPropRaw, aUnionRaw, aReplacements);

    if (aReplacements.isEmpty ())
      return;

    // Apply them on the raw text to preserve formatting
    String sContent = SimpleFileIO.getFileAsString (aPOMFile, StandardCharsets.UTF_8);
    if (sContent == null)
    {
      _warn (aProject, "Failed to read POM file content of " + aPOMFile.getAbsolutePath ());
      return;
    }

    final ICommonsList <Replacement> aApplied = new CommonsArrayList <> ();
    for (final Replacement aReplacement : aReplacements.values ())
    {
      final String sNewContent = aReplacement.apply (sContent);
      if (sNewContent == null)
      {
        _warn (aProject, "Failed to locate the text to update for " + aReplacement.getLogText ());
        continue;
      }
      sContent = sNewContent;
      aApplied.add (aReplacement);
    }

    if (aApplied.isEmpty ())
      return;

    if (!DRY_RUN)
      SimpleFileIO.writeFile (aPOMFile, sContent, StandardCharsets.UTF_8);

    s_nModifiedFiles++;
    _info (aProject,
           (DRY_RUN ? "Would update " : "Updated ") +
                     aPOMFile.getAbsolutePath () +
                     " (" +
                     aApplied.size () +
                     " change(s))");
    for (final Replacement aReplacement : aApplied)
      _info (aProject, "  " + aReplacement.getLogText ());
  }

  public static void main (final String [] args)
  {
    int nProjects = 0;
    for (final IProject aProject : ProjectList.getAllProjects (p -> p.getBaseDir ().exists () && !p.isDeprecated ()))
    {
      try
      {
        _updateProject (aProject);
      }
      catch (final RuntimeException ex)
      {
        LOGGER.error ("Error updating " + aProject.getPOMFile ().getAbsolutePath (), ex);
        throw ex;
      }
      ++nProjects;
    }
    LOGGER.info ("Done - " +
                 s_nModifiedFiles +
                 " " +
                 (DRY_RUN ? "would modify" : "modified") +
                 " file(s) for " +
                 nProjects +
                 " projects");
  }
}
