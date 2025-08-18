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
package com.helger.meta.tools.wsdlgen;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.io.file.FilenameHelper;
import com.helger.io.file.SimpleFileIO;
import com.helger.io.resource.FileSystemResource;
import com.helger.meta.tools.wsdlgen.model.WGInterface;

public final class XSDWriterTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (XSDWriterTest.class);
  private static final boolean [] BOOLS = new boolean [] { true, false };

  @Test
  public void testReadAndGenerateAll ()
  {
    for (final String sFilename : InterfaceReaderTest.FILENAMES)
    {
      LOGGER.info (sFilename);
      final File aFile = new File ("src/test/resources/external", sFilename);
      final WGInterface aInterface = InterfaceReader.readInterface (new FileSystemResource (aFile));
      assertNotNull (sFilename, aInterface);

      for (final boolean bCreateDocumentation : BOOLS)
        for (final boolean bDocumentLiteral : BOOLS)
        {
          final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
          new XSDWriter (aInterface).setCreateDocumentation (bCreateDocumentation)
                                    .setDocumentLiteral (bDocumentLiteral)
                                    .generatedXSD (aBAOS);
        }
    }
  }

  @Test
  public void testWriteAll ()
  {
    for (final String sFilename : InterfaceReaderTest.FILENAMES)
    {
      final File aFile = new File ("src/test/resources/external", sFilename);
      final WGInterface aInterface = InterfaceReader.readInterface (new FileSystemResource (aFile));
      assertNotNull (sFilename, aInterface);

      try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
      {
        new XSDWriter (aInterface).setDocumentLiteral (true).generatedXSD (aBAOS);
        SimpleFileIO.writeFile (new File ("generated/xsd", FilenameHelper.getWithoutExtension (sFilename) + ".xsd"),
                                aBAOS.toByteArray ());
      }
    }
  }
}
