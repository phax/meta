package com.helger.meta.cmdline;

import java.io.File;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.charset.CCharset;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.meta.CMeta;
import com.helger.meta.EProject;

/**
 * Create a set of batch files that contains content that in most cases is
 * relevant to all projects.
 *
 * @author Philip Helger
 */
public class MainCreateBatchFiles
{
  private static final Charset CHARSET_BATCH = CCharset.CHARSET_ISO_8859_1_OBJ;
  private static final String HEADER = "@echo off\nrem This files is generated - DO NOT EDIT\nrem A total of " +
                                       EProject.values ().length +
                                       " projects are handled\n";
  private static final String FOOTER = "goto end\n:error\necho An error occured!!!\npause\n:end\n";

  private static void _createMvn (@Nonnull @Nonempty final String sMavenCommand,
                                  @Nonnull @Nonempty final String sBatchFileName)
  {
    final StringBuilder aSB = new StringBuilder ();
    aSB.append (HEADER);
    for (final EProject e : EProject.values ())
      aSB.append ("echo ")
         .append (e.getProjectName ())
         .append ("\ncd ")
         .append (e.getProjectName ())
         .append ("\ncall mvn ")
         .append (sMavenCommand)
         .append (" %*\nif errorlevel 1 goto error\ncd ..\n");
    aSB.append (FOOTER);
    SimpleFileIO.writeFile (new File (CMeta.GIT_BASE_DIR, sBatchFileName), aSB.toString (), CHARSET_BATCH);
  }

  public static void main (final String [] args)
  {
    _createMvn ("license:format", "mvn_license_format.cmd");
    _createMvn ("dependency:go-offline dependency:sources", "mvn_dependency_go_offline_and_sources.cmd");
    _createMvn ("clean", "mvn_clean.cmd");
    _createMvn ("clean install", "mvn_clean_install.cmd");
    System.out.println ("Batch files created in " + CMeta.GIT_BASE_DIR);
  }
}
