---
status: accepted
date: long-standing convention since Java 1.8
decision-makers: Philip Helger
---

# Fine-grained trait interfaces — composition over inheritance

## Context and Problem Statement

Domain types share small, orthogonal capabilities: "has a name", "has a display name",
"has a description", "has a size", "has display text", "has an ID". Modelling these with
deep inheritance hierarchies is rigid (a type either is-a `NamedDescribedThing` or it
isn't) and does not compose. A way to mix in individual capabilities as needed is
required.

## Considered Options

* **Many small single-capability interfaces** (`IHasName`, `IHasDisplayName`,
  `IHasDescription`, `IHasSize`, `IHasID`, `IHasDisplayText`, …), often
  `@FunctionalInterface`, composed per type.
* **Broad, coarse-grained interfaces or abstract base classes** bundling several
  properties.

## Decision Outcome

Chosen option: **Fine-grained trait interfaces**, composed via multiple-interface
implementation, because a type declares exactly the capabilities it has (e.g. a cache is
`IHasName` + `IHasSize`) without being forced into an unrelated hierarchy. Many are
functional interfaces, so they can be satisfied with a lambda/method reference. Generic
code can depend on just the trait it needs (`IHasID`, `IHasName`) rather than a concrete
type.

### Consequences

* Good, because capabilities compose freely and generic utilities target minimal
  contracts.
* Good, because it avoids brittle, deep inheritance trees.
* Bad, because the namespace holds a large number of tiny `IHas*` interfaces to learn and
  discover.

## More Information

* ph-commons module `ph-base`: `com.helger.base.name.IHasName`,
  `com.helger.base.iface.IHasSize`, and the wider `IHas*` family;
  [ADR-0017](0017-typed-id-pattern.md)'s `IHasID` is a member.
* Example composition: `ph-cache` `com.helger.cache.ICache` (`IHasName` + `IHasSize`).
* **Single-method traits are `@FunctionalInterface`** so they can be satisfied by a
  lambda/method reference — e.g. ph-masterdata `com.helger.masterdata.currency.IHasCurrency`.
* **Domain interfaces are assembled from traits**: ph-masterdata
  `com.helger.masterdata.price.IPrice` = `IHasCurrency` + `IHasVATItem`;
  `com.helger.tenancy.tenant.ITenant` = `IHasDisplayName` + `IHasUIText`. New cross-cutting
  concerns (tenant, accounting-area) are added the same way — see
  [ADR-0030](0030-audited-tenant-scoped-business-objects.md).
