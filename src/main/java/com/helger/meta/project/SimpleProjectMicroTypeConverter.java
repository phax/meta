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
package com.helger.meta.project;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.IMicroTypeConverter;
import com.helger.commons.microdom.impl.MicroElement;

public final class SimpleProjectMicroTypeConverter implements IMicroTypeConverter
{
  private static final String ATTR_PROJECT_NAME = "projectname";
  private static final String ATTR_PROJECT_TYPE = "projecttype";
  private static final String ATTR_BASE_DIR = "basedir";
  private static final String ATTR_LAST_PUBLISHED_VERSION = "lastpubversion";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull @Nonempty final String sTagName)
  {
    final SimpleProject aPrice = (SimpleProject) aObject;
    final IMicroElement ret = new MicroElement (sNamespaceURI, sTagName);
    ret.setAttribute (ATTR_PROJECT_NAME, aPrice.getProjectName ());
    ret.setAttribute (ATTR_PROJECT_TYPE, aPrice.getProjectType ().getID ());
    ret.setAttribute (ATTR_BASE_DIR, aPrice.getBaseDir ().getAbsolutePath ());
    ret.setAttribute (ATTR_LAST_PUBLISHED_VERSION, aPrice.getLastPublishedVersionString ());
    return ret;
  }

  @Nonnull
  public SimpleProject convertToNative (@Nonnull final IMicroElement ePrice)
  {
    final String sProjectName = ePrice.getAttributeValue (ATTR_PROJECT_NAME);

    final String sProjectTypeID = ePrice.getAttributeValue (ATTR_PROJECT_TYPE);
    final EProjectType eProjectType = EProjectType.getFromIDOrNull (sProjectTypeID);

    final String sBaseDir = ePrice.getAttributeValue (ATTR_BASE_DIR);
    final File aBaseDir = new File (sBaseDir);

    final String sLastPublishedVersion = ePrice.getAttributeValue (ATTR_LAST_PUBLISHED_VERSION);

    return new SimpleProject (sProjectName, eProjectType, aBaseDir, sLastPublishedVersion);
  }
}