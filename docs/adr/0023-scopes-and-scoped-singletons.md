---
status: accepted
date: long-standing convention
decision-makers: Philip Helger
---

# Scopes and scoped singletons

## Context and Problem Statement

Server/web applications need state bound to well-defined lifetimes: application-global
state, per-user-session state, and per-request state — each created and destroyed at the
right moment, with a clear containment relationship. Ad-hoc `static` fields and manual
`ThreadLocal`s do not model these lifetimes or their cleanup, and leak across sessions or
requests.

## Considered Options

* **An explicit scope hierarchy** (`IGlobalScope` → `ISessionScope` → `IRequestScope`,
  all `IScope extends IHasID`) with lifecycle hooks (`IScopeDestructionAware`,
  `IScopeRenewalAware`), plus **scoped singletons** (`AbstractSingleton` and
  Global/Application/Session/Request variants) whose instances live and die with their
  scope.
* **Rely on a web framework's** built-in scoping (Servlet session, CDI, Spring).
* **Manual `static` fields and `ThreadLocal`s.**

## Decision Outcome

Chosen option: **A first-class scope model with scoped singletons**, because it gives the
ecosystem a framework-independent, uniform way to bind state to global/session/request
lifetimes and to run cleanup/renewal hooks at the boundaries. `AbstractSingleton`
subclasses provide singleton semantics *within* a scope, so "one per session" or "one per
request" is expressed declaratively rather than reinvented per component.

### Consequences

* Good, because state lifetime and cleanup are explicit and consistent, independent of any
  particular web framework.
* Good, because objects can opt into destruction/renewal via marker interfaces without the
  scope knowing their concrete type.
* Bad, because it is a parallel scoping model to whatever the host framework (Servlet/CDI)
  provides, which must be bridged and understood.
* Bad, because scoped singletons rely on reflection-based instantiation, which is less
  transparent than direct construction.

## More Information

* ph-commons `ph-scopes`: `com.helger.scope.IScope`, `IGlobalScope`, `ISessionScope`,
  `IRequestScope`; `IScopeDestructionAware`, `IScopeRenewalAware`;
  `com.helger.scope.singleton.AbstractSingleton` and its scope variants.
* Related: [ADR-0017](0017-typed-id-pattern.md) (`IScope` is an `IHasID`).
