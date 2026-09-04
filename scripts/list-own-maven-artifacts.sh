#!/usr/bin/env bash
#
# Copyright (C) 2014-2026 Philip Helger (www.helger.com)
# philip[at]helger[dot]com
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Convenience wrapper around list-github-maven-artifacts.sh that lists only the
# Maven artifacts published by me.
#
# Forked repositories are included, because quite a few of them are published
# under one of my own groupIds (e.g. phax/maven-jaxb2-plugin is published as
# com.helger.maven:jaxb-maven-plugin). The groupId filters below then remove the
# artifacts that are still published under the groupId of the upstream project.
#
# All arguments are passed through to list-github-maven-artifacts.sh, e.g.
#   ./list-own-maven-artifacts.sh --root
#   ./list-own-maven-artifacts.sh -f csv --only-published
#

set -uo pipefail

# All groupIds (prefixes) I publish under
OWN_GROUP_PREFIXES=(com.helger
                    at.austriapro
                    at.peppol
                    org.conformatron)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

declare -a aArgs=(--include-forks --no-archived --no-private -f md --root-only)
for sPrefix in "${OWN_GROUP_PREFIXES[@]}"; do
  aArgs+=(--group-prefix "$sPrefix")
done

exec "$SCRIPT_DIR/list-github-maven-artifacts.sh" "${aArgs[@]}" "$@"
