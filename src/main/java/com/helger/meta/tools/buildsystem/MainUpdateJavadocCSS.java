/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import com.helger.commons.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Ensure the src/etc/javadoc.css file is the same in all projects, based on a
 * template in this project. Afterwards <code>mvn license:format</code> must be
 * run on all projects!
 *
 * @author Philip Helger
 */
public final class MainUpdateJavadocCSS extends AbstractProjectMain
{
  public static void main (final String [] args)
  {
    final String sSrcCSS = SimpleFileIO.getFileAsString (new File ("src/raw/source-javadoc.css"),
                                                         StandardCharsets.UTF_8);

    for (final IProject aProject : ProjectList.getAllProjects (p -> p.isBuildInProject () &&
                                                                    p.getBaseDir ().exists () &&
                                                                    !p.isDeprecated () &&
                                                                    p.getProjectType ().hasJavaCode ()))
    {
      final File f = new File (aProject.getBaseDir (), "src/etc/javadoc.css");
      assert f.exists ();
      SimpleFileIO.writeFile (f, sSrcCSS, StandardCharsets.UTF_8);
    }
    s_aLogger.info ("Done - run mvn license:format on all projects");
  }
}
