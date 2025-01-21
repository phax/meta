/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.meta.tools.filelist;

import java.io.File;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.file.FileSystemIterator;

/**
 * Create the listing of a directory suitable for inclusion in Java code.
 *
 * @author Philip Helger
 */
public final class MainListFiles
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainListFiles.class);

  public static void main (final String [] args)
  {
    final String sDir = "C:\\dev\\git\\phive-rules\\phive-rules-peppol-italy\\src\\test\\resources\\test-files\\2.2.9\\order";
    final ICommonsList <File> aFiles = new CommonsArrayList <> (new FileSystemIterator (sDir)).getSortedInline (Comparator.comparing (File::getName));
    final int nMax = aFiles.size ();
    final StringBuilder aSB = new StringBuilder ();
    for (int i = 0; i < nMax; ++i)
    {
      if (i > 0)
        aSB.append (",\n");
      aSB.append ('"').append (aFiles.get (i).getName ()).append ('"');
    }
    LOGGER.info ("\n" + aSB.toString ());
  }
}
