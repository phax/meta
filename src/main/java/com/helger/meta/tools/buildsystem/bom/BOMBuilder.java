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

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringReplace;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.project.EHostingPlatform;
import com.helger.meta.project.EProject;
import com.helger.meta.project.EProjectType;
import com.helger.meta.project.IProject;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.util.MicroHelper;

/**
 * Pure logic to derive the dedicated BOM module of a multi-module project from {@link EProject} and
 * the root <code>pom.xml</code> of that project. Nothing in here writes files.
 *
 * @author Philip Helger
 */
public final class BOMBuilder
{
  public static final String PARENT_POM_GROUPID = "com.helger";
  public static final String PARENT_POM_ARTIFACTID = "parent-pom";
  public static final String SUFFIX_PARENT_POM = "-parent-pom";
  public static final String SUFFIX_BOM = "-bom";
  public static final String MEMBER_VERSION = "${project.version}";

  /** The top-level elements copied verbatim from the root pom.xml, in output order */
  public static final ICommonsList <String> METADATA_ELEMENTS = new CommonsArrayList <> ("url",
                                                                                         "inceptionYear",
                                                                                         "licenses",
                                                                                         "scm",
                                                                                         "organization",
                                                                                         "developers",
                                                                                         "issueManagement");

  /**
   * A direct child element of the document element, located in the raw XML text.
   *
   * @param name
   *        The tag name
   * @param start
   *        Index of the opening <code>&lt;</code> (inclusive)
   * @param end
   *        Index after the closing <code>&gt;</code> of the end tag (exclusive)
   * @author Philip Helger
   */
  record TopLevelElement (@NonNull String name, int start, int end)
  {}

  private BOMBuilder ()
  {}

  private static int _indexAfter (@NonNull final String sXML, @NonNull final String sSearch, final int nFrom)
  {
    final int nIndex = sXML.indexOf (sSearch, nFrom);
    if (nIndex < 0)
      throw new IllegalStateException ("Unterminated XML construct - missing '" + sSearch + "'");
    return nIndex + sSearch.length ();
  }

  /**
   * Locate all direct child elements of the document element in the raw XML text. Comments,
   * processing instructions and CDATA sections are skipped. This is a minimal scanner sufficient
   * for pom.xml files - it does not support <code>&gt;</code> inside attribute values.
   *
   * @param sXML
   *        The raw XML text
   * @return The top-level elements in document order. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  static ICommonsList <TopLevelElement> getAllTopLevelElements (@NonNull final String sXML)
  {
    final ICommonsList <TopLevelElement> ret = new CommonsArrayList <> ();
    final int nLen = sXML.length ();
    int nDepth = 0;
    String sCurName = null;
    int nCurStart = -1;
    int i = 0;
    while (i < nLen)
    {
      final int nLT = sXML.indexOf ('<', i);
      if (nLT < 0)
        break;
      if (sXML.startsWith ("<!--", nLT))
      {
        i = _indexAfter (sXML, "-->", nLT + 4);
        continue;
      }
      if (sXML.startsWith ("<![CDATA[", nLT))
      {
        i = _indexAfter (sXML, "]]>", nLT + 9);
        continue;
      }
      if (sXML.startsWith ("<?", nLT))
      {
        i = _indexAfter (sXML, "?>", nLT + 2);
        continue;
      }
      if (sXML.startsWith ("<!", nLT))
      {
        // DOCTYPE
        i = _indexAfter (sXML, ">", nLT + 2);
        continue;
      }

      final int nTagEnd = _indexAfter (sXML, ">", nLT + 1);
      if (sXML.charAt (nLT + 1) == '/')
      {
        // End tag
        nDepth--;
        if (nDepth == 1 && sCurName != null)
        {
          ret.add (new TopLevelElement (sCurName, nCurStart, nTagEnd));
          sCurName = null;
        }
      }
      else
      {
        // Start tag
        final boolean bSelfClosing = sXML.charAt (nTagEnd - 2) == '/';
        int nNameEnd = nLT + 1;
        while (nNameEnd < nTagEnd - 1 &&
               !Character.isWhitespace (sXML.charAt (nNameEnd)) &&
               sXML.charAt (nNameEnd) != '/' &&
               sXML.charAt (nNameEnd) != '>')
          nNameEnd++;
        final String sName = sXML.substring (nLT + 1, nNameEnd);
        if (nDepth == 1)
        {
          if (bSelfClosing)
            ret.add (new TopLevelElement (sName, nLT, nTagEnd));
          else
          {
            sCurName = sName;
            nCurStart = nLT;
          }
        }
        if (!bSelfClosing)
          nDepth++;
      }
      i = nTagEnd;
    }
    return ret;
  }

  @Nullable
  static TopLevelElement findTopLevelElement (@NonNull final String sXML, @NonNull final String sName)
  {
    return getAllTopLevelElements (sXML).findFirst (x -> x.name ().equals (sName));
  }

  @NonNull
  private static String _normaliseLineEndings (@NonNull final String s)
  {
    return StringReplace.replaceAll (s, "\r\n", "\n");
  }

  /**
   * @param sXML
   *        The raw root pom.xml text
   * @return The first comment between the XML declaration and the document element, verbatim, or
   *         <code>null</code> if there is none.
   */
  @Nullable
  private static String _getLicenseHeader (@NonNull final String sXML)
  {
    final int nProject = sXML.indexOf ("<project");
    if (nProject < 0)
      return null;
    final int nStart = sXML.indexOf ("<!--");
    if (nStart < 0 || nStart > nProject)
      return null;
    final int nEnd = _indexAfter (sXML, "-->", nStart + 4);
    return _normaliseLineEndings (sXML.substring (nStart, nEnd));
  }

  /**
   * Derive the BOM artifactId from the root artifactId.
   *
   * @param sRootArtifactID
   *        The artifactId of the root project, e.g. <code>ph-web-parent-pom</code>
   * @param sBaseDirName
   *        The directory name of the root project, e.g. <code>ph-web</code>. Used if the root
   *        artifactId does not end with {@value #SUFFIX_PARENT_POM}.
   * @return The BOM artifactId, e.g. <code>ph-web-bom</code>
   */
  @NonNull
  @Nonempty
  public static String getBOMArtifactID (@NonNull @Nonempty final String sRootArtifactID,
                                         @NonNull @Nonempty final String sBaseDirName)
  {
    if (sRootArtifactID.endsWith (SUFFIX_PARENT_POM))
      return sRootArtifactID.substring (0, sRootArtifactID.length () - SUFFIX_PARENT_POM.length ()) + SUFFIX_BOM;
    return sBaseDirName + SUFFIX_BOM;
  }

  @NonNull
  @Nonempty
  public static String getBOMArtifactID (@NonNull final IProject aRoot)
  {
    return getBOMArtifactID (aRoot.getMavenArtifactID (), aRoot.getBaseDir ().getName ());
  }

  /**
   * The basic BOM membership rule.
   *
   * @param eProjectType
   *        The type of the child project
   * @param bIsExcludedFromBOM
   *        <code>true</code> if the child carries <code>@IsExcludedFromBOM</code>. Takes
   *        precedence.
   * @param bIsIncludedInBOM
   *        <code>true</code> if the child carries <code>@IsIncludedInBOM</code>
   * @return <code>true</code> if the child is part of the BOM
   */
  public static boolean isBOMMember (@NonNull final EProjectType eProjectType,
                                     final boolean bIsExcludedFromBOM,
                                     final boolean bIsIncludedInBOM)
  {
    if (bIsExcludedFromBOM)
      return false;
    if (bIsIncludedInBOM)
      return true;
    return eProjectType == EProjectType.JAVA_LIBRARY || eProjectType == EProjectType.RESOURCES_ONLY;
  }

  /**
   * @param aChild
   *        The child project to check
   * @param sBOMArtifactID
   *        The artifactId of the BOM of the root project. The BOM itself is never a member.
   * @return <code>true</code> if the child is part of the BOM
   */
  public static boolean isBOMMember (@NonNull final IProject aChild, @NonNull final String sBOMArtifactID)
  {
    if (aChild.getMavenArtifactID ().equals (sBOMArtifactID))
      return false;
    return isBOMMember (aChild.getProjectType (), aChild.isExcludedFromBOM (), aChild.isIncludedInBOM ());
  }

  /**
   * @param aRoot
   *        The root project
   * @return All {@link EProject} entries having the passed project as parent, in declaration order,
   *         including the BOM entry if present.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsList <IProject> getAllChildProjects (@NonNull final IProject aRoot)
  {
    final ICommonsList <IProject> ret = new CommonsArrayList <> ();
    for (final EProject e : EProject.values ())
      if (e.getParentProject () == aRoot)
        ret.add (e);
    return ret;
  }

  @NonNull
  @ReturnsMutableCopy
  public static ICommonsList <IProject> getAllBOMMemberProjects (@NonNull final IProject aRoot)
  {
    final String sBOMArtifactID = getBOMArtifactID (aRoot);
    final ICommonsList <IProject> ret = new CommonsArrayList <> ();
    for (final IProject aChild : getAllChildProjects (aRoot))
      if (isBOMMember (aChild, sBOMArtifactID))
        ret.add (aChild);
    return ret;
  }

  /**
   * @param aRoot
   *        The root project
   * @return The {@link EProject} entry of the BOM module of the passed root project or
   *         <code>null</code> if there is none.
   */
  @Nullable
  public static IProject findBOMProject (@NonNull final IProject aRoot)
  {
    final String sBOMArtifactID = getBOMArtifactID (aRoot);
    return getAllChildProjects (aRoot).findFirst (x -> x.getMavenArtifactID ().equals (sBOMArtifactID));
  }

  /**
   * Check if the passed project is a multi-module root project that is considered for a BOM. This
   * does not consider <code>@HasNoBOM</code>, so that the verifier can still check these projects.
   *
   * @param aProject
   *        The project to check
   * @return <code>true</code> if it is an active, public, GitHub hosted root project with at least
   *         one child project other than the BOM itself.
   */
  public static boolean isBOMCandidate (@NonNull final IProject aProject)
  {
    if (aProject.getParentProject () != null ||
        aProject.isDeprecated () ||
        aProject.isGitHubPrivate () ||
        aProject.getHostingPlatform () != EHostingPlatform.GITHUB)
      return false;

    final String sBOMArtifactID = getBOMArtifactID (aProject);
    return getAllChildProjects (aProject).containsAny (x -> !x.getMavenArtifactID ().equals (sBOMArtifactID));
  }

  /**
   * @param aRoot
   *        The root project. Must be an {@link EProject}.
   * @return The {@link EProject} enum constant line to declare the BOM module, e.g.
   *         <code>PH_WEB_BOM (PH_WEB_PARENT_POM, "ph-web-bom", EProjectType.MAVEN_POM),</code>
   */
  @NonNull
  @Nonempty
  public static String getBOMEnumLine (@NonNull final IProject aRoot)
  {
    if (!(aRoot instanceof final EProject eRoot))
      throw new IllegalArgumentException ("Not an EProject: " + aRoot);
    final String sBOMArtifactID = getBOMArtifactID (aRoot);
    final String sConstantName = sBOMArtifactID.toUpperCase (Locale.ROOT).replace ('-', '_').replace ('.', '_');
    return sConstantName + " (" + eRoot.name () + ", \"" + sBOMArtifactID + "\", EProjectType.MAVEN_POM),";
  }

  /**
   * Create the BOM model from the passed root pom.xml content.
   *
   * @param aRoot
   *        The root project
   * @param sRootPOMContent
   *        The raw content of the root pom.xml
   * @param sParentPOMVersion
   *        The version of <code>com.helger:parent-pom</code> to use
   * @return The model and never <code>null</code>.
   * @throws IllegalStateException
   *         if the root pom.xml cannot be parsed or has no version
   */
  @NonNull
  public static BOMModel createModel (@NonNull final IProject aRoot,
                                      @NonNull final String sRootPOMContent,
                                      @NonNull @Nonempty final String sParentPOMVersion)
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (sRootPOMContent);
    if (aDoc == null)
      throw new IllegalStateException ("Failed to parse root pom.xml of " + aRoot.getProjectName ());
    final String sVersion = MicroHelper.getChildTextContentTrimmed (aDoc.getDocumentElement (), "version");
    if (StringHelper.isEmpty (sVersion))
      throw new IllegalStateException ("Root pom.xml of " + aRoot.getProjectName () + " has no <version>");

    final ICommonsList <TopLevelElement> aTopLevel = getAllTopLevelElements (sRootPOMContent);
    final ICommonsList <String> aMetadataBlocks = new CommonsArrayList <> ();
    for (final String sElementName : METADATA_ELEMENTS)
    {
      final TopLevelElement aElement = aTopLevel.findFirst (x -> x.name ().equals (sElementName));
      if (aElement != null)
        aMetadataBlocks.add (_normaliseLineEndings (sRootPOMContent.substring (aElement.start (), aElement.end ())));
    }

    final ICommonsList <BOMModel.Member> aMembers = new CommonsArrayList <> ();
    for (final IProject aChild : getAllBOMMemberProjects (aRoot))
      aMembers.add (new BOMModel.Member (aChild.getMavenGroupID (), aChild.getMavenArtifactID ()));

    final String sBOMArtifactID = getBOMArtifactID (aRoot);
    return new BOMModel (sParentPOMVersion,
                         aRoot.getMavenGroupID (),
                         sBOMArtifactID,
                         sVersion,
                         sBOMArtifactID,
                         "Bill of Materials for all " + aRoot.getBaseDir ().getName () + " artifacts",
                         _getLicenseHeader (sRootPOMContent),
                         aMetadataBlocks,
                         aMembers);
  }

  /**
   * Create the BOM model from the root pom.xml on disk, using the last published version of
   * {@link EProject#PH_PARENT_POM} as the parent version.
   *
   * @param aRoot
   *        The root project
   * @return The model and never <code>null</code>.
   * @throws IllegalStateException
   *         if the root pom.xml cannot be read
   */
  @NonNull
  public static BOMModel createModel (@NonNull final IProject aRoot)
  {
    final String sContent = SimpleFileIO.getFileAsString (aRoot.getPOMFile (), StandardCharsets.UTF_8);
    if (sContent == null)
      throw new IllegalStateException ("Failed to read " + aRoot.getPOMFile ().getAbsolutePath ());
    return createModel (aRoot, sContent, EProject.PH_PARENT_POM.getLastPublishedVersionString ());
  }

  /**
   * Create the complete BOM pom.xml text. The output is deterministic: same model, same bytes.
   *
   * @param aModel
   *        The model to serialise
   * @return The pom.xml content with <code>\n</code> line endings
   */
  @NonNull
  public static String createPOMContent (@NonNull final BOMModel aModel)
  {
    final StringBuilder aSB = new StringBuilder ();
    aSB.append ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    if (aModel.licenseHeader () != null)
      aSB.append (aModel.licenseHeader ()).append ('\n');
    aSB.append ("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n");
    aSB.append ("  <modelVersion>4.0.0</modelVersion>\n");
    aSB.append ("  <parent>\n");
    aSB.append ("    <groupId>").append (PARENT_POM_GROUPID).append ("</groupId>\n");
    aSB.append ("    <artifactId>").append (PARENT_POM_ARTIFACTID).append ("</artifactId>\n");
    aSB.append ("    <version>").append (aModel.parentVersion ()).append ("</version>\n");
    aSB.append ("    <relativePath />\n");
    aSB.append ("  </parent>\n");
    aSB.append ("  <groupId>").append (aModel.groupID ()).append ("</groupId>\n");
    aSB.append ("  <artifactId>").append (aModel.artifactID ()).append ("</artifactId>\n");
    aSB.append ("  <version>").append (aModel.version ()).append ("</version>\n");
    aSB.append ("  <packaging>pom</packaging>\n");
    aSB.append ("  <name>").append (aModel.name ()).append ("</name>\n");
    aSB.append ("  <description>").append (aModel.description ()).append ("</description>\n");
    for (final String sBlock : aModel.metadataBlocks ())
      aSB.append ("  ").append (sBlock).append ('\n');
    aSB.append ("  <dependencyManagement>\n");
    aSB.append ("    <dependencies>\n");
    for (final BOMModel.Member aMember : aModel.members ())
    {
      aSB.append ("      <dependency>\n");
      aSB.append ("        <groupId>").append (aMember.groupID ()).append ("</groupId>\n");
      aSB.append ("        <artifactId>").append (aMember.artifactID ()).append ("</artifactId>\n");
      aSB.append ("        <version>").append (MEMBER_VERSION).append ("</version>\n");
      aSB.append ("      </dependency>\n");
    }
    aSB.append ("    </dependencies>\n");
    aSB.append ("  </dependencyManagement>\n");
    aSB.append ("</project>\n");
    return aSB.toString ();
  }

  /**
   * Check if the passed module is listed in the top-level <code>&lt;modules&gt;</code> of the
   * passed root pom.xml text. Commented-out modules are ignored.
   *
   * @param sRootPOMContent
   *        The raw root pom.xml text
   * @param sModule
   *        The module name
   * @return <code>true</code> if it is listed
   */
  public static boolean containsTopLevelModule (@NonNull final String sRootPOMContent, @NonNull final String sModule)
  {
    final TopLevelElement aModules = findTopLevelElement (sRootPOMContent, "modules");
    if (aModules == null)
      return false;
    final String sBlock = sRootPOMContent.substring (aModules.start (), aModules.end ())
                                         .replaceAll ("(?s)<!--.*?-->", "");
    return Pattern.compile ("<module>\\s*" + Pattern.quote (sModule) + "\\s*</module>").matcher (sBlock).find ();
  }

  /**
   * Insert the passed module as the first entry of the top-level <code>&lt;modules&gt;</code> of
   * the passed root pom.xml text. Only the text is modified, so formatting and comments are
   * preserved. The indentation and line ending of the existing first line inside
   * <code>&lt;modules&gt;</code> are reused.
   *
   * @param sRootPOMContent
   *        The raw root pom.xml text
   * @param sModule
   *        The module to insert
   * @return The unchanged text if the module is already listed, the modified text if it was
   *         inserted, or <code>null</code> if there is no top-level <code>&lt;modules&gt;</code>
   *         element or its layout is not supported.
   */
  @Nullable
  public static String insertFirstModule (@NonNull final String sRootPOMContent, @NonNull final String sModule)
  {
    if (containsTopLevelModule (sRootPOMContent, sModule))
      return sRootPOMContent;

    final TopLevelElement aModules = findTopLevelElement (sRootPOMContent, "modules");
    if (aModules == null)
      return null;

    final Matcher aMatcher = Pattern.compile ("<modules>[ \\t]*(\\r?\\n)([ \\t]*)")
                                    .matcher (sRootPOMContent)
                                    .region (aModules.start (), aModules.end ());
    if (!aMatcher.lookingAt ())
      return null;

    final String sNewline = aMatcher.group (1);
    final String sIndent = aMatcher.group (2);
    final int nInsertAt = aMatcher.end ();
    return sRootPOMContent.substring (0, nInsertAt) +
           "<module>" +
           sModule +
           "</module>" +
           sNewline +
           sIndent +
           sRootPOMContent.substring (nInsertAt);
  }
}
