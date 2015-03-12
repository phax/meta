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
import java.util.Map;

import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;

/**
 * Check whether the Maven pom.xml of a project is consistent to the
 * requirements
 *
 * @author Philip Helger
 */
public final class MainCreateREADMEList extends AbstractProjectMain
{
  private static final Map <String, IProject> ALL_PROJECTS = getAllProjects ();

  public static void main (final String [] args)
  {
    final StringBuilder aSB = new StringBuilder ("Current list of all projects (as of " +
                                                 new Date ().toString () +
                                                 "):\n\n");
    for (final IProject e : ALL_PROJECTS.values ())
      if (e.isBuildInProject () && !e.isDeprecated ())
      {
        aSB.append (" * [")
           .append (e.getProjectName ())
           .append ("](https://github.com/phax/")
           .append (e.getProjectName ())
           .append (") - ");
        if (e.isPublished ())
          aSB.append ("Version ").append (e.getLastPublishedVersionString ());
        else
          aSB.append ("no release so far");
        aSB.append ('\n');
      }
    System.out.println (aSB.toString ());
  }
}
