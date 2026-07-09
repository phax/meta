---
status: accepted
date: Since ph-commons 10.0.0
decision-makers: Philip Helger
---

# Builder pattern with `IBuilder<T>`

## Context and Problem Statement

Objects with many optional construction parameters (caches, URLs, HTTP settings, CLI
options) are awkward to build with telescoping constructors or long argument lists, and
error-prone to build with post-construction setters on a mutable object. A consistent,
fluent, discoverable construction idiom is needed across the ecosystem.

## Considered Options

* **A standardised builder contract `IBuilder<T>`** (a single `build()` method) that all
  fluent builders implement, with per-type builders (`CacheBuilder`, `URLBuilder`, …).
* **Telescoping constructors / static factory methods** with positional arguments.
* **Mutable objects configured via setters** after construction.

## Decision Outcome

Chosen option: **Fluent builders implementing `IBuilder<T>`**, because the shared marker
interface gives every builder the same `build()` terminal method, fluent `withX(…)`
configuration reads clearly and keeps optional parameters self-documenting, and building
into an immutable result avoids half-configured objects escaping.

### Consequences

* Good, because construction of parameter-heavy objects is readable, discoverable
  (IDE autocomplete on `with…`), and yields immutable results.
* Good, because the common `IBuilder<T>` contract lets generic code treat builders
  uniformly.
* Bad, because each builder is extra code to write and keep in sync with its target
  type's fields.

## More Information

* ph-commons module `ph-base`: `com.helger.base.builder.IBuilder`.
* Examples: `ph-cache` `com.helger.cache.impl.CacheBuilder`; `ph-url`
  `com.helger.url.URLBuilder`.
* Related: [ADR-0031](0031-self-typed-fluent-mutators.md) — the in-place fluent-mutator
  alternative for long-lived mutable objects (vs. this immutable-result builder).
