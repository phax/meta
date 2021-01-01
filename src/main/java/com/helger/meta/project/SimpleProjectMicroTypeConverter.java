/**
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
package com.helger.meta.project;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.StringParser;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

public final class SimpleProjectMicroTypeConverter implements IMicroTypeConverter <SimpleProject>
{
  private static final String ATTR_HOSTING_PLATFORM = "hostingplatform";
  private static final String ATTR_PROJECT_OWNER = "projectowner";
  private static final String ATTR_PROJECT_NAME = "projectname";
  private static final String ATTR_PROJECT_TYPE = "projecttype";
  private static final String ATTR_DIR = "dir";
  private static final String ATTR_IS_DEPRECATED = "isdeprecated";
  private static final String ATTR_HAS_PAGES = "haspages";
  private static final String ATTR_HAS_WIKI = "haswiki";
  private static final String ATTR_LAST_PUBLISHED_VERSION = "lastpubversion";
  private static final String ATTR_MIN_JDK_VERSION = "minjdk";
  private static final String ATTR_GITHUB_PRIVATE = "githubprivate";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final SimpleProject aValue,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull @Nonempty final String sTagName)
  {
    final IMicroElement ret = new MicroElement (sNamespaceURI, sTagName);
    ret.setAttribute (ATTR_HOSTING_PLATFORM, aValue.getHostingPlatform ().getID ());
    ret.setAttribute (ATTR_PROJECT_OWNER, aValue.getProjectOwner ());
    ret.setAttribute (ATTR_PROJECT_NAME, aValue.getProjectName ());
    ret.setAttribute (ATTR_PROJECT_TYPE, aValue.getProjectType ().getID ());
    ret.setAttribute (ATTR_DIR, aValue.getBaseDir ().getName ());
    ret.setAttribute (ATTR_IS_DEPRECATED, aValue.isDeprecated ());
    ret.setAttribute (ATTR_HAS_PAGES, aValue.hasPagesProject ());
    ret.setAttribute (ATTR_HAS_WIKI, aValue.hasWikiProject ());
    ret.setAttribute (ATTR_LAST_PUBLISHED_VERSION, aValue.getLastPublishedVersionString ());
    ret.setAttribute (ATTR_MIN_JDK_VERSION, aValue.getMinimumJDKVersion ().getMajor ());
    ret.setAttribute (ATTR_GITHUB_PRIVATE, aValue.isGitHubPrivate ());
    return ret;
  }

  @Nonnull
  public SimpleProject convertToNative (@Nonnull final IMicroElement aElement)
  {
    final EHostingPlatform eHostingPlatform = EHostingPlatform.getFromIDOrDefault (aElement.getAttributeValue (ATTR_HOSTING_PLATFORM),
                                                                                   EHostingPlatform.GITHUB);

    // Added later
    String sProjectOwner = aElement.getAttributeValue (ATTR_PROJECT_OWNER);
    if (sProjectOwner == null)
      sProjectOwner = IProject.DEFAULT_PROJECT_OWNER;

    final String sProjectName = aElement.getAttributeValue (ATTR_PROJECT_NAME);

    final String sProjectTypeID = aElement.getAttributeValue (ATTR_PROJECT_TYPE);
    final EProjectType eProjectType = EProjectType.getFromIDOrNull (sProjectTypeID);

    String sDir = aElement.getAttributeValue (ATTR_DIR);
    if (sDir == null)
      sDir = sProjectName;

    final File aBaseDir = ProjectList.findBaseDirectory (sDir);

    final boolean bIsDeprecated = aElement.getAttributeValueAsBool (ATTR_IS_DEPRECATED, false);
    final boolean bHasPages = aElement.getAttributeValueAsBool (ATTR_HAS_PAGES, false);
    final boolean bHasWiki = aElement.getAttributeValueAsBool (ATTR_HAS_WIKI, false);

    final String sLastPublishedVersion = aElement.getAttributeValue (ATTR_LAST_PUBLISHED_VERSION);

    final EJDK eMinJDK = EJDK.getFromMajorOrNull (StringParser.parseInt (aElement.getAttributeValue (ATTR_MIN_JDK_VERSION), -1));

    final boolean bIsGitHubPrivate = aElement.getAttributeValueAsBool (ATTR_GITHUB_PRIVATE, false);

    return new SimpleProject (eHostingPlatform,
                              (IProject) null,
                              sProjectOwner,
                              sProjectName,
                              eProjectType,
                              aBaseDir,
                              EIsDeprecated.valueOf (bIsDeprecated),
                              EHasPages.valueOf (bHasPages),
                              EHasWiki.valueOf (bHasWiki),
                              sLastPublishedVersion,
                              eMinJDK,
                              bIsGitHubPrivate);
  }

  @Nonnull
  public static SimpleProject convertToNativeWithParent (@Nonnull final IProject aParentProject, @Nonnull final IMicroElement aElement)
  {
    final EHostingPlatform eHostingPlatform = EHostingPlatform.getFromIDOrDefault (aElement.getAttributeValue (ATTR_HOSTING_PLATFORM),
                                                                                   EHostingPlatform.GITHUB);

    // Added later
    String sProjectOwner = aElement.getAttributeValue (ATTR_PROJECT_OWNER);
    if (sProjectOwner == null)
      sProjectOwner = IProject.DEFAULT_PROJECT_OWNER;

    final String sProjectName = aElement.getAttributeValue (ATTR_PROJECT_NAME);

    final String sProjectTypeID = aElement.getAttributeValue (ATTR_PROJECT_TYPE);
    final EProjectType eProjectType = EProjectType.getFromIDOrDefault (sProjectTypeID, EProjectType.JAVA_LIBRARY);

    String sDir = aElement.getAttributeValue (ATTR_DIR);
    if (sDir == null)
      sDir = sProjectName;
    final File aBaseDir = new File (aParentProject.getBaseDir (), sDir);

    final boolean bIsDeprecated = aElement.getAttributeValueAsBool (ATTR_IS_DEPRECATED, false);

    String sLastPublishedVersion = aElement.getAttributeValue (ATTR_LAST_PUBLISHED_VERSION);
    if (sLastPublishedVersion == null)
      sLastPublishedVersion = aParentProject.getLastPublishedVersionString ();

    final boolean bIsGitHubPrivate = aElement.getAttributeValueAsBool (ATTR_GITHUB_PRIVATE, aParentProject.isGitHubPrivate ());

    return new SimpleProject (eHostingPlatform,
                              aParentProject,
                              sProjectOwner,
                              sProjectName,
                              eProjectType,
                              aBaseDir,
                              EIsDeprecated.valueOf (bIsDeprecated),
                              EHasPages.valueOf (aParentProject.hasPagesProject ()),
                              EHasWiki.valueOf (aParentProject.hasWikiProject ()),
                              sLastPublishedVersion,
                              aParentProject.getMinimumJDKVersion (),
                              bIsGitHubPrivate);
  }
}
