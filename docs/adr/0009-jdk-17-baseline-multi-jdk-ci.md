---
status: accepted
date: current as of 2025; JDK baseline is raised periodically
decision-makers: Philip Helger
---

# JDK 17 baseline with a multi-JDK CI matrix

## Context and Problem Statement

The ecosystem must pick a minimum JDK level: too old and it forgoes useful language and
API features; too new and it excludes consumers stuck on older runtimes. It must also
ensure the code keeps working on the JDK versions its users actually run, which are newer
than the baseline.

## Considered Options

* **Baseline JDK 17, CI matrix additionally building 21 and 25.**
* **Track the latest LTS aggressively** as the minimum.
* **Stay on an older baseline** (e.g. JDK 8/11) for maximum reach.

## Decision Outcome

Chosen option: **JDK 17 as the active development baseline**, with CI matrix builds on
Java 17, 21 and 25 on `ubuntu-latest`, because 17 is a widely-adopted LTS that most
consumers can meet, while building on 21 and 25 proves forward-compatibility on the
runtimes users are moving to.

Deployment is asymmetric: only the **JDK 17** job publishes (a snapshot deploy to Maven
Central via `-P release-snapshot`); the 21 and 25 jobs run `mvn install` for
verification only. This keeps artifacts compiled against the baseline while still gating
merges on newer runtimes.

Some specific application projects may select newer Java LTS releases as their baseline,
but library projects must stick to the common JDK baseline version.

### Consequences

* Good, because consumers on JDK 17+ can use the artifacts, and regressions on newer
  JDKs are caught before release.
* Good, because publishing from a single (baseline) job avoids ambiguity about which JDK
  produced the released bytecode.
* Bad, because language/API features newer than 17 cannot be used in main code.
* Bad, because the matrix multiplies CI time and must be updated as JDK versions age out
  and new ones appear.

## More Information

* [`DevelopmentProcess.md`](../../DevelopmentProcess.md), "Continuous Integration" and
  "Workstation Setup".
* CI version pins are kept aligned across all repos by `MainCheckGitHubActionVersions`.
* This baseline is expected to be raised over time; supersede this ADR with a new one
  when it moves.
