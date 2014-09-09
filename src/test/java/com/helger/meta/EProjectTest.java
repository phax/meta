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
        assertNotNull (aFile.getName () + " is missing", EProject.getFromProjectNameOrNull (aFile.getName ()));
    }
  }
}
