package com.helger.meta.tools.mergefile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.file.FileSystemIterator;
import com.helger.commons.io.file.IFileFilter;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.regex.RegExHelper;

public class MainCreateMergeList
{

  private static final Logger LOGGER = LoggerFactory.getLogger (MainCreateMergeList.class);

  public static void main (final String [] args)
  {
    final File aBaseDir = new File ("C:\\a\\F1 NL\\R");
    final ICommonsList <File> files = new CommonsArrayList <> ();
    for (final File f : new FileSystemIterator (aBaseDir).withFilter (IFileFilter.filenameMatchAnyRegEx ("[0-9]+.*\\.mp4")))
      files.add (f);
    if (files.isNotEmpty ())
    {
      files.sort (Comparator.comparingInt (f -> Integer.parseInt (RegExHelper.getAllMatchingGroupValues ("^([0-9]+).*",
                                                                                                         f.getName ())[0])));

      final StringBuilder sb = new StringBuilder ();
      for (final File f : files)
        sb.append ("file '").append (f.getName ()).append ("'\n");
      SimpleFileIO.writeFile (new File (aBaseDir, "_a.txt"), sb.toString (), StandardCharsets.ISO_8859_1);
      SimpleFileIO.writeFile (new File (aBaseDir, "_a.cmd"),
                              "ffmpeg -safe 0 -f concat -i _a.txt -c copy _a.mp4",
                              StandardCharsets.ISO_8859_1);
    }
    else
      LOGGER.error ("No file found");
  }
}
