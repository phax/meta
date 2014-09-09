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
package com.helger.meta;

import java.io.File;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * Constants for this project
 *
 * @author Philip Helger
 */
@Immutable
public final class CMeta
{
  public static final File GIT_BASE_DIR = new File ("").getAbsoluteFile ().getParentFile ();

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final CMeta s_aInstance = new CMeta ();

  private CMeta ()
  {}
}
