package com.helger.meta.tools.mergefile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.file.FileSystemIterator;
import com.helger.commons.io.file.IFileFilter;
import com.helger.commons.io.file.SimpleFileIO;

public class MainCreateMergeList
{
  public static void main (final String [] args)
  {
    final File aBaseDir = new File ("C:\\a\\Spielberg");
    final ICommonsList <File> files = new CommonsArrayList <> ();
    for (final File f : new FileSystemIterator (aBaseDir).withFilter (IFileFilter.filenameMatchAnyRegEx ("[0-9]+.*\\.mp4")))
      files.add (f);
    files.sort (Comparator.comparing (File::getName));

    final StringBuilder sb = new StringBuilder ();
    for (final File f : files)
      sb.append ("file '").append (f.getName ()).append ("'\n");
    SimpleFileIO.writeFile (new File (aBaseDir, "_a.txt"), sb.toString (), StandardCharsets.ISO_8859_1);
    SimpleFileIO.writeFile (new File (aBaseDir, "_a.cmd"),
                            "ffmpeg -safe 0 -f concat -i _a.txt -c copy _a.mp4",
                            StandardCharsets.ISO_8859_1);
  }
}
