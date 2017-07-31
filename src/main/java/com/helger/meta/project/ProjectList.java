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
package com.helger.meta.project;

import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.StringHelper;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.convert.MicroTypeConverter;
import com.helger.xml.microdom.serialize.MicroReader;

public final class ProjectList
{
  private static final ICommonsOrderedMap <String, IProject> s_aName2Project = new CommonsLinkedHashMap<> ();

  private static void _add (@Nonnull final IProject aProject)
  {
    final String sKey = aProject.getProjectName ();
    if (s_aName2Project.containsKey (sKey))
      throw new IllegalArgumentException ("Another project with name '" + sKey + "' is already contained!");
    s_aName2Project.put (sKey, aProject);
  }

  static
  {
    // Build in projects
    for (final IProject aProject : EProject.values ())
      _add (aProject);
    for (final IProject aProject : EProjectDeprecated.values ())
      _add (aProject);

    // Other projects
    final IReadableResource aRes = new ClassPathResource ("other-projects.xml");
    if (aRes.exists ())
    {
      final IMicroDocument aOthers = MicroReader.readMicroXML (aRes);
      if (aOthers != null)
        for (final IMicroElement eProject : aOthers.getDocumentElement ().getAllChildElements ("project"))
        {
          final SimpleProject aProject = MicroTypeConverter.convertToNative (eProject, SimpleProject.class);
          _add (aProject);
        }
    }
  }

  private ProjectList ()
  {}

  @Nullable
  public static IProject getProjectOfName (@Nullable final String sName)
  {
    return s_aName2Project.get (sName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <IProject> getAllProjects ()
  {
    return s_aName2Project.copyOfValues ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <IProject> getAllProjects (@Nonnull final Predicate <IProject> aFilter)
  {
    return s_aName2Project.copyOfValues (aFilter);
  }

  @Nonnegative
  public static int size ()
  {
    return s_aName2Project.size ();
  }

  public static boolean containsProjectOfDir (@Nullable final String sDirName)
  {
    if (StringHelper.hasNoText (sDirName))
      return false;

    return CollectionHelper.containsAny (s_aName2Project.values (), p -> p.getBaseDir ().getName ().equals (sDirName));
  }
}
