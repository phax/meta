---
status: accepted
date: long-standing convention
decision-makers: Philip Helger
---

# No constant-only interfaces

## Context and Problem Statement

A common Java anti-pattern ("constant interface") is to declare an interface containing
only `static final` constants and `implements` it to gain unqualified access to those
constants. Doing so leaks the constants into the public API of every implementing class
and pollutes their namespace, and it misuses `implements` for something that is not a
type relationship.

## Considered Options

* **Forbid constant-only interfaces**; use static imports when unqualified access is
  genuinely needed.
* **Allow constant interfaces** for convenient constant access.

## Decision Outcome

Chosen option: **Forbid constant-only interfaces**. Constants live on a class (or enum),
and code that wants unqualified access uses a static import "if absolutely necessary",
because implementing an interface is a type/contract statement — it should not be
repurposed as a constant-sharing mechanism, and doing so bloats the namespace of every
implementer.

### Consequences

* Good, because implementing a class's `implements` list reflects real type relationships,
  and constants do not leak into subtypes' APIs.
* Bad, because accessing constants requires qualification or an explicit static import
  rather than being implicitly in scope — a minor, deliberate inconvenience.

## More Information

* [`CodingStyleguide.md`](../../CodingStyleguide.md), "General conventions": "Don't use
  interfaces which contain only constant values as this bloats the overall namespace of
  all classes implementing it. Use static imports if absolutely necessary."
* Related naming rule: `static final` constants use `UPPER_CASE`
  ([ADR-0004](0004-hungarian-notation-and-scope-prefixes.md)).
