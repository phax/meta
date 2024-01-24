/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FileOperations;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

public final class MainEclipseCompilerErrorsRestoreBackup extends AbstractProjectMain
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainEclipseCompilerErrorsRestoreBackup.class);

  public static void main (final String [] args)
  {
    for (final IProject aProject : ProjectList.getAllProjects (p -> p.getProjectType ().hasJavaCode () &&
                                                                    !p.isDeprecated () &&
                                                                    p.getBaseDir ().exists ()))
    {
      final File fCur = new File (aProject.getBaseDir (), ".settings/org.eclipse.jdt.core.prefs");
      final File fBackup = new File (aProject.getBaseDir (), ".settings/org.eclipse.jdt.core.prefs.bup");
      if (fBackup.exists ())
      {
        FileOperations.deleteDirIfExisting (fCur);
        FileOperations.renameFile (fBackup, fCur);
        _info (aProject, "Done restoring backup");
      }
    }
    LOGGER.info ("Done");
  }
}
