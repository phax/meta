---
status: accepted
date: TODO — back-fill if a specific date is wanted
decision-makers: Philip Helger
---

# Audited, tenant-scoped business-object base model

## Context and Problem Statement

Higher-level applications built on the ecosystem share the same needs for their persistent
domain entities: a stable typed identity, an audit trail (who/when created, last modified,
deleted — with soft-delete rather than physical removal), a bag of extensible attributes,
and, in multi-tenant deployments, unambiguous ownership by a tenant (and optionally a
finer organizational scope). Re-implementing this per entity or per application causes
drift and subtle multi-tenancy leaks.

## Considered Options

* **A shared base contract** in `ph-tenancy`: `IBusinessObject` (extending
  `ITypedObject` + creation/last-modification/deletion audit traits + an attribute map),
  with tenant/organizational scope layered on via trait composition
  (`ITenantObject extends IBusinessObject + IHasTenant`;
  `IAccountingAreaObject extends ITenantObject + IHasAccountingArea`) and an
  `AbstractBusinessObject` base implementation.
* **Per-application/per-entity ad-hoc** id/audit/tenant fields.
* **A heavyweight ORM/framework entity model** (JPA `@Entity`, framework base classes).

## Decision Outcome

Chosen option: **The `ph-tenancy` business-object base model**. Persistent business
entities build on `IBusinessObject` (typed ID via `ITypedObject`/`IHasID`, plus
creation/last-modification/deletion audit stamps and a generic attribute map) and layer
scoping through the trait interfaces `IHasTenant` and `IHasAccountingArea`. This is chosen
because it standardises identity, audit and tenant-ownership once — reusably across all
downstream applications — using the ecosystem's own trait-composition and typed-ID idioms
rather than a third-party ORM. Soft-deletion (a deletion stamp) preserves history instead
of physically removing rows.

### Consequences

* Good, because every business entity gets consistent identity, audit trail and
  tenant-scoping "for free", and tenant/scope can be enforced polymorphically against
  `ITenantObject`.
* Good, because it reuses the ecosystem idioms — [ADR-0017](0017-typed-id-pattern.md)
  (typed ID), [ADR-0018](0018-trait-interfaces.md) (trait composition),
  [ADR-0016](0016-semantic-state-enums-as-return-types.md) (mutators return `EChange`) —
  rather than importing a framework entity model.
* Bad, because tenant-scope enforcement still relies on callers filtering by
  `IHasTenant`/`IHasAccountingArea` — the model enables correct scoping but does not by
  itself prevent a query that forgets to apply it.
* Neutral, because `AbstractBusinessObject` is `@NotThreadSafe` (with a cached hashCode);
  concurrency is the caller's responsibility.

## More Information

* ph-masterdata module `ph-tenancy`: `com.helger.tenancy.IBusinessObject`,
  `AbstractBusinessObject`; `com.helger.tenancy.tenant.ITenant`, `ITenantObject`,
  `IHasTenant`; `com.helger.tenancy.accarea.IAccountingArea`, `IHasAccountingArea`.
* Base identity: ph-commons `ph-base` `com.helger.base.type.ITypedObject`.
* Scope note: this governs the **stateful/persistent business-application layer** of the
  ecosystem (the projects that build on `ph-tenancy`), not every library.
