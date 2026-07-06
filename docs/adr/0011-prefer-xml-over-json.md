---
status: accepted
date: long-standing personal preference
decision-makers: Philip Helger
---

# Prefer XML over JSON for data formats

## Context and Problem Statement

Many projects in the ecosystem process, validate, transform or emit structured data (a
large share of it in regulated e-procurement / e-invoicing / EDI domains). The choice of
default serialization format — and where to invest tooling (schemas, validation,
transformation) — is a recurring architectural decision. Both XML and JSON are viable;
they differ sharply in validation, namespacing, transformation and typing capabilities.

## Considered Options

* **XML** — verbose but backed by namespaces, XSD/RelaxNG/Schematron validation, XPath,
  XSLT/XSL-FO, richer data types, and standardized metadata (comments, processing
  instructions).
* **JSON** — compact, ubiquitous in web contexts, faster/simpler to parse, but with
  flat naming, weaker/draft schema story, less powerful JSONPath, limited data types,
  and no standardized metadata or transformation.

## Decision Outcome

Chosen option: **XML**, as the preferred format where the choice is open, because the
ecosystem's problem domains value **rigorous validation and transformation** above
compactness: namespaces for unambiguous naming, XSD/RelaxNG *plus* Schematron (an ISO
standard) for structural and semantic validation, XPath for querying, and XSLT/XSL-FO for
standardized transformation to XML/HTML/text/PDF. JSON's advantages (size, parse speed,
web-native) do not outweigh these for the domains served.

This is a *default preference*, not a prohibition — JSON is still supported (e.g. the
`ph-json` library) where a use case calls for it.

### Consequences

* Good, because the ecosystem can offer strong, standards-based validation and
  transformation guarantees.
* Good, because XML's maturity (XPath since 1999, ISO-standard Schematron, XSLT) gives a
  stable, powerful tooling base.
* Good, because correctness and predicatable and interoperability is of XML is
  considered to be better.
* Bad, because XML is more verbose, heavier to parse, and less convenient for
  web/JavaScript consumers than JSON.
* Bad, because XML carries security footguns (XXE, DTD, remote includes) that must be
  actively disabled when parsing untrusted input.

## More Information

* Full comparison and rationale: [`XmlVsJson.md`](../../XmlVsJson.md) (this ADR is the
  formal record; that document holds the detailed feature-by-feature analysis and
  sources).
