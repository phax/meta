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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.file.FileOperationManager;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EProject;
import com.helger.meta.project.IProject;

/**
 * Create or update the dedicated <code>&lt;name&gt;-bom</code> module of every multi-module project
 * in {@link EProject}, and list it as the first module of the root <code>pom.xml</code>. The
 * missing {@link EProject} entries for the BOM modules are only printed - {@link EProject} is never
 * modified. Git is never touched.
 *
 * @author Philip Helger
 * @see MainVerifyBOMs
 */
public final class MainCreateBOMs extends AbstractProjectMain
{
  /**
   * If <code>true</code> the changes are only logged but the files are not written.
   */
  private static final boolean DRY_RUN = true;

  private static final Logger LOGGER = LoggerFactory.getLogger (MainCreateBOMs.class);

  private static int s_nCreated = 0;
  private static int s_nUpdated = 0;
  private static int s_nUnchanged = 0;
  private static int s_nSkipped = 0;
  private static final ICommonsList <String> MISSING_ENUM_LINES = new CommonsArrayList <> ();

  private static void _createBOM (@NonNull final IProject aRoot)
  {
    if (aRoot.hasNoBOM ())
    {
      _info (aRoot, "Skipping because of @HasNoBOM");
      s_nSkipped++;
      return;
    }

    final File aRootPOMFile = aRoot.getPOMFile ();
    if (!aRoot.getBaseDir ().isDirectory () || !aRootPOMFile.isFile ())
    {
      _warn (aRoot, "Skipping because root pom.xml " + aRootPOMFile.getAbsolutePath () + " does not exist");
      s_nSkipped++;
      return;
    }

    final BOMModel aModel = BOMBuilder.createModel (aRoot);
    if (aModel.members ().isEmpty ())
    {
      _warn (aRoot, "Skipping because no child project qualifies as BOM member");
      s_nSkipped++;
      return;
    }

    // BOM pom.xml
    final String sBOMArtifactID = aModel.artifactID ();
    final File aBOMDir = new File (aRoot.getBaseDir (), sBOMArtifactID);
    final File aBOMPOMFile = new File (aBOMDir, "pom.xml");
    final String sNewBOMContent = BOMBuilder.createPOMContent (aModel);
    final String sOldBOMContent = aBOMPOMFile.isFile () ? SimpleFileIO.getFileAsString (aBOMPOMFile,
                                                                                        StandardCharsets.UTF_8) : null;
    if (sNewBOMContent.equals (sOldBOMContent))
    {
      _info (aRoot, "BOM " + aBOMPOMFile.getAbsolutePath () + " is up to date");
      s_nUnchanged++;
    }
    else
    {
      final boolean bCreate = sOldBOMContent == null;
      if (!DRY_RUN)
      {
        FileOperationManager.INSTANCE.createDirRecursiveIfNotExisting (aBOMDir);
        SimpleFileIO.writeFile (aBOMPOMFile, sNewBOMContent, StandardCharsets.UTF_8);
      }
      _info (aRoot,
             (DRY_RUN ? "Would " + (bCreate ? "create " : "update ") : bCreate ? "Created " : "Updated ") +
                    aBOMPOMFile.getAbsolutePath () +
                    " with " +
                    aModel.members ().size () +
                    " member(s)");
      if (DRY_RUN)
        LOGGER.info ("Content of " + aBOMPOMFile.getAbsolutePath () + ":\n" + sNewBOMContent);
      if (bCreate)
        s_nCreated++;
      else
        s_nUpdated++;
    }

    // Root pom.xml <modules>
    final String sRootContent = SimpleFileIO.getFileAsString (aRootPOMFile, StandardCharsets.UTF_8);
    final String sNewRootContent = BOMBuilder.insertFirstModule (sRootContent, sBOMArtifactID);
    if (sNewRootContent == null)
      _warn (aRoot, "Failed to locate a supported top-level <modules> element in " + aRootPOMFile.getAbsolutePath ());
    else
      if (!sNewRootContent.equals (sRootContent))
      {
        if (!DRY_RUN)
          SimpleFileIO.writeFile (aRootPOMFile, sNewRootContent, StandardCharsets.UTF_8);
        _info (aRoot,
               (DRY_RUN ? "Would add " : "Added ") +
                      "<module>" +
                      sBOMArtifactID +
                      "</module> as first module to " +
                      aRootPOMFile.getAbsolutePath ());
      }

    // EProject entry
    if (BOMBuilder.findBOMProject (aRoot) == null)
      MISSING_ENUM_LINES.add (BOMBuilder.getBOMEnumLine (aRoot));
  }

  public static void main (final String [] args)
  {
    for (final EProject eProject : EProject.values ())
      if (BOMBuilder.isBOMCandidate (eProject))
        _createBOM (eProject);

    if (MISSING_ENUM_LINES.isNotEmpty ())
    {
      final StringBuilder aSB = new StringBuilder ("Missing EProject entries (add directly after the root entry):");
      for (final String sLine : MISSING_ENUM_LINES)
        aSB.append ("\n  ").append (sLine);
      LOGGER.info (aSB.toString ());
    }

    LOGGER.info ("Done - " +
                 (DRY_RUN ? "[DRY RUN] " : "") +
                 s_nCreated +
                 " created, " +
                 s_nUpdated +
                 " updated, " +
                 s_nUnchanged +
                 " unchanged, " +
                 s_nSkipped +
                 " skipped");
  }
}
