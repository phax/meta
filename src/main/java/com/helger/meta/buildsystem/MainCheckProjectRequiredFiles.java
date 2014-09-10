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
