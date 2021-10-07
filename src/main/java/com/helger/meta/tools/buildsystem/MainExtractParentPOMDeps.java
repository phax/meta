/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EExternalDependency;
import com.helger.meta.project.ProjectList;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.util.MicroHelper;
import com.helger.xml.microdom.util.MicroRecursiveIterator;

public final class MainExtractParentPOMDeps extends AbstractProjectMain
{
  public static void main (final String [] args)
  {
    final IMicroDocument eRoot = MicroReader.readMicroXML (new File ("../ph-parent-pom/pom.xml"));
    final ICommonsMap <String, String> aProperties = new CommonsHashMap <> ();

    int i = 0;
    final StringBuilder aSB = new StringBuilder ();

    // Check all relevant dependencies or the like
    for (final IMicroNode aNode : new MicroRecursiveIterator (eRoot))
      if (aNode.isElement ())
      {
        final IMicroElement aElement = (IMicroElement) aNode;
        // groupId is optional e.g. for the defined artefact
        if (aElement.hasLocalName ("artifactId"))
        {
          // Check if the current artefact is in the "com.helger" group
          String sGroupID = MicroHelper.getChildTextContentTrimmed ((IMicroElement) aElement.getParent (), "groupId");
          if (sGroupID != null && sGroupID.contains ("$"))
          {
            // Try to resolve through properties. May be null if properties
            // are in the parent POM
            sGroupID = aProperties.get (sGroupID);
          }
          String sArtifactID = aElement.getTextContentTrimmed ();
          if (sArtifactID != null && sArtifactID.contains ("$"))
          {
            // Try to resolve through properties. May be null if properties
            // are in the parent POM
            sArtifactID = aProperties.get (sArtifactID);
          }
          // Version is optional e.g. when dependencyManagement is used
          String sVersion = MicroHelper.getChildTextContentTrimmed ((IMicroElement) aElement.getParent (), "version");
          if (sVersion != null && sVersion.contains ("$"))
          {
            // Try to resolve through properties. May be null if properties
            // are in the parent POM
            sVersion = aProperties.get (sVersion);
          }

          if (sGroupID != null && sArtifactID != null && sVersion != null)
          {
            // Avoid SNAPSHOT references
            // Avoid including external dependencies already present
            final String sFinalGroupID = sGroupID;
            final String sFinalArtifactID = sArtifactID;
            if (!sVersion.endsWith ("-SNAPSHOT") &&
                EExternalDependency.findAll (x -> !x.name ().startsWith ("PARENT_POM_") &&
                                                  x.hasGroupID (sFinalGroupID) &&
                                                  x.hasArtifactID (sFinalArtifactID))
                                   .isEmpty () &&
                ProjectList.getAllProjects (x -> x.hasMavenGroupID (sFinalGroupID) && x.hasMavenArtifactID (sFinalArtifactID)).isEmpty ())
            {
              aSB.append ("PARENT_POM_")
                 .append (i++)
                 .append (" (\"")
                 .append (sGroupID)
                 .append ("\",\"")
                 .append (sArtifactID)
                 .append ("\",\"")
                 .append (sVersion)
                 .append ("\", EJDK.JDK8),\n");
            }
          }
        }
      }
    LOGGER.info (aSB.toString ());
  }
}
