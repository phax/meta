/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.meta.translation;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

@Immutable
public final class TextInLocale
{
  private final String m_sLocale;
  private final String m_sText;

  public TextInLocale (@Nonnull @Nonempty final String sLocale, @Nonnull final String sText)
  {
    ValueEnforcer.notEmpty (sLocale, "Locale");
    ValueEnforcer.notNull (sText, "Text");
    m_sLocale = sLocale.intern ();
    m_sText = sText;
  }

  @Nonnull
  public String getLocale ()
  {
    return m_sLocale;
  }

  @Nonnull
  public String getText ()
  {
    return m_sText;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final TextInLocale rhs = (TextInLocale) o;
    return m_sLocale.equals (rhs.m_sLocale) && m_sText.equals (rhs.m_sText);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sLocale).append (m_sText).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("locale", m_sLocale).append ("text", m_sText).getToString ();
  }
}
