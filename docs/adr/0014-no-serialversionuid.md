---
status: accepted
date: long-standing convention
decision-makers: Philip Helger
---

# Do not declare `serialVersionUID`

## Context and Problem Statement

Java `Serializable` classes may declare an explicit `serialVersionUID`. Omitting it makes
the runtime compute one from the class structure; declaring it pins serialization
compatibility manually. A consistent, ecosystem-wide rule avoids per-developer guesswork
and the boilerplate/lint noise of maintaining these fields.

## Considered Options

* **Omit `serialVersionUID`** and let the Java runtime derive it automatically.
* **Always declare an explicit `serialVersionUID`** on every `Serializable` type.

## Decision Outcome

Chosen option: **Omit `serialVersionUID`**. No class defines it; the Java runtime derives
it upon execution, because Java serialization is not used as a stable long-term
persistence/interchange format in this ecosystem (structured formats like XML are
preferred for that — see [ADR-0011](0011-prefer-xml-over-json.md)). Given that, the
manual field would add boilerplate and a false sense of managed compatibility without
providing real value.

### Consequences

* Good, because it removes boilerplate and the burden of manually maintaining version IDs.
* Bad, because binary serialization compatibility across builds is *not* guaranteed — the
  auto-computed UID changes when the class structure changes. This is an accepted
  trade-off because Java binary serialization is not relied on for cross-version data
  interchange here.

## More Information

* [`CodingStyleguide.md`](../../CodingStyleguide.md), "General conventions": "No class
  defines a `serialVersionUID` as this is automatically done by the Java runtime upon
  execution."
