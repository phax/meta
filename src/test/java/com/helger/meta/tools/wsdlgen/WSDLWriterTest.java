/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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

import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.meta.tools.wsdlgen.model.WGInterface;

public final class WSDLWriterTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (WSDLWriterTest.class);
  private static final boolean [] BOOLS = new boolean [] { true, false };

  @Test
  public void testReadAndGenerateAll ()
  {
    for (final String sFilename : InterfaceReaderTest.FILENAMES)
    {
      LOGGER.info (sFilename);
      final File aFile = new File ("src/test/resources", sFilename);
      final WGInterface aInterface = InterfaceReader.readInterface (new FileSystemResource (aFile));
      assertNotNull (sFilename, aInterface);

      for (final boolean bCreateDocumentation : BOOLS)
        for (final boolean bCreateWSAddressing : BOOLS)
          for (final EStyle eStyle : EStyle.values ())
            for (final EUse eUse : EUse.values ())
            {
              final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
              new WSDLWriter (aInterface).setCreateDocumentation (bCreateDocumentation)
                                         .setCreateWSAddressing (bCreateWSAddressing)
                                         .generatedWSDL (aBAOS, eStyle, eUse);
            }
    }
  }

  @Test
  public void testWriteAll ()
  {
    for (final String sFilename : InterfaceReaderTest.FILENAMES)
    {
      final File aFile = new File ("src/test/resources", sFilename);
      final WGInterface aInterface = InterfaceReader.readInterface (new FileSystemResource (aFile));
      assertNotNull (sFilename, aInterface);

      final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
      new WSDLWriter (aInterface).generatedWSDL (aBAOS, EStyle.DOCUMENT, EUse.LITERAL);
      SimpleFileIO.writeFile (new File ("wsdl", FilenameHelper.getWithoutExtension (sFilename) + ".wsdl"),
                              aBAOS.toByteArray ());
    }
  }
}
