package com.helger.meta.tools.buildsystem;

import java.io.File;

import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

public final class MainEclipseCompilerErrorsRestoreBackup extends AbstractProjectMain
{
  public static void main (final String [] args)
  {
    for (final IProject aProject : ProjectList.getAllProjects (p -> p.getProjectType ().hasJavaCode () &&
                                                                    !p.isDeprecated () &&
                                                                    p.getBaseDir ().exists () &&
                                                                    p.getMinimumJDKVersion ().isAtLeast8 ()))
    {
      final File fCur = new File (aProject.getBaseDir (), ".settings/org.eclipse.jdt.core.prefs");
      final File fBackup = new File (aProject.getBaseDir (), ".settings/org.eclipse.jdt.core.prefs.bup");
      if (fBackup.exists ())
      {
        fCur.delete ();
        fBackup.renameTo (fCur);
        _info (aProject, "Done restoring backup");
      }
    }
  }
}
