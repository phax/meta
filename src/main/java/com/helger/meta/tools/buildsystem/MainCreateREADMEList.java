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
package com.helger.meta.tools.buildsystem;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import com.helger.commons.charset.CCharset;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.compare.AbstractComparator;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Create the README.md file of this project.
 *
 * @author Philip Helger
 */
public final class MainCreateREADMEList extends AbstractProjectMain
{
  @Nonnull
  private static String _getGitHubRepoName (@Nonnull final IProject aProject)
  {
    if (aProject.getParentProject () != null)
      return _getGitHubRepoName (aProject.getParentProject ());
    return aProject.getBaseDir ().getName ();
  }

  private static void _addBadgeMavenCentral (@Nonnull final IProject aProject, @Nonnull final StringBuilder aSB)
  {
    final String sGroupID = aProject.getMavenGroupID ();
    final String sArticfactID = aProject.getMavenArtifactID ();
    aSB.append ("\n   [![Maven Central](https://maven-badges.herokuapp.com/maven-central/")
       .append (sGroupID)
       .append ("/")
       .append (sArticfactID)
       .append ("/badge.svg)](https://maven-badges.herokuapp.com/maven-central/")
       .append (sGroupID)
       .append ("/")
       .append (sArticfactID)
       .append (") ");
  }

  private static void _addBadgeTravis (@Nonnull final IProject aProject, @Nonnull final StringBuilder aSB)
  {
    final String sRepoName = _getGitHubRepoName (aProject);
    aSB.append ("\n   [![Build Status](https://travis-ci.org/phax/")
       .append (sRepoName)
       .append (".svg?branch=master)](https://travis-ci.org/phax/")
       .append (sRepoName)
       .append (")");
  }

  public static void main (final String [] args)
  {
    final StringBuilder aSB = new StringBuilder ();

    final List <IProject> aSortedProjects = CollectionHelper.getSorted (ProjectList.getAllProjects (),
                                                                        new AbstractComparator <IProject> ()
                                                                        {
                                                                          @Override
                                                                          protected int mainCompare (final IProject aElement1,
                                                                                                     final IProject aElement2)
                                                                          {
                                                                            int ret = aElement1.getBaseDir ()
                                                                                               .compareTo (aElement2.getBaseDir ());
                                                                            if (ret == 0)
                                                                              ret = aElement1.getProjectName ()
                                                                                             .compareTo (aElement2.getProjectName ());
                                                                            return ret;
                                                                          }
                                                                        });

    // Show all
    aSB.append ("Current list of all projects (as of ").append (new Date ().toString ()).append ("):\n\n");
    for (final IProject aProject : aSortedProjects)
      if (aProject.isBuildInProject () && !aProject.isDeprecated () && aProject.isPublished ())
      {
        final String sRepoName = _getGitHubRepoName (aProject);

        aSB.append (" * [")
           .append (aProject.getFullBaseDirName ())
           .append ("](https://github.com/phax/")
           .append (sRepoName)
           .append (") - Version ")
           .append (aProject.getLastPublishedVersionString ())
           .append ("\n");
        _addBadgeMavenCentral (aProject, aSB);
        _addBadgeTravis (aProject, aSB);
        aSB.append ('\n');
      }

    aSB.append ("\nCurrent list of all unreleased projects:\n\n");
    for (final IProject aProject : aSortedProjects)
      if (aProject.isBuildInProject () && !aProject.isDeprecated () && !aProject.isPublished ())
      {
        aSB.append (" * [")
           .append (aProject.getFullBaseDirName ())
           .append ("](https://github.com/phax/")
           .append (_getGitHubRepoName (aProject))
           .append (")\n");
        _addBadgeTravis (aProject, aSB);
        aSB.append ('\n');
      }

    // Add deprecated projects
    aSB.append ("\nAll deprecated projects:\n\n");
    for (final IProject aProject : aSortedProjects)
      if (aProject.isBuildInProject () && aProject.isDeprecated ())
      {
        aSB.append (" * [")
           .append (aProject.getFullBaseDirName ())
           .append ("](https://github.com/phax/")
           .append (_getGitHubRepoName (aProject))
           .append (") - ");
        if (aProject.isPublished ())
        {
          aSB.append ("Version ").append (aProject.getLastPublishedVersionString ()).append ('\n');
          _addBadgeMavenCentral (aProject, aSB);
        }
        else
          aSB.append ("never released");
        aSB.append ('\n');
      }

    // Header
    aSB.insert (0,
                "#meta\n\nA meta project for easy management of my other projects :)\nThis project is not meant to be released but only helps me internally to get all of them aligned.\n\n");

    // Footer
    aSB.append ("\n---\n\nOn Twitter: <a href=\"https://twitter.com/philiphelger\">Follow @philiphelger</a>\n");

    SimpleFileIO.writeFile (new File ("README.md"), aSB.toString (), CCharset.CHARSET_UTF_8_OBJ);
    System.out.println ("Done");
  }
}
