---
status: accepted
date: long-standing ph-commons convention
decision-makers: Philip Helger
---

# In-process extensibility via callbacks (`ICallback` / `CallbackList`)

## Context and Problem Statement

Beyond classpath-level SPI discovery ([ADR-0019](0019-spi-serviceloader-extensibility.md)),
components need *programmatic* extension points that callers register at runtime: "notify
me when this changes", "handle this DAO read exception", "observe this event". A uniform,
type-safe way to model and manage such hooks is needed.

## Considered Options

* **A marker interface `ICallback` plus a managed `CallbackList<T>`** holding an ordered,
  thread-safe list of typed callbacks, with specific callback interfaces (e.g.
  `IChangeCallback<T>`, `IDAOReadExceptionCallback`) extending `ICallback`.
* **Ad-hoc listener lists** per component, each reinventing add/remove/iterate/threading.
* **A third-party event-bus library.**

## Decision Outcome

Chosen option: **`ICallback` + `CallbackList`**, because tagging all callbacks with a
common marker and managing them through a single `CallbackList` gives every component the
same registration/iteration/thread-safety semantics for free, while typed sub-interfaces
keep each hook's signature explicit. This is the runtime-registration complement to
compile-time SPI discovery.

### Consequences

* Good, because all extension points share one consistent, thread-safe registration
  mechanism instead of bespoke listener plumbing.
* Good, because it pairs naturally with the state enums — e.g. fire change callbacks only
  when an operation returns `EChange.CHANGED` (see
  [ADR-0016](0016-semantic-state-enums-as-return-types.md)).
* Bad, because it is another in-house abstraction rather than a widely-known event API.

## More Information

* ph-commons `ph-base`: `com.helger.base.callback.ICallback`,
  `com.helger.base.callback.CallbackList`, `com.helger.base.callback.IChangeCallback`.
* Example downstream callback: `ph-dao` `com.helger.dao.IDAOReadExceptionCallback`.
