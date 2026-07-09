---
status: accepted
date: long-standing convention
decision-makers: Philip Helger
---

# Typed-ID pattern (`IHasID<IDTYPE>`)

## Context and Problem Statement

A very large share of domain objects across the ecosystem have a stable identity — enum
constants, scopes, registry entries, Peppol participants and document types, etc. Exposing
that identity ad-hoc (a `getId()` here, a `getKey()` there, sometimes typed as `String`,
sometimes `int`) makes generic lookup/registry/comparison code impossible to write once.

## Considered Options

* **A single generic interface `IHasID<IDTYPE>`** with `getID()`, implemented uniformly
  by everything that has an identity (usually `IHasID<String>`).
* **Per-type ad-hoc identity accessors** with no shared contract.

## Decision Outcome

Chosen option: **`IHasID<IDTYPE>`**, implemented uniformly wherever an object has an
identity, because it lets generic infrastructure — ID-keyed registries, lookups,
comparators, enum-from-ID resolution — be written once against `IHasID` and reused
everywhere. It composes with the trait interfaces ([ADR-0018](0018-trait-interfaces.md))
and underpins the higher-level identifier abstractions in downstream projects (e.g. the
Peppol `IIdentifier` hierarchy).
Additionally, by using an explicit ID value, it is safe from unintended side effects when
e.g. renaming an enum constant.

### Consequences

* Good, because generic registry/lookup/comparator utilities work against any identified
  type without per-type code.
* Good, because it standardises `getID()` naming (and the `ID`-always-uppercase rule).
* Bad, because it is one more interface to implement; trivially cheap but non-zero.

## More Information

* ph-commons module `ph-base`: `com.helger.base.id.IHasID`; e.g. `ph-scopes`
  `com.helger.scope.IScope` extends it.
* Downstream: peppol-commons `com.helger.peppolid.IIdentifier` and its participant /
  document-type / process sub-interfaces build on the same idea.
* **Enums are `IHasID` too**, and the `String` ID deliberately carries the external/standard
  code so it round-trips: ph-masterdata `com.helger.masterdata.currency.ECurrency`
  (`IHasID<String>` = ISO 4217 code), `com.helger.masterdata.trade.EIncoterm` (Incoterm
  abbreviation). This makes `getFromIDOrNull(code)` the canonical parse from an external
  identifier. E.g. ph-masterdata has 28+ `IHasID` implementations across enums and domain
  classes.
* Related: [ADR-0018](0018-trait-interfaces.md),
  [ADR-0030](0030-audited-tenant-scoped-business-objects.md).
