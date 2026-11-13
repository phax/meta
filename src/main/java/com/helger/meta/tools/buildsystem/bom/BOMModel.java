/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.meta.tools.buildsystem.bom;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.collection.commons.ICommonsList;

/**
 * The content of a single generated BOM <code>pom.xml</code>. Created by
 * {@link BOMBuilder#createModel(com.helger.meta.project.IProject)}.
 *
 * @param parentVersion
 *        The version of <code>com.helger:parent-pom</code> to use as the parent
 * @param groupID
 *        The groupId of the BOM (same as the root project)
 * @param artifactID
 *        The artifactId of the BOM
 * @param version
 *        The version of the BOM (the current version of the root project)
 * @param name
 *        The name of the BOM
 * @param description
 *        The description of the BOM
 * @param licenseHeader
 *        The verbatim license header comment of the root <code>pom.xml</code> (including
 *        <code>&lt;!--</code> and <code>--&gt;</code>). May be <code>null</code>.
 * @param metadataBlocks
 *        The verbatim top-level metadata elements copied from the root <code>pom.xml</code> (e.g.
 *        <code>&lt;scm&gt;</code>), in output order, without leading indentation
 * @param members
 *        The ordered list of BOM members
 * @author Philip Helger
 */
public record BOMModel (@NonNull String parentVersion,
                        @NonNull String groupID,
                        @NonNull String artifactID,
                        @NonNull String version,
                        @NonNull String name,
                        @NonNull String description,
                        @Nullable String licenseHeader,
                        @NonNull ICommonsList <String> metadataBlocks,
                        @NonNull ICommonsList <Member> members)
{
  /**
   * A single artifact listed in the BOM.
   *
   * @param groupID
   *        The groupId of the artifact
   * @param artifactID
   *        The artifactId of the artifact
   * @author Philip Helger
   */
  public record Member (@NonNull String groupID, @NonNull String artifactID)
  {
    @NonNull
    public String getKey ()
    {
      return groupID + ":" + artifactID;
    }
  }
}
