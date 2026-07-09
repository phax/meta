---
status: accepted
date: TODO — long-standing convention; back-fill if known
decision-makers: Philip Helger
---

# No CDI / dependency-injection container — explicit wiring

## Context and Problem Statement

Objects that collaborate need to find one another: a service needs its dependencies, a
request handler needs the shared singletons, an extension point needs its implementations.
The mainstream Java answer is a dependency-injection container (Jakarta CDI / JSR-365,
Spring, Guice) that discovers beans by annotation, resolves them by type, and injects them
reflectively at runtime. That machinery is powerful but implicit: the wiring lives in the
container's runtime graph, not in the code, and errors surface as runtime resolution
failures rather than compile errors. A decision is needed on whether the ecosystem depends
on such a container.

## Considered Options

* **A CDI (or Spring / Guice) container** — annotate beans (`@Inject`, `@ApplicationScoped`,
  `@Named`), let the container scan the classpath, build the object graph and inject
  dependencies reflectively at runtime.
* **No container — explicit wiring.** Objects are constructed with plain constructors and
  setters; shared state is reached through the ecosystem's own scoped-singleton model
  ([ADR-0023](0023-scopes-and-scoped-singletons.md)); pluggable implementations come from
  `ServiceLoader`/SPI ([ADR-0019](0019-spi-serviceloader-extensibility.md)) and in-process
  callbacks ([ADR-0022](0022-callback-lists.md)); construction of complex objects uses
  builders ([ADR-0020](0020-builder-pattern-ibuilder.md)).

## Decision Outcome

Chosen option: **no CDI / DI container — wire collaborators explicitly**, because the
ecosystem values *control over magic*: the flow of construction and dependency lookup should
be readable in the code itself and verifiable by the compiler, not deferred to a runtime
container that resolves an implicit graph by reflection and annotation scanning. Libraries in
this ecosystem must stay usable as plain JARs with no container, no bean-discovery step and
no framework lifecycle wrapped around them.

The ecosystem already provides first-class answers for each job a DI container would do:

* **Shared/scoped state** → the scope and scoped-singleton model
  ([ADR-0023](0023-scopes-and-scoped-singletons.md)) — `getInstance()`-style accessors bound
  to a global / session / request scope, without container-managed bean scopes.
* **Pluggable implementations discovered at runtime** → `ServiceLoader`/SPI
  ([ADR-0019](0019-spi-serviceloader-extensibility.md)), an explicit `META-INF/services`
  contract rather than classpath annotation scanning.
* **In-process extension / observer hooks** → callback lists
  ([ADR-0022](0022-callback-lists.md)).
* **Assembling complex objects** → the `IBuilder<T>` builder pattern
  ([ADR-0020](0020-builder-pattern-ibuilder.md)), with plain constructors and setters for the
  rest.

This is a decision about the ecosystem libraries themselves. A downstream *application* that
already runs inside a CDI/Spring container is free to instantiate and register these classes
as beans — nothing here prevents that. The rule is that the ecosystem code must never
*require* a container to function.

### Consequences

* Good, because the wiring is explicit and local: you can read, in ordinary Java, exactly how
  an object is built and where its collaborators come from, and the compiler checks it.
* Good, because the libraries have no dependency on a DI runtime and work as plain JARs in any
  context — inside a container, in a CLI tool, in a test — without bootstrapping a framework.
* Good, because failures are compile-time or obvious construction-time errors, not runtime
  bean-resolution failures uncovered only when a particular code path first executes.
* Good, because it composes with the existing SPI, callback, scope and builder decisions,
  which already cover the legitimate needs that a DI container would otherwise serve.
* Bad, because wiring that a container would infer must be written by hand, which is more
  boilerplate for large object graphs.
* Bad, because contributors arriving from Spring/CDI-centric codebases must learn the
  ecosystem's explicit idioms instead of reaching for familiar `@Inject` annotations.

## More Information

* Related: [ADR-0023](0023-scopes-and-scoped-singletons.md) (scoped singletons — the shared-state
  answer), [ADR-0019](0019-spi-serviceloader-extensibility.md) (SPI/ServiceLoader — the
  pluggable-implementation answer), [ADR-0022](0022-callback-lists.md) (callbacks — the
  in-process extension answer), [ADR-0020](0020-builder-pattern-ibuilder.md) (builders — the
  object-assembly answer).
