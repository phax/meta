---
status: accepted
date: long-standing ph-commons convention
decision-makers: Philip Helger
---

# Semantic state enums as return types

## Context and Problem Statement

Mutating operations frequently need to communicate an outcome: did anything actually
change? did it succeed? is the input valid? should iteration continue? A `boolean` return
answers this ambiguously (`true` = what, exactly?) and `void` throws the information away,
forcing callers to re-query state. A consistent, self-documenting way to return operation
outcomes is needed across the whole ecosystem.

## Considered Options

* **A family of two/three-state enums** used as return types — `EChange`
  (CHANGED/UNCHANGED), `ESuccess` (SUCCESS/FAILURE), `EValidity` (VALID/INVALID),
  `EContinue` (CONTINUE/BREAK), plus `ETriState` — each backed by an "indicator"
  interface (`IChangeIndicator`, `ISuccessIndicator`, `IValidityIndicator`,
  `IContinueIndicator`) offering `isChanged()`/`isSuccess()`/… and logical `and()`/`or()`.
* **Raw `boolean`** return values.
* **`void` plus exceptions** for failure and re-querying for change.

## Decision Outcome

Chosen option: **Semantic state enums**. Mutators return `EChange`/`ESuccess`/… instead
of `boolean` or `void`, because the enum makes the meaning explicit at the call site
(`if (x.putIn (…).isChanged ())` reads unambiguously), enables change-notification
plumbing (fire a callback only when `CHANGED`), and the shared indicator interfaces let
results be combined (`aChange.or (bChange)`).

### Consequences

* Good, because call sites are self-documenting and outcomes can be composed and
  propagated without re-querying object state.
* Good, because "did it change" drives callback/notification logic cleanly
  (see [ADR-0022](0022-callback-lists.md)).
* Bad, because it is another ecosystem-specific idiom newcomers must learn instead of
  plain `boolean`.
* Neutral, because these enums add a tiny amount of ceremony versus `boolean`.

## More Information

* ph-commons module `ph-base`: `com.helger.base.state.EChange`, `ESuccess`, `EValidity`,
  `EContinue`, `ETriState`; indicators `com.helger.base.state.IChangeIndicator`,
  `ISuccessIndicator`, `IValidityIndicator`, `IContinueIndicator`, and `IClearable`
  (`removeAll()` returns `EChange`).
* Related: [ADR-0022](0022-callback-lists.md).
