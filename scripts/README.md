# scripts

Helper shell scripts for the meta project.

## list-github-maven-artifacts.sh

Lists all GitHub repositories of the owners `phax`, `austriapro`, `Conformatron` and
`helger-it` and extracts the Maven `groupId` and `artifactId` of every Maven module
found in them - the root `pom.xml` plus all `<modules>` referenced from it, recursively.
Nothing is cloned; everything is read via the GitHub API.

Requires `gh` (authenticated via `gh auth login`), `jq` and `xmllint`.

```sh
./list-github-maven-artifacts.sh                          # aligned text table
./list-github-maven-artifacts.sh -f csv > artifacts.csv   # CSV
./list-github-maven-artifacts.sh --only-published -g com.helger -g at.austriapro
./list-github-maven-artifacts.sh -r phax/ph-commons -f md
./list-github-maven-artifacts.sh --root                   # one row per repository
./list-github-maven-artifacts.sh --help
```

Notes:

* Forked repositories are skipped. Use `--include-forks` to get them back - a few of them
  are published under a `com.helger*` groupId (e.g. `phax/maven-jaxb2-plugin` ->
  `com.helger.maven:jaxb-maven-plugin`, `phax/junrar` -> `com.helger:junrar`). See
  `list-own-maven-artifacts.sh` below.
* Archived and private repositories are included as well and are flagged accordingly.
* `--only-published` hides the aggregator POMs (`packaging` = `pom`).
* A full run over all four owners takes about 1 minute.

## list-own-maven-artifacts.sh

Wrapper around `list-github-maven-artifacts.sh` that lists only the Maven artifacts
published under one of my own groupIds - `com.helger*`, `at.austriapro`, `at.peppol*`,
`at.clip`, `org.conformatron`, `eu.toop`, `airhacks` and `net.sf.jpdfunit`.

It runs with `--include-forks`, because several forks are published under a
`com.helger*` groupId; the groupId filters then drop the artifacts that are still
published under the groupId of the upstream project.

All arguments are passed through to `list-github-maven-artifacts.sh`:

```sh
./list-own-maven-artifacts.sh                             # all modules
./list-own-maven-artifacts.sh --root                      # one row per repository
./list-own-maven-artifacts.sh -f csv --only-published
```
