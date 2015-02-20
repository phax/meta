/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.meta.project;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

public enum EHasWiki implements IHasID <String>
{
  TRUE,
  FALSE;

  @Nonnull
  @ReturnsMutableCopy
  public String getID ()
  {
    return Boolean.toString (isTrue ());
  }

  public boolean isTrue ()
  {
    return this == TRUE;
  }

  @Nullable
  public static EHasWiki getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EHasWiki.class, sID);
  }
}
