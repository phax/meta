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

import java.io.File;

import javax.annotation.Nonnull;

import com.helger.commons.CGlobal;
import com.helger.commons.changelog.CChangeLog;
import com.helger.commons.charset.CCharset;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EProject;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Check whether all project has all the required files
 *
 * @author Philip Helger
 */
public final class MainCheckProjectRequiredFiles extends AbstractProjectMain
{
  private static void _checkFileExisting (@Nonnull final IProject eProject, @Nonnull final String sRelativeFilename)
  {
    final File f = new File (eProject.getBaseDir (), sRelativeFilename);
    if (!f.exists ())
      _warn (eProject, "File " + f.getAbsolutePath () + " does not exist!");
  }

  private static void _checkFileNotExisting (@Nonnull final IProject eProject, @Nonnull final String sRelativeFilename)
  {
    final File f = new File (eProject.getBaseDir (), sRelativeFilename);
    if (f.exists ())
      _warn (eProject, "File " + f.getAbsolutePath () + " should not exist!");
  }

  private static void _checkFileContains (@Nonnull final IProject eProject,
                                          @Nonnull final String sRelativeFilename,
                                          @Nonnull final String sExpectedContent)
  {
    final File f = new File (eProject.getBaseDir (), sRelativeFilename);
    final String sContent = SimpleFileIO.getFileAsString (f, CCharset.CHARSET_UTF_8_OBJ);
    if (sContent == null || !sContent.contains (sExpectedContent))
      _warn (eProject, "File " + f.getAbsolutePath () + " does not contain phrase '" + sExpectedContent + "'!");
  }

  private static void _validateProject (@Nonnull final IProject eProject)
  {
    // Check for file existence
    _checkFileExisting (eProject, ".classpath");
    _checkFileExisting (eProject, ".project");
    _checkFileExisting (eProject, "pom.xml");
    if (!eProject.isNestedProject ())
      _checkFileExisting (eProject, "README.MD");
    _checkFileExisting (eProject, "findbugs-exclude.xml");
    _checkFileExisting (eProject, "src/etc/javadoc.css");
    _checkFileExisting (eProject, "src/etc/license-template.txt");
    if (false)
      _checkFileExisting (eProject, "src/main/resources/changelog.xml");
    if (eProject != EProject.JCODEMODEL)
    {
      // Not Apache2 license
      _checkFileExisting (eProject, "src/main/resources/LICENSE");
      _checkFileExisting (eProject, "src/main/resources/NOTICE");
    }
    _checkFileNotExisting (eProject, "LICENSE");

    // Check for file contents
    _checkFileContains (eProject, "src/etc/license-template.txt", Integer.toString (CGlobal.CURRENT_YEAR));
    if (new File (eProject.getBaseDir (), "src/main/resources/changelog.xml").isFile ())
      _checkFileContains (eProject, "src/main/resources/changelog.xml", CChangeLog.CHANGELOG_NAMESPACE_10);
  }

  public static void main (final String [] args)
  {
    for (final IProject aProject : ProjectList.getAllProjects ())
      if (aProject.getProjectType ().hasJavaCode () &&
          aProject.isBuildInProject () &&
          aProject.getBaseDir ().exists () &&
          !aProject.isDeprecated ())
      {
        _validateProject (aProject);
      }
    s_aLogger.info ("Done - " + getWarnCount () + " warning(s) for " + ProjectList.size () + " projects");
  }
}
