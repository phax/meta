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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.string.StringReplace;
import com.helger.base.system.ENewLineMode;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EProject;
import com.helger.meta.project.EProjectOwner;
import com.helger.meta.project.EProjectType;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * Update README.md badges for all projects to use consistent Sonatype Central and Javadoc badges
 * wrapped in ph-badge-start/end comment markers.
 *
 * @author Philip Helger
 */
public final class MainUpdateREADMEBadges extends AbstractProjectMain
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainUpdateREADMEBadges.class);
  private static final String BADGE_START = "<!-- ph-badge-start -->";
  private static final String BADGE_END = "<!-- ph-badge-end -->";
  private static final Charset README_CHARSET = StandardCharsets.UTF_8;

  /**
   * Read the first &lt;module&gt; from a multi-module POM and resolve its Maven coordinates from
   * its own pom.xml.
   *
   * @return String array [groupId, artifactId] or <code>null</code>
   */
  @Nullable
  private static String [] _getFirstSubmoduleCoordinates (@NonNull final IProject aProject)
  {
    // Read parent POM to find first module
    final IMicroDocument aDoc = MicroReader.readMicroXML (aProject.getPOMFile ());
    if (aDoc == null)
      return null;

    final IMicroElement eModules = aDoc.getDocumentElement ().getFirstChildElement ("modules");
    if (eModules == null)
      return null;

    final IMicroElement eFirstModule = eModules.getFirstChildElement ("module");
    if (eFirstModule == null)
      return null;

    final String sFirstModuleName = eFirstModule.getTextContentTrimmed ();
    if (sFirstModuleName == null || sFirstModuleName.isEmpty ())
      return null;

    // Read the submodule's pom.xml
    final File fModulePOM = new File (new File (aProject.getBaseDir (), sFirstModuleName), "pom.xml");
    final IMicroDocument aModuleDoc = MicroReader.readMicroXML (fModulePOM);
    if (aModuleDoc == null)
      return null;

    final IMicroElement eRoot = aModuleDoc.getDocumentElement ();

    // Resolve groupId: direct child first, then from parent
    String sGroupID = null;
    IMicroElement eGroupID = eRoot.getFirstChildElement ("groupId");
    if (eGroupID == null)
    {
      final IMicroElement eParent = eRoot.getFirstChildElement ("parent");
      if (eParent != null)
        eGroupID = eParent.getFirstChildElement ("groupId");
    }
    if (eGroupID != null)
      sGroupID = eGroupID.getTextContentTrimmed ();

    // Resolve artifactId
    String sArtifactID = null;
    final IMicroElement eArtifactID = eRoot.getFirstChildElement ("artifactId");
    if (eArtifactID != null)
      sArtifactID = eArtifactID.getTextContentTrimmed ();

    if (sGroupID != null && sArtifactID != null)
      return new String [] { sGroupID, sArtifactID };

    return null;
  }

  @NonNull
  private static String _createBadgeSection (@NonNull final String sSonatypeGroupID,
                                             @NonNull final String sSonatypeArtifactID,
                                             @NonNull final String sJavadocGroupID,
                                             @NonNull final String sJavadocArtifactID)
  {
    return BADGE_START +
           "\n" +
           "[![Sonatype Central](https://maven-badges.sml.io/sonatype-central/" +
           sSonatypeGroupID +
           "/" +
           sSonatypeArtifactID +
           "/badge.svg)](https://maven-badges.sml.io/sonatype-central/" +
           sSonatypeGroupID +
           "/" +
           sSonatypeArtifactID +
           "/)\n" +
           "[![javadoc](https://javadoc.io/badge2/" +
           sJavadocGroupID +
           "/" +
           sJavadocArtifactID +
           "/javadoc.svg)](https://javadoc.io/doc/" +
           sJavadocGroupID +
           "/" +
           sJavadocArtifactID +
           ")\n" +
           BADGE_END +
           "\n";
  }

  /**
   * Find the end of the first line (the h1 heading). Returns the index of the newline character, or
   * -1.
   */
  private static int _findH1End (@NonNull final String sContent)
  {
    if (!sContent.startsWith ("# "))
      return -1;
    return sContent.indexOf ('\n');
  }

  /**
   * Starting from a given index, skip any lines that look like existing badge lines (starting with
   * "[![") or are blank. Returns the index in the content string where the non-badge content
   * starts.
   */
  private static int _skipExistingBadgeLines (@NonNull final String sContent, final int nStartIndex)
  {
    int nPos = nStartIndex;
    while (nPos < sContent.length ())
    {
      // Find end of current line
      final int nLineEnd = sContent.indexOf ('\n', nPos);
      final String sLine;
      if (nLineEnd < 0)
        sLine = sContent.substring (nPos);
      else
        sLine = sContent.substring (nPos, nLineEnd);

      final String sTrimmed = sLine.trim ();

      // Skip blank lines and badge lines
      if (!sTrimmed.isEmpty () && !sTrimmed.startsWith ("[!["))
      {
        break;
      }
      nPos = nLineEnd < 0 ? sContent.length () : nLineEnd + 1;
    }
    return nPos;
  }

  public static void main (final String [] args)
  {
    int nUpdated = 0;

    for (final IProject aProject : ProjectList.getAllProjects (p -> p.isBuildInProject () &&
                                                                    p.getBaseDir ().exists () &&
                                                                    !p.isDeprecated () &&
                                                                    !p.isNestedProject () &&
                                                                    p.isPublished ()))
    {
      if (!aProject.getProjectOwner ().equals (EProjectOwner.DEFAULT_PROJECT_OWNER))
        continue;

      if (aProject == EProject.PH_PARENT_POM)
        continue;

      final File fReadme = new File (aProject.getBaseDir (), "README.md");
      if (!fReadme.exists ())
      {
        _warn (aProject, "README.md does not exist");
        continue;
      }

      String sContent = SimpleFileIO.getFileAsString (fReadme, README_CHARSET);
      if (sContent == null)
      {
        _warn (aProject, "Failed to read README.md");
        continue;
      }

      // Normalize newlines to \n
      sContent = StringReplace.replaceAll (sContent, ENewLineMode.DEFAULT.getText (), "\n").trim ();

      // Determine Sonatype badge coordinates (always from the project itself)
      final String sSonatypeGroupID = aProject.getMavenGroupID ();
      final String sSonatypeArtifactID = aProject.getMavenArtifactID ();

      // Determine Javadoc badge coordinates
      String sJavadocGroupID = sSonatypeGroupID;
      String sJavadocArtifactID = sSonatypeArtifactID;

      if (aProject.getProjectType () == EProjectType.MAVEN_POM)
      {
        final String [] aCoords = _getFirstSubmoduleCoordinates (aProject);
        if (aCoords != null)
        {
          sJavadocGroupID = aCoords[0];
          sJavadocArtifactID = aCoords[1];
        }
        else
        {
          _warn (aProject, "Failed to determine first submodule coordinates for javadoc badge");
        }
      }

      final String sBadgeSection = _createBadgeSection (sSonatypeGroupID,
                                                        sSonatypeArtifactID,
                                                        sJavadocGroupID,
                                                        sJavadocArtifactID);

      final String sNewContent;
      final int nBadgeStart = sContent.indexOf (BADGE_START);
      final int nBadgeEnd = sContent.indexOf (BADGE_END);

      if (nBadgeStart >= 0 && nBadgeEnd >= 0)
      {
        // Replace existing badge section between markers
        sNewContent = sContent.substring (0, nBadgeStart) +
                      sBadgeSection +
                      "\n" +
                      sContent.substring (nBadgeEnd + BADGE_END.length ()).trim ();
      }
      else
      {
        _warn (aProject, "Couldn't find the badge section in README.md");

        // No markers present - need to find h1 and handle existing badges
        final int nH1End = _findH1End (sContent);
        if (nH1End < 0)
        {
          _warn (aProject, "Cannot find h1 heading in README.md");
          continue;
        }

        // Position right after the h1 line (after the newline)
        final int nAfterH1 = nH1End + 1;

        // Skip any existing badge lines and blank lines after h1
        final int nContentStart = _skipExistingBadgeLines (sContent, nAfterH1);

        sNewContent = sContent.substring (0, nAfterH1) +
                      "\n" +
                      sBadgeSection +
                      "\n" +
                      (nContentStart < sContent.length () ? sContent.substring (nContentStart) : "");
      }

      if (!sNewContent.equals (sContent))
      {
        // Convert newlines back to system default
        // Make sure file ends with a newline
        final String sFinalContent = StringReplace.replaceAll (sNewContent, "\n", ENewLineMode.DEFAULT.getText ()) +
                                     ENewLineMode.DEFAULT.getText ();
        SimpleFileIO.writeFile (fReadme, sFinalContent, README_CHARSET);
        _info (aProject, "README.md badges updated");
        nUpdated++;
      }
    }

    LOGGER.info ("Done - " +
                 nUpdated +
                 " file(s) updated, " +
                 getWarnCount () +
                 " warning(s) for " +
                 ProjectList.size () +
                 " projects");
  }
}
