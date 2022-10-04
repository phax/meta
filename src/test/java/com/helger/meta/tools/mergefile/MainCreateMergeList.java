package com.helger.meta.tools.mergefile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.file.FileSystemIterator;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.file.IFileFilter;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.regex.RegExHelper;

public final class MainCreateMergeList
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCreateMergeList.class);

  public static void main (final String [] args)
  {
    final File aBaseDir = new File ("C:\\a\\F1 Singapur\\R");
    final String sRegEx = "([0-9]+)?.*\\.mp4";

    final ICommonsList <File> files = new CommonsArrayList <> ();
    for (final File f : new FileSystemIterator (aBaseDir).withFilter (IFileFilter.filenameMatchAnyRegEx (sRegEx)))
    {
      final String s1 = f.getName ();
      final String s2 = FilenameHelper.getAsSecureValidASCIIFilename (s1);
      if (!s1.equals (s2))
        f.renameTo (new File (f.getParentFile (), s2));
    }
    for (final File f : new FileSystemIterator (aBaseDir).withFilter (IFileFilter.filenameMatchAnyRegEx (sRegEx)))
      files.add (f);
    if (files.isNotEmpty ())
    {
      if (true)
        files.sort (Comparator.comparingLong (File::lastModified));
      else
        files.sort (Comparator.comparingInt (f -> Integer.parseInt (RegExHelper.getAllMatchingGroupValues ("^([0-9]+).*",
                                                                                                           f.getName ())[0])));

      final StringBuilder sb = new StringBuilder ();
      for (final File f : files)
        sb.append ("file '").append (f.getName ()).append ("'\n");
      SimpleFileIO.writeFile (new File (aBaseDir, "_a.txt"), sb.toString (), StandardCharsets.ISO_8859_1);

      // Add "-v debug" for debug info
      SimpleFileIO.writeFile (new File (aBaseDir, "_a.cmd"),
                              "@echo off\r\n" + "ffmpeg -f concat -safe 0 -i _a.txt -c copy _a.mp4\r\n",
                              StandardCharsets.ISO_8859_1);
    }
    else
      LOGGER.error ("No file found");
  }
}
