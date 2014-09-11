/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.meta;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.helger.commons.io.file.iterate.FileSystemIterator;
import com.helger.commons.string.StringHelper;

/**
 * Test class for {@link EProject}
 *
 * @author Philip Helger
 */
public class EProjectTest
{
  @Test
  public void testBasic ()
  {
    for (final EProject e : EProject.values ())
    {
      assertTrue (StringHelper.hasText (e.getProjectName ()));
      assertTrue (e.getPOMFile ().exists ());
      assertNotNull (e.getProjectType ());
      assertSame (e, EProject.getFromProjectNameOrNull (e.getProjectName ()));
    }
  }

  @Test
  public void testForMissingEProject ()
  {
    for (final File aFile : new FileSystemIterator (CMeta.GIT_BASE_DIR))
    {
      // Ignore all Wiki directories
      if (aFile.isDirectory () && !aFile.getName ().endsWith (".wiki"))
        assertNotNull (aFile.getName () + " is missing in the project list",
                       EProject.getFromProjectNameOrNull (aFile.getName ()));
    }
  }
}
