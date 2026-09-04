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
# Lists all GitHub repositories of the configured owners and extracts the Maven
# groupId and artifactId of every Maven module contained in them.
#
# Requirements: gh (authenticated), jq, xmllint (libxml2)
#

set -uo pipefail

DEFAULT_OWNERS=(phax austriapro Conformatron helger-it)

SELF="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/$(basename "${BASH_SOURCE[0]}")"

TAB=$'\t'

_usage ()
{
  cat << 'EOF'
Usage: list-github-maven-artifacts.sh [options]

Lists all GitHub repositories of the given owners and extracts the Maven
groupId/artifactId of every Maven module found in them (the root pom.xml plus
all <modules> referenced from it, recursively).

Options:
  -o, --owner OWNER      Only scan this owner (repeatable).
                         Default: phax austriapro Conformatron helger-it
  -r, --repo OWNER/REPO  Only scan this single repository (repeatable).
                         Can be combined with or used instead of --owner.
  -f, --format FORMAT    Output format: text (default), tsv, csv, md, json
  -j, --jobs N           Number of repositories fetched in parallel (default 8)
      --root-only        Only look at the root pom.xml, do not descend into
                         the <modules> of a multi module project
      --include-forks    Also list the forked repositories. They are skipped by
                         default, even though some of them are published under
                         a com.helger* groupId.
      --no-archived      Skip archived repositories
      --no-private       Skip private repositories
      --only-published   Only show modules with packaging != pom (i.e. skip the
                         aggregator/parent POMs)
  -g, --group-prefix P   Only show modules whose groupId starts with P
                         (repeatable).
  -q, --quiet            No progress output on stderr
  -h, --help             This help

Output columns:
  repository, module path inside the repository, groupId, artifactId, version,
  packaging, and the repository flags (archived/fork/private).

Examples:
  ./list-github-maven-artifacts.sh
  ./list-github-maven-artifacts.sh -f csv > maven-artifacts.csv
  ./list-github-maven-artifacts.sh -o phax -g com.helger --only-published
  ./list-github-maven-artifacts.sh -r phax/ph-commons -f md
EOF
}

# $1 = file, $2 = element name below <project>
_pom_value ()
{
  xmllint --xpath "string(/*[local-name()='project']/*[local-name()='$2'])" "$1" 2> /dev/null | tr -d '\r\n\t '
}

# $1 = file, $2 = element name below <project>/<parent>
_pom_parent_value ()
{
  xmllint --xpath "string(/*[local-name()='project']/*[local-name()='parent']/*[local-name()='$2'])" "$1" 2> /dev/null | tr -d '\r\n\t '
}

# $1 = file; lists all <module> entries (also the ones inside <profile>s)
_pom_modules ()
{
  xmllint --xpath "//*[local-name()='modules']/*[local-name()='module']" "$1" 2> /dev/null \
    | sed -e 's|<module>|\n|g' -e 's|</module>|\n|g' \
    | tr -d '\r' \
    | sed -e 's|^[[:space:]]*||' -e 's|[[:space:]]*$||' \
    | grep -v '^$'
}

# $1 = repo, $2 = ref, $3 = path inside the repo, $4 = target file
_fetch_file ()
{
  gh api -H "Accept: application/vnd.github.raw" "repos/$1/contents/$3?ref=$2" > "$4" 2> /dev/null
}

# Fallback for repositories without a root pom.xml: search the Git tree for
# pom.xml files up to 2 directory levels deep.
# $1 = repo, $2 = ref
_find_poms_in_tree ()
{
  gh api "repos/$1/git/trees/$2?recursive=1" 2> /dev/null \
    | jq -r '.tree // [] | .[] | select (.type == "blob") | .path' 2> /dev/null \
    | grep -E '^(([^/]+/){0,2})pom\.xml$'
}

# The worker for a single repository. Called via xargs.
# $1 = 1-based line number inside "$WORK_DIR/repos-filtered.tsv".
# Only the line number is passed, because xargs replaces the tabs of the TSV
# line with blanks.
_worker ()
{
  local sLine sRepo sArchived sFork sVisibility sBranch
  sLine="$(sed -n "${1}p" "$WORK_DIR/repos-filtered.tsv")"
  IFS="$TAB" read -r sRepo sArchived sFork sVisibility sBranch <<< "$sLine"

  local sOut="$WORK_DIR/out/${sRepo//\//__}.tsv"
  : > "$sOut"

  [ "$QUIET" = "true" ] || echo "  scanning $sRepo" >&2

  if [ -z "$sBranch" ]; then
    printf '%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n' \
           "$sRepo" "-" "" "" "" "(empty repository)" "$sArchived" "$sFork" "$sVisibility" >> "$sOut"
    return
  fi

  local sTmpDir="$WORK_DIR/tmp/${sRepo//\//__}"
  mkdir -p "$sTmpDir"

  # Paths still to be processed - starts with the root POM
  local -a aTodo=("pom.xml")
  local -a aDone=()

  if ! _fetch_file "$sRepo" "$sBranch" "pom.xml" "$sTmpDir/root.pom"; then
    # No root POM - look for POMs further down the tree
    aTodo=()
    local sFound
    while IFS= read -r sFound; do
      [ -n "$sFound" ] && aTodo+=("$sFound")
    done < <(_find_poms_in_tree "$sRepo" "$sBranch")

    if [ ${#aTodo[@]} -eq 0 ]; then
      printf '%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n' \
             "$sRepo" "-" "" "" "" "(no Maven project)" "$sArchived" "$sFork" "$sVisibility" >> "$sOut"
      return
    fi
  fi

  local nIndex=0
  while [ $nIndex -lt ${#aTodo[@]} ]; do
    local sPath="${aTodo[$nIndex]}"
    nIndex=$((nIndex + 1))

    # Avoid processing the same POM twice
    local sSeen bSeen="false"
    for sSeen in ${aDone[@]+"${aDone[@]}"}; do
      [ "$sSeen" = "$sPath" ] && bSeen="true" && break
    done
    [ "$bSeen" = "true" ] && continue
    aDone+=("$sPath")

    local sPomFile="$sTmpDir/$(echo "$sPath" | tr '/' '_')"
    if ! _fetch_file "$sRepo" "$sBranch" "$sPath" "$sPomFile"; then
      [ "$QUIET" = "true" ] || echo "    missing $sRepo/$sPath" >&2
      continue
    fi

    local sGroupID sArtifactID sVersion sPackaging
    sGroupID="$(_pom_value "$sPomFile" "groupId")"
    sArtifactID="$(_pom_value "$sPomFile" "artifactId")"
    sVersion="$(_pom_value "$sPomFile" "version")"
    sPackaging="$(_pom_value "$sPomFile" "packaging")"

    # groupId and version may be inherited from the parent POM
    [ -z "$sGroupID" ] && sGroupID="$(_pom_parent_value "$sPomFile" "groupId")"
    [ -z "$sVersion" ] && sVersion="$(_pom_parent_value "$sPomFile" "version")"
    [ -z "$sPackaging" ] && sPackaging="jar"

    if [ -z "$sArtifactID" ]; then
      [ "$QUIET" = "true" ] || echo "    unparseable $sRepo/$sPath" >&2
      continue
    fi

    local sModulePath="${sPath%pom.xml}"
    sModulePath="${sModulePath%/}"
    [ -z "$sModulePath" ] && sModulePath="."

    printf '%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n' \
           "$sRepo" "$sModulePath" "$sGroupID" "$sArtifactID" "$sVersion" "$sPackaging" \
           "$sArchived" "$sFork" "$sVisibility" >> "$sOut"

    # Descend into the modules of an aggregator POM
    if [ "$ROOT_ONLY" != "true" ]; then
      local sBaseDir="${sPath%pom.xml}"
      local sModule
      while IFS= read -r sModule; do
        [ -z "$sModule" ] && continue
        case "$sModule" in
          *.xml) aTodo+=("${sBaseDir}${sModule}") ;;
          *)     aTodo+=("${sBaseDir}${sModule%/}/pom.xml") ;;
        esac
      done < <(_pom_modules "$sPomFile")
    fi
  done

  rm -rf "$sTmpDir"
}

# --- worker entry point (invoked by xargs) -----------------------------------

if [ "${1:-}" = "--_worker" ]; then
  _worker "$2"
  exit 0
fi

# --- argument parsing --------------------------------------------------------

declare -a OWNERS=()
declare -a REPOS=()
FORMAT="text"
JOBS=8
ROOT_ONLY="false"
SKIP_FORKS="true"
SKIP_ARCHIVED="false"
SKIP_PRIVATE="false"
ONLY_PUBLISHED="false"
declare -a GROUP_PREFIXES=()
QUIET="false"

while [ $# -gt 0 ]; do
  case "$1" in
    -o|--owner)      OWNERS+=("$2"); shift 2 ;;
    -r|--repo)       REPOS+=("$2"); shift 2 ;;
    -f|--format)     FORMAT="$2"; shift 2 ;;
    -j|--jobs)       JOBS="$2"; shift 2 ;;
    --root-only)     ROOT_ONLY="true"; shift ;;
    --include-forks) SKIP_FORKS="false"; shift ;;
    --no-archived)   SKIP_ARCHIVED="true"; shift ;;
    --no-private)    SKIP_PRIVATE="true"; shift ;;
    --only-published) ONLY_PUBLISHED="true"; shift ;;
    -g|--group-prefix) GROUP_PREFIXES+=("$2"); shift 2 ;;
    -q|--quiet)      QUIET="true"; shift ;;
    -h|--help)       _usage; exit 0 ;;
    *) echo "Unknown option '$1'" >&2; _usage >&2; exit 1 ;;
  esac
done

case "$FORMAT" in
  text|tsv|csv|md|json) ;;
  *) echo "Unsupported format '$FORMAT'" >&2; exit 1 ;;
esac

for sCmd in gh jq xmllint; do
  command -v "$sCmd" > /dev/null 2>&1 || { echo "Required command '$sCmd' not found" >&2; exit 1; }
done
gh auth status > /dev/null 2>&1 || { echo "Not logged in to GitHub - run 'gh auth login'" >&2; exit 1; }

if [ ${#OWNERS[@]} -eq 0 ] && [ ${#REPOS[@]} -eq 0 ]; then
  OWNERS=("${DEFAULT_OWNERS[@]}")
fi

export QUIET ROOT_ONLY

# --- collect the repository list ---------------------------------------------

WORK_DIR="$(mktemp -d "${TMPDIR:-/tmp}/gh-maven-artifacts.XXXXXX")"
export WORK_DIR
mkdir -p "$WORK_DIR/out" "$WORK_DIR/tmp"
trap 'rm -rf "$WORK_DIR"' EXIT

REPO_LIST="$WORK_DIR/repos.tsv"
: > "$REPO_LIST"

JQ_FILTER='.[] | [ .nameWithOwner,
                   (.isArchived|tostring),
                   (.isFork|tostring),
                   (if .isPrivate then "private" else "public" end),
                   (.defaultBranchRef.name // "") ] | @tsv'

for sOwner in ${OWNERS[@]+"${OWNERS[@]}"}; do
  [ "$QUIET" = "true" ] || echo "Listing repositories of '$sOwner' ..." >&2
  if ! gh repo list "$sOwner" --limit 1000 \
         --json nameWithOwner,isArchived,isFork,isPrivate,defaultBranchRef \
         --jq "$JQ_FILTER" >> "$REPO_LIST"; then
    echo "Failed to list the repositories of '$sOwner'" >&2
    exit 1
  fi
done

for sRepo in ${REPOS[@]+"${REPOS[@]}"}; do
  [ "$QUIET" = "true" ] || echo "Querying repository '$sRepo' ..." >&2
  if ! gh api "repos/$sRepo" \
         --jq '[ .full_name,
                 (.archived|tostring),
                 (.fork|tostring),
                 (if .private then "private" else "public" end),
                 (.default_branch // "") ] | @tsv' >> "$REPO_LIST"; then
    echo "Failed to query the repository '$sRepo'" >&2
    exit 1
  fi
done

# Apply the repository level filters
FILTERED="$WORK_DIR/repos-filtered.tsv"
: > "$FILTERED"
while IFS= read -r sLine; do
  [ -z "$sLine" ] && continue
  IFS="$TAB" read -r sR sArchived sFork sVisibility sBranch <<< "$sLine"
  [ "$SKIP_ARCHIVED" = "true" ] && [ "$sArchived" = "true" ] && continue
  [ "$SKIP_FORKS" = "true" ] && [ "$sFork" = "true" ] && continue
  [ "$SKIP_PRIVATE" = "true" ] && [ "$sVisibility" = "private" ] && continue
  printf '%s\n' "$sLine" >> "$FILTERED"
done < <(sort -u "$REPO_LIST")

nRepos=$(wc -l < "$FILTERED" | tr -d ' ')
[ "$QUIET" = "true" ] || echo "Scanning $nRepos repositories with $JOBS parallel jobs ..." >&2

# --- scan all repositories in parallel ---------------------------------------

if [ "$nRepos" -gt 0 ]; then
  seq 1 "$nRepos" | xargs -P "$JOBS" -I '{}' "$SELF" --_worker '{}'
fi

RESULT="$WORK_DIR/result.tsv"
cat "$WORK_DIR"/out/*.tsv 2> /dev/null | sort -t "$TAB" -k1,1f -k2,2f > "$RESULT"

if [ "$ONLY_PUBLISHED" = "true" ]; then
  awk -F "$TAB" '$6 != "pom"' "$RESULT" > "$RESULT.f" && mv "$RESULT.f" "$RESULT"
fi

if [ ${#GROUP_PREFIXES[@]} -gt 0 ]; then
  : > "$RESULT.f"
  for sPrefix in "${GROUP_PREFIXES[@]}"; do
    awk -F "$TAB" -v prefix="$sPrefix" 'index ($3, prefix) == 1' "$RESULT" >> "$RESULT.f"
  done
  sort -u -t "$TAB" -k1,1f -k2,2f "$RESULT.f" > "$RESULT" && rm -f "$RESULT.f"
fi

# --- output ------------------------------------------------------------------

_flags ()
{
  local s=""
  [ "$1" = "true" ] && s="${s}archived,"
  [ "$2" = "true" ] && s="${s}fork,"
  [ "$3" = "private" ] && s="${s}private,"
  echo "${s%,}"
}

case "$FORMAT" in
  tsv)
    printf 'repository\tmodule\tgroupId\tartifactId\tversion\tpackaging\tarchived\tfork\tvisibility\n'
    cat "$RESULT"
    ;;
  csv)
    echo 'repository,module,groupId,artifactId,version,packaging,archived,fork,visibility'
    awk -F "$TAB" 'BEGIN{OFS=","} {
      for (i = 1; i <= NF; i++) { gsub (/"/, "\"\"", $i); $i = "\"" $i "\"" }
      print
    }' "$RESULT"
    ;;
  json)
    jq -R -s 'split ("\n") | map (select (length > 0)) | map (split ("\t")) |
              map ({ repository: .[0], module: .[1], groupId: .[2], artifactId: .[3],
                     version: .[4], packaging: .[5],
                     archived: (.[6] == "true"), fork: (.[7] == "true"),
                     visibility: .[8] })' "$RESULT"
    ;;
  md)
    echo '| Repository | Module | groupId | artifactId | Version | Packaging | Flags |'
    echo '| --- | --- | --- | --- | --- | --- | --- |'
    while IFS="$TAB" read -r sR sM sG sA sV sP sArc sFrk sVis; do
      printf '| %s | %s | %s | %s | %s | %s | %s |\n' \
             "$sR" "$sM" "$sG" "$sA" "$sV" "$sP" "$(_flags "$sArc" "$sFrk" "$sVis")"
    done < "$RESULT"
    ;;
  text)
    {
      printf 'REPOSITORY\tMODULE\tGROUPID\tARTIFACTID\tVERSION\tPACKAGING\tFLAGS\n'
      while IFS="$TAB" read -r sR sM sG sA sV sP sArc sFrk sVis; do
        printf '%s\t%s\t%s\t%s\t%s\t%s\t%s\n' \
               "$sR" "$sM" "$sG" "$sA" "$sV" "$sP" "$(_flags "$sArc" "$sFrk" "$sVis")"
      done < "$RESULT"
    } | column -t -s "$TAB"
    ;;
esac

if [ "$QUIET" != "true" ]; then
  nArtifacts=$(awk -F "$TAB" '$4 != ""' "$RESULT" | wc -l | tr -d ' ')
  nWithMaven=$(awk -F "$TAB" '$4 != "" { print $1 }' "$RESULT" | sort -u | wc -l | tr -d ' ')
  echo "" >&2
  echo "$nRepos repositories scanned, $nWithMaven of them are Maven projects, $nArtifacts Maven modules found" >&2
fi
