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
package com.helger.meta.tools.buildsystem;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

public class MainCreateGitHubActionYaml
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCreateGitHubActionYaml.class);

  public static void main (final String [] args) throws Exception
  {
    final String sContentReleaseToSlack = """
        name: Notify Slack on Release
        on:
          release:
            types: [published]

        jobs:
          notify:
            runs-on: ubuntu-latest
            steps:
              - name: Post to Slack
                env:
                  SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
                run: |
                  curl -sf -X POST -H 'Content-type: application/json' \
                    --data '{
                      "text": "*New release: ${{ github.event.repository.name }} ${{ github.event.release.tag_name }}*\n${{ github.event.release.html_url }}"
                    }' \
                    "$SLACK_WEBHOOK_URL"
        """;
    final ICommonsMap <String, String> aFiles = new CommonsHashMap <> ();
    aFiles.put ("release-to-slack.yml", sContentReleaseToSlack);

    for (final IProject aProject : ProjectList.getAllProjects (p -> p.isBuildInProject () &&
                                                                    p.getBaseDir ().exists () &&
                                                                    !p.isDeprecated ()))
    {
      final File fGitHub = new File (aProject.getBaseDir (), ".github/workflows");
      fGitHub.mkdirs ();

      for (final var e : aFiles.entrySet ())
        SimpleFileIO.writeFile (new File (fGitHub, e.getKey ()), e.getValue (), StandardCharsets.UTF_8);
    }
    LOGGER.info ("Done");
  }
}
