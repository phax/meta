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
package com.helger.meta.tools.buildsystem.bom;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.string.StringHelper;
import com.helger.base.string.StringImplode;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EProject;
import com.helger.meta.project.EProjectType;
import com.helger.meta.project.IProject;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.util.MicroHelper;
import com.helger.xml.microdom.util.MicroRecursiveIterator;

/**
 * Read-only verification of the dedicated <code>&lt;name&gt;-bom</code> modules created by
 * {@link MainCreateBOMs}, plus drift checks between {@link EProject} and the root
 * <code>pom.xml</code> files. Exits with code 1 if errors were found.
 *
 * @author Philip Helger
 */
public final class MainVerifyBOMs extends AbstractProjectMain
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainVerifyBOMs.class);
  private static final ICommonsList <String> FORBIDDEN_TOP_LEVEL = new CommonsArrayList <> ("dependencies",
                                                                                            "modules",
                                                                                            "build",
                                                                                            "properties");

  /**
   * The groupIds that com.helger:parent-pom deliberately keeps in its dependencyManagement. Every
   * BOM passes them on to its consumers, which is accepted.
   */
  private static final ICommonsList <String> ACCEPTED_PARENT_POM_MANAGED_GROUP_IDS = new CommonsArrayList <> ("org.slf4j",
                                                                                                              "org.junit.jupiter",
                                                                                                              "org.junit.platform",
                                                                                                              "org.junit.vintage",
                                                                                                              "org.apiguardian");

  /**
   * Project types that are deliberately not part of a BOM even though they are deployed (CLIs,
   * webapps, Maven plugins). A deployed non-member of these types is only reported as info.
   */
  private static final ICommonsList <EProjectType> DEPLOYED_NON_MEMBER_TYPES = new CommonsArrayList <> (EProjectType.JAVA_APPLICATION,
                                                                                                        EProjectType.JAVA_WEB_APPLICATION,
                                                                                                        EProjectType.MAVEN_PLUGIN);

  private static int s_nErrors = 0;
  private static int s_nProjectErrors = 0;

  private static void _error (@NonNull final IProject aProject, @NonNull final String sMsg)
  {
    _warn (aProject, "[ERROR] " + sMsg);
    s_nErrors++;
    s_nProjectErrors++;
  }

  private static void _globalError (@NonNull final String sMsg)
  {
    LOGGER.error (sMsg);
    s_nErrors++;
  }

  /**
   * @param eRoot
   *        Root element of a pom.xml
   * @return All <code>&lt;module&gt;</code> entries, top-level first, then all profiles. Never
   *         <code>null</code>.
   */
  @NonNull
  private static ICommonsOrderedSet <String> _getAllModulesInclProfiles (@NonNull final IMicroElement eRoot)
  {
    final ICommonsOrderedSet <String> ret = new CommonsLinkedHashSet <> ();
    final IMicroElement eModules = eRoot.getFirstChildElement ("modules");
    if (eModules != null)
      for (final IMicroElement eModule : eModules.getAllChildElements ("module"))
        ret.add (StringHelper.trimEnd (eModule.getTextContentTrimmed (), "/"));
    final IMicroElement eProfiles = eRoot.getFirstChildElement ("profiles");
    if (eProfiles != null)
      for (final IMicroElement eProfile : eProfiles.getAllChildElements ("profile"))
      {
        final IMicroElement eProfileModules = eProfile.getFirstChildElement ("modules");
        if (eProfileModules != null)
          for (final IMicroElement eModule : eProfileModules.getAllChildElements ("module"))
            ret.add (StringHelper.trimEnd (eModule.getTextContentTrimmed (), "/"));
      }
    return ret;
  }

  /**
   * @param aProject
   *        The project to check
   * @return <code>null</code> if the pom.xml of the project does not skip deployment, otherwise the
   *         reason why it is considered skipped.
   */
  @Nullable
  private static String _getDeploySkipReason (@NonNull final IProject aProject)
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (aProject.getPOMFile ());
    if (aDoc == null)
      return null;
    for (final IMicroNode aNode : new MicroRecursiveIterator (aDoc.getDocumentElement ()))
      if (aNode instanceof final IMicroElement eElement)
      {
        final String sName = eElement.getLocalName ();
        if (("maven.deploy.skip".equals (sName) || "skipPublishing".equals (sName)) &&
            "true".equals (eElement.getTextContentTrimmed ()))
          return "<" + sName + ">true</" + sName + ">";
        if ("plugin".equals (sName) &&
            "maven-deploy-plugin".equals (MicroHelper.getChildTextContentTrimmed (eElement, "artifactId")))
          for (final IMicroNode aPluginNode : new MicroRecursiveIterator (eElement))
            if (aPluginNode instanceof final IMicroElement ePluginElement &&
                "skip".equals (ePluginElement.getLocalName ()) &&
                "true".equals (ePluginElement.getTextContentTrimmed ()))
              return "maven-deploy-plugin <skip>true</skip>";
      }
    return null;
  }

  private static void _checkMemberDeployment (@NonNull final IProject aRoot, @NonNull final String sBOMArtifactID)
  {
    for (final IProject aChild : BOMBuilder.getAllChildProjects (aRoot))
    {
      if (aChild.getMavenArtifactID ().equals (sBOMArtifactID))
        continue;
      if (!aChild.getPOMFile ().isFile ())
        continue;

      final String sSkipReason = _getDeploySkipReason (aChild);
      if (BOMBuilder.isBOMMember (aChild, sBOMArtifactID))
      {
        if (sSkipReason != null)
          _warn (aRoot,
                 "BOM member '" +
                        aChild.getMavenArtifactID () +
                        "' is not deployed (" +
                        sSkipReason +
                        ") - should it carry @IsExcludedFromBOM?");
      }
      else
        if (sSkipReason == null && !aChild.isExcludedFromBOM ())
        {
          if (DEPLOYED_NON_MEMBER_TYPES.contains (aChild.getProjectType ()))
            _info (aRoot,
                   "Non-member '" +
                          aChild.getMavenArtifactID () +
                          "' (" +
                          aChild.getProjectType () +
                          ") is deployed but excluded by type");
          else
            _warn (aRoot,
                   "Non-member '" +
                          aChild.getMavenArtifactID () +
                          "' (" +
                          aChild.getProjectType () +
                          ") is apparently deployed - should it carry @IsIncludedInBOM?");
        }
    }
  }

  private static void _checkModuleDrift (@NonNull final IProject aRoot,
                                         @NonNull final IMicroElement eRootPOM,
                                         @NonNull final String sBOMArtifactID)
  {
    final ICommonsOrderedSet <String> aPOMModules = _getAllModulesInclProfiles (eRootPOM);
    final ICommonsOrderedSet <String> aEProjectModules = new CommonsLinkedHashSet <> ();
    for (final IProject aChild : BOMBuilder.getAllChildProjects (aRoot))
      aEProjectModules.add (aChild.getBaseDir ().getName ());

    for (final String sModule : aPOMModules)
      if (!aEProjectModules.contains (sModule) && !sModule.equals (sBOMArtifactID))
        _warn (aRoot, "Module '" + sModule + "' is in the root pom.xml but not in EProject - EProject stale?");
    for (final String sModule : aEProjectModules)
      if (!aPOMModules.contains (sModule))
        _warn (aRoot, "EProject child '" + sModule + "' is not a module in the root pom.xml - EProject stale?");
  }

  private static void _checkBOMContent (@NonNull final IProject aRoot,
                                        @NonNull final String sRootContent,
                                        @NonNull final IMicroElement eRootPOM,
                                        @NonNull final File aBOMPOMFile)
  {
    final String sBOMContent = SimpleFileIO.getFileAsString (aBOMPOMFile, StandardCharsets.UTF_8);
    final IMicroDocument aBOMDoc = sBOMContent == null ? null : MicroReader.readMicroXML (sBOMContent);
    if (aBOMDoc == null)
    {
      _error (aRoot, "Failed to read BOM " + aBOMPOMFile.getAbsolutePath ());
      return;
    }
    final IMicroElement eBOM = aBOMDoc.getDocumentElement ();
    final int nErrorsBefore = s_nErrors;

    // Parent
    final IMicroElement eParent = eBOM.getFirstChildElement ("parent");
    final String sParentGroupID = MicroHelper.getChildTextContentTrimmed (eParent, "groupId");
    final String sParentArtifactID = MicroHelper.getChildTextContentTrimmed (eParent, "artifactId");
    final String sParentVersion = MicroHelper.getChildTextContentTrimmed (eParent, "version");
    final String sExpectedParentVersion = EProject.PH_PARENT_POM.getLastPublishedVersionString ();
    if (!BOMBuilder.PARENT_POM_GROUPID.equals (sParentGroupID) ||
        !BOMBuilder.PARENT_POM_ARTIFACTID.equals (sParentArtifactID))
      _error (aRoot,
              "BOM parent is '" +
                     sParentGroupID +
                     ":" +
                     sParentArtifactID +
                     "' but must be '" +
                     BOMBuilder.PARENT_POM_GROUPID +
                     ":" +
                     BOMBuilder.PARENT_POM_ARTIFACTID +
                     "'");
    else
      if (!sExpectedParentVersion.equals (sParentVersion))
        _error (aRoot, "BOM parent version is '" + sParentVersion + "' but must be '" + sExpectedParentVersion + "'");

    // GAV
    final String sRootVersion = MicroHelper.getChildTextContentTrimmed (eRootPOM, "version");
    final String sBOMVersion = MicroHelper.getChildTextContentTrimmed (eBOM, "version");
    if (sBOMVersion == null || !sBOMVersion.equals (sRootVersion))
      _error (aRoot, "BOM version is '" + sBOMVersion + "' but root version is '" + sRootVersion + "'");
    final String sBOMGroupID = MicroHelper.getChildTextContentTrimmed (eBOM, "groupId");
    if (!aRoot.getMavenGroupID ().equals (sBOMGroupID))
      _error (aRoot, "BOM groupId is '" + sBOMGroupID + "' but root groupId is '" + aRoot.getMavenGroupID () + "'");

    // Forbidden top-level elements
    for (final String sForbidden : FORBIDDEN_TOP_LEVEL)
      if (eBOM.getFirstChildElement (sForbidden) != null)
        _error (aRoot, "BOM contains forbidden element <" + sForbidden + ">");

    // Members
    final ICommonsOrderedSet <String> aExpectedKeys = new CommonsLinkedHashSet <> ();
    for (final IProject aMember : BOMBuilder.getAllBOMMemberProjects (aRoot))
      aExpectedKeys.add (aMember.getMavenGroupID () + ":" + aMember.getMavenArtifactID ());
    final ICommonsOrderedSet <String> aChildKeys = new CommonsLinkedHashSet <> ();
    for (final IProject aChild : BOMBuilder.getAllChildProjects (aRoot))
      aChildKeys.add (aChild.getMavenGroupID () + ":" + aChild.getMavenArtifactID ());

    final ICommonsOrderedSet <String> aActualKeys = new CommonsLinkedHashSet <> ();
    final IMicroElement eDepMgmt = eBOM.getFirstChildElement ("dependencyManagement");
    final IMicroElement eDepMgmtDeps = eDepMgmt == null ? null : eDepMgmt.getFirstChildElement ("dependencies");
    if (eDepMgmtDeps != null)
      for (final IMicroElement eDep : eDepMgmtDeps.getAllChildElements ("dependency"))
      {
        final String sKey = MicroHelper.getChildTextContentTrimmed (eDep, "groupId") +
                            ":" +
                            MicroHelper.getChildTextContentTrimmed (eDep, "artifactId");
        aActualKeys.add (sKey);
        if ("import".equals (MicroHelper.getChildTextContentTrimmed (eDep, "scope")))
          _error (aRoot, "BOM entry '" + sKey + "' uses <scope>import</scope>");
        final String sVersion = MicroHelper.getChildTextContentTrimmed (eDep, "version");
        if (!BOMBuilder.MEMBER_VERSION.equals (sVersion))
          _error (aRoot,
                  "BOM entry '" +
                         sKey +
                         "' has version '" +
                         sVersion +
                         "' but must be '" +
                         BOMBuilder.MEMBER_VERSION +
                         "'");
        if (!aChildKeys.contains (sKey))
          _error (aRoot, "BOM entry '" + sKey + "' is not a child project of " + aRoot.getProjectName ());
      }

    final ICommonsList <String> aMissing = new CommonsArrayList <> ();
    for (final String sKey : aExpectedKeys)
      if (!aActualKeys.contains (sKey))
        aMissing.add (sKey);
    final ICommonsList <String> aUnexpected = new CommonsArrayList <> ();
    for (final String sKey : aActualKeys)
      if (!aExpectedKeys.contains (sKey) && aChildKeys.contains (sKey))
        aUnexpected.add (sKey);
    if (aMissing.isNotEmpty ())
      _error (aRoot, "BOM is missing member(s): " + StringImplode.getImploded (", ", aMissing));
    if (aUnexpected.isNotEmpty ())
      _error (aRoot, "BOM contains unexpected member(s): " + StringImplode.getImploded (", ", aUnexpected));

    // Compare with the generated content
    final String sExpected = BOMBuilder.createPOMContent (BOMBuilder.createModel (aRoot,
                                                                                  sRootContent,
                                                                                  sExpectedParentVersion));
    if (!sExpected.equals (sBOMContent) && s_nErrors == nErrorsBefore)
    {
      if (sExpected.replaceAll ("\\s+", "").equals (sBOMContent.replaceAll ("\\s+", "")))
        _info (aRoot, "BOM differs from the generated content only in formatting - run MainCreateBOMs");
      else
        _warn (aRoot, "BOM differs from the generated content (e.g. copied metadata or order) - run MainCreateBOMs");
    }
  }

  private static void _verifyProject (@NonNull final IProject aRoot)
  {
    final String sBOMArtifactID = BOMBuilder.getBOMArtifactID (aRoot);
    final File aRootPOMFile = aRoot.getPOMFile ();
    final String sRootContent = SimpleFileIO.getFileAsString (aRootPOMFile, StandardCharsets.UTF_8);
    final IMicroDocument aRootDoc = sRootContent == null ? null : MicroReader.readMicroXML (sRootContent);
    if (aRootDoc == null)
    {
      _error (aRoot, "Failed to read root pom.xml " + aRootPOMFile.getAbsolutePath ());
      return;
    }
    final IMicroElement eRootPOM = aRootDoc.getDocumentElement ();
    final File aBOMDir = new File (aRoot.getBaseDir (), sBOMArtifactID);
    final File aBOMPOMFile = new File (aBOMDir, "pom.xml");
    final boolean bListed = BOMBuilder.containsTopLevelModule (sRootContent, sBOMArtifactID);

    if (aRoot.hasNoBOM ())
    {
      if (aBOMDir.exists () || bListed)
        _error (aRoot, "Project has @HasNoBOM but still has a BOM module '" + sBOMArtifactID + "'");
      _checkModuleDrift (aRoot, eRootPOM, sBOMArtifactID);
      return;
    }

    if (!aBOMPOMFile.isFile ())
      _error (aRoot, "BOM " + aBOMPOMFile.getAbsolutePath () + " is missing");
    if (!bListed)
      _error (aRoot, "BOM module '" + sBOMArtifactID + "' is not listed in the root <modules>");
    else
    {
      final IMicroElement eModules = eRootPOM.getFirstChildElement ("modules");
      final IMicroElement eFirst = eModules == null ? null : eModules.getFirstChildElement ("module");
      if (eFirst == null || !sBOMArtifactID.equals (eFirst.getTextContentTrimmed ()))
        _warn (aRoot, "BOM module '" + sBOMArtifactID + "' is not the first <module> in the root pom.xml");
    }
    if (BOMBuilder.findBOMProject (aRoot) == null)
      _error (aRoot, "No EProject entry for the BOM module - add: " + BOMBuilder.getBOMEnumLine (aRoot));

    for (final IProject aChild : BOMBuilder.getAllChildProjects (aRoot))
      if (aChild.isExcludedFromBOM () && aChild.isIncludedInBOM ())
        _error (aRoot, "Child '" + aChild.getProjectName () + "' carries both @IsExcludedFromBOM and @IsIncludedInBOM");

    if (aBOMPOMFile.isFile ())
      _checkBOMContent (aRoot, sRootContent, eRootPOM, aBOMPOMFile);

    _checkModuleDrift (aRoot, eRootPOM, sBOMArtifactID);
    _checkMemberDeployment (aRoot, sBOMArtifactID);
  }

  private static void _checkParentPOMLeak ()
  {
    final File aParentPOMFile = EProject.PH_PARENT_POM.getPOMFile ();
    final IMicroDocument aDoc = MicroReader.readMicroXML (aParentPOMFile);
    if (aDoc == null)
    {
      LOGGER.warn ("Failed to read " + aParentPOMFile.getAbsolutePath () + " - cannot check for leaks");
      return;
    }
    final IMicroElement eDepMgmt = aDoc.getDocumentElement ().getFirstChildElement ("dependencyManagement");
    final IMicroElement eDeps = eDepMgmt == null ? null : eDepMgmt.getFirstChildElement ("dependencies");
    if (eDeps == null)
      return;

    final ICommonsList <String> aAccepted = new CommonsArrayList <> ();
    final ICommonsList <String> aUnexpected = new CommonsArrayList <> ();
    for (final IMicroElement eDep : eDeps.getAllChildElements ("dependency"))
    {
      final String sGroupID = MicroHelper.getChildTextContentTrimmed (eDep, "groupId");
      final String sKey = sGroupID + ":" + MicroHelper.getChildTextContentTrimmed (eDep, "artifactId");
      if (ACCEPTED_PARENT_POM_MANAGED_GROUP_IDS.contains (sGroupID))
        aAccepted.add (sKey);
      else
        aUnexpected.add (sKey);
    }
    if (aAccepted.isNotEmpty ())
      LOGGER.info ("BOMs pass on the accepted parent-pom dependencyManagement: " +
                   StringImplode.getImploded (", ", aAccepted));
    if (aUnexpected.isNotEmpty ())
      _globalError ("BOMs would leak parent-pom dependencyManagement; flatten-maven-plugin with flattenMode=bom is required. Unexpected entries: " +
                    StringImplode.getImploded (", ", aUnexpected));
  }

  public static void main (final String [] args)
  {
    _checkParentPOMLeak ();

    int nProjects = 0;
    for (final EProject eProject : EProject.values ())
      if (BOMBuilder.isBOMCandidate (eProject))
      {
        _verifyProject (eProject);
        nProjects++;
      }

    LOGGER.info ("Done - verified " +
                 nProjects +
                 " project(s): " +
                 s_nErrors +
                 " error(s), " +
                 (getWarnCount () - s_nProjectErrors) +
                 " warning(s)");
    if (s_nErrors > 0)
      System.exit (1);
  }
}
