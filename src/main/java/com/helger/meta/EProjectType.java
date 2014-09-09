package com.helger.meta;

import javax.annotation.Nonnull;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.id.IHasID;

public enum EProjectType implements IHasID <String>
{
  JAVA_LIBRARY ("java-library"),
  JAVA_APPLICATION ("java-application"),
  JAVA_WEB_APPLICATION ("java-web-application"),
  MAVEN_PLUGIN ("maven-plugin"),
  MAVEN_POM ("maven-pom"),
  JAXB_PLUGIN ("jaxb-plugin");

  private final String m_sID;

  private EProjectType (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }
}
