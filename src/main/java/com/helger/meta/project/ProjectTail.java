/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.base.version.Version;

/**
 * A single tail train of a root project, as defined by the "Tip &amp; Tail" model of JEP 14. A tail
 * is forked from a designated tip release, is baselined on a fixed JDK version for its whole life
 * time and receives critical bug fixes and security patches only.<br>
 * Tails are declared on the root project of a repository - which is the parent POM project in
 * nearly every case - and are inherited by all contained modules.
 *
 * @author Philip Helger
 * @see ProjectTailBuilder
 * @see IProject#getAllTails()
 */
@Immutable
public class ProjectTail
{
  private final String m_sLastPublishedVersion;
  private final Version m_aLastPublishedVersion;
  private final EJDK m_eMinJDK;
  private final boolean m_bIsMaintained;

  /**
   * Constructor.
   *
   * @param sLastPublishedVersion
   *        The last published version of this tail train. May neither be <code>null</code> nor
   *        empty.
   * @param eMinJDK
   *        The JDK version this tail train is baselined on. It is frozen for the life time of the
   *        train. May not be <code>null</code>.
   * @param bIsMaintained
   *        <code>true</code> if this tail train still receives fixes, <code>false</code> if it
   *        reached its end of life.
   */
  public ProjectTail (@NonNull @Nonempty final String sLastPublishedVersion,
                      @NonNull final EJDK eMinJDK,
                      final boolean bIsMaintained)
  {
    ValueEnforcer.notEmpty (sLastPublishedVersion, "LastPublishedVersion");
    ValueEnforcer.notNull (eMinJDK, "MinJDK");

    m_sLastPublishedVersion = sLastPublishedVersion;
    m_aLastPublishedVersion = Version.parse (sLastPublishedVersion);
    m_eMinJDK = eMinJDK;
    m_bIsMaintained = bIsMaintained;
  }

  /**
   * @return The last published version of this tail train as a String. Neither <code>null</code>
   *         nor empty.
   */
  @NonNull
  @Nonempty
  public final String getLastPublishedVersionString ()
  {
    return m_sLastPublishedVersion;
  }

  /**
   * @return The last published version of this tail train as a parsed object. Never
   *         <code>null</code>.
   */
  @NonNull
  public final Version getLastPublishedVersion ()
  {
    return m_aLastPublishedVersion;
  }

  /**
   * @return The major version of this tail train. Within one major version the baseline never
   *         changes, so this is the identity of the train.
   */
  @Nonnegative
  public final int getMajorVersion ()
  {
    return m_aLastPublishedVersion.getMajor ();
  }

  /**
   * @return The JDK version this tail train is baselined on. Never <code>null</code>.
   */
  @NonNull
  public final EJDK getMinimumJDKVersion ()
  {
    return m_eMinJDK;
  }

  /**
   * @return <code>true</code> if this tail train still receives critical fixes and security
   *         patches, <code>false</code> if it reached its end of life.
   */
  public final boolean isMaintained ()
  {
    return m_bIsMaintained;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ProjectTail rhs = (ProjectTail) o;
    return m_sLastPublishedVersion.equals (rhs.m_sLastPublishedVersion) &&
           m_eMinJDK.equals (rhs.m_eMinJDK) &&
           m_bIsMaintained == rhs.m_bIsMaintained;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sLastPublishedVersion)
                                       .append (m_eMinJDK)
                                       .append (m_bIsMaintained)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("LastPublishedVersion", m_sLastPublishedVersion)
                                       .append ("MinJDK", m_eMinJDK)
                                       .append ("IsMaintained", m_bIsMaintained)
                                       .getToString ();
  }

  /**
   * @return A new builder for {@link ProjectTail} objects. Never <code>null</code>.
   */
  @NonNull
  public static ProjectTailBuilder builder ()
  {
    return new ProjectTailBuilder ();
  }

  /**
   * Create a new builder, filled with the values of the provided tail train.
   *
   * @param aSrc
   *        The source object to copy from. May not be <code>null</code>.
   * @return A new builder for {@link ProjectTail} objects. Never <code>null</code>.
   */
  @NonNull
  public static ProjectTailBuilder builder (@NonNull final ProjectTail aSrc)
  {
    return new ProjectTailBuilder (aSrc);
  }
}
