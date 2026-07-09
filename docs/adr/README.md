# Architecture Decision Records (ADRs)

This directory collects the **cross-cutting architectural decisions** that apply
across the whole phax open-source ecosystem (all sibling projects governed by the
`meta` project — see [`EProject`](../../src/main/java/com/helger/meta/project/EProject.java)).

Decisions that are local to a single library (e.g. an internal design choice inside
`ph-json`) belong in that project's own repository. Everything recorded here is meant
to be true for **all** projects at once.

## What is an ADR?

An ADR is a short, immutable markdown file that captures **one** architectural
decision: the *context* that forced a choice, the *options* considered, the
*decision* taken, and its *consequences*. ADRs are append-only history: when a
decision changes, a **new** ADR supersedes the old one — the old file stays for the
record, with its `status` updated to `superseded by ADR-XXXX`.

The concept and the templates used here come from the
[architecture-decision-record](https://github.com/joelparkerhenderson/architecture-decision-record)
collection. We use the **MADR** (Markdown Any Decision Record) template — see
[`template.md`](template.md).

## How to add a new ADR

1. Copy [`template.md`](template.md) to `NNNN-short-kebab-title.md`, using the next
   free four-digit number.
2. Fill in context, options and the decision. Keep it short — one decision per file.
3. Set `status: accepted` (or `proposed` while under discussion).
4. Add a row to the index below.
5. Never edit the decision of a merged ADR. To change it, write a new ADR and set the
   old one's status to `superseded by ADR-NNNN`.

## Index

| ADR | Title | Status |
| --- | --- | --- |
| [0000](0000-record-architecture-decisions.md) | Record architecture decisions using MADR | Accepted |
| [0001](0001-ph-commons-as-jdk-extension-foundation.md) | ph-commons as the shared JDK-extension foundation | Accepted |
| [0002](0002-avoid-java-stream-api.md) | Avoid the Java Stream API | Accepted |
| [0003](0003-ph-commons-collection-types.md) | Use ph-commons collection types (`ICommonsList` / `CommonsArrayList`) | Accepted |
| [0004](0004-hungarian-notation-and-scope-prefixes.md) | Hungarian notation and scope prefixes | Accepted |
| [0005](0005-jspecify-nullability-annotations.md) | JSpecify nullability annotations (`@NonNull` / `@Nullable` / `@Nonempty`) | Accepted |
| [0006](0006-return-copies-of-collections.md) | Return copies of collections and arrays | Accepted |
| [0007](0007-slf4j-inline-string-concatenation.md) | SLF4J logging with inline string concatenation | Accepted |
| [0008](0008-thread-safety-via-readwritelock.md) | Thread-safety via `ReadWriteLock` and FindBugs annotations | Accepted |
| [0009](0009-jdk-17-baseline-multi-jdk-ci.md) | JDK 17 baseline with a multi-JDK CI matrix | Accepted |
| [0010](0010-sibling-repo-layout-meta-single-source.md) | Sibling-repo layout with `meta` as single source of truth | Accepted |
| [0011](0011-prefer-xml-over-json.md) | Prefer XML over JSON for data formats | Accepted |
| [0012](0012-apache-2-license-single-copyright.md) | Apache 2.0 license under a single copyright holder | Accepted |
| [0013](0013-enforce-forbidden-apis.md) | Enforce API hygiene with forbidden-apis | Accepted |
| [0014](0014-no-serialversionuid.md) | Do not declare `serialVersionUID` | Accepted |
| [0015](0015-no-constant-only-interfaces.md) | No constant-only interfaces | Accepted |
| [0016](0016-semantic-state-enums-as-return-types.md) | Semantic state enums as return types (`EChange` / `ESuccess` / …) | Accepted |
| [0017](0017-typed-id-pattern.md) | Typed-ID pattern (`IHasID<IDTYPE>`) | Accepted |
| [0018](0018-trait-interfaces.md) | Fine-grained trait interfaces — composition over inheritance | Accepted |
| [0019](0019-spi-serviceloader-extensibility.md) | SPI / ServiceLoader extensibility with `@IsSPIImplementation` | Accepted |
| [0020](0020-builder-pattern-ibuilder.md) | Builder pattern with `IBuilder<T>` | Accepted |
| [0021](0021-value-equality-contract.md) | Value equality via `EqualsHelper` / `HashCodeGenerator` | Accepted |
| [0022](0022-callback-lists.md) | In-process extensibility via callbacks (`ICallback` / `CallbackList`) | Accepted |
| [0023](0023-scopes-and-scoped-singletons.md) | Scopes and scoped singletons | Accepted |
| [0024](0024-micro-dom-xml-model.md) | Micro-DOM as the lightweight in-memory XML model | Accepted |
| [0025](0025-generate-code-from-authoritative-sources.md) | Generate code from authoritative sources (JAXB/xjc, code-list enums) | Accepted |
| [0026](0026-schematron-semantic-validation.md) | Schematron for semantic validation beyond XSD | Accepted |
| [0027](0027-http-access-via-ph-httpclient.md) | HTTP access via `ph-httpclient` settings-based wrapper | Accepted |
| [0028](0028-readonly-mutable-interface-split.md) | Read-only interface / mutable sub-interface split (`I…` vs `IMutable…`) | Accepted |
| [0029](0029-bigdecimal-for-money-and-exact-decimals.md) | `BigDecimal` for money & exact decimals, never `float`/`double` | Accepted |
| [0030](0030-audited-tenant-scoped-business-objects.md) | Audited, tenant-scoped business-object base model | Accepted |
| [0031](0031-self-typed-fluent-mutators.md) | Self-typed fluent mutators via `IGenericImplTrait<IMPLTYPE>` | Accepted |
| [0032](0032-no-cdi-explicit-wiring.md) | No CDI / dependency-injection container — explicit wiring | Accepted |

## Project-specific decisions (recorded elsewhere)

The scan of `ph-commons` and `peppol-commons` also surfaced decisions that are *specific
to one project's domain* rather than cross-cutting. By the rule in
[ADR-0000](0000-record-architecture-decisions.md) / [ADR-0010](0010-sibling-repo-layout-meta-single-source.md),
these belong in that project's own repository (its own `docs/adr/`), **not** here. They are
listed as candidates only:

* **peppol-commons** — unified Peppol identifier abstraction (`IIdentifier` +
  scheme/value factories); predefined identifier enums and multi-version *Policy for use of
  Identifiers* support (`@Pfuoi430`/`@Pfuoi440`); SML/SMP metadata providers with NAPTR/DNS
  discovery (`ISMLInfo`, `ISMPURLProvider`); layered Peppol PKI trust stores and mandatory
  certificate revocation checking; SBDH reading with injected identifier factory;
  versioned Directory business-card marshallers; caching SMP client with TTL.
* **ph-commons** — the DAO/WAL persistence model (`ph-dao`); hierarchy visitor traversal
  control enums (`ph-collection`); the `IExpirable` time-based lifecycle (`ph-datetime`).
* **ph-masterdata** — VAT/tax modelling (`IVATItem`: country-scoped, temporally-bounded,
  UN/ECE tax categories); the accounting-area organizational-scope layer
  (`IAccountingArea`); ISO code-list enums (currency/country/incoterm); the various
  address/email/phone/person value objects and their `IMicroTypeConverter` registrars.
* **ph-pdf-layout** — the two-phase prepare/render pipeline (`PreparationContext` →
  `prepare()` sizing → `PageRenderContext` → `render()`); vertical split/fragment handling
  with ancestor/`originalID` tracking (`IPLSplittableObject`); the CSS-like
  margin/border/padding box model (`IPLHasMarginBorderPadding` trait family); the `PL*`
  class-name prefix; static debug flags with a pluggable output sink (`PLDebugLog` /
  `PLDebugRender` / `IPLDebugOutput`); and the "package-private access shim" technique —
  helper classes placed in a third-party package namespace to reach package-private /
  protected APIs (`org.apache.pdfbox.pdmodel.font.PDFontHelper`,
  `org.apache.pdfbox.pdmodel.PDDocumentHelper`). The shim technique is arguably
  cross-cutting; left here as a candidate pending a second occurrence in another repo.
