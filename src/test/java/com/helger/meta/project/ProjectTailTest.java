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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.version.Version;

/**
 * Test class for class {@link ProjectTail} and {@link ProjectTailBuilder}
 *
 * @author Philip Helger
 */
public final class ProjectTailTest
{
  @Test
  public void testBasic ()
  {
    final ProjectTail aTail = new ProjectTail ("11.2.7", EJDK.JDK11, true);
    assertEquals ("11.2.7", aTail.getLastPublishedVersionString ());
    assertEquals (new Version (11, 2, 7), aTail.getLastPublishedVersion ());
    assertEquals (11, aTail.getMajorVersion ());
    assertEquals (EJDK.JDK11, aTail.getMinimumJDKVersion ());
    assertTrue (aTail.isMaintained ());
    assertNotNull (aTail.toString ());
  }

  @Test
  public void testBuilder ()
  {
    // Maintained is true by default
    final ProjectTail aTail = ProjectTail.builder ().lastPublishedVersion ("12.4.1").minJDK (EJDK.JDK17).build ();
    assertEquals ("12.4.1", aTail.getLastPublishedVersionString ());
    assertEquals (12, aTail.getMajorVersion ());
    assertEquals (EJDK.JDK17, aTail.getMinimumJDKVersion ());
    assertTrue (aTail.isMaintained ());

    final ProjectTail aEOL = ProjectTail.builder ()
                                        .lastPublishedVersion ("10.2.5")
                                        .minJDK (EJDK.JDK8)
                                        .maintained (false)
                                        .build ();
    assertEquals (EJDK.JDK8, aEOL.getMinimumJDKVersion ());
    assertFalse (aEOL.isMaintained ());
  }

  @Test
  public void testBuilderCopy ()
  {
    final ProjectTail aTail = ProjectTail.builder ()
                                         .lastPublishedVersion ("10.2.5")
                                         .minJDK (EJDK.JDK8)
                                         .maintained (false)
                                         .build ();
    final ProjectTail aCopy = ProjectTail.builder (aTail).build ();
    assertEquals (aTail, aCopy);
    assertEquals (aTail.hashCode (), aCopy.hashCode ());

    // A different baseline is a different train
    assertNotEquals (aTail, ProjectTail.builder (aTail).minJDK (EJDK.JDK11).build ());
    assertNotEquals (aTail, ProjectTail.builder (aTail).maintained (true).build ());
    assertNotEquals (aTail, ProjectTail.builder (aTail).lastPublishedVersion ("10.2.4").build ());
  }

  @Test
  public void testBuilderIncomplete ()
  {
    try
    {
      // Version is missing
      ProjectTail.builder ().minJDK (EJDK.JDK17).build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // Expected
    }

    try
    {
      // MinJDK is missing
      ProjectTail.builder ().lastPublishedVersion ("12.4.1").build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // Expected
    }
  }
}
