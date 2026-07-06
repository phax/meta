---
status: accepted
date: SimpleReadWriteLock arrived with ph-commons 8
decision-makers: Philip Helger
---

# Thread-safety via `ReadWriteLock` and FindBugs annotations

## Context and Problem Statement

Many components are read far more often than they are written (caches, registries,
configuration holders). Guarding them with a single mutual-exclusion lock (`synchronized`)
serialises readers unnecessarily. The ecosystem also needs a consistent, declared way to
communicate each class's thread-safety level to callers.

## Considered Options

* **`java.util.concurrent.locks.ReadWriteLock`** (multiple concurrent readers, single
  writer), via `com.helger.commons.concurrent.SimpleReadWriteLock` for better lambda
  ergonomics.
* **`synchronized` / intrinsic locks** everywhere.
* **Concurrent collections only** (`ConcurrentHashMap`, etc.) without explicit locking.

## Decision Outcome

Chosen option: **`ReadWriteLock`**, using `SimpleReadWriteLock` (a `ReentrantReadWriteLock`
subclass with better lambda support) for critical sections, because it allows many
readers to proceed in parallel while still giving writers exclusive access — matching the
read-heavy access pattern of most components.

Thread-safety is additionally **declared** on domain classes with FindBugs annotations:
`@Immutable`, `@ThreadSafe` or `@NotThreadSafe`, so the contract is explicit and
statically checkable.

### Consequences

* Good, because read-heavy components scale better than under a single mutex.
* Good, because the thread-safety annotation makes each class's concurrency contract
  explicit to callers and to static analysis.
* Bad, because read/write locking is more error-prone than `synchronized` (lock
  acquisition/release discipline, no reader-to-writer upgrade); `SimpleReadWriteLock`'s
  lambda helpers mitigate this by scoping lock/unlock around a callback.

## More Information

* [`CodingStyleguide.md`](../../CodingStyleguide.md), "General conventions" and "JDK 8
  specialties".
* Related: [ADR-0005](0005-jspecify-nullability-annotations.md) (annotation policy),
  [ADR-0006](0006-return-copies-of-collections.md).
