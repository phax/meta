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
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.base.version.Version;

/**
 * A single tail train of a root project, as defined by the "Tip &amp; Tail" model of JEP 14. A tail
 * is forked from a designated tip release, is baselined on a fixed JDK version for its whole life
 * time and receives critical bug fixes and security patches only.<br>
 * Tails are declared on the root project of a repository - which is the parent POM project in
 * nearly every case - and are inherited by all contained modules.
 *
 * @author Philip Helger
 * @see Builder
 * @see IProject#getAllTails()
 */
@Immutable
public record ProjectTail (@NonNull @Nonempty String lastPublishedVersionString,
                           @NonNull EJDK minimumJDKVersion,
                           boolean maintained)
{
  /**
   * Constructor.
   *
   * @param lastPublishedVersionString
   *        The last published version of this tail train. May neither be <code>null</code> nor
   *        empty.
   * @param minimumJDKVersion
   *        The JDK version this tail train is baselined on. It is frozen for the life time of the
   *        train. May not be <code>null</code>.
   * @param maintained
   *        <code>true</code> if this tail train still receives fixes, <code>false</code> if it
   *        reached its end of life.
   */
  public ProjectTail
  {
    ValueEnforcer.notEmpty (lastPublishedVersionString, "LastPublishedVersion");
    ValueEnforcer.notNull (minimumJDKVersion, "MinJDK");
  }

  /**
   * @return The last published version of this tail train as a parsed object. Never
   *         <code>null</code>.
   */
  @NonNull
  public Version getLastPublishedVersion ()
  {
    return Version.parse (lastPublishedVersionString);
  }

  /**
   * @return The major version of this tail train. Within one major version the baseline never
   *         changes, so this is the identity of the train.
   */
  @Nonnegative
  public int getMajorVersion ()
  {
    return getLastPublishedVersion ().getMajor ();
  }

  /**
   * @return A new builder for {@link ProjectTail} objects. Never <code>null</code>.
   */
  @NonNull
  public static Builder builder ()
  {
    return new Builder ();
  }

  /**
   * Create a new builder, filled with the values of the provided tail train.
   *
   * @param aSrc
   *        The source object to copy from. May not be <code>null</code>.
   * @return A new builder for {@link ProjectTail} objects. Never <code>null</code>.
   */
  @NonNull
  public static Builder builder (@NonNull final ProjectTail aSrc)
  {
    return new Builder (aSrc);
  }

  /**
   * Builder class for class {@link ProjectTail}. A newly created builder assumes the tail train to
   * be maintained - call {@link #maintained(boolean)} with <code>false</code> for trains that
   * reached their end of life.
   *
   * @author Philip Helger
   */
  @NotThreadSafe
  public static class Builder implements IBuilder <ProjectTail>
  {
    private String m_sLastPublishedVersion;
    private EJDK m_eMinJDK;
    private boolean m_bIsMaintained = true;

    /**
     * Default constructor.
     */
    public Builder ()
    {}

    /**
     * Copy constructor from an existing tail train.
     *
     * @param aSrc
     *        The source object to copy from. May not be <code>null</code>.
     */
    public Builder (@NonNull final ProjectTail aSrc)
    {
      lastPublishedVersion (aSrc.lastPublishedVersionString ()).minJDK (aSrc.minimumJDKVersion ())
                                                               .maintained (aSrc.maintained ());
    }

    /**
     * Set the last published version of the tail train.
     *
     * @param s
     *        The version to use. May be <code>null</code>.
     * @return this for chaining
     */
    @NonNull
    public final Builder lastPublishedVersion (@Nullable final String s)
    {
      m_sLastPublishedVersion = s;
      return this;
    }

    /**
     * Set the JDK version the tail train is baselined on.
     *
     * @param e
     *        The JDK version to use. May be <code>null</code>.
     * @return this for chaining
     */
    @NonNull
    public final Builder minJDK (@Nullable final EJDK e)
    {
      m_eMinJDK = e;
      return this;
    }

    /**
     * Set whether the tail train still receives fixes. The default is <code>true</code>.
     *
     * @param b
     *        <code>true</code> if the train is maintained, <code>false</code> if it reached its end
     *        of life.
     * @return this for chaining
     */
    @NonNull
    public final Builder maintained (final boolean b)
    {
      m_bIsMaintained = b;
      return this;
    }

    /**
     * Build the {@link ProjectTail} from the provided parameters.
     *
     * @return A new {@link ProjectTail} instance. Never <code>null</code>.
     * @throws IllegalStateException
     *         if any required parameter is missing.
     */
    @NonNull
    public ProjectTail build () throws IllegalStateException
    {
      if (StringHelper.isEmpty (m_sLastPublishedVersion))
        throw new IllegalStateException ("LastPublishedVersion is empty");
      if (m_eMinJDK == null)
        throw new IllegalStateException ("MinJDK is missing");

      return new ProjectTail (m_sLastPublishedVersion, m_eMinJDK, m_bIsMaintained);
    }
  }
}
