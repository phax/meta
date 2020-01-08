/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * Check which project has a non-empty <code>findbugs-exclude.xml</code> file.
 *
 * @author Philip Helger
 */
public final class MainCheckFindBugsExcludes extends AbstractProjectMain
{
  public static void main (final String [] args)
  {
    for (final IProject aProject : ProjectList.getAllProjects (p -> p.getProjectType ().hasJavaCode ()))
    {
      final File f = new File (aProject.getBaseDir (), "findbugs-exclude.xml");
      if (f.exists ())
      {
        final IMicroDocument aDoc = MicroReader.readMicroXML (f);
        if (aDoc == null)
          _warn (aProject, "Failed to read " + f.getAbsolutePath () + " as XML!");
        else
          if (aDoc.getDocumentElement () == null)
            _warn (aProject, "File " + f.getAbsolutePath () + " seems to be empty!");
          else
            if (!"FindBugsFilter".equals (aDoc.getDocumentElement ().getTagName ()))
              _warn (aProject, "File " + f.getAbsolutePath () + " contains an unknown root element!");
            else
              if (aDoc.getDocumentElement ().getChildElementCount () > 0)
                _info (aProject, "Has FindBugs excludes defines!");
      }
    }
    LOGGER.info ("Done - " + getWarnCount () + " warning(s) for " + ProjectList.size () + " projects");
  }
}
