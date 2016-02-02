/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.string.StringHelper;

public final class ProjectList
{
  private static final Map <String, IProject> s_aName2Project = new LinkedHashMap <String, IProject> ();

  static
  {
    // Build in projects
    for (final IProject aProject : EProject.values ())
      s_aName2Project.put (aProject.getProjectName (), aProject);
    for (final IProject aProject : EProjectDeprecated.values ())
      s_aName2Project.put (aProject.getProjectName (), aProject);

    // Other projects
    final IReadableResource aRes = new ClassPathResource ("other-projects.xml");
    if (aRes.exists ())
    {
      final IMicroDocument aOthers = MicroReader.readMicroXML (aRes);
      if (aOthers != null)
        for (final IMicroElement eProject : aOthers.getDocumentElement ().getAllChildElements ("project"))
        {
          final SimpleProject aProject = MicroTypeConverter.convertToNative (eProject, SimpleProject.class);
          s_aName2Project.put (aProject.getProjectName (), aProject);
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

  @Nullable
  public static Iterable <IProject> getAllProjects ()
  {
    return s_aName2Project.values ();
  }

  @Nullable
  public static List <IProject> getAllProjects (@Nonnull final Predicate <IProject> aFilter)
  {
    return CollectionHelper.getAll (s_aName2Project.values (), aFilter);
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
