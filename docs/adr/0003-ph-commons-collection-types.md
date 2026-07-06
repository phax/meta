---
status: accepted
date: introduced with ph-commons 8
decision-makers: Philip Helger
---

# Use ph-commons collection types (`ICommonsList` / `CommonsArrayList`)

## Context and Problem Statement

The JDK collection interfaces (`List`, `Set`, `Map`) are minimal and force callers to
write boilerplate for common tasks (null-safe access, copying, filtering, mapping,
first/last element, conversion). Because the Stream API is intentionally avoided
([ADR-0002](0002-avoid-java-stream-api.md)), those conveniences must come from the
collection types themselves.

## Considered Options

* **Extended collection interfaces/classes (`ICommonsList`, `CommonsArrayList`, ...)**
  from ph-commons that add default sanity/convenience methods on top of the JDK types.
* **Plain JDK collections** everywhere, with helpers called externally.

## Decision Outcome

Chosen option: **ph-commons extended collections**, because they carry "tons of default
sanity methods" directly on the collection, which replaces most of what streams would
otherwise provide while staying allocation-light.

A key sub-rule: use the extended types as **return types** (where callers benefit from
the convenience methods), but keep **parameter types as narrow as possible** (plain
`List`, `Map`, `Iterable`) so callers are not forced to adopt the extended types just to
call a method.

### Consequences

* Good, because call sites are concise without the Stream API.
* Good, because narrow parameter types keep the public API permissive and interoperable
  with plain JDK collections.
* Bad, because two parallel collection hierarchies coexist, which newcomers must learn.

## More Information

* [`CodingStyleguide.md`](../../CodingStyleguide.md), "JDK 8 specialties": the
  `ICommons*` / `Commons*` collections "offer tons of default sanity methods";
  "Usually the extended collections are used as return types ... but not as parameter
  types (which should be as narrow as possible)."
* Related: [ADR-0006](0006-return-copies-of-collections.md) (return copies, annotated
  `@ReturnsMutableCopy`).
