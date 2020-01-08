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
package com.helger.meta;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.string.StringHelper;
import com.helger.meta.project.IProject;

/**
 * Base class for the main utilities in this package
 *
 * @author Philip Helger
 */
public abstract class AbstractProjectMain
{
  public static final String BATCH_HEADER = "@echo off\n" +
                                            "rem This files is generated - DO NOT EDIT - " +
                                            PDTFactory.getCurrentLocalDateTime ().toString () +
                                            "\n";
  public static final String BATCH_FOOTER = "goto end\n" +
                                            ":error\n" +
                                            "echo An error occured!!!\n" +
                                            "pause\n" +
                                            "goto exit\n" +
                                            ":end\n" +
                                            "echo Successfully done\n" +
                                            ":exit\n";
  public static final Charset BATCH_CHARSET = StandardCharsets.ISO_8859_1;

  protected static final Logger LOGGER = LoggerFactory.getLogger (AbstractProjectMain.class);
  private static int s_nWarnCount = 0;

  static
  {
    GlobalDebug.setDebugModeDirect (false);
  }

  @Nonnull
  @Nonempty
  protected static final String _getLogPrefix (@Nonnull final IProject aProject)
  {
    return "[" + aProject.getProjectName () + "] ";
  }

  protected static final void _warn (@Nonnull final IProject aProject, @Nonnull final String sMsg)
  {
    LOGGER.warn (_getLogPrefix (aProject) + sMsg);
    s_nWarnCount++;
  }

  @Nonnegative
  protected static final int getWarnCount ()
  {
    return s_nWarnCount;
  }

  protected static final void _info (@Nonnull final IProject aProject, @Nonnull final String sMsg)
  {
    LOGGER.info (_getLogPrefix (aProject) + sMsg);
  }

  @Nonnull
  @Nonempty
  protected static final String getBatchLabel (@Nonnull final String sPrefix, @Nonnull final IProject aProject)
  {
    final String ret = sPrefix + "-" + aProject.getProjectName ();
    return StringHelper.removeAll (ret, '-');
  }
}
