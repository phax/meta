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

public final class MainEclipseCompilerErrorsSetDefault extends AbstractProjectMain
{
  public static void main (final String [] args)
  {
    final String sContent = "eclipse.preferences.version=1\n" +
                            "org.eclipse.jdt.core.compiler.codegen.targetPlatform=1.8\n" +
                            "org.eclipse.jdt.core.compiler.compliance=1.8\n" +
                            "org.eclipse.jdt.core.compiler.problem.forbiddenReference=warning\n" +
                            "org.eclipse.jdt.core.compiler.source=1.8\n" +
                            "org.eclipse.jdt.core.javaFormatter=org.eclipse.jdt.core.defaultJavaFormatter\n";

    for (final IProject aProject : ProjectList.getAllProjects (p -> p.getProjectType ().hasJavaCode () &&
                                                                    !p.isDeprecated () &&
                                                                    p.getBaseDir ().exists () &&
                                                                    p.getMinimumJDKVersion ().isAtLeast8 ()))
    {
      final File fCur = new File (aProject.getBaseDir (), ".settings/org.eclipse.jdt.core.prefs");
      assert fCur.exists ();
      final File fBackup = new File (aProject.getBaseDir (), ".settings/org.eclipse.jdt.core.prefs.bup");

      if (!fBackup.exists ())
      {
        fCur.renameTo (fBackup);
      }
      SimpleFileIO.writeFile (fCur, sContent.getBytes (StandardCharsets.UTF_8));
      _info (aProject, "Done restoring default");
    }
    s_aLogger.info ("Done");
  }
}
