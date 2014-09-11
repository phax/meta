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
package com.helger.meta.buildsystem;

import java.io.File;

import javax.annotation.Nonnull;

import com.helger.meta.EProject;
import com.helger.meta.EProjectType;

/**
 * Check whether all project has all the required files
 *
 * @author Philip Helger
 */
public final class MainCheckProjectRequiredFiles extends AbstractMainUtils
{
  private static void _checkFile (@Nonnull final EProject eProject, @Nonnull final String sRelativeFilename)
  {
    final File f = new File (eProject.getBaseDir (), sRelativeFilename);
    if (!f.exists ())
      _warn (eProject, "File " + f.getAbsolutePath () + " does not exist!");
  }

  private static void _validateProject (@Nonnull final EProject eProject)
  {
    _checkFile (eProject, ".classpath");
    _checkFile (eProject, ".project");
    _checkFile (eProject, "pom.xml");
    _checkFile (eProject, "README.MD");
    _checkFile (eProject, "findbugs-exclude.xml");
    _checkFile (eProject, "src/etc/javadoc.css");
    _checkFile (eProject, "src/etc/license-template.txt");
  }

  public static void main (final String [] args)
  {
    for (final EProject e : EProject.values ())
      if (e.getProjectType () != EProjectType.MAVEN_POM)
      {
        _validateProject (e);
      }
    s_aLogger.info ("Done - " + getWarnCount () + " warning(s)");
  }
}
