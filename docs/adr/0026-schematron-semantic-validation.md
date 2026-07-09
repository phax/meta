---
status: accepted
date: long-standing ph-commons convention
decision-makers: Philip Helger
---

# Schematron for semantic validation beyond XSD

## Context and Problem Statement

XSD (and RelaxNG) can validate an XML document's *structure* — elements, attributes,
cardinalities, data types — but cannot express cross-field business rules ("if document
type is X then process must be Y", conditional requiredness, value interdependencies).
The ecosystem's regulated domains (Peppol, e-invoicing) are defined largely by exactly
these business rules.

## Considered Options

* **Two-layer validation**: XSD/RelaxNG for structure, plus **Schematron** (via
  `ph-schematron`) for semantic/business-rule validation, with versioned rule sets.
* **XSD only**, pushing business-rule checks into ad-hoc imperative Java.
* **All validation in imperative Java.**

## Decision Outcome

Chosen option: **XSD for structure + Schematron for semantics**. Schematron rules are
authored declaratively (XPath assertions), shipped as versioned `.sch`/compiled resources
inside the JARs, and executed via the `ph-schematron` library. This is chosen because
Schematron is an ISO standard purpose-built for rule-based semantic validation, keeps
business rules declarative and traceable to the specification rather than buried in Java,
and versioned rule sets let the same code validate against different specification
versions. It is the semantic complement to the structural XML choices in
[ADR-0011](0011-prefer-xml-over-json.md).

### Consequences

* Good, because complex business rules are expressed declaratively, standards-based, and
  versioned alongside the specs they encode.
* Good, because it cleanly separates "is it well-formed/structurally valid" (XSD) from "is
  it semantically compliant" (Schematron).
* Bad, because Schematron execution (XSLT-based) adds runtime cost and a dependency on the
  `ph-schematron` toolchain.
* Bad, because maintaining multiple rule-set versions in parallel adds bulk to the
  artifacts.

## More Information

* Library: `ph-schematron` (separate sibling repo).
* Example: peppol-commons `peppol-mls`
  `com.helger.peppol.mls.PeppolMLSValidator` loads versioned Schematron resources
  (`peppol-mls-1.0.1.sch`, …).
* Related: [ADR-0011](0011-prefer-xml-over-json.md),
  [ADR-0025](0025-generate-code-from-authoritative-sources.md).
