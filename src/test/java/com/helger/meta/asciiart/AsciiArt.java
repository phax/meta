/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.meta.asciiart;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;

public class AsciiArt
{
  public static final class Settings
  {
    final Font m_aFont;
    final int m_nWidth;
    final int m_nHeight;
    private final String m_sNewLine;

    public Settings (final Font font, final int width, final int height, final String sNewLine)
    {
      m_aFont = font;
      m_nWidth = width;
      m_nHeight = height;
      m_sNewLine = sNewLine;
    }
  }

  public AsciiArt ()
  {}

  public static void main (final String [] args)
  {
    final String text = "ph-oton";
    final int nPixelPerChar = 30;
    final Settings settings = new Settings (new Font ("Courier New", Font.BOLD, nPixelPerChar),
                                            text.length () * nPixelPerChar,
                                            nPixelPerChar,
                                            "\n");

    final String s = drawString (text, settings);
    System.out.println (s);
  }

  public static String drawString (final String text, final Settings aSettings)
  {
    final BufferedImage image = new BufferedImage (aSettings.m_nWidth, aSettings.m_nHeight, BufferedImage.TYPE_INT_RGB);
    final Graphics2D graphics2D = (Graphics2D) image.getGraphics ();
    graphics2D.setFont (aSettings.m_aFont);
    // final FontRenderContext frc = new FontRenderContext (null, false, false);
    graphics2D.setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    final double dScale = 0.9;
    final double dOffsetLeft = (1 - dScale) / 2;
    graphics2D.drawString (text, (int) (aSettings.m_nWidth / text.length () * dOffsetLeft), (int) (aSettings.m_nHeight * dScale));

    final int nLineLen = aSettings.m_nWidth + aSettings.m_sNewLine.length ();
    final ICommonsList <String> aLines = new CommonsArrayList <> (aSettings.m_nHeight);
    for (int y = 0; y < aSettings.m_nHeight; y++)
    {
      final StringBuilder aSB = new StringBuilder (nLineLen);
      for (int x = 0; x < aSettings.m_nWidth; x++)
      {
        final int nRGB = image.getRGB (x, y);
        aSB.append (nRGB == -16777216 ? ' ' : nRGB == -1 ? '#' : '*');
      }
      // Ignore empty lines
      final String sRow = aSB.toString ();
      if (sRow.trim ().length () == 0)
        continue;
      aLines.add (sRow);
    }

    // Determine how many chars to remove on the right side
    int nMinTrailingSpaces = Integer.MAX_VALUE;
    for (final String s : aLines)
    {
      final int nTrailingSpaces = s.length () - s.trim ().length ();
      nMinTrailingSpaces = Math.min (nMinTrailingSpaces, nTrailingSpaces);
    }
    final int nCharsToCut = nMinTrailingSpaces;

    // Combine to a single row
    return StringHelper.getImplodedMapped (aSettings.m_sNewLine, aLines, x -> x.substring (0, x.length () - nCharsToCut));
  }
}
