---
status: accepted
date: 1996 — long-standing convention
decision-makers: Philip Helger
---

# Hungarian notation and scope prefixes

## Context and Problem Statement

Across a large, long-lived, single-maintainer-led codebase, being able to read a
variable's type and scope from its name — without an IDE, in a code review, in a diff,
or in a log — has ongoing value. A consistent naming scheme also makes the automated
coding-style checks (`com.helger.meta.tools.codingstyleguide`) tractable.

## Considered Options

* **Hungarian notation with a single-letter type prefix plus scope prefixes**
  (`m_sName`, `s_aForwarder`, `DEFAULT_VALUE`).
* **Plain modern-Java naming** (no type prefixes; rely on the IDE and types).

## Decision Outcome

Chosen option: **Hungarian notation with scope prefixes**, applied uniformly:

* Type prefix: `a` (object/array), `b` (boolean), `c` (char), `d` (double), `e` (enum),
  `f` (float), `n` (byte/short/int/long), `s` (String).
* Scope prefix: `m_` (instance field), `s_` (static field); `static final` constants use
  `UPPER_CASE` without a prefix (`LOGGER` is the standing exception).
* Type naming: interfaces `I…` (mutating variants `IMutable…`), enums `E…`, abstract
  classes `Abstract…`.
* Private methods start with `_` and are not declared `final` (they are implicitly
  final).

The consistency itself is the value: it is applied everywhere and enforced by tooling.

### Consequences

* Good, because type and scope are visible everywhere a name appears, including outside
  the IDE.
* Good, because the uniformity makes automated style checks and the "find illegal
  constants" regex feasible.
* Good, because it avoid common mistakes with the `this` keyword.
* Bad, because Hungarian notation is out of fashion and unfamiliar to most Java
  developers, adding a learning curve and friction for external contributions.
* Bad, because a type change requires a rename to stay consistent.

## More Information

* [`CodingStyleguide.md`](../../CodingStyleguide.md), "Naming conventions".
* Enforced in part by `com.helger.meta.tools.codingstyleguide` (ASM-based checks).
