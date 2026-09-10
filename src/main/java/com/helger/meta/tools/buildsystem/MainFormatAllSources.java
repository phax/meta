/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.io.file.FileSystemRecursiveIterator;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EJDK;
import com.helger.meta.project.EProjectOwner;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * Format the Java sources of all self owned projects with the Eclipse formatter profile from
 * <code>eclipse-formatter-ph.xml</code>. This is the command line equivalent of "Source / Format"
 * in Eclipse and requires no Eclipse installation. Pass <code>--dry-run</code> to just list the
 * files that would be changed. Pass one or more project names to limit the run to those projects
 * instead of all of them.
 *
 * @author Philip Helger
 */
public final class MainFormatAllSources extends AbstractProjectMain
{
  public static final String ARG_DRY_RUN = "--dry-run";

  private static final Logger LOGGER = LoggerFactory.getLogger (MainFormatAllSources.class);
  private static final File FORMATTER_CONFIG_FILE = new File ("eclipse-formatter-ph.xml");
  private static final String FORMATTER_PROFILE_NAME = "ph";
  private static final Charset CHARSET = StandardCharsets.UTF_8;
  // All sources use Unix line endings - pin it, so that no CRLF can sneak in
  private static final String LINE_SEPARATOR = "\n";
  private static final String [] SOURCE_DIRS = { "src/main/java", "src/test/java" };

  private static int s_nChanged = 0;
  private static int s_nUnchanged = 0;

  @NonNull
  private static ICommonsOrderedMap <String, String> _readFormatterOptions (@NonNull final File aConfigFile)
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (aConfigFile);
    if (aDoc == null || aDoc.getDocumentElement () == null)
      throw new IllegalStateException ("Failed to read " + aConfigFile.getAbsolutePath () + " as XML!");

    final ICommonsOrderedMap <String, String> ret = new CommonsLinkedHashMap <> ();
    for (final IMicroElement eProfile : aDoc.getDocumentElement ().getAllChildElements ("profile"))
      if (FORMATTER_PROFILE_NAME.equals (eProfile.getAttributeValue ("name")))
        for (final IMicroElement eSetting : eProfile.getAllChildElements ("setting"))
          ret.put (eSetting.getAttributeValue ("id"), eSetting.getAttributeValue ("value"));

    if (ret.isEmpty ())
      throw new IllegalStateException ("Failed to find the formatter profile '" +
                                       FORMATTER_PROFILE_NAME +
                                       "' in " +
                                       aConfigFile.getAbsolutePath ());
    return ret;
  }

  @NonNull
  private static String _getJavaCoreVersion (@NonNull final EJDK eJDK)
  {
    // JavaCore uses "1.8" for Java 8 but "17" for Java 17
    final int nMajor = eJDK.getMajor ();
    return nMajor > 8 ? Integer.toString (nMajor) : "1." + nMajor;
  }

  private static void _formatFile (@NonNull final CodeFormatter aFormatter,
                                   @NonNull final IProject aProject,
                                   @NonNull final File aFile,
                                   final boolean bDryRun)
  {
    final String sOldContent = SimpleFileIO.getFileAsString (aFile, CHARSET);
    if (sOldContent == null)
    {
      _warn (aProject, "Failed to read " + aFile.getAbsolutePath ());
      return;
    }

    final TextEdit aEdit = aFormatter.format (CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS,
                                              sOldContent,
                                              0,
                                              sOldContent.length (),
                                              0,
                                              LINE_SEPARATOR);
    if (aEdit == null)
    {
      _warn (aProject, "Failed to format " + aFile.getAbsolutePath () + " - it is probably not parsable");
      return;
    }

    final IDocument aDoc = new Document (sOldContent);
    try
    {
      aEdit.apply (aDoc);
    }
    catch (final MalformedTreeException | BadLocationException ex)
    {
      _warn (aProject, "Failed to apply the formatting to " + aFile.getAbsolutePath () + " - " + ex.getMessage ());
      return;
    }

    final String sNewContent = aDoc.get ();
    if (sNewContent.equals (sOldContent))
    {
      s_nUnchanged++;
      return;
    }

    s_nChanged++;
    if (bDryRun)
      LOGGER.info (_getLogPrefix (aProject) + "Would format " + aFile.getAbsolutePath ());
    else
      SimpleFileIO.writeFile (aFile, sNewContent, CHARSET);
  }

  private static void _formatProject (@NonNull final IProject aProject,
                                      @NonNull final Map <String, String> aBaseOptions,
                                      final boolean bDryRun)
  {
    // The formatter needs to know the source level to parse e.g. records or switch expressions
    final ICommonsOrderedMap <String, String> aOptions = new CommonsLinkedHashMap <> (aBaseOptions);
    final String sVersion = _getJavaCoreVersion (aProject.getMinimumJDKVersion ());
    aOptions.put (JavaCore.COMPILER_SOURCE, sVersion);
    aOptions.put (JavaCore.COMPILER_COMPLIANCE, sVersion);
    aOptions.put (JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, sVersion);

    final CodeFormatter aFormatter = ToolFactory.createCodeFormatter (aOptions);
    for (final String sSourceDir : SOURCE_DIRS)
    {
      final File aDir = new File (aProject.getBaseDir (), sSourceDir);
      if (!aDir.isDirectory ())
        continue;

      for (final File aFile : new FileSystemRecursiveIterator (aDir))
        if (aFile.isFile () && aFile.getName ().endsWith (".java"))
          _formatFile (aFormatter, aProject, aFile, bDryRun);
    }
  }

  public static void main (final String [] args)
  {
    boolean bDryRun = false;
    final ICommonsList <String> aOnlyProjectNames = new CommonsArrayList <> ();
    for (final String sArg : args)
      if (ARG_DRY_RUN.equals (sArg))
        bDryRun = true;
      else
        aOnlyProjectNames.add (sArg);

    final ICommonsOrderedMap <String, String> aBaseOptions = _readFormatterOptions (FORMATTER_CONFIG_FILE);
    LOGGER.info ("Formatting all owned sources with the " +
                 aBaseOptions.size () +
                 " settings of profile '" +
                 FORMATTER_PROFILE_NAME +
                 "'" +
                 (bDryRun ? " (dry run)" : ""));

    for (final IProject aProject : ProjectList.getAllProjects (p -> (p.getProjectOwner () ==
                                                                     EProjectOwner.PROJECT_OWNER_PHAX ||
                                                                     p.getProjectOwner () ==
                                                                                                         EProjectOwner.PROJECT_OWNER_HELGER_IT) &&
                                                                    p.getProjectType ().hasJavaCode () &&
                                                                    !p.isDeprecated () &&
                                                                    p.getBaseDir ().exists () &&
                                                                    (aOnlyProjectNames.isEmpty () ||
                                                                     aOnlyProjectNames.contains (p.getProjectName ()))))
    {
      _formatProject (aProject, aBaseOptions, bDryRun);
    }

    LOGGER.info ("Done - " +
                 s_nChanged +
                 (bDryRun ? " file(s) would be changed, " : " file(s) changed, ") +
                 s_nUnchanged +
                 " file(s) unchanged, " +
                 getWarnCount () +
                 " warning(s)");
  }
}
