---
status: accepted
date: long-standing ph-commons convention
decision-makers: Philip Helger
---

# Micro-DOM as the lightweight in-memory XML model

## Context and Problem Statement

Much ecosystem code reads, builds and writes XML but does not need the full weight and API
friction of the W3C DOM (`org.w3c.dom`) — which is verbose, mutable in awkward ways,
namespace-clumsy, and not designed for the ph-commons idioms (typed getters, `ICommons*`
collections, fluent building). Nor is JAXB always appropriate (it needs a schema/bound
classes and is heavier for ad-hoc or generic XML).

## Considered Options

* **A purpose-built lightweight tree model, "Micro-DOM"** (`IMicroDocument`,
  `IMicroElement`, …), with its own reader/writer and a Micro-DOM ↔ object conversion
  registry (`IMicroTypeConverter`).
* **The standard W3C DOM** everywhere.
* **JAXB** everywhere.

## Decision Outcome

Chosen option: **Micro-DOM** as the default in-memory XML model for hand-written XML
handling, because it is a lean, ph-commons-idiomatic tree (fluent building, typed
accessors, `ICommons*` return types) that is far easier to work with than W3C DOM and does
not require bound classes like JAXB. Object ↔ XML mapping is handled by `IMicroTypeConverter`
implementations registered via SPI ([ADR-0019](0019-spi-serviceloader-extensibility.md)),
so domain types can be (de)serialised to Micro-DOM uniformly.

JAXB is still used where a schema-bound model is the right tool — notably for
specification-defined XML generated from XSD (see
[ADR-0025](0025-generate-code-from-authoritative-sources.md)). Micro-DOM and JAXB coexist:
Micro-DOM for internal/ad-hoc/persistence XML, JAXB for standardised schema-bound
documents.

### Consequences

* Good, because everyday XML handling is concise and consistent with the rest of
  ph-commons, avoiding W3C DOM's verbosity.
* Good, because `IMicroTypeConverter` + SPI gives a uniform, extensible object↔XML mapping.
* Bad, because it is a bespoke XML model that newcomers must learn and that interop code
  must sometimes bridge to/from W3C DOM.
* Neutral, because two XML technologies (Micro-DOM and JAXB) are in play; the boundary
  (ad-hoc vs schema-bound) must be understood.

## More Information

* ph-commons `ph-xml`: `com.helger.xml.microdom.IMicroDocument`,
  `com.helger.xml.microdom.IMicroElement`, `com.helger.xml.microdom.MicroElement`;
  conversion `com.helger.xml.microdom.convert.IMicroTypeConverter` and
  `IMicroTypeConverterRegistrarSPI`.
* Related: [ADR-0011](0011-prefer-xml-over-json.md),
  [ADR-0025](0025-generate-code-from-authoritative-sources.md).
