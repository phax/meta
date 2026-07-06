---
status: accepted
date: foundational since the meta project's inception (2014)
decision-makers: Philip Helger
---

# Sibling-repo layout with `meta` as single source of truth

## Context and Problem Statement

The ecosystem is not a monorepo — each library is its own independently-versioned,
independently-released Git repository (~66 of them). Yet they share conventions, build
scripts, CI workflows, license headers, and a strict inter-project dependency (build)
order. Something has to coordinate cross-cutting operations and be the authoritative
registry of "what projects exist and in what order they build".

## Considered Options

* **Many sibling repos under one parent folder, coordinated by a `meta` project** that
  holds the authoritative project registry and generates the cross-cutting artifacts.
* **A monorepo** containing all projects.
* **Fully independent repos** with per-repo, hand-maintained tooling and no central
  registry.

## Decision Outcome

Chosen option: **Sibling repos coordinated by `meta`**. Every project is checked out as a
sibling directory under one common parent folder; cross-cutting scripts live in that
parent and operate on all siblings. The `meta` project is the single source of truth:

* [`EProject`](../../src/main/java/com/helger/meta/project/EProject.java) is the central
  enum of active projects (coordinates, owner, type, version, JDK level, Pages/Wiki
  flags), **declared in dependency (build) order**. `EProjectDeprecated` holds retired
  projects; externally-defined projects merge in via XML through `ProjectList`.
* `Main*` tools iterate `EProject` to **generate** the shell scripts, aggregate
  `README.md`, `LinesOfCode.md`/`stats.md`, and to **check** cross-repo consistency
  (`MainCheckPOMArtifactVersions`, `MainCheckGitHubActionVersions`,
  `MainCheckProjectRequiredFiles`, ...).

This keeps independent versioning/release per library while centralising the shared
conventions in one place.

### Consequences

* Good, because each library keeps independent versioning and release cadence, yet
  conventions and build order stay consistent ecosystem-wide.
* Good, because generated artifacts and automated checkers prevent drift across ~66
  repos.
* Bad, because the sibling-folder assumption is a hard prerequisite — tooling only works
  when every repo is checked out as a sibling under the same parent
  (`CMeta.GIT_BASE_DIR`).
* Bad, because `EProject` must be manually kept in correct dependency order, and adding a
  module means editing `meta` and regenerating artifacts.

## More Information

* [`DevelopmentProcess.md`](../../DevelopmentProcess.md), "Repository Layout", "The
  `EProject` Registry", "Adding a New Module".
* [`CLAUDE.md`](../../CLAUDE.md) — architecture overview.
* This is the ADR that justifies keeping all *cross-cutting* ADRs in `meta`
  (see [ADR-0000](0000-record-architecture-decisions.md)).
