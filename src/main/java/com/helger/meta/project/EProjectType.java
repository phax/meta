/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Defines the overall project type, based on a very subjective point of view :)
 *
 * @author Philip Helger
 */
public enum EProjectType implements IHasID <String>
{
  JAVA_LIBRARY ("java-library"),
  JAVA_APPLICATION ("java-application"),
  JAVA_WEB_APPLICATION ("java-web-application"),
  MAVEN_PLUGIN ("maven-plugin"),
  MAVEN_POM ("maven-pom"),
  OTHER_PLUGIN ("other-plugin"),
  RESOURCES_ONLY ("resources-only");

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

  public boolean hasJavaCode ()
  {
    return this != MAVEN_POM && this != RESOURCES_ONLY;
  }

  @Nullable
  public static EProjectType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EProjectType.class, sID);
  }

  @Nullable
  public static EProjectType getFromIDOrDefault (@Nullable final String sID, @Nullable final EProjectType eDefault)
  {
    return EnumHelper.getFromIDOrDefault (EProjectType.class, sID, eDefault);
  }
}
