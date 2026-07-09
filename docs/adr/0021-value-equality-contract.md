---
status: accepted
date: TODO — long-standing ph-commons convention; back-fill if known
decision-makers: Philip Helger
---

# Value equality via `EqualsHelper` / `HashCodeGenerator` and `@MustImplementEqualsAndHashcode`

## Context and Problem Statement

Correct `equals()`/`hashCode()` is essential for value-semantic types used as map keys,
in sets, or in comparisons — and getting them wrong (array identity vs content,
inconsistent null handling, forgetting a field) causes silent, critical bugs. Hand-written
implementations across hundreds of classes drift and are error-prone, and there is no
compiler check that a type which *needs* value equality actually provides it.

## Considered Options

* **Shared helpers plus a marker annotation**: build `equals()` with `EqualsHelper`, build
  `hashCode()` with `HashCodeGenerator`, and tag types that require value equality with
  `@MustImplementEqualsAndHashcode`.
* **Apache commons-lang** providing equals and hashCode support classes
* **Hand-written `equals`/`hashCode`** per class (or IDE-generated).
* **A records / third-party (Lombok/AutoValue) approach.**

## Decision Outcome

Chosen option: **`EqualsHelper` + `HashCodeGenerator` + `@MustImplementEqualsAndHashcode`**,
because the helpers centralise the tricky cases (null-safe comparison, array *content*
equality, float/double handling) so implementations are consistent and correct, while the
`@MustImplementEqualsAndHashcode` marker documents — and lets tooling enforce — that
subtypes of value-semantic contracts (e.g. readable resources) provide proper equality.

### Consequences

* Good, because value equality is consistent, null-safe, and array-content-aware across
  the ecosystem.
* Good, because the marker annotation turns "this must have equals/hashCode" from tribal
  knowledge into a checkable contract.
* Bad, because implementing equality is still manual per class (helpers reduce but do not
  eliminate the boilerplate).

## More Information

* ph-commons `ph-base`: `com.helger.base.equals.EqualsHelper`,
  `com.helger.base.hashcode.HashCodeGenerator`.
* Marker: `ph-annotations` `com.helger.annotation.style.MustImplementEqualsAndHashcode`
  (e.g. on `ph-io` `com.helger.io.resource.IReadableResource`).
* Pluggable equality/hashCode for third-party types is also registrable via SPI
  (see [ADR-0019](0019-spi-serviceloader-extensibility.md)).
