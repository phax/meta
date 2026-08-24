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
package com.helger.meta.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.helger.base.string.StringHelper;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.file.FileSystemIterator;
import com.helger.io.file.IFileFilter;
import com.helger.meta.CMeta;

/**
 * Test class for {@link EProject}
 *
 * @author Philip Helger
 */
@Ignore ("for Travis")
public final class EProjectTest
{
  @Test
  public void testBasic ()
  {
    for (final IProject e : EProject.values ())
    {
      assertTrue (StringHelper.isNotEmpty (e.getProjectName ()));
      assertTrue (e.getPOMFile ().getAbsoluteFile () + " does not exist!", e.getPOMFile ().exists ());
      assertNotNull (e.getProjectType ());
      if (e.isPublished ())
      {
        assertNotNull (e.getLastPublishedVersionString ());
        assertNotNull (e.getLastPublishedVersion ());
      }
      else
      {
        assertNull (e.getLastPublishedVersionString ());
        assertNull (e.getLastPublishedVersion ());
      }
    }
  }

  @Test
  public void testTails ()
  {
    // ph-commons: branch "v11" and the dead branch "10.x"
    final ICommonsList <ProjectTail> aCommonsTails = EProject.PH_COMMONS_PARENT_POM.getAllTails ();
    assertEquals (2, aCommonsTails.size ());
    assertTrue (EProject.PH_COMMONS_PARENT_POM.hasTails ());
    assertTrue (EProject.PH_COMMONS_PARENT_POM.hasMaintainedTail ());

    final ProjectTail aTail11 = aCommonsTails.getFirstOrNull ();
    assertEquals (11, aTail11.getMajorVersion ());
    assertEquals (EJDK.JDK11, aTail11.getMinimumJDKVersion ());
    assertTrue (aTail11.isMaintained ());

    final ProjectTail aTail10 = aCommonsTails.getLastOrNull ();
    assertEquals (10, aTail10.getMajorVersion ());
    assertEquals (EJDK.JDK8, aTail10.getMinimumJDKVersion ());
    assertFalse (aTail10.isMaintained ());

    // Modules inherit the tails of their root project
    assertEquals (aCommonsTails, EProject.PH_BASE.getAllTails ());

    // ph-parent-pom: branch "v2.x" and branch "v1.x"
    final ICommonsList <ProjectTail> aParentTails = EProject.PH_PARENT_POM.getAllTails ();
    assertEquals (2, aParentTails.size ());
    assertEquals (2, aParentTails.getFirstOrNull ().getMajorVersion ());
    assertEquals (EJDK.JDK11, aParentTails.getFirstOrNull ().getMinimumJDKVersion ());
    assertEquals (1, aParentTails.getLastOrNull ().getMajorVersion ());
    assertEquals (EJDK.JDK8, aParentTails.getLastOrNull ().getMinimumJDKVersion ());

    // phase2: branch "v5.1" and the superseded branch "5.0.x" - both on JDK 11
    final ICommonsList <ProjectTail> aPhase2Tails = EProject.PHASE2_PARENT_POM.getAllTails ();
    assertEquals (2, aPhase2Tails.size ());
    for (final ProjectTail aTail : aPhase2Tails)
      assertEquals (EJDK.JDK11, aTail.getMinimumJDKVersion ());
    assertTrue (aPhase2Tails.getFirstOrNull ().isMaintained ());
    assertFalse (aPhase2Tails.getLastOrNull ().isMaintained ());

    // A tail baseline may never be newer than the tip baseline
    for (final IProject aProject : EProject.values ())
      for (final ProjectTail aTail : aProject.getAllTails ())
        assertTrue (aProject.getProjectName () + " tail " + aTail.getLastPublishedVersionString (),
                    aTail.getMinimumJDKVersion ().isCompatibleToRuntimeVersion (aProject.getMinimumJDKVersion ()));
  }

  @Test
  public void testForMissingEProject ()
  {
    for (final File aFile : new FileSystemIterator (CMeta.GIT_BASE_DIR).withFilter (IFileFilter.directoryOnly ()))
    {
      String sProjectName = aFile.getName ();

      // ebinterface-ubl-mapping: different GitHub entity
      if (!"ebinterface-ubl-mapping".equals (sProjectName) &&
          !"Holodeck-B2B".equals (sProjectName) &&
          !"junrar".equals (sProjectName))
      {
        // Ignore all Pages and Wiki directories
        sProjectName = StringHelper.trimEnd (sProjectName, IProject.EXTENSION_PAGES_PROJECT);
        sProjectName = StringHelper.trimEnd (sProjectName, IProject.EXTENSION_WIKI_PROJECT);

        assertTrue (sProjectName + " is missing in the project list", ProjectList.containsProjectOfDir (sProjectName));
      }
    }
  }
}
