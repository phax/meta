package com.helger.meta.tools.buildsystem;

import java.io.File;

import com.helger.commons.collection.ext.CommonsHashMap;
import com.helger.commons.collection.ext.ICommonsMap;
import com.helger.meta.AbstractProjectMain;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.util.MicroHelper;
import com.helger.xml.microdom.util.MicroRecursiveIterator;

public class MainExtractParentPOMDeps extends AbstractProjectMain
{
  public static void main (final String [] args)
  {
    final IMicroDocument eRoot = MicroReader.readMicroXML (new File ("../ph-parent-pom/pom.xml"));
    final ICommonsMap <String, String> aProperties = new CommonsHashMap<> ();

    int i = 0;
    final StringBuilder aSB = new StringBuilder ();

    // Check all relevant dependencies or the like
    for (final IMicroNode aNode : new MicroRecursiveIterator (eRoot))
      if (aNode.isElement ())
      {
        final IMicroElement aElement = (IMicroElement) aNode;
        // groupId is optional e.g. for the defined artefact
        if (aElement.getLocalName ().equals ("artifactId"))
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
            aSB.append ("PARENT_POM_" +
                        (i++) +
                        " (\"" +
                        sGroupID +
                        "\",\"" +
                        sArtifactID +
                        "\",\"" +
                        sVersion +
                        "\"),\n");
        }
      }
    s_aLogger.info (aSB.toString ());
  }
}
