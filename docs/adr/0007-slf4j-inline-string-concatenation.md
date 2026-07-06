---
status: accepted
date: long-standing convention
decision-makers: Philip Helger
---

# SLF4J logging with inline string concatenation

## Context and Problem Statement

All projects log via SLF4J. SLF4J's idiomatic API uses parameterized messages with `{}`
placeholders (`LOGGER.info("x={} y={}", x, y)`) to defer string construction until the
level is known to be enabled. The ecosystem needs a single, consistent logging style so
that log statements read and grep uniformly across all projects.

## Considered Options

* **Inline string concatenation** — `LOGGER.info ("x=" + x + " y=" + y)`.
* **SLF4J `{}` placeholders** — `LOGGER.info ("x={} y={}", x, y)`.

## Decision Outcome

Chosen option: **Inline string concatenation** in log calls, because it keeps the log
message readable as one contiguous string (the message reads the same in the source as
in the output), avoids placeholder/argument-count mismatches, and the micro-cost of
eagerly building the string is acceptable for this code. The logger is declared as
`private static final Logger LOGGER` (occasionally `protected`).

### Consequences

* Good, because log statements are unambiguous and easy to read and maintain — no
  counting placeholders against arguments.
* Bad, because the message string is built even when the log level is disabled; hot-path
  or high-volume logs at `debug`/`trace` must be guarded with `if (LOGGER.isDebugEnabled ())`
  where the cost matters.
* Bad, because it diverges from mainstream SLF4J guidance, so external tooling and
  contributors may expect placeholders.

## More Information

* [`CodingStyleguide.md`](../../CodingStyleguide.md), "General conventions" (logger
  declaration) and the project naming rules (inline concatenation, not `{}`
  placeholders).
