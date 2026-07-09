---
status: accepted
date: long-standing convention
decision-makers: Philip Helger
---

# `BigDecimal` for monetary and exact-decimal values, never `float`/`double`

## Context and Problem Statement

Binary floating-point (`float`/`double`) cannot represent most decimal fractions exactly,
so it silently introduces rounding errors that are unacceptable for money, tax
percentages, exchange rates and quantities — the core of a masterdata/e-invoicing
ecosystem. A monetary amount is also meaningless without its currency, yet a bare
`BigDecimal` (or worse, a `double`) carries no currency and no defined scale/rounding.

## Considered Options

* **`BigDecimal` for all exact-decimal quantities, never `float`/`double`**, with monetary
  amounts additionally paired with their currency in a dedicated value type, and
  scale/rounding defined per currency in one place.
* **`double`/`float`** for amounts and percentages.
* **Minor-unit `long`** (integer cents) for money.

## Decision Outcome

Chosen option: **`BigDecimal`**, never `float`/`double`, for money and other
exact-decimal values. Monetary amounts are never naked: an amount is paired with an
`ECurrency` in a value object, and the scale and rounding mode are sourced centrally
per currency rather than hard-coded at call sites. This is chosen because it eliminates
representation error, makes currency a compile-time companion of every amount, and keeps
rounding policy consistent and configurable.

### Consequences

* Good, because arithmetic is exact and rounding is deliberate and consistent
  (ISO 4217-aware per-currency scale/rounding).
* Good, because "amount without currency" and "amount with wrong scale" become
  structurally hard to express.
* Bad, because `BigDecimal` arithmetic is more verbose and slower than primitive
  floating-point (an accepted trade-off — correctness over speed, cf.
  [ADR-0002](0002-avoid-java-stream-api.md)'s guiding principle).

## More Information

* Reference implementation: ph-masterdata `com.helger.masterdata.currencyvalue.ICurrencyValue`
  / `CurrencyValue` (BigDecimal + `ECurrency`); `com.helger.masterdata.currency.PerCurrencySettings`
  (per-currency scale/rounding); `com.helger.masterdata.price.IPrice`,
  `com.helger.masterdata.exchangeratio.ExchangeRatio`. The money packages contain zero
  `float`/`double` usage.
* Immutable arithmetic ("derive a new instance") methods are marked `@CheckReturnValue`
  (see [ADR-0006](0006-return-copies-of-collections.md)).
