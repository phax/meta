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

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.builder.IBuilder;
import com.helger.base.string.StringHelper;

/**
 * Builder class for class {@link ProjectTail}. A newly created builder assumes the tail train to be
 * maintained - call {@link #maintained(boolean)} with <code>false</code> for trains that reached
 * their end of life.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ProjectTailBuilder implements IBuilder <ProjectTail>
{
  private String m_sLastPublishedVersion;
  private EJDK m_eMinJDK;
  private boolean m_bIsMaintained = true;

  /**
   * Default constructor.
   */
  public ProjectTailBuilder ()
  {}

  /**
   * Copy constructor from an existing tail train.
   *
   * @param aSrc
   *        The source object to copy from. May not be <code>null</code>.
   */
  public ProjectTailBuilder (@NonNull final ProjectTail aSrc)
  {
    lastPublishedVersion (aSrc.getLastPublishedVersionString ()).minJDK (aSrc.getMinimumJDKVersion ())
                                                                .maintained (aSrc.isMaintained ());
  }

  /**
   * Set the last published version of the tail train.
   *
   * @param s
   *        The version to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final ProjectTailBuilder lastPublishedVersion (@Nullable final String s)
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
  public final ProjectTailBuilder minJDK (@Nullable final EJDK e)
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
  public final ProjectTailBuilder maintained (final boolean b)
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
