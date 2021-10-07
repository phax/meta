/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.string.StringHelper;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Ensure the src/etc/license-template.txt contains the correct year. Afterwards
 * <code>mvn license:format</code> must be run on all projects!
 *
 * @author Philip Helger
 */
public final class MainUpdateLicenseTemplateYear extends AbstractProjectMain
{
  public static void main (final String [] args)
  {
    final int nThisYear = PDTFactory.getCurrentYear ();
    final String sPrevYear = Integer.toString (nThisYear - 1);
    final String sThisYear = Integer.toString (nThisYear);

    final String sSearch1 = "-" + sPrevYear;
    final String sReplace1 = "-" + sThisYear;
    final String sSearch2 = sPrevYear;
    final String sReplace2 = sPrevYear + "-" + sThisYear;

    for (final IProject aProject : ProjectList.getAllProjects (p -> p.getBaseDir ().exists () && !p.isDeprecated ()))
    {
      final File f = new File (aProject.getBaseDir (), "src/etc/license-template.txt");
      if (!f.exists ())
      {
        System.err.println (f.getAbsolutePath () + " does not exist!");
        continue;
      }

      System.out.println (f.getAbsolutePath ());
      final String sCurrent = SimpleFileIO.getFileAsString (f, StandardCharsets.UTF_8);

      boolean bChange = false;
      String sNew = sCurrent;
      if (sNew.contains (sSearch1))
      {
        sNew = StringHelper.replaceAll (sNew, sSearch1, sReplace1);
        bChange = true;
      }
      else
        if (sNew.contains (sSearch2) && !sNew.contains (sReplace2))
        {
          sNew = StringHelper.replaceAll (sNew, sSearch2, sReplace2);
          bChange = true;
        }

      if (bChange)
      {
        SimpleFileIO.writeFile (f, sNew, StandardCharsets.UTF_8);
        System.out.println ("  changed");
      }
    }
    LOGGER.info ("Done - run mvn license:format on all projects");
  }
}
