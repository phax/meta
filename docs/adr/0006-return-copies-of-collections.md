---
status: accepted
date: long-standing convention
decision-makers: Philip Helger
---

# Return copies of collections and arrays

## Context and Problem Statement

When a getter hands out the object's internal collection or array directly, callers can
mutate the owner's state behind its back, which breaks encapsulation and is a frequent
source of thread-safety bugs. A consistent, predictable rule is needed for what a
collection/array getter returns.

## Considered Options

* **Return a copy by default**, and expose explicit `add` / `remove` / `clear` methods
  for controlled mutation of the owner.
* **Return the internal collection directly** (fast, but callers can corrupt state).
* **Return an unmodifiable view** (`Collections.unmodifiableList`).

## Decision Outcome

Chosen option: **Return copies by default**, with the return type annotated to make the
contract explicit:

* `@ReturnsMutableCopy` — a fresh copy; mutating it does not affect the owner (the
  default, and the safe choice).
* `@ReturnsMutableObject` — the internal object is returned directly (only for a
  deliberate reason, documented in the annotation).
* `@ReturnsImmutableObject` — an unmodifiable view is returned (e.g.
  `Collections.unmodifiableList`; not applicable to arrays).

Direct mutation of the owner goes through explicit `add` / `remove` / `clear` methods.

### Consequences

* Good, because it preserves encapsulation and supports thread-safety — callers cannot
  accidentally mutate internal state.
* Good, because the annotation makes the (otherwise invisible) copy-vs-reference contract
  explicit at every call site.
* Bad, because defensive copying costs allocations and time on hot paths; the
  `@ReturnsMutableObject` escape hatch exists for the deliberate exceptions.

## More Information

* [`CodingStyleguide.md`](../../CodingStyleguide.md), "Special annotations".
* The same "don't hand back something the caller might mistake for the owner's state"
  discipline extends to **immutable value objects**: methods that *derive a new instance*
  (e.g. `getAdded`/`getMultiplied` on ph-masterdata `IPrice`/`ICurrencyValue`) are marked
  `@CheckReturnValue`, so a caller who ignores the returned copy — expecting an in-place
  mutation — is flagged. (`@CheckReturnValue` is used 50+ times across ph-masterdata.)
* The copy-on-modify discipline also appears as a **wither idiom** on `@Immutable` value
  objects: instead of setters, they expose `getCloneWith…` methods that return a *new*
  instance with one field changed (with an identity check to skip the allocation when the
  value is unchanged). Example — ph-pdf-layout `com.helger.pdflayout.spec.MarginSpec`
  (`getCloneWithTop`/`Right`/`Bottom`/`Left`), `FontSpec.getCloneWithDifferentFont(…)`,
  `BorderSpec`, `PaddingSpec` — all marked `@Immutable`.
* Related: [ADR-0003](0003-ph-commons-collection-types.md),
  [ADR-0008](0008-thread-safety-via-readwritelock.md),
  [ADR-0028](0028-readonly-mutable-interface-split.md),
  [ADR-0029](0029-bigdecimal-for-money-and-exact-decimals.md).
