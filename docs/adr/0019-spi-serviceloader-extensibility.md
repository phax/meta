---
status: accepted
date: long-standing ph-commons convention
decision-makers: Philip Helger
---

# SPI / ServiceLoader extensibility with `@IsSPIImplementation`

## Context and Problem Statement

Core libraries must be extensible by downstream modules without the core knowing about
them at compile time: a new module should be able to register type converters,
Micro-DOM converters, equals/hashCode implementations, cleanup handlers, or (downstream)
Peppol identifier validators, simply by being on the classpath. A decoupled, classpath-
driven discovery mechanism is needed.

## Considered Options

* **Java `ServiceLoader` / SPI** — interfaces discovered via `META-INF/services` entries,
  with implementations tagged `@IsSPIImplementation` for clarity.
* **Central hard-coded registries** in the core that must be edited to add an extension.
* **A third-party dependency-injection container.**

## Decision Outcome

Chosen option: **Java SPI via `ServiceLoader`**, with each implementation annotated
`@IsSPIImplementation`, because it is a zero-dependency JDK mechanism that lets a module
extend the core just by shipping a `META-INF/services` file — the core stays unaware of
concrete extensions. The ecosystem uses "registrar" SPIs (an SPI whose job is to register
many entries into a registry) for the recurring cases: type conversion, Micro-DOM
conversion, equals/hashCode, and cleanup.

### Consequences

* Good, because downstream modules extend core behaviour purely by being on the classpath
  — no core changes, no DI container.
* Good, because `@IsSPIImplementation` documents intent and supports tooling/validation.
* Bad, because SPI wiring is implicit — a missing or malformed `META-INF/services` file
  fails silently at discovery time, which can be hard to diagnose.
* Bad, because load order across SPI implementations is not generally guaranteed.

## More Information

* Marker: ph-commons `ph-annotations` `com.helger.annotation.style.IsSPIImplementation`.
* Registrar SPIs: `ph-typeconvert` `com.helger.typeconvert.ITypeConverterRegistrarSPI`;
  `ph-xml` `com.helger.xml.microdom.convert.IMicroTypeConverterRegistrarSPI`;
  cleanup `com.helger.base.cleanup.ICleanUpRegistrarSPI`.
* Downstream example: peppol-commons
  `com.helger.peppolid.peppol.validator.IParticipantIdentifierValidatorSPI`
  (per-issuing-agency participant-ID validation).
* Related: [ADR-0021](0021-value-equality-contract.md),
  [ADR-0024](0024-micro-dom-xml-model.md).
