/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EJDK;
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
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCheckProjectRequiredFiles.class);

  private static boolean _isDirExisting (@Nonnull final IProject aProject, @Nonnull final String sRelativeDirName)
  {
    final File f = new File (aProject.getBaseDir (), sRelativeDirName);
    return f.isDirectory ();
  }

  @Nonnull
  private static ESuccess _checkFileExisting (@Nonnull final IProject aProject, @Nonnull final String sRelativeFilename)
  {
    final File f = new File (aProject.getBaseDir (), sRelativeFilename);
    if (f.exists ())
      return ESuccess.SUCCESS;
    _warn (aProject, "File " + f.getAbsolutePath () + " does not exist!");
    return ESuccess.FAILURE;
  }

  @Nonnull
  private static ESuccess _checkFileNotExisting (@Nonnull final IProject aProject,
                                                 @Nonnull final String sRelativeFilename)
  {
    final File f = new File (aProject.getBaseDir (), sRelativeFilename);
    if (!f.exists ())
      return ESuccess.SUCCESS;
    _warn (aProject, "File " + f.getAbsolutePath () + " should not exist!");
    return ESuccess.FAILURE;
  }

  private static boolean _checkFileContains (@Nonnull final IProject aProject,
                                             @Nonnull final String sRelativeFilename,
                                             @Nonnull final String sExpectedContent)
  {
    final File f = new File (aProject.getBaseDir (), sRelativeFilename);
    final String sContent = SimpleFileIO.getFileAsString (f, StandardCharsets.UTF_8);
    if (sContent != null)
    {
      if (sContent.contains (sExpectedContent))
        return true;
    }
    _warn (aProject, "File " + f.getAbsolutePath () + " does not contain phrase '" + sExpectedContent + "'!");
    return false;
  }

  @SuppressWarnings ("unused")
  private static boolean _checkFileContainsOR (@Nonnull final IProject aProject,
                                               @Nonnull final String sRelativeFilename,
                                               @Nonnull final String... aExpectedContentsOR)
  {
    ValueEnforcer.notEmptyNoNullValue (aExpectedContentsOR, "ExpectedContentsOR");
    final File f = new File (aProject.getBaseDir (), sRelativeFilename);
    final String sContent = SimpleFileIO.getFileAsString (f, StandardCharsets.UTF_8);
    if (sContent != null)
    {
      for (final String sExpectedContent : aExpectedContentsOR)
        if (sContent.contains (sExpectedContent))
          return true;
    }
    _warn (aProject,
           "File " +
                     f.getAbsolutePath () +
                     " does not contain phrase " +
                     StringHelper.getImploded (", ", aExpectedContentsOR) +
                     "!");
    return false;
  }

  @SuppressWarnings ("unused")
  private static boolean _checkFileContainsNot (@Nonnull final IProject aProject,
                                                @Nonnull final String sRelativeFilename,
                                                @Nonnull final String sExpectedContent)
  {
    final File f = new File (aProject.getBaseDir (), sRelativeFilename);
    final String sContent = SimpleFileIO.getFileAsString (f, StandardCharsets.UTF_8);
    if (sContent != null && !sContent.contains (sExpectedContent))
      return true;
    _warn (aProject, "File " + f.getAbsolutePath () + " contains phrase '" + sExpectedContent + "'!");
    return false;
  }

  private static boolean _isApache2Project (@Nonnull final IProject aProject)
  {
    return aProject != EProject.JCODEMODEL;
  }

  private static void _validateProjectWithJavaCode (@Nonnull final IProject aProject)
  {
    // Check for file existence
    if (false)
      _checkFileExisting (aProject, ".classpath");
    if (false)
      _checkFileExisting (aProject, ".project");
    _checkFileExisting (aProject, "pom.xml");
    if (!aProject.isNestedProject ())
    {
      _checkFileExisting (aProject, "README.md");
      _checkFileExisting (aProject, "CODE_OF_CONDUCT.md");
    }
    _checkFileExisting (aProject, "findbugs-exclude.xml");
    _checkFileExisting (aProject, "src/etc/javadoc.css");
    if (_checkFileExisting (aProject, "src/etc/license-template.txt").isSuccess ())
    {
      // Check for file contents
      _checkFileContains (aProject, "src/etc/license-template.txt", Integer.toString (PDTFactory.getCurrentYear ()));
    }
    _checkFileNotExisting (aProject, "pom.xml.versionsBackup");
    if (false)
      _checkFileExisting (aProject, "src/main/resources/changelog.xml");
    if (_isApache2Project (aProject))
    {
      _checkFileExisting (aProject, "src/main/resources/LICENSE");
      _checkFileExisting (aProject, "src/main/resources/NOTICE");
    }
  }

  private static void _validateProjectWithoutJavaCode (@Nonnull final IProject aProject)
  {
    // Check for file existence
    if (false)
      _checkFileExisting (aProject, ".project");
    if (false)
      _checkFileExisting (aProject, "pom.xml");

    if (!aProject.isNestedProject ())
      _checkFileExisting (aProject, "README.MD");
  }

  private static void _validateTopLevelProject (final IProject aProject)
  {
    if (!aProject.isNestedProject ())
    {
      // So that GitHub displays the license
      if (aProject.isBuildInProject ())
        _checkFileExisting (aProject, "LICENSE.txt");
      else
        _checkFileNotExisting (aProject, "LICENSE.txt");
      _checkFileNotExisting (aProject, "LICENSE");

      if (_isDirExisting (aProject, ".github"))
      {
        _checkFileNotExisting (aProject, ".github/stale.yml");
        if (_checkFileExisting (aProject, ".github/workflows/maven.yml").isSuccess () &&
            aProject != EProject.PHASE4_PEPPOL_STANDALONE)
        {
          _checkFileContains (aProject, ".github/workflows/maven.yml", "server-id: central");
          _checkFileContains (aProject, ".github/workflows/maven.yml", " -P release-snapshot");
          if (aProject.getMinimumJDKVersion () == EJDK.JDK11)
          {
            _checkFileContains (aProject, ".github/workflows/maven.yml", "[ 11, 17, 21, 24 ]");
            _checkFileContains (aProject, ".github/workflows/maven.yml", "if: matrix.java == 11");
          }
          else
          {
            _checkFileContains (aProject, ".github/workflows/maven.yml", "[ 17, 21, 24 ]");
            _checkFileContains (aProject, ".github/workflows/maven.yml", "if: matrix.java == 17");
          }
        }
      }
    }
  }

  public static void main (final String [] args)
  {
    for (final IProject aProject : ProjectList.getAllProjects (p -> p.isBuildInProject () &&
                                                                    p.getBaseDir ().exists () &&
                                                                    !p.isDeprecated ()))
    {
      if (aProject.getProjectType ().hasJavaCode ())
        _validateProjectWithJavaCode (aProject);
      else
        _validateProjectWithoutJavaCode (aProject);

      _validateTopLevelProject (aProject);
    }
    LOGGER.info ("Done - " + getWarnCount () + " warning(s) for " + ProjectList.size () + " projects");
  }
}
