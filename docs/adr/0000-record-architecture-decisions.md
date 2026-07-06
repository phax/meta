---
status: accepted
date: 2026-07-06 — date this ADR practice was adopted
decision-makers: Philip Helger
---

# Record architecture decisions using MADR

## Context and Problem Statement

The phax ecosystem spans ~66 sibling projects that share a large, mostly implicit set
of cross-cutting conventions (naming, collections, logging, threading, build and
release policy). These conventions live partly in prose documents
([`CodingStyleguide.md`](../../CodingStyleguide.md),
[`DevelopmentProcess.md`](../../DevelopmentProcess.md)) and partly only in the
author's head. The *rules* are written down, but the *reasoning* behind them — why the
Stream API is avoided, why XML is preferred over JSON — is scattered or undocumented.
New contributors (human or AI) repeatedly ask "why is it done this way?".

We need a durable, low-ceremony place to record the *reasoning* behind architectural
decisions so it survives independently of any single person's memory.

## Considered Options

* **MADR** (Markdown Any Decision Record) — lightweight, adds "Considered Options" and
  explicit pros/cons.
* **Nygard-style ADR** — even more minimal (Context / Decision / Consequences only).
* **Keep decisions in the existing prose docs** (`CodingStyleguide.md`, etc.).
* **A wiki page or external documentation site.**

## Decision Outcome

Chosen option: **MADR**, recorded as numbered markdown files under `docs/adr/` in the
`meta` project, because:

* MADR's explicit "Considered Options" section matches how these decisions were
  actually made (e.g. the existing [`XmlVsJson.md`](../../XmlVsJson.md) is already an
  informal options comparison).
* `meta` is already declared as the "single source of truth" for the ecosystem and is
  the only repo that governs *all* projects, so cross-cutting ADRs belong here (see
  [ADR-0010](0010-sibling-repo-layout-meta-single-source.md)).
* Numbered markdown files sit naturally alongside the existing root-level markdown docs
  and require no new tooling or hosting.

`CodingStyleguide.md` continues to state *the rule*; the ADR states *the reasoning*.
The two link to each other.

### Consequences

* Good, because the rationale behind ecosystem-wide conventions is now explicit,
  versioned, and reviewable.
* Good, because superseding (rather than editing) decisions preserves history.
* Bad, because there are now two places to keep aligned — the rule in
  `CodingStyleguide.md` and the reasoning in the ADR. This is mitigated by keeping the
  ADR focused on *why* and the styleguide focused on *what*.

## More Information

* Template: [`template.md`](template.md)
* ADR concept and templates: <https://github.com/joelparkerhenderson/architecture-decision-record>
* Optional future tooling: a `MainCreateMetaADRIndex` generator in
  `com.helger.meta.tools.buildsystem` could regenerate the index table in
  [`README.md`](README.md) from each file's front-matter, consistent with the existing
  generated-docs pattern (`MainCreateMetaREADME`, `MainCreateMetaLinesOfCode`).
