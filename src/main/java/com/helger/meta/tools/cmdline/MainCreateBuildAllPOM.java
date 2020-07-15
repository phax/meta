/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.meta.tools.cmdline;

import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.CMeta;
import com.helger.meta.project.EProject;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.microdom.util.MicroHelper;
import com.helger.xml.microdom.util.MicroRecursiveIterator;

/**
 * Check whether the Maven pom.xml of a project is consistent to the
 * requirements
 *
 * @author Philip Helger
 */
public final class MainCreateBuildAllPOM extends AbstractProjectMain
{
  private static final String MAVEN_NS = "http://maven.apache.org/POM/4.0.0";

  private static boolean _isSupportedGroupID (@Nullable final String sGroupID)
  {
    return "com.helger".equals (sGroupID) || "com.helger.maven".equals (sGroupID);
  }

  private static void _readPOM (@Nonnull final IProject aProject,
                                @Nonnull final IMicroDocument aDoc,
                                @Nonnull final ICommonsMap <IProject, ICommonsSet <IProject>> aTree)
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug (aProject.getProjectName ());

    final IMicroElement eRoot = aDoc.getDocumentElement ();

    final String sThisArtefactID = MicroHelper.getChildTextContentTrimmed (eRoot, "artifactId");
    final IProject aThisProject = ProjectList.getProjectOfName (sThisArtefactID);
    if (aThisProject != aProject)
      throw new IllegalStateException (sThisArtefactID + " is weird: " + aThisProject + " vs. " + aProject);

    // Check all relevant dependencies or the like
    for (final IMicroNode aNode : new MicroRecursiveIterator (eRoot))
      if (aNode.isElement ())
      {
        final IMicroElement aElement = (IMicroElement) aNode;
        // groupId is optional e.g. for the defined artefact
        if (aElement.getLocalName ().equals ("artifactId"))
        {
          // Check if the current artefact is in the "com.helger" group
          final String sGroupID = MicroHelper.getChildTextContentTrimmed ((IMicroElement) aElement.getParent (), "groupId");
          if (_isSupportedGroupID (sGroupID))
          {
            // Match!
            final String sArtifactID = aElement.getTextContentTrimmed ();
            final IProject aReferencedProject = ProjectList.getProjectOfName (sArtifactID);
            if (aReferencedProject == null)
            {
              _warn (aProject, "Referenced unknown project '" + sArtifactID + "'");
            }
            else
            {
              if (!sArtifactID.equals (sThisArtefactID))
              {
                aTree.computeIfAbsent (aThisProject, k -> new CommonsHashSet <> ()).add (aReferencedProject);
              }
            }
          }
        }
      }
  }

  public static void main (final String [] args)
  {
    // Read all dependencies
    final ICommonsMap <IProject, ICommonsSet <IProject>> aTree = new CommonsHashMap <> ();
    for (final IProject aProject : ProjectList.getAllProjects (p -> p.isBuildInProject () && !p.isDeprecated ()))
    {
      final IMicroDocument aDoc = MicroReader.readMicroXML (aProject.getPOMFile ());
      if (aDoc == null)
        throw new IllegalStateException ("Failed to read " + aProject.getPOMFile ());
      _readPOM (aProject, aDoc, aTree);
    }

    // Fill transitive dependencies
    boolean bChanged;
    int nIterations = 0;
    do
    {
      bChanged = false;
      ++nIterations;
      for (final Map.Entry <IProject, ICommonsSet <IProject>> aEntry : aTree.entrySet ())
      {
        final int nOld = aEntry.getValue ().size ();
        for (final IProject eReferencedProject : new CommonsArrayList <> (aEntry.getValue ()))
        {
          final Set <IProject> aTransitiveDeps = aTree.get (eReferencedProject);
          if (aTransitiveDeps != null)
            aEntry.getValue ().addAll (aTransitiveDeps);
        }
        if (aEntry.getValue ().size () > nOld)
          bChanged = true;
      }
    } while (bChanged);
    LOGGER.info ("Found all transitive dependencies after " + nIterations + " iterations");

    // Evaluate dependencies
    final ICommonsList <Map.Entry <IProject, ICommonsSet <IProject>>> aEntries = new CommonsArrayList <> (aTree.entrySet ());
    aEntries.sort ( (o1, o2) -> {
      // Less dependencies before many dependencies, because transitivity was
      // already handled
      int ret = o1.getValue ().size () - o2.getValue ().size ();
      if (ret == 0)
      {
        // Same dependency count
        if (o1.getValue ().contains (o2.getKey ()))
        {
          // First depends on second
          ret = +1;
        }
        else
          if (o2.getValue ().contains (o1.getKey ()))
          {
            // Second depends on first
            ret = -1;
          }
          else
          {
            // By name
            ret = o1.getKey ().compareTo (o2.getKey ());
          }
      }

      return ret;
    });

    // Create builder POM
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eProject = aDoc.appendElement (MAVEN_NS, "project");
    eProject.appendElement (MAVEN_NS, "modelVersion").appendText ("4.0.0");
    eProject.appendElement (MAVEN_NS, "groupId").appendText ("com.helger.builder");
    eProject.appendElement (MAVEN_NS, "artifactId").appendText ("all-builder");
    eProject.appendElement (MAVEN_NS, "packaging").appendText ("pom");
    eProject.appendElement (MAVEN_NS, "name").appendText ("all-builder");
    eProject.appendElement (MAVEN_NS, "version").appendText ("1");
    final IMicroElement eModules = eProject.appendElement (MAVEN_NS, "modules");
    eModules.appendElement (MAVEN_NS, "module").appendText (EProject.PH_PARENT_POM.getProjectName ());

    // Parent POM and Maven plugins always go first!
    for (final Map.Entry <IProject, ICommonsSet <IProject>> aEntry : aEntries)
    {
      final IProject eCurProject = aEntry.getKey ();
      eModules.appendComment (eCurProject + " -> " + aEntry.getValue ());
      eModules.appendElement (MAVEN_NS, "module").appendText (eCurProject.getFullBaseDirName ());
    }

    MicroWriter.writeToFile (aDoc, new File (CMeta.GIT_BASE_DIR, "pom-all.xml"));
    LOGGER.info ("Done - " + getWarnCount () + " warning(s)");
  }
}
