---
status: accepted
date: long-standing convention
decision-makers: Philip Helger
---

# Self-typed fluent mutators via `IGenericImplTrait<IMPLTYPE>`

## Context and Problem Statement

Many ecosystem types are configured in place through a long series of setters
(`setX(…).setY(…)`), and those types sit in deep inheritance hierarchies. A plain
`setX()` declared on a base class returns the base type, so chaining a base-class setter
followed by a subclass setter breaks — the base setter has "forgotten" the concrete type.
A way is needed to make in-place fluent configuration chain correctly across an
inheritance chain, without every subclass re-declaring every inherited setter.

## Considered Options

* **The curiously-recurring generic pattern (CRGP): parameterise each type on its own
  concrete self-type** — `AbstractFoo<IMPLTYPE extends AbstractFoo<IMPLTYPE>>` — and have
  every mutator return `IMPLTYPE` via a shared `thisAsT()` cast, provided by the ph-commons
  trait `IGenericImplTrait<IMPLTYPE>`.
* **A separate builder object** implementing `IBuilder<T>` (see [ADR-0020](0020-builder-pattern-ibuilder.md)),
  building an immutable result.
* **Plain setters returning `void`** (no chaining) or setters returning the declaring base
  type (chaining that degrades to the base type after the first base-class call).

## Decision Outcome

Chosen option: **self-typed fluent mutators built on `IGenericImplTrait<IMPLTYPE>`**, because
it gives fluent, chainable, in-place configuration that stays typed as the concrete class the
whole way down the hierarchy — a base-class `setID(…)` and a leaf-class `setFillColor(…)` can
be chained in either order and still return the leaf type. Setters return `@Nonnull IMPLTYPE`
and end in `return thisAsT ();`; the unchecked self-cast is centralised once in the trait's
default method rather than repeated in every class.

This is deliberately **distinct from the builder pattern** ([ADR-0020](0020-builder-pattern-ibuilder.md)):
a builder is a separate object that produces an immutable result, whereas this idiom mutates
the live object and hands *it* back. Use a builder when the result should be immutable and
half-configured instances must not escape; use self-typed mutators for long-lived, mutable,
reconfigurable objects.

### Consequences

* Good, because fluent chains stay typed as the concrete leaf class across arbitrarily deep
  hierarchies, with no per-subclass boilerplate re-declaring inherited setters.
* Good, because the unchecked `(IMPLTYPE) this` cast is written once, inside
  `IGenericImplTrait.thisAsT()`, instead of being scattered and repeated.
* Good, because it composes with fine-grained trait interfaces ([ADR-0018](0018-trait-interfaces.md)):
  a trait such as `IPLHasMargin<IMPLTYPE>` can declare default `setMargin(…)` methods that
  return `IMPLTYPE` without knowing the concrete type.
* Bad, because the recursive generic signature `<IMPLTYPE extends X<IMPLTYPE>>` is noisy and
  unfamiliar, and the self-cast is technically unchecked — it relies on the discipline that a
  class parameterises the trait on *itself*.

## More Information

* ph-commons module `ph-base`: `com.helger.base.trait.IGenericImplTrait` (the `thisAsT()`
  default method).
* Example — ph-pdf-layout: `com.helger.pdflayout.base.AbstractPLObject<IMPLTYPE extends
  AbstractPLObject<IMPLTYPE>>` (setters end in `return thisAsT ();`), and the trait interfaces
  `com.helger.pdflayout.base.IPLHasMargin<IMPLTYPE>` / `IPLHasPadding` / `IPLHasBorder`, whose
  default `setX(…)` methods return `IMPLTYPE`.
* Related: [ADR-0018](0018-trait-interfaces.md) (composition via fine-grained traits),
  [ADR-0020](0020-builder-pattern-ibuilder.md) (the immutable-result builder alternative),
  [ADR-0005](0005-jspecify-nullability-annotations.md) (setters carry `@Nonnull` returns).
