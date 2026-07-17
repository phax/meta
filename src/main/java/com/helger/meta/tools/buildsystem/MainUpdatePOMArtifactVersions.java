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
import com.helger.io.file.FileHelper;
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
 * the version in the file is <b>strictly lower</b> than the known latest version. A
 * <code>-SNAPSHOT</code> version whose base version was already released (e.g.
 * <code>10.0.0-SNAPSHOT</code> when <code>10.0.0</code> is the latest version) is also rewritten to
 * the release version; equal, newer and other pre-release versions are left untouched.
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
    // The pom.xml file to modify. May be the project's own pom.xml or an ancestor pom.xml (for a
    // property defined in a parent POM of the same repository).
    private final File m_aTargetFile;
    // artifactId for LITERAL, property name for PROPERTY, unused for PARENT
    private final String m_sAnchor;
    private final String m_sOldVersion;
    private final String m_sNewVersion;

    Replacement (@NonNull final EReplaceKind eKind,
                 @NonNull final File aTargetFile,
                 @Nullable final String sAnchor,
                 @NonNull final String sOldVersion,
                 @NonNull final String sNewVersion)
    {
      m_eKind = eKind;
      m_aTargetFile = aTargetFile;
      m_sAnchor = sAnchor;
      m_sOldVersion = sOldVersion;
      m_sNewVersion = sNewVersion;
    }

    @NonNull
    File getTargetFile ()
    {
      return m_aTargetFile;
    }

    @NonNull
    String getKey ()
    {
      return m_aTargetFile.getAbsolutePath () + "|" + m_eKind + "|" + StringHelper.getNotNull (m_sAnchor) + "|" + m_sOldVersion;
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
   * A single pom.xml in the parent chain of a project, together with the raw properties it defines.
   *
   * @author Philip Helger
   */
  private static final class PomLayer
  {
    private final File m_aFile;
    private final IMicroElement m_aRoot;
    private final ICommonsMap <String, String> m_aOwnProps;

    PomLayer (@NonNull final File aFile, @NonNull final IMicroElement aRoot)
    {
      m_aFile = aFile;
      m_aRoot = aRoot;
      m_aOwnProps = new CommonsLinkedHashMap <> ();
      Shared.forEachActiveProperty (aRoot, m_aOwnProps::put);
    }
  }

  /**
   * The property that actually holds a literal version value, together with the pom.xml file it is
   * defined in.
   *
   * @author Philip Helger
   */
  private static final class TerminalProperty
  {
    private final String m_sName;
    private final String m_sValue;
    private final File m_aFile;

    TerminalProperty (@NonNull final String sName, @NonNull final String sValue, @NonNull final File aFile)
    {
      m_sName = sName;
      m_sValue = sValue;
      m_aFile = aFile;
    }
  }

  /**
   * Build the list of pom.xml layers of a project, starting with the project's own pom.xml, followed
   * by its parent POMs (resolved via <code>&lt;relativePath&gt;</code>) as long as they can be read.
   * The chain naturally stops at the repository boundary, because the shared external parent POM is
   * not reachable via a relative path.
   *
   * @param aOwnPOMFile
   *        The project's own pom.xml file
   * @param eOwnRoot
   *        The already parsed root element of the project's own pom.xml
   * @return The ordered list of layers, nearest (own) first. Never empty.
   */
  @NonNull
  private static File _canonical (@NonNull final File aFile)
  {
    final File aCanonical = FileHelper.getCanonicalFileOrNull (aFile);
    return aCanonical != null ? aCanonical : aFile.getAbsoluteFile ();
  }

  @NonNull
  private static ICommonsList <PomLayer> _buildLayers (@NonNull final File aOwnPOMFile,
                                                       @NonNull final IMicroElement eOwnRoot)
  {
    final ICommonsList <PomLayer> aLayers = new CommonsArrayList <> ();
    final ICommonsList <String> aVisited = new CommonsArrayList <> ();

    File aFile = _canonical (aOwnPOMFile);
    IMicroElement eRoot = eOwnRoot;
    for (int nDepth = 0; nDepth < MAX_DEPTH; ++nDepth)
    {
      // Avoid cycles
      if (aVisited.contains (aFile.getAbsolutePath ()))
        break;
      aVisited.add (aFile.getAbsolutePath ());

      aLayers.add (new PomLayer (aFile, eRoot));

      final File aParentFile = Shared.getParentPOMFileOrNull (aFile, eRoot);
      if (aParentFile == null)
        break;

      final IMicroDocument aParentDoc = MicroReader.readMicroXML (aParentFile);
      if (aParentDoc == null)
        break;

      aFile = aParentFile;
      eRoot = aParentDoc.getDocumentElement ();
    }
    return aLayers;
  }

  /**
   * For a version reference of the form <code>${name}</code>, follow potential property chains
   * across the passed pom.xml layers (own pom.xml plus parent POMs) and return the property that
   * actually holds the literal version value, together with the file it is defined in. A child
   * definition overrides a parent definition (Maven semantics).
   *
   * @param sVarName
   *        The initial property name
   * @param aLayers
   *        The pom.xml layers, nearest first
   * @return The terminal property or <code>null</code> if it is not defined in the chain or the
   *         chain is not a simple property reference.
   */
  @Nullable
  private static TerminalProperty _findTerminalProperty (@NonNull final String sVarName,
                                                         @NonNull final ICommonsList <PomLayer> aLayers)
  {
    String sCur = sVarName;
    for (int nDepth = 0; nDepth < MAX_DEPTH; ++nDepth)
    {
      // Nearest layer that defines this property wins
      PomLayer aDefiningLayer = null;
      for (final PomLayer aLayer : aLayers)
        if (aLayer.m_aOwnProps.containsKey (sCur))
        {
          aDefiningLayer = aLayer;
          break;
        }
      if (aDefiningLayer == null)
      {
        // Not defined anywhere in the chain (e.g. defined in the external parent POM)
        return null;
      }

      final String sRaw = aDefiningLayer.m_aOwnProps.get (sCur);
      if (!sRaw.contains ("${"))
      {
        // Found the literal
        return new TerminalProperty (sCur, sRaw, aDefiningLayer.m_aFile);
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

  private static void _collectDependencyUpdates (@NonNull final IProject aProject,
                                                 @NonNull final ICommonsList <PomLayer> aLayers,
                                                 @NonNull final ICommonsMap <String, String> aUnionRaw,
                                                 @NonNull final ICommonsMap <String, Replacement> aReplacements)
  {
    final EJDK eProjectJDK = aProject.getMinimumJDKVersion ();
    final PomLayer aOwnLayer = aLayers.get (0);

    for (final IMicroNode aNode : new MicroRecursiveIterator (aOwnLayer.m_aRoot))
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
        final File aTargetFile;
        final String sAnchor;
        final String sCurVerStr;
        if (!sVersionRaw.contains ("$"))
        {
          // Literal version directly on the dependency/plugin (always in the project's own pom.xml)
          if (sArtifactRaw.contains ("$"))
          {
            // Cannot anchor the text replacement on a non-literal artifactId
            continue;
          }
          eKind = EReplaceKind.LITERAL;
          aTargetFile = aOwnLayer.m_aFile;
          sAnchor = sArtifactRaw;
          sCurVerStr = sVersionRaw;
        }
        else
          if (sVersionRaw.startsWith ("${") && sVersionRaw.endsWith ("}") && sVersionRaw.indexOf ("${", 2) < 0)
          {
            // Version defined via a single property - may live in this pom.xml or a parent POM of
            // the same repository
            final String sVarName = sVersionRaw.substring (2, sVersionRaw.length () - 1);
            final TerminalProperty aTerminal = _findTerminalProperty (sVarName, aLayers);
            if (aTerminal == null)
            {
              // Not defined in the parent chain (e.g. external parent POM or ${project.version})
              continue;
            }
            eKind = EReplaceKind.PROPERTY;
            aTargetFile = aTerminal.m_aFile;
            sAnchor = aTerminal.m_sName;
            sCurVerStr = aTerminal.m_sValue;
          }
          else
          {
            // Complex expression - do not touch
            continue;
          }

        // A "-SNAPSHOT" version whose base version was already released is out of date as well -
        // compare on the base version. Other pre-release versions (alpha, beta, rc, ...) are left
        // untouched.
        final boolean bCurIsSnapshot = sCurVerStr.endsWith (Shared.SUFFIX_SNAPSHOT);
        if (!bCurIsSnapshot && Shared.isSnapshotVersion (sCurVerStr))
          continue;

        final Version aCurVer = Version.parse (bCurIsSnapshot ? StringHelper.trimEnd (sCurVerStr,
                                                                                      Shared.SUFFIX_SNAPSHOT)
                                                              : sCurVerStr);

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
          // For a SNAPSHOT the equal base version means the release is out
          if (bCurIsSnapshot ? aCurVer.isLE (aRef.getLastPublishedVersion ())
                             : aCurVer.isLT (aRef.getLastPublishedVersion ()))
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

          if (aSel != null &&
              (bCurIsSnapshot ? aCurVer.isLE (aSel.getLastPublishedVersion ())
                              : aCurVer.isLT (aSel.getLastPublishedVersion ())))
            sNewVerStr = aSel.getLastPublishedVersionString ();
        }

        if (sNewVerStr == null || sNewVerStr.equals (sCurVerStr))
          continue;

        final Replacement aReplacement = new Replacement (eKind, aTargetFile, sAnchor, sCurVerStr, sNewVerStr);
        aReplacements.putIfAbsent (aReplacement.getKey (), aReplacement);
      }
  }

  private static void _collectParentUpdate (@NonNull final File aOwnPOMFile,
                                            @NonNull final IMicroElement eRoot,
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
        if (sVersion == null || sVersion.contains ("$"))
          return;

        // A "-SNAPSHOT" version whose base version was already released is out of date as well
        final boolean bCurIsSnapshot = sVersion.endsWith (Shared.SUFFIX_SNAPSHOT);
        if (!bCurIsSnapshot && Shared.isSnapshotVersion (sVersion))
          return;

        final String sLastPublished = Shared.getParentPOMVersionString ();
        final Version aCurVer = Version.parse (bCurIsSnapshot ? StringHelper.trimEnd (sVersion,
                                                                                      Shared.SUFFIX_SNAPSHOT)
                                                              : sVersion);
        if (bCurIsSnapshot ? aCurVer.isLE (Shared.getParentPOMVersion ())
                           : aCurVer.isLT (Shared.getParentPOMVersion ()))
        {
          final Replacement aReplacement = new Replacement (EReplaceKind.PARENT, aOwnPOMFile, null, sVersion, sLastPublished);
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

    // Build the pom.xml layer chain (own pom.xml + parent POMs of the same repository)
    final ICommonsList <PomLayer> aLayers = _buildLayers (aPOMFile, eRoot);

    // Union of all properties (nearest definition wins) plus the predefined project coordinates -
    // used to resolve ${...} in groupId/artifactId/version
    final ICommonsMap <String, String> aUnionRaw = new CommonsLinkedHashMap <> ();
    // Far-to-near, so that a nearer definition overrides a farther one
    for (int i = aLayers.size () - 1; i >= 0; --i)
      aUnionRaw.putAll (aLayers.get (i).m_aOwnProps);
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

    // Collect all required replacements (may target the own pom.xml or an ancestor pom.xml)
    final ICommonsMap <String, Replacement> aReplacements = new CommonsLinkedHashMap <> ();
    _collectParentUpdate (aLayers.get (0).m_aFile, eRoot, aReplacements);
    _collectDependencyUpdates (aProject, aLayers, aUnionRaw, aReplacements);

    if (aReplacements.isEmpty ())
      return;

    // Group the replacements by the file they target and apply them file by file
    final ICommonsMap <File, ICommonsList <Replacement>> aByFile = new CommonsLinkedHashMap <> ();
    for (final Replacement aReplacement : aReplacements.values ())
      aByFile.computeIfAbsent (aReplacement.getTargetFile (), k -> new CommonsArrayList <> ()).add (aReplacement);

    aByFile.forEach ( (aFile, aFileReplacements) -> _applyToFile (aProject, aFile, aFileReplacements));
  }

  private static void _applyToFile (@NonNull final IProject aProject,
                                    @NonNull final File aFile,
                                    @NonNull final ICommonsList <Replacement> aReplacements)
  {
    // Apply on the raw text to preserve formatting
    String sContent = SimpleFileIO.getFileAsString (aFile, StandardCharsets.UTF_8);
    if (sContent == null)
    {
      _warn (aProject, "Failed to read POM file content of " + aFile.getAbsolutePath ());
      return;
    }

    final ICommonsList <Replacement> aApplied = new CommonsArrayList <> ();
    for (final Replacement aReplacement : aReplacements)
    {
      final String sNewContent = aReplacement.apply (sContent);
      if (sNewContent == null)
      {
        _warn (aProject,
               "Failed to locate the text to update for " +
                         aReplacement.getLogText () +
                         " in " +
                         aFile.getAbsolutePath ());
        continue;
      }
      sContent = sNewContent;
      aApplied.add (aReplacement);
    }

    if (aApplied.isEmpty ())
      return;

    if (!DRY_RUN)
      SimpleFileIO.writeFile (aFile, sContent, StandardCharsets.UTF_8);

    s_nModifiedFiles++;
    _info (aProject,
           (DRY_RUN ? "Would update " : "Updated ") +
                     aFile.getAbsolutePath () +
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
