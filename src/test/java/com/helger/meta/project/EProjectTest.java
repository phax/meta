/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.helger.commons.io.file.FileSystemIterator;
import com.helger.commons.io.file.IFileFilter;
import com.helger.commons.string.StringHelper;
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
      assertTrue (StringHelper.hasText (e.getProjectName ()));
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
