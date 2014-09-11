/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.meta.buildsystem;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.meta.EProject;

/**
 * Base class for the main utilities in this package
 *
 * @author Philip Helger
 */
public abstract class AbstractMainUtils
{
  protected static final Logger s_aLogger = LoggerFactory.getLogger (AbstractMainUtils.class);
  private static int s_nWarnCount = 0;

  protected static final void _warn (@Nonnull final EProject eProject, @Nonnull final String sMsg)
  {
    s_aLogger.warn ("[" + eProject.getProjectName () + "] " + sMsg);
    s_nWarnCount++;
  }

  @Nonnegative
  protected static int getWarnCount ()
  {
    return s_nWarnCount;
  }
}
