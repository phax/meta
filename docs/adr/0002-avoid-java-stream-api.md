---
status: accepted
date: decided around the ph-commons 8 / JDK 8 timeframe
decision-makers: Philip Helger
---

# Avoid the Java Stream API

## Context and Problem Statement

Java 8 introduced the Stream API (`java.util.stream`) as the idiomatic way to process
collections. It is expressive, but it carries per-operation allocation and pipeline
setup overhead, and it can obscure control flow and complicate debugging. These
libraries are used as low-level building blocks in high-throughput, latency-sensitive
contexts, so the cost of the abstraction is paid by every downstream user.

## Considered Options

* **Use the Stream API** as the default idiom for collection processing.
* **Avoid streams; use explicit loops plus `CollectionHelper` abstractions** from
  ph-commons for the common cases.

## Decision Outcome

Chosen option: **Avoid the Stream API**. Use `CollectionHelper` for simple abstractions
(filter, map, find) and hand-written loops for everything else, because the guiding
principle is *"better the developer has increased effort than the user is impacted
negatively"* — the library author absorbs the extra verbosity so that consumers get
predictable, allocation-light performance.

In rare cases, when multiple different collection layers need to be scanned, the
Stream API is okay to be used - but it should be the exception, not the default.

### Consequences

* Good, because collection processing has predictable cost and is easy to profile and
  debug (plain loops, clear stack traces).
* Good, because it keeps a consistent idiom across the whole codebase.
* Bad, because code is more verbose than the equivalent stream pipeline.
* Bad, because it runs against mainstream Java style, which raises questions from
  newcomers and complicates copy-pasting external examples.

## More Information

* [`CodingStyleguide.md`](../../CodingStyleguide.md), "JDK 8 specialties": "I'm not
  using the stream API since there are performance considerations ... I'm a big fan of
  'better the developer has increased effort than the user is impacted negatively'."
* Related: [ADR-0003](0003-ph-commons-collection-types.md).
