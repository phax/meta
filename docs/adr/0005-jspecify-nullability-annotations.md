---
status: accepted
date: 2025-02-11
decision-makers: Philip Helger
---

# JSpecify nullability annotations (`@NonNull` / `@Nullable` / `@Nonempty`)

## Context and Problem Statement

Nullability is the most common source of contract confusion and NPEs in a Java API.
The intent ("can this parameter/return be null?") needs to be documented on the API in a
machine-readable, tool-checkable way, without imposing any runtime cost on consumers.
Several competing annotation families exist (JSR-305 `javax.annotation`, JetBrains,
Eclipse JDT, Jakarta, Checker Framework, JSpecify), and the JSR-305 set is effectively
abandoned.

## Considered Options

* **JSpecify** (`@NonNull` / `@Nullable`) plus a project `@Nonempty` for strings,
  collections and arrays.
* **JSR-305** `javax.annotation.*` (the historical de-facto choice, now unmaintained).
* **No annotations** — document nullability in prose only.

## Decision Outcome

Chosen option: **JSpecify** annotations on all non-primitive parameters and returns,
augmented with `@Nonempty` where empty values are also disallowed, because JSpecify is
the actively maintained, vendor-neutral standard with a well-defined specification,
while JSR-305 is dead. The annotations are documentation/tooling hints only and have no
runtime impact.

### Consequences

* Good, because nullability contracts are explicit, checkable by IDEs/analysis tools,
  and carry zero runtime cost.
* Good, because JSpecify is under active stewardship, avoiding the dead-end of JSR-305.
* Bad, because migrating from any previously-used annotation set requires a sweep across
  every project.
* Bad, because annotations are only as reliable as their coverage — an unannotated
  element is ambiguous.

## More Information

* [`CodingStyleguide.md`](../../CodingStyleguide.md), "Special annotations".
* `@Nonempty` applies to Strings, collections and arrays (non-null *and* non-empty).
* `@Nonempty` is sourced from the `ph-annotations` module of ph-commons, as
  `com.helger.annotation.Nonempty`. The `@Immutable` / `@ThreadSafe` /
  `@NotThreadSafe` annotations from ADR-0008 live in the same module under
  `com.helger.annotation.concurrent.*` (no longer the FindBugs/JSR-305 originals).
* The JSR-305 (`javax.annotation`) → JSpecify migration was completed with
  [ph-commons-parent-pom-12.1.0](https://github.com/phax/ph-commons/releases/tag/ph-commons-parent-pom-12.1.0).
