---
status: accepted
date: Introduction 2014 - ph-commons predates this ADR
decision-makers: Philip Helger
---

# ph-commons as the shared JDK-extension foundation

## Context and Problem Statement

Every project in the ecosystem needs the same low-level building blocks: sanity-checked
collections, string/IO helpers, error handling, state enums, ID abstractions,
thread-safe locking helpers, etc. The JDK provides raw primitives but not the opinionated
conveniences these projects rely on. Duplicating such helpers per project would cause
drift and inconsistency across ~66 repositories.

## Considered Options

* **A single foundational library (`ph-commons`)** that all other projects depend on and
  that can be treated as "the JDK, extended".
* **Rely only on the raw JDK** plus ad-hoc per-project helpers.
* **Third-party commons libraries** (Guava, Apache Commons) as the shared base.

## Decision Outcome

Chosen option: **`ph-commons`**, treated as a JDK extension that is the lowest common
dependency of the ecosystem, because it gives a single, controlled place to define the
conventions (collections, nullability, threading, immutability annotations) that every
other decision in this ADR set builds on. Only JDK-extension projects themselves are
exempt from depending on it.

The reason for chosing this stemps from a long time ago.
The dependency management of other "commons" libraries was not good.
A lot of dependency inconsistencies occurred.
Different libraries using different Java minimum versions.
Sometime including a new library with a few 100KB size for a single method sounded overkill.
Sometimes the performance of third-party libraries was not to my liking.


### Consequences

* Good, because conventions are defined once and inherited everywhere; upgrades and
  fixes propagate through a single dependency.
* Good, because the ecosystem controls its own foundation rather than tracking a
  third-party library's roadmap and deprecations.
* Bad, because it is a large single point of dependency — a breaking change in
  `ph-commons` ripples across the whole ecosystem (managed via the parent-pom BOM and
  the `MainCheck*` alignment tools).
* Bad, because it is a "not invented here" foundation that newcomers must learn instead
  of reusing familiar third-party commons libraries.

## More Information

* [`CodingStyleguide.md`](../../CodingStyleguide.md) — "All projects (except for JDK
  extensions) use ph-commons which is the most basic library and can be considered a
  JDK extension."
* Version alignment is done through the `ph-commons-parent-pom` BOM (see the `meta`
  [`pom.xml`](../../pom.xml)).
* Related: [ADR-0003](0003-ph-commons-collection-types.md),
  [ADR-0006](0006-return-copies-of-collections.md),
  [ADR-0008](0008-thread-safety-via-readwritelock.md).
