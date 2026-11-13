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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.project.EProject;
import com.helger.meta.project.EProjectType;

/**
 * Test class for {@link BOMBuilder}. Note: {@link EProject} requires the sibling checkouts to be
 * present, but the root pom.xml content is taken from a fixture.
 *
 * @author Philip Helger
 */
public final class BOMBuilderTest
{
  private static final String MODULES_POM = """
      <?xml version="1.0" encoding="UTF-8"?>
      <project>
        <artifactId>x-parent-pom</artifactId>
        <modules>
          <module>a</module>
          <module>b</module>
        </modules>
        <profiles>
          <profile>
            <modules>
              <module>c</module>
            </modules>
          </profile>
        </profiles>
      </project>
      """;

  @Test
  public void testPhWebModel ()
  {
    final String sRootPOM = SimpleFileIO.getFileAsString (new File ("src/test/resources/bom/ph-web/pom.xml"),
                                                          StandardCharsets.UTF_8);
    assertNotNull (sRootPOM);

    final BOMModel aModel = BOMBuilder.createModel (EProject.PH_WEB_PARENT_POM, sRootPOM, "3.1.1");
    assertEquals ("3.1.1", aModel.parentVersion ());
    assertEquals ("com.helger.web", aModel.groupID ());
    assertEquals ("ph-web-bom", aModel.artifactID ());
    assertEquals ("ph-web-bom", aModel.name ());
    assertEquals ("11.5.1-SNAPSHOT", aModel.version ());
    assertEquals ("Bill of Materials for all ph-web artifacts", aModel.description ());
    assertNotNull (aModel.licenseHeader ());
    assertTrue (aModel.licenseHeader ().startsWith ("<!--"));
    assertTrue (aModel.licenseHeader ().endsWith ("-->"));

    // url, inceptionYear, licenses, scm, organization, developers - no issueManagement
    assertEquals (6, aModel.metadataBlocks ().size ());
    assertEquals ("<url>https://github.com/phax/ph-web</url>", aModel.metadataBlocks ().get (0));
    assertEquals ("<inceptionYear>2014</inceptionYear>", aModel.metadataBlocks ().get (1));
    assertTrue (aModel.metadataBlocks ().get (3).startsWith ("<scm>"));

    // EProject declaration order
    final ICommonsList <String> aExpected = new CommonsArrayList <> ("ph-dns",
                                                                     "ph-network",
                                                                     "ph-useragent",
                                                                     "ph-servlet",
                                                                     "ph-mail",
                                                                     "ph-smtp",
                                                                     "ph-httpclient",
                                                                     "ph-sitemap",
                                                                     "ph-web",
                                                                     "ph-xservlet",
                                                                     "ph-jsch");
    final ICommonsList <String> aActual = new CommonsArrayList <> ();
    for (final BOMModel.Member aMember : aModel.members ())
    {
      assertEquals ("com.helger.web", aMember.groupID ());
      aActual.add (aMember.artifactID ());
    }
    assertEquals (aExpected, aActual);

    // Deterministic
    final String sContent = BOMBuilder.createPOMContent (aModel);
    assertEquals (sContent, BOMBuilder.createPOMContent (aModel));
    assertEquals (sContent,
                  BOMBuilder.createPOMContent (BOMBuilder.createModel (EProject.PH_WEB_PARENT_POM, sRootPOM, "3.1.1")));
    assertTrue (sContent.startsWith ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!--"));
    assertTrue (sContent.contains ("    <version>3.1.1</version>\n    <relativePath />\n  </parent>\n"));
    assertTrue (sContent.contains ("  <version>11.5.1-SNAPSHOT</version>\n  <packaging>pom</packaging>\n"));
    assertFalse (sContent.contains ("<scope>import</scope>"));
    assertFalse (sContent.contains ("\r"));

    // Parent version is taken from the parameter only
    final String sOtherParent = BOMBuilder.createPOMContent (BOMBuilder.createModel (EProject.PH_WEB_PARENT_POM,
                                                                                     sRootPOM,
                                                                                     "9.8.7"));
    assertTrue (sOtherParent.contains ("    <version>9.8.7</version>\n    <relativePath />\n  </parent>\n"));
    assertTrue (sOtherParent.contains ("  <version>11.5.1-SNAPSHOT</version>\n"));

    // CRLF input gives the same output
    assertEquals (sContent,
                  BOMBuilder.createPOMContent (BOMBuilder.createModel (EProject.PH_WEB_PARENT_POM,
                                                                       sRootPOM.replace ("\n", "\r\n"),
                                                                       "3.1.1")));

    assertEquals ("PH_WEB_BOM (PH_WEB_PARENT_POM, \"ph-web-bom\", EProjectType.MAVEN_POM),",
                  BOMBuilder.getBOMEnumLine (EProject.PH_WEB_PARENT_POM));
  }

  @Test
  public void testBOMArtifactID ()
  {
    assertEquals ("ph-web-bom", BOMBuilder.getBOMArtifactID ("ph-web-parent-pom", "ph-web"));
    assertEquals ("phoss-smp-bom", BOMBuilder.getBOMArtifactID ("phoss-smp-parent-pom", "phoss-smp"));
    // Suffix wins over the directory name
    assertEquals ("ph-commons-bom", BOMBuilder.getBOMArtifactID ("ph-commons-parent-pom", "other-dir"));
    // No suffix - fall back to the directory name
    assertEquals ("ph-web-bom", BOMBuilder.getBOMArtifactID ("ph-web-parent", "ph-web"));
    assertEquals ("phlox-bom", BOMBuilder.getBOMArtifactID ("phlox", "phlox"));
    assertEquals ("dir-bom", BOMBuilder.getBOMArtifactID ("ph-parent-pom-x", "dir"));
  }

  @Test
  public void testMembership ()
  {
    for (final EProjectType eType : EProjectType.values ())
    {
      final boolean bByType = eType == EProjectType.JAVA_LIBRARY || eType == EProjectType.RESOURCES_ONLY;
      assertTrue (eType.name (), bByType == BOMBuilder.isBOMMember (eType, false, false));
      // @IsIncludedInBOM forces inclusion
      assertTrue (eType.name (), BOMBuilder.isBOMMember (eType, false, true));
      // @IsExcludedFromBOM forces exclusion and takes precedence
      assertFalse (eType.name (), BOMBuilder.isBOMMember (eType, true, false));
      assertFalse (eType.name (), BOMBuilder.isBOMMember (eType, true, true));
    }
    assertTrue (BOMBuilder.isBOMMember (EProjectType.JAVA_LIBRARY, false, false));
    assertTrue (BOMBuilder.isBOMMember (EProjectType.RESOURCES_ONLY, false, false));
    assertFalse (BOMBuilder.isBOMMember (EProjectType.MAVEN_POM, false, false));
    assertFalse (BOMBuilder.isBOMMember (EProjectType.MAVEN_PLUGIN, false, false));
    assertFalse (BOMBuilder.isBOMMember (EProjectType.JAVA_APPLICATION, false, false));
    assertFalse (BOMBuilder.isBOMMember (EProjectType.JAVA_WEB_APPLICATION, false, false));
    assertFalse (BOMBuilder.isBOMMember (EProjectType.OTHER_PLUGIN, false, false));
  }

  @Test
  public void testTopLevelElements ()
  {
    final ICommonsList <String> aNames = new CommonsArrayList <> ();
    for (final BOMBuilder.TopLevelElement aElement : BOMBuilder.getAllTopLevelElements (MODULES_POM))
      aNames.add (aElement.name ());
    assertEquals (new CommonsArrayList <> ("artifactId", "modules", "profiles"), aNames);
  }

  @Test
  public void testInsertFirstModule ()
  {
    // Existing block - inserted first, with the indentation of the existing entries; profiles
    // untouched
    final String sResult = BOMBuilder.insertFirstModule (MODULES_POM, "x-bom");
    assertNotNull (sResult);
    assertTrue (sResult.contains ("  <modules>\n    <module>x-bom</module>\n    <module>a</module>\n"));
    assertTrue (sResult.contains ("      <modules>\n        <module>c</module>\n"));
    assertEquals (MODULES_POM.length () + "    <module>x-bom</module>\n".length (), sResult.length ());
    assertTrue (BOMBuilder.containsTopLevelModule (sResult, "x-bom"));

    // Already contained - no change
    assertSame (sResult, BOMBuilder.insertFirstModule (sResult, "x-bom"));
    assertEquals (sResult, BOMBuilder.insertFirstModule (sResult, "x-bom"));

    // Already contained, but not first - no change
    final String sNotFirst = MODULES_POM.replace ("<module>b</module>", "<module>x-bom</module>");
    assertSame (sNotFirst, BOMBuilder.insertFirstModule (sNotFirst, "x-bom"));

    // A module only in a profile does not count as top-level
    assertFalse (BOMBuilder.containsTopLevelModule (MODULES_POM, "c"));

    // CRLF line endings are preserved
    final String sCRLF = MODULES_POM.replace ("\n", "\r\n");
    final String sCRLFResult = BOMBuilder.insertFirstModule (sCRLF, "x-bom");
    assertNotNull (sCRLFResult);
    assertTrue (sCRLFResult.contains ("  <modules>\r\n    <module>x-bom</module>\r\n    <module>a</module>\r\n"));

    // No top-level <modules> at all
    assertNull (BOMBuilder.insertFirstModule ("<project>\n  <artifactId>a</artifactId>\n</project>\n", "x-bom"));
  }

  @Test
  public void testInsertFirstModuleWithComments ()
  {
    final String sPOM = """
        <?xml version="1.0" encoding="UTF-8"?>
        <!-- <modules><module>x-bom</module></modules> -->
        <project>
          <!-- <modules> -->
          <modules>
            <!-- the core -->
            <module>a</module>
            <!-- <module>x-bom</module> -->
            <module>b</module>
          </modules>
        </project>
        """;
    // Commented-out modules do not count
    assertFalse (BOMBuilder.containsTopLevelModule (sPOM, "x-bom"));

    final String sResult = BOMBuilder.insertFirstModule (sPOM, "x-bom");
    assertNotNull (sResult);
    // Inserted before the first comment inside <modules>, comments preserved
    assertTrue (sResult.contains ("  <!-- <modules> -->\n  <modules>\n    <module>x-bom</module>\n    <!-- the core -->\n    <module>a</module>\n    <!-- <module>x-bom</module> -->\n"));
    assertTrue (BOMBuilder.containsTopLevelModule (sResult, "x-bom"));
    assertSame (sResult, BOMBuilder.insertFirstModule (sResult, "x-bom"));
  }
}
