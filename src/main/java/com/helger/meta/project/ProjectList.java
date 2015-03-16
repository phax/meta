package com.helger.meta.project;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import com.helger.commons.io.IReadableResource;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.serialize.MicroReader;

public final class ProjectList
{
  private static final Map <String, IProject> s_aProjects = new LinkedHashMap <String, IProject> ();

  static
  {
    // Build in projects
    for (final IProject aProject : EProject.values ())
      s_aProjects.put (aProject.getProjectName (), aProject);

    // Other projects
    final IReadableResource aRes = new ClassPathResource ("other-projects.xml");
    if (aRes.exists ())
    {
      final IMicroDocument aOthers = MicroReader.readMicroXML (aRes);
      if (aOthers != null)
        for (final IMicroElement eProject : aOthers.getDocumentElement ().getAllChildElements ("project"))
        {
          final SimpleProject aProject = MicroTypeConverter.convertToNative (eProject, SimpleProject.class);
          s_aProjects.put (aProject.getProjectName (), aProject);
        }
    }
  }

  private ProjectList ()
  {}

  @Nullable
  public static IProject getProjectOfName (@Nullable final String sName)
  {
    // Handle differences between directory name and project name
    if ("parent-pom".equals (sName))
      return EProject.PH_PARENT_POM;
    if ("webservice-client".equals (sName))
      return EProject.ERECHNUNG_WS_CLIENT;
    return s_aProjects.get (sName);
  }

  @Nullable
  public static Iterable <IProject> getAllProjects ()
  {
    return s_aProjects.values ();
  }

  @Nonnegative
  public static int size ()
  {
    return s_aProjects.size ();
  }
}
