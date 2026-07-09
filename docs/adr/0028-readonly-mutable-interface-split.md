---
status: accepted
date: TODO — long-standing convention (stated in CodingStyleguide); back-fill if known
decision-makers: Philip Helger
---

# Read-only interface / mutable sub-interface split (`I…` vs `IMutable…`)

## Context and Problem Statement

Domain and value types are consumed in two very different modes: most callers only *read*
them, while a few need to *modify* them. Exposing setters on the interface everyone uses
lets read-only consumers accidentally mutate shared state, and makes it impossible to hand
out a value with a compile-time guarantee that the recipient cannot change it. A uniform
way to separate the read contract from the write contract is needed.

## Considered Options

* **A two-interface split**: a read-only interface `IFoo` containing only accessors, and a
  mutable sub-interface `IMutableFoo extends IFoo` that adds the modifying methods (which
  return [state enums](0016-semantic-state-enums-as-return-types.md) such as `EChange`).
  Concrete classes implement `IMutableFoo`; APIs expose `IFoo` when they mean "read-only".
* **A single interface** with both getters and setters.
* **Immutability only** — no mutable variant at all.

## Decision Outcome

Chosen option: **The `I…` / `IMutable…` split**. By default an interface named `IFoo`
contains only read-only methods; the naming convention `IMutable…` signals that an
interface also contains modifying methods. This is chosen because it lets a method's
signature state precisely how much power it grants: returning `IPrice` guarantees the
caller cannot mutate, while `IMutablePrice` opts into modification. Mutating methods on the
mutable variant return `EChange`/`ESuccess` rather than `void`/`boolean`.

### Consequences

* Good, because read-only exposure is enforced by the type system — no defensive copying
  needed just to prevent mutation.
* Good, because the naming makes intent obvious at every declaration site and composes with
  the trait interfaces ([ADR-0018](0018-trait-interfaces.md)).
* Bad, because it doubles the interface count for mutable types (`IFoo` + `IMutableFoo`).
* Neutral, because purely immutable value types simply omit the `IMutable…` variant.

## More Information

* Stated in [`CodingStyleguide.md`](../../CodingStyleguide.md), "Naming conventions": an
  interface is read-only "except the interface name starts with `IMutable` in which case
  the interface also contains modifying methods".
* Reference implementation: ph-masterdata `com.helger.masterdata.price.IPrice` /
  `IMutablePrice` / `Price`; `com.helger.masterdata.currencyvalue.ICurrencyValue` /
  `IMutableCurrencyValue`.
* Related: [ADR-0016](0016-semantic-state-enums-as-return-types.md),
  [ADR-0018](0018-trait-interfaces.md).
