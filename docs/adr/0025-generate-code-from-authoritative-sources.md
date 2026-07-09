---
status: accepted
date: long-standing convention
decision-makers: Philip Helger
---

# Generate code from authoritative sources

## Context and Problem Statement

A lot of the ecosystem's Java surface is a faithful representation of an external
authority: XML schemas (UBL, SBDH, Peppol Directory business cards, SMP/SML WSDL) and
official code lists (Peppol document-type / process / participant-scheme identifiers).
Hand-writing and hand-maintaining these against evolving specifications is laborious and
drifts out of sync with the source of truth.

## Considered Options

* **Generate the code from the authoritative artifact**: JAXB/`xjc` (via the
  `ph-jaxb-plugin`) from XSD/WSDL for bound document classes, and JCodeModel-based
  generators from official code-list XML for predefined enums.
* **Hand-write and hand-maintain** the Java classes and enums.

## Decision Outcome

Chosen option: **Generate from the authoritative source**. Schema-defined XML types are
produced by JAXB/`xjc` configured with the ecosystem's `ph-jaxb-plugin` (which applies the
house conventions to generated code: null-marked packages, JSpecify annotations,
`equals`/`hashCode`/`toString`, `ICommons*` list extensions). Predefined identifier enums
are generated from the official Peppol code-list XML by dedicated generator mains
(JCodeModel). This keeps the Java representation traceable to — and regenerable from — the
specification, mirroring the meta project's own "generate, don't hand-maintain"
philosophy ([ADR-0010](0010-sibling-repo-layout-meta-single-source.md)).

### Consequences

* Good, because the generated Java stays faithful to the specification and is cheap to
  regenerate when the spec or code list changes.
* Good, because `ph-jaxb-plugin` applies the ecosystem conventions uniformly to all
  generated classes, so generated code matches hand-written code's contracts.
* Bad, because the toolchain (xjc bindings, plugin, generator mains) is itself something to
  maintain and understand, and generation steps must be re-run and the output committed on
  each spec update.
* Neutral, because generated enums with rich metadata (introduced-in version, deprecation
  date) enable downstream policy/versioning logic.

## More Information

* Build config: peppol-commons `peppol-sml-client/pom.xml`,
  `peppol-directory-businesscard/pom.xml` (JAXB plugins wired to `ph-jaxb-plugin`);
  ph-commons `ph-jaxb` / `ph-jaxb-adapter` provide the JAXB base.
* Enum generators: peppol-commons `peppol-id` supplementary tool
  `MainCreatePredefinedEnumsFromXML_v9x` → e.g.
  `com.helger.peppolid.peppol.doctype.EPredefinedDocumentTypeIdentifier`, generated from
  `peppol-id/src/main/resources/external/codelists/Peppol Code Lists - *.xml`.
* Related: [ADR-0011](0011-prefer-xml-over-json.md),
  [ADR-0024](0024-micro-dom-xml-model.md),
  [ADR-0026](0026-schematron-semantic-validation.md).
