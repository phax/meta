---
status: accepted
date: in effect since inception (2014)
decision-makers: Philip Helger
---

# Apache 2.0 license under a single copyright holder

## Context and Problem Statement

Open-source libraries intended for broad adoption — including in commercial and
government contexts — need a permissive, legally clear license, and a consistent
copyright/attribution story across every file and every repository. Inconsistent or
copyleft licensing would deter exactly the enterprise consumers these libraries target.

## Considered Options

* **Apache License 2.0**, single copyright holder (Philip Helger), header in every
  source file.
* **A copyleft license** (GPL/LGPL).
* **Other permissive licenses** (MIT/BSD) without an explicit patent grant.

## Decision Outcome

Chosen option: **Apache 2.0** under a single copyright holder, with the full license
header applied to every source file, because Apache 2.0 is permissive (enterprise- and
government-friendly), includes an explicit patent grant (which MIT/BSD lack), and a
single copyright holder keeps relicensing and provenance simple. The header year range is
kept current across the whole ecosystem by tooling.

### Consequences

* Good, because consumers (including commercial/government) can adopt the libraries with
  minimal legal friction, backed by a patent grant.
* Good, because single-holder copyright keeps licensing decisions unambiguous.
* Bad, because every source file must carry the header, requiring automation to apply and
  to roll the copyright year.
* Bad/consequential, because accepting external contributions must preserve the
  single-holder model (e.g. contributors licensing their work under the same terms).

## More Information

* [`pom.xml`](../../pom.xml) — license declaration and header.
* Header year is bumped ecosystem-wide by `MainUpdateLicenseTemplateYear`, then applied
  with `./mvn_license_format.sh` (see [`DevelopmentProcess.md`](../../DevelopmentProcess.md),
  "Housekeeping").
