/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import java.util.Date;

import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Check whether the Maven pom.xml of a project is consistent to the
 * requirements
 *
 * @author Philip Helger
 */
public final class MainCreateREADMEList extends AbstractProjectMain
{
  public static void main (final String [] args)
  {
    final StringBuilder aSB = new StringBuilder ();

    // Show all
    aSB.append ("Current list of all projects (as of ").append (new Date ().toString ()).append ("):\n\n");
    for (final IProject aProject : ProjectList.getAllProjects ())
      if (aProject.isBuildInProject () && !aProject.isNestedProject () && !aProject.isDeprecated ())
      {
        aSB.append (" * [")
           .append (aProject.getProjectName ())
           .append ("](https://github.com/phax/")
           .append (aProject.getFullBaseDirName ())
           .append (") - ");
        if (aProject.isPublished ())
          aSB.append ("Version ").append (aProject.getLastPublishedVersionString ());
        else
          aSB.append ("no release so far");
        aSB.append ('\n');
      }

    // Add deprecated projects
    aSB.append ("\nAll deprecated projects:\n\n");
    for (final IProject aProject : ProjectList.getAllProjects ())
      if (aProject.isBuildInProject () && !aProject.isNestedProject () && aProject.isDeprecated ())
      {
        aSB.append (" * [")
           .append (aProject.getProjectName ())
           .append ("](https://github.com/phax/")
           .append (aProject.getFullBaseDirName ())
           .append (") - ");
        if (aProject.isPublished ())
          aSB.append ("Version ").append (aProject.getLastPublishedVersionString ());
        else
          aSB.append ("never released");
        aSB.append ('\n');
      }
    System.out.println (aSB.toString ());
  }
}
