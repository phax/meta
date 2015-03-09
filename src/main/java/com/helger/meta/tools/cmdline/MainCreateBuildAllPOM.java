/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.microdom.serialize.MicroWriter;
import com.helger.commons.microdom.utils.MicroRecursiveIterator;
import com.helger.commons.microdom.utils.MicroUtils;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.CMeta;
import com.helger.meta.project.EProject;
import com.helger.meta.project.EProjectType;
import com.helger.meta.project.IProject;

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

  private static void _readPOM (@Nonnull final IProject eProject,
                                @Nonnull final IMicroDocument aDoc,
                                @Nonnull final Map <IProject, Set <IProject>> aTree)
  {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug (eProject.getProjectName ());

    final IMicroElement eRoot = aDoc.getDocumentElement ();

    final String sThisArtefactID = MicroUtils.getChildTextContentTrimmed (eRoot, "artifactId");
    final EProject eThisProject = EProject.getFromProjectNameOrNull (sThisArtefactID);
    if (eThisProject != eProject)
      throw new IllegalStateException (sThisArtefactID + " is weird: " + eThisProject + " vs. " + eProject);

    // Check all relevant dependencies or the like
    for (final IMicroNode aNode : new MicroRecursiveIterator (eRoot))
      if (aNode.isElement ())
      {
        final IMicroElement aElement = (IMicroElement) aNode;
        // groupId is optional e.g. for the defined artefact
        if (aElement.getLocalName ().equals ("artifactId"))
        {
          // Check if the current artefact is in the "com.helger" group
          final String sGroupID = MicroUtils.getChildTextContentTrimmed ((IMicroElement) aElement.getParent (),
                                                                         "groupId");
          if (_isSupportedGroupID (sGroupID))
          {
            // Match!
            final String sArtifactID = aElement.getTextContentTrimmed ();
            final EProject eReferencedProject = EProject.getFromProjectNameOrNull (sArtifactID);
            if (eReferencedProject == null)
            {
              _warn (eProject, "Referenced unknown project '" + sArtifactID + "'");
            }
            else
            {
              if (!sArtifactID.equals (sThisArtefactID))
              {
                Set <IProject> aRefProjects = aTree.get (eThisProject);
                if (aRefProjects == null)
                {
                  aRefProjects = new HashSet <IProject> ();
                  aTree.put (eThisProject, aRefProjects);
                }
                aRefProjects.add (eReferencedProject);
              }
            }
          }
        }
      }
  }

  public static void main (final String [] args)
  {
    // Read all dependencies
    final Map <IProject, Set <IProject>> aTree = new HashMap <IProject, Set <IProject>> ();
    for (final IProject e : getAllProjects ())
      if (e.getProjectType () != EProjectType.MAVEN_POM && e.isBuildInProject ())
      {
        final IMicroDocument aDoc = MicroReader.readMicroXML (e.getPOMFile ());
        if (aDoc == null)
          throw new IllegalStateException ("Failed to read " + e.getPOMFile ());
        _readPOM (e, aDoc, aTree);
      }

    // Fill transitive dependencies
    boolean bChanged;
    int nIterations = 0;
    do
    {
      bChanged = false;
      ++nIterations;
      for (final Map.Entry <IProject, Set <IProject>> aEntry : aTree.entrySet ())
      {
        final int nOld = aEntry.getValue ().size ();
        for (final IProject eReferencedProject : ContainerHelper.newList (aEntry.getValue ()))
        {
          final Set <IProject> aTransitiveDeps = aTree.get (eReferencedProject);
          if (aTransitiveDeps != null)
            aEntry.getValue ().addAll (aTransitiveDeps);
        }
        if (aEntry.getValue ().size () > nOld)
          bChanged = true;
      }
    } while (bChanged);
    s_aLogger.info ("Found all transitive dependencies after " + nIterations + " iterations");

    // Evaluate dependencies
    final List <Map.Entry <IProject, Set <IProject>>> aEntries = ContainerHelper.newList (aTree.entrySet ());
    ContainerHelper.getSortedInline (aEntries, new Comparator <Map.Entry <IProject, Set <IProject>>> ()
    {
      public int compare (final Entry <IProject, Set <IProject>> o1, final Entry <IProject, Set <IProject>> o2)
      {
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
      }
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
    for (final Map.Entry <IProject, Set <IProject>> aEntry : aEntries)
    {
      final IProject eCurProject = aEntry.getKey ();
      eModules.appendComment (eCurProject + " -> " + aEntry.getValue ());
      eModules.appendElement (MAVEN_NS, "module").appendText (eCurProject.getProjectName ());
    }

    MicroWriter.writeToFile (aDoc, new File (CMeta.GIT_BASE_DIR, "pom-all.xml"));
    s_aLogger.info ("Done - " + getWarnCount () + " warning(s)");
  }
}
