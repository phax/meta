# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **meta project** for managing and aligning Philip Helger's Java open-source ecosystem (phax). It is not meant to be released — it contains tooling that operates across sibling Git repositories to check build consistency, generate README files, validate POM versions, create WSDL/XSD files, and more.

The project assumes all related Git repos are checked out as siblings under the same parent directory (`CMeta.GIT_BASE_DIR` resolves to the parent of this project's directory).

## Build Commands

```bash
mvn compile          # Compile
mvn test             # Run all tests
mvn test -Dtest=EProjectTest           # Run a single test class
mvn test -Dtest=EProjectTest#testName  # Run a single test method
```

Uses Maven 3.x with parent POM `com.helger:parent-pom`. JUnit 4 for tests. JDK 17 required.

## Architecture

### Core: `com.helger.meta.project`

- **`EProject`** — Enum listing all active projects with their metadata (owner, type, version, JDK level, GitHub pages/wiki flags). This is the central registry.
- **`EProjectDeprecated`** — Same structure for deprecated/legacy projects.
- **`ProjectList`** — Aggregates `EProject`, `EProjectDeprecated`, and custom XML-defined projects into a unified lookup.
- **`IProject`** / **`SimpleProject`** — Project interface and its implementation.
- **`EExternalDependency`** — Registry of known external (non-helger) Maven dependencies and their versions.

### Tools: `com.helger.meta.tools`

All tools are runnable `Main*` classes (with `public static void main`), not tests. They extend `AbstractProjectMain` and iterate over `ProjectList` entries:

- **`buildsystem/`** — POM version checking, GitHub Actions version checking, README generation (`MainCreateMetaREADME`), lines-of-code stats, license year updates, Eclipse settings management, required files checks.
- **`cmdline/`** — Generates batch/shell scripts and build-all POMs.
- **`wsdlgen/`** — Reads `.interface` files and generates WSDL/XSD into `generated/`.
- **`codingstyleguide/`** — Validates coding conventions across projects.
- **`translation/`** — Extracts translatable strings.

### Key dependencies

All from `com.helger.commons` (`ph-commons-parent-pom` BOM): `ph-io`, `ph-xml`, `ph-json`, `ph-datetime`. Also uses ASM (`asm-tree`) for bytecode analysis and SLF4J for logging.

## Coding Style

- Hungarian notation for member variables: `s` (String), `n` (int/long), `b` (boolean), `a` (object/array), `e` (enum), `c` (char), `d` (double), `f` (float)
- Instance fields prefixed `m_`, static fields prefixed `s_` (except `static final` constants in UPPER_CASE)
- Interfaces start with `I` + uppercase (e.g. `IProject`), enums with `E`, abstract classes with `Abstract`
- Private methods start with `_` and are never declared `final`
- Collection-returning methods return copies (annotated `@ReturnsMutableCopy`)
- Use `@NonNull`/`@Nullable` (JSpecify) on all non-primitive parameters
- Logger declared as `private static final Logger LOGGER`
- No stream API — use `CollectionHelper` or manual loops
- Use `ICommonsList`, `CommonsArrayList`, etc. (ph-commons collections) instead of raw JDK collections
