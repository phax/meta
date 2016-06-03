package com.helger.meta.tools.buildsystem;

import java.io.File;

import com.helger.commons.charset.CCharset;
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
      SimpleFileIO.writeFile (fCur, sContent.getBytes (CCharset.CHARSET_ISO_8859_1_OBJ));
      _info (aProject, "Done restoring default");
    }
  }
}
