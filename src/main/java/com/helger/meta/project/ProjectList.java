/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import java.io.File;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.StringHelper;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.convert.MicroTypeConverter;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * Overall project list. Contains:
 * <ul>
 * <li>{@link EProject}</li>
 * <li>{@link EProjectDeprecated}</li>
 * <li>and all custom projects</li>
 * </ul>
 *
 * @author Philip Helger
 */
public final class ProjectList
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ProjectList.class);
  private static final ICommonsOrderedSet <File> BASE_DIRS = new CommonsLinkedHashSet <> ();
  private static final ICommonsOrderedMap <String, IProject> NAME_TO_PROJECT = new CommonsLinkedHashMap <> ();

  private static void _add (@Nonnull final IProject aProject)
  {
    final String sKey = aProject.getProjectName ();
    if (NAME_TO_PROJECT.containsKey (sKey))
      throw new IllegalArgumentException ("Another project with name '" + sKey + "' is already contained!");
    NAME_TO_PROJECT.put (sKey, aProject);
  }

  static
  {
    // Build in projects
    for (final IProject aProject : EProject.values ())
      _add (aProject);
    if (false)
      for (final IProject aProject : EProjectToop.values ())
        _add (aProject);
    for (final IProject aProject : EProjectDeprecated.values ())
      _add (aProject);

    // Other projects
    final IReadableResource aRes = new ClassPathResource ("other-projects.xml");
    if (aRes.exists ())
    {
      final IMicroDocument aOthers = MicroReader.readMicroXML (aRes);
      if (aOthers != null)
      {
        for (final IMicroElement eBaseDir : aOthers.getDocumentElement ().getAllChildElements ("basedir"))
        {
          final String sBaseDir = eBaseDir.getTextContentTrimmed ();
          final File aBaseDir = new File (sBaseDir);
          if (aBaseDir.exists ())
            if (!BASE_DIRS.add (aBaseDir))
              LOGGER.warn ("Duplicate base dir present: " + aBaseDir);
            else
              if (LOGGER.isDebugEnabled ())
                LOGGER.debug ("Added base dir " + aBaseDir.getAbsolutePath ());
        }

        if (BASE_DIRS.isEmpty ())
          LOGGER.error ("No base directory is present - resolution of other projects will fail!");

        for (final IMicroElement eProject : aOthers.getDocumentElement ().getAllChildElements ("project"))
        {
          final SimpleProject aProject = MicroTypeConverter.convertToNative (eProject, SimpleProject.class);
          _add (aProject);

          for (final IMicroElement eSubProject : eProject.getAllChildElements ("project"))
          {
            final SimpleProject aSubProject = SimpleProjectMicroTypeConverter.convertToNativeWithParent (aProject, eSubProject);
            _add (aSubProject);
          }
        }
      }
    }
  }

  private ProjectList ()
  {}

  @Nullable
  public static IProject getProjectOfName (@Nullable final String sName)
  {
    return NAME_TO_PROJECT.get (sName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <IProject> getAllProjects ()
  {
    return NAME_TO_PROJECT.copyOfValues ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <IProject> getAllProjects (@Nonnull final Predicate <IProject> aFilter)
  {
    return NAME_TO_PROJECT.copyOfValues (aFilter);
  }

  @Nonnegative
  public static int size ()
  {
    return NAME_TO_PROJECT.size ();
  }

  public static boolean containsProjectOfDir (@Nullable final String sDirName)
  {
    if (StringHelper.hasNoText (sDirName))
      return false;

    return CollectionHelper.containsAny (NAME_TO_PROJECT.values (), p -> p.getBaseDir ().getName ().equals (sDirName));
  }

  @Nonnull
  public static File findBaseDirectory (@Nonnull @Nonempty final String sDir)
  {
    ValueEnforcer.notEmpty (sDir, "Dir");
    for (final File aBaseDir : BASE_DIRS)
    {
      final File ret = new File (aBaseDir, sDir);
      if (ret.exists ())
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Resolved '" + sDir + "' to " + ret.getAbsolutePath ());
        return ret;
      }
    }
    throw new IllegalStateException ("Failed to resolve directory '" + sDir + "' in " + BASE_DIRS.toString ());
  }
}
