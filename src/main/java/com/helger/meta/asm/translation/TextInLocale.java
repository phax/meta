package com.helger.meta.asm.translation;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.hash.HashCodeGenerator;
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
    if (!(o instanceof TextInLocale))
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
    return new ToStringGenerator (this).append ("locale", m_sLocale).append ("text", m_sText).toString ();
  }
}
