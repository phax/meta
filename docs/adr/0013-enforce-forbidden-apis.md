---
status: accepted
date: 2016
decision-makers: Philip Helger
---

# Enforce API hygiene with forbidden-apis

## Context and Problem Statement

Certain JDK APIs are unsafe, non-portable, or locale/charset-dependent in ways that cause
subtle bugs (e.g. default-charset `String.getBytes()`, `Unsafe`, reflection into JDK
internals, deprecated APIs) or unnecessarily synchronized (e.g. `ByteArrayInputStream` or
`StringWriter`). Relying on reviewers to catch these across all repositories
does not scale. Enforcement needs to be automated and uniform.

## Considered Options

* **The `forbiddenapis` Maven plugin** with bundled JDK signature sets, configured in the
  shared parent POM.
* **Convention and code review** only.
* **A custom bytecode checker** (the ecosystem already has ASM-based tooling in
  `com.helger.meta.tools.codingstyleguide`).

## Decision Outcome

Chosen option: **`de.thetaphi:forbiddenapis`**, configured centrally so every project
inherits the same bans, because it is a purpose-built, well-maintained plugin with
curated JDK signature bundles, and central configuration guarantees a uniform policy.

The enabled bundled signatures are: `jdk-unsafe`, `jdk-deprecated`, `jdk-reflection`,
`jdk-internal`, `jdk-non-portable`. `jdk-system-out` is intentionally **not** enabled
(left commented out), so `System.out`/`System.err` usage is permitted.

### Consequences

* Good, because unsafe/non-portable/deprecated API usage fails the build automatically
  and consistently everywhere.
* Good, because the policy lives in one shared place and evolves in lockstep across
  repos.
* Bad, because legitimate uses of a flagged API require explicit per-site suppression.
* Neutral, because `jdk-system-out` being off means console output is not caught by this
  tool (a deliberate allowance).

## More Information

* [`pom.xml`](../../pom.xml) — `forbiddenapis` plugin management with the enabled
  `bundledSignatures` (the `meta` project mirrors the parent-POM policy locally).
* A `./mvn_clean_install_forbiddenapis.sh` variant runs the check across all repos
  (see [`DevelopmentProcess.md`](../../DevelopmentProcess.md)).
