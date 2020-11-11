/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsTreeSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FileSystemRecursiveIterator;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.locale.LocaleFormatter;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.ENewLineMode;
import com.helger.commons.timing.StopWatch;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Create the README.md file of this project.
 *
 * @author Philip Helger
 */
public final class MainCreateMetaLinesOfCode extends AbstractProjectMain
{
  private enum EFileType
  {
    BATCH ("Batch", true, new CommonsTreeSet <> ("bat", "cmd")),
    CODEGEN ("Code generation", true, new CommonsTreeSet <> ("jj", "jjt")),
    CSS ("CSS", true, new CommonsTreeSet <> ("css", "scss")),
    CSV ("CSV", true, new CommonsTreeSet <> ("csv")),
    GIT ("git", true, new CommonsTreeSet <> ("gitignore")),
    GROOVY ("Groovy", true, new CommonsTreeSet <> ("groovy")),
    HTML ("HTML", true, new CommonsTreeSet <> ("html", "htm", "xhtml")),
    JAVA ("Java", true, new CommonsTreeSet <> ("java", "jsp")),
    JS ("JavaScript", true, new CommonsTreeSet <> ("js")),
    JSON ("JSON", true, new CommonsTreeSet <> ("json")),
    PROPERTIES ("Properties", true, new CommonsTreeSet <> ("properties", "conf")),
    RELAX_NG ("RelaxNG", true, new CommonsTreeSet <> ("rng", "rnc")),
    SQL ("SQL", true, new CommonsTreeSet <> ("sql")),
    TEXT ("Text", true, new CommonsTreeSet <> ("txt", "md", "text")),
    XML ("XML",
         true,
         new CommonsTreeSet <> ("cva",
                                "dita",
                                "dtd",
                                "gc",
                                "gc_",
                                "jrxml",
                                "sch",
                                "svrl",
                                "wsdl",
                                "xjb",
                                "xjc",
                                "xml",
                                "xq",
                                "xsd",
                                "xsl",
                                "xslt")),
    JDM ("JDM", true, new CommonsTreeSet <> ("jdm")),
    // Binary stuff
    EXCEL ("Excel", false, new CommonsTreeSet <> ("xls", "xlsx", "ods")),
    FLASH ("Flash", false, new CommonsTreeSet <> ("swf")),
    FONT ("Font", false, new CommonsTreeSet <> ("ttf", "eot")),
    IMAGE ("Image", false, new CommonsTreeSet <> ("bmp", "gif", "icc", "ico", "jpg", "otf", "png", "svg", "woff", "woff2")),
    KEYSTORE ("Keystore", false, new CommonsTreeSet <> ("cer", "crt", "der", "jks", "pem", "pfx", "p12")),
    PDF ("PDF", false, new CommonsTreeSet <> ("pdf")),
    WORD ("Word", false, new CommonsTreeSet <> ("doc", "docx", "odt")),
    ZIP ("ZIP", false, new CommonsTreeSet <> ("asice", "gz", "zip")),
    // known rest
    KNOWN_REST ("Known other",
                false,
                new CommonsTreeSet <> ("",
                                       "95",
                                       "attachment",
                                       "b64",
                                       "bak",
                                       "cat",
                                       "dat",
                                       "ent",
                                       "episode",
                                       "flow",
                                       "header",
                                       "ijmap",
                                       "in",
                                       "incl",
                                       "interface",
                                       "map",
                                       "mapping",
                                       "md5",
                                       "mime",
                                       "mmd",
                                       "mod",
                                       "pac",
                                       "payload",
                                       "rxg",
                                       "rxm",
                                       "sha256",
                                       "template",
                                       "types")),
    UNKNOWN_OTHER ("Unknown other", false, new CommonsTreeSet <> ());

    private final String m_sName;
    private final String m_sExtensions;
    private final boolean m_bIsText;
    private final ICommonsSet <String> m_aExts;

    private EFileType (@Nonnull @Nonempty final String sName, final boolean bIsText, @Nonnull final ICommonsSet <String> aExts)
    {
      m_sName = sName + " files";
      m_sExtensions = StringHelper.getImplodedMappedNonEmpty (", ", aExts, x -> x.length () == 0 ? x : "." + x);
      m_bIsText = bIsText;
      m_aExts = aExts;
    }

    @Nonnull
    @Nonempty
    public String getName ()
    {
      return m_sName;
    }

    @Nonnull
    public String getExtensions ()
    {
      return m_sExtensions;
    }

    public boolean isCountLines ()
    {
      return m_bIsText;
    }

    @Nonnull
    public Charset getCharset ()
    {
      if (this == BATCH)
        return StandardCharsets.ISO_8859_1;
      return StandardCharsets.UTF_8;
    }

    @Nonnull
    public static EFileType getFromFilename (final String sFilename)
    {
      final String sExtension = FilenameHelper.getExtension (sFilename).toLowerCase (Locale.ROOT);
      for (final EFileType e : values ())
        if (e.m_aExts.contains (sExtension))
          return e;
      throw new IllegalStateException ("Unknown '" + sFilename + "'");
    }
  }

  private static final class FileTypeCount
  {
    private long m_nFilesTotal = 0;
    private long m_nFilesBytes = 0;
    private long m_nCharsTotal = 0;
    private long m_nCharsWhitepaces = 0;
    private long m_nLinesTotal = 0;
    private long m_nLinesWhitespaceOnly = 0;

    public void addFile (@Nonnull final File aFile)
    {
      m_nFilesTotal++;
      m_nFilesBytes += aFile.length ();
    }

    public void addLine (@Nonnull final String sLine)
    {
      m_nLinesTotal++;
      int nNonWhitespaces = 0;
      for (final char c : sLine.toCharArray ())
      {
        m_nCharsTotal++;
        if (Character.isWhitespace (c))
          m_nCharsWhitepaces++;
        else
          nNonWhitespaces++;
      }
      if (nNonWhitespaces == 0)
        m_nLinesWhitespaceOnly++;
    }

    public void addEndOfLine (@Nonnull final String sEOL)
    {
      m_nCharsTotal += sEOL.length ();
    }

    @Nonnegative
    public long getFileCount ()
    {
      return m_nFilesTotal;
    }

    @Nonnegative
    public long getFileBytes ()
    {
      return m_nFilesBytes;
    }

    @Nonnegative
    public long getLinesTotal ()
    {
      return m_nLinesTotal;
    }

    @Nonnegative
    public long getLinesWhitespaceOnly ()
    {
      return m_nLinesWhitespaceOnly;
    }

    @Nonnegative
    public double getLinesWhitespaceOnlyPerc ()
    {
      if (m_nLinesTotal == 0)
        return 0;
      return (double) m_nLinesWhitespaceOnly / m_nLinesTotal;
    }

    @Nonnegative
    public long getCharsTotal ()
    {
      return m_nCharsTotal;
    }

    @Nonnegative
    public long getCharsWhitepaces ()
    {
      return m_nCharsWhitepaces;
    }

    @Nonnegative
    public double getCharsWhitepacesPerc ()
    {
      if (m_nCharsTotal == 0)
        return 0;
      return (double) m_nCharsWhitepaces / m_nCharsTotal;
    }

    public void incrementFrom (@Nonnull final FileTypeCount rhs)
    {
      m_nFilesTotal += rhs.m_nFilesTotal;
      m_nFilesBytes += rhs.m_nFilesBytes;
      m_nCharsTotal += rhs.m_nCharsTotal;
      m_nCharsWhitepaces += rhs.m_nCharsWhitepaces;
      m_nLinesTotal += rhs.m_nLinesTotal;
      m_nLinesWhitespaceOnly += rhs.m_nLinesWhitespaceOnly;
    }
  }

  private static void _scan (@Nonnull final File aBaseDir, @Nonnull final ICommonsMap <EFileType, FileTypeCount> aMap) throws IOException
  {
    if (aBaseDir.isDirectory ())
    {
      final String sEOL = ENewLineMode.DEFAULT.getText ();
      for (final File aFile : new FileSystemRecursiveIterator (aBaseDir, x -> !x.getName ().equals ("services")))
      {
        if (aFile.isFile ())
        {
          final EFileType eFileType = EFileType.getFromFilename (aFile.getAbsolutePath ());
          // Map was prefilled
          final FileTypeCount aFTCount = aMap.get (eFileType);

          // Always add file
          aFTCount.addFile (aFile);

          if (eFileType.isCountLines ())
          {
            try (NonBlockingBufferedReader aReader = FileHelper.getBufferedReader (aFile, eFileType.getCharset ()))
            {
              String sLine;
              while ((sLine = aReader.readLine ()) != null)
              {
                aFTCount.addLine (sLine);
                aFTCount.addEndOfLine (sEOL);
              }
            }
          }
        }
      }
    }
  }

  private static void _addTableHead (@Nonnull final StringBuilder aSB)
  {
    aSB.append ("<tr>")
       .append ("<th>File type</th>")
       .append ("<th>Context</th>")
       .append ("<th>File count</th>")
       .append ("<th>File bytes</th>")
       .append ("<th>Lines total</th>")
       .append ("<th>Lines empty</th>")
       .append ("<th>Lines empty %</th>")
       .append ("<th>Chars total</th>")
       .append ("<th>Chars WS</th>")
       .append ("<th>Chars WS %</th>")
       .append ("</tr>\n");
  }

  private static void _addTableRow (@Nonnull final StringBuilder aSB,
                                    @Nullable final EFileType eFileType,
                                    @Nonnull final String sType,
                                    @Nonnull final String sContext,
                                    @Nonnull final FileTypeCount aCount)
  {
    final Locale aDisplayLocale = Locale.US;
    aSB.append ("<tr>");
    aSB.append ("<td>").append (sType).append ("</td>");
    aSB.append ("<td>").append (sContext).append ("</td>");
    aSB.append ("<td>").append (LocaleFormatter.getFormatted (aCount.getFileCount (), aDisplayLocale)).append ("</td>");
    aSB.append ("<td>").append (LocaleFormatter.getFormatted (aCount.getFileBytes (), aDisplayLocale)).append ("</td>");
    if (eFileType != null && !eFileType.isCountLines ())
    {
      aSB.append ("<td colspan=\"6\">No lines counted</td>");
    }
    else
    {
      aSB.append ("<td>").append (LocaleFormatter.getFormatted (aCount.getLinesTotal (), aDisplayLocale)).append ("</td>");
      aSB.append ("<td>").append (LocaleFormatter.getFormatted (aCount.getLinesWhitespaceOnly (), aDisplayLocale)).append ("</td>");
      aSB.append ("<td>")
         .append (LocaleFormatter.getFormattedPercent (aCount.getLinesWhitespaceOnlyPerc (), 2, aDisplayLocale))
         .append ("</td>");
      aSB.append ("<td>").append (LocaleFormatter.getFormatted (aCount.getCharsTotal (), aDisplayLocale)).append ("</td>");
      aSB.append ("<td>").append (LocaleFormatter.getFormatted (aCount.getCharsWhitepaces (), aDisplayLocale)).append ("</td>");
      aSB.append ("<td>")
         .append (LocaleFormatter.getFormattedPercent (aCount.getCharsWhitepacesPerc (), 2, aDisplayLocale))
         .append ("</td>");
    }
    aSB.append ("</tr>\n");
  }

  private static class FileTypeCountMap extends CommonsHashMap <EFileType, FileTypeCount>
  {
    public FileTypeCountMap ()
    {
      for (final EFileType eFileType : EFileType.values ())
        put (eFileType, new FileTypeCount ());
    }

    public void incrementFrom (@Nonnull final FileTypeCountMap aMap)
    {
      for (final EFileType eFileType : EFileType.values ())
        get (eFileType).incrementFrom (aMap.get (eFileType));
    }
  }

  private static void _addLineCount (@Nonnull final IProject aProject,
                                     @Nonnull final StringBuilder aSB,
                                     @Nonnull final FileTypeCountMap aOverallMain,
                                     @Nonnull final FileTypeCountMap aOverallTest) throws IOException
  {
    final StopWatch aSW = StopWatch.createdStarted ();
    final File aBaseDir = new File (aProject.getBaseDir (), "src");
    final FileTypeCountMap aMain = new FileTypeCountMap ();
    final FileTypeCountMap aTest = new FileTypeCountMap ();
    _scan (new File (aBaseDir, "main"), aMain);
    _scan (new File (aBaseDir, "test"), aTest);
    aSW.stop ();

    aOverallMain.incrementFrom (aMain);
    aOverallTest.incrementFrom (aTest);

    LOGGER.info (aBaseDir.getAbsolutePath () + " took " + aSW.getMillis () + " ms");

    final StringBuilder aSB2 = new StringBuilder ();
    aSB2.append ("<table><thead>");
    _addTableHead (aSB2);
    aSB2.append ("</thead><tbody>\n");

    final FileTypeCount aSumMain = new FileTypeCount ();
    final FileTypeCount aSumTest = new FileTypeCount ();
    int nRows = 0;
    for (final EFileType eFileType : EFileType.values ())
    {
      final FileTypeCount aCountMain = aMain.get (eFileType);
      final FileTypeCount aCountTest = aTest.get (eFileType);
      aSumMain.incrementFrom (aCountMain);
      aSumTest.incrementFrom (aCountTest);

      final FileTypeCount aCountSum = new FileTypeCount ();
      aCountSum.incrementFrom (aCountMain);
      aCountSum.incrementFrom (aCountTest);

      if (eFileType.isCountLines () && aCountSum.getFileCount () > 0)
      {
        _addTableRow (aSB2, eFileType, eFileType.getName (), "main", aCountMain);
        _addTableRow (aSB2, eFileType, eFileType.getName (), "test", aCountTest);
        _addTableRow (aSB2, eFileType, eFileType.getName (), "sum", aCountSum);
        ++nRows;
      }
    }

    if (nRows > 0)
    {
      final FileTypeCount aSum = new FileTypeCount ();
      aSum.incrementFrom (aSumMain);
      aSum.incrementFrom (aSumTest);

      aSB2.append ("</tbody><tfoot>");
      _addTableRow (aSB2, null, "Total sum", "main", aSumMain);
      _addTableRow (aSB2, null, "Total sum", "test", aSumTest);
      _addTableRow (aSB2, null, "Total sum", "sum", aSum);
      aSB2.append ("</tfoot></table>\n");
      aSB.append (aSB2);
    }
    else
      aSB.append ("no source files\n");
    aSB.append ("\n");
  }

  public static void main (final String [] args) throws IOException
  {
    final StringBuilder aSB = new StringBuilder ();
    aSB.append ("Line count as of ").append (PDTFactory.getCurrentLocalDateTime ().toString ()).append (":\n\n");

    final ICommonsList <IProject> aSortedProjects = ProjectList.getAllProjects (p -> p.isBuildInProject () && !p.isGitHubPrivate ())
                                                               .getSortedInline (Comparator.comparing (IProject::getBaseDir)
                                                                                           .thenComparing (IProject::getProjectName));

    // Show all
    aSB.append ("\nCurrent list of all released projects:\n\n");

    final FileTypeCountMap aOverallMain = new FileTypeCountMap ();
    final FileTypeCountMap aOverallTest = new FileTypeCountMap ();

    for (final IProject aProject : aSortedProjects)
      if (!aProject.isDeprecated () && aProject.isPublished () && aProject.getProjectType ().hasJavaCode ())
      {
        final String sProjectOwner = aProject.getProjectOwner ();
        final String sRepoName = MainCreateMetaREADME.getGitHubRepoName (aProject);

        aSB.append ("### [")
           .append (aProject.getFullBaseDirName ())
           .append ("](https://github.com/")
           .append (sProjectOwner)
           .append ('/')
           .append (sRepoName)
           .append (") - last release ")
           .append (aProject.getLastPublishedVersionString ())
           .append ("\n\n");
        _addLineCount (aProject, aSB, aOverallMain, aOverallTest);
        aSB.append ('\n');
      }

    aSB.append ("\nCurrent list of all unreleased projects:\n\n");
    for (final IProject aProject : aSortedProjects)
      if (!aProject.isDeprecated () && !aProject.isPublished () && aProject.getProjectType ().hasJavaCode ())
      {
        final String sProjectOwner = aProject.getProjectOwner ();

        aSB.append ("### [")
           .append (aProject.getFullBaseDirName ())
           .append ("](https://github.com/")
           .append (sProjectOwner)
           .append ('/')
           .append (MainCreateMetaREADME.getGitHubRepoName (aProject))
           .append (")\n\n");
        _addLineCount (aProject, aSB, aOverallMain, aOverallTest);
        aSB.append ('\n');
      }

    // Summary
    {
      final FileTypeCountMap aOverallSum = new FileTypeCountMap ();
      aOverallSum.incrementFrom (aOverallMain);
      aOverallSum.incrementFrom (aOverallTest);

      final StringBuilder aSB2 = new StringBuilder ();
      aSB2.append ("<table><thead>");
      _addTableHead (aSB2);
      aSB2.append ("</thead><tbody>\n");

      final FileTypeCount aSumMain = new FileTypeCount ();
      final FileTypeCount aSumTest = new FileTypeCount ();
      final FileTypeCount aSumSum = new FileTypeCount ();
      int nRows = 0;
      for (final EFileType eFileType : EFileType.values ())
      {
        final FileTypeCount aCountMain = aOverallMain.get (eFileType);
        final FileTypeCount aCountTest = aOverallTest.get (eFileType);
        final FileTypeCount aCountSum = aOverallSum.get (eFileType);
        if (aCountSum.getFileCount () > 0)
        {
          aSumMain.incrementFrom (aCountMain);
          aSumTest.incrementFrom (aCountTest);
          aSumSum.incrementFrom (aCountSum);

          _addTableRow (aSB2, eFileType, eFileType.getName (), "main", aCountMain);
          _addTableRow (aSB2, eFileType, eFileType.getName (), "test", aCountTest);
          _addTableRow (aSB2, eFileType, eFileType.getName (), "sum", aCountSum);

          ++nRows;
        }
      }

      if (nRows > 0)
      {
        aSB2.append ("</tbody><tfoot>");
        _addTableRow (aSB2, null, "Total sum", "main", aSumMain);
        _addTableRow (aSB2, null, "Total sum", "test", aSumTest);
        _addTableRow (aSB2, null, "Total sum", "sum", aSumSum);
        aSB2.append ("</tfoot></table>\n");
        aSB.append ("### Overall sum\n\n").append (aSB2);
      }
    }

    // Header
    aSB.insert (0, "# Lines of Code\nNote: This file was automatically generated.\n\n");

    // Footer
    aSB.append ("\n## Legend\n\n");

    aSB.append ("* Context: either `main` for the Maven `src/maìn` folder or `test` for `src/test`\n");
    aSB.append ("* File count: affected files\n");
    aSB.append ("* File bytes: the total number of bytes of the affected files\n");
    aSB.append ("* Lines total: the total number of lines read\n");
    aSB.append ("* Lines empty: the number of lines containing no characters or only whitespace characters\n");
    aSB.append ("* Lines empty %: the percentage of empty lines on the total lines\n");
    aSB.append ("* Chars total: the total number of characters read (assuming Windows line ending)\n");
    aSB.append ("* Chars WS: the number of whitespace characters read (excluding line ending)\n");
    aSB.append ("* Chars WS %: the percentage of whitespace characters on the total characters\n");

    aSB.append ("\n### File types\n\n");
    for (final EFileType e : EFileType.values ())
    {
      aSB.append ("* ").append (e.getName ()).append ("\n");
      aSB.append ("    * Extensions: ");
      if (e == EFileType.UNKNOWN_OTHER)
        aSB.append ("*anything else*\n");
      else
        aSB.append ('`').append (e.getExtensions ()).append ("`\n");
      aSB.append ("    * Charset: `").append (e.getCharset ().name ()).append ("`\n");
    }

    aSB.append (MainUpdateREADMEFooter.COMMON_FOOTER);

    SimpleFileIO.writeFile (new File ("LinesOfCode.md"), aSB.toString (), StandardCharsets.UTF_8);
    LOGGER.info ("Done creating lines of code data");
  }
}
