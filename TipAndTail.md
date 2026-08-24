# Tip & Tail in My Development Methodology

Draft -- 2026-08-24. Discussion document, not yet policy.

Reference: [JEP 14: The Tip & Tail Model of Library Development](https://openjdk.org/jeps/14)
(Informational, Active; Alex Buckley, Brian Goetz, Ron Pressler; created 2024-09-30, updated 2025-02-24, issue 8341287).

This document answers one question: **how do I apply JEP 14 across ~66 sibling repos, where the
libraries must stay boring and the applications should be allowed to be modern?**
It is the strategic counterpart to [DevelopmentProcess.md](DevelopmentProcess.md), which describes
the mechanics of getting work released.

---

## 1. What JEP 14 actually says

| Term | JEP 14 definition |
| --- | --- |
| **tip** | The single release train that moves forward: new features, functional enhancements, plus the largest possible set of bug fixes, security patches and performance improvements. |
| **tail** | A train forked from a designated tip release. Receives *critical bug fixes and security patches -- and nothing else*. There may be many tails; there is only one tip. |
| **feature release** | The JDK's name for a tip release (every 6 months). |
| **update release** | The JDK's name for a tail release (every 3 months). |

The mantra for library developers, quoted:

> * Add new features and functional enhancements only in the tip, not in the tails.
> * Backport as little as possible from the tip to the tails.
> * Ensure that tail releases depend on the tail releases of other libraries, where available.

And for baselines:

> * In the tip train, baseline each tip release on the JDK version that best supports the library's new features and enhancements.
> * Baseline a tail train on a JDK version designated as a long-term support release. Keep the baseline as constant as possible over the life of the tail train.

Two further points that matter for my situation:

* **The cascade of doom.** A patch release that drags in a minor dependency upgrade, which drags in a
  major upgrade further down, defeats the whole point. Tails must depend on tails.
* **Freedoms.** JEP 14 deliberately does *not* prescribe release cadence, version numbering,
  when tails are forked, or when they are discontinued. Those are mine to choose.

What JEP 14 does *not* say: it says nothing about applications. The application-side half of this
document is my own extrapolation from the JEP's own framing -- "users building new systems" use tip
releases; "users focused on stability" use tail releases. My applications are users building new
systems.

---

## 2. Where I stand today (2026-08)

| Fact | Value | Source |
| --- | --- | --- |
| Library baseline | JDK 17 for 77 of 81 top-level entries | `EJDK.JDK17` in `src/main/java/com/helger/meta/project/EProject.java` |
| Exceptions upward | `phoss-ap` (JDK 21) | `EProject.java:1093` |
| Exceptions downward | `ph-forbidden-apis`, `ph-isorelax` (JDK 8) | `EProject.java:52`, `EProject.java:62` |
| Where the baseline is set | `<java.version>17</java.version>` feeding `maven.compiler.source/target/release` | `../ph-parent-pom/pom.xml:47`, `:72-75` |
| Per-project override | `<java.version>21</java.version>` in the project's own parent POM | `../phoss-ap/pom.xml` |
| CI matrix | `java: [ 17, 21, 25 ]`, snapshot deploy on 17 only | `.github/workflows/maven.yml` in every repo |
| Known JDK levels in tooling | `JDK8`, `JDK11`, `JDK17`, `JDK21`, `JDK25` | `src/main/java/com/helger/meta/project/EJDK.java` |
| Preview features in use | none (`--enable-preview` appears in no POM) | grep over `../*/pom.xml` |

**Honest assessment.** Today I run one-size-fits-all with a conservative baseline: one train per
project, baselined on the oldest JDK I am willing to support, verified on three JDKs in CI. This is
precisely the model JEP 14 argues against -- but I am closer to tip & tail than it looks:

* `ph-commons` already carries the comment `10.2.6 for JDK 8` / `11.2.6 for JDK 11`
  (`EProject.java:64-65`) above the current `12.3.5` on JDK 17. That is a tip and two dead tails.
  What is missing is not the structure, it is the *promise*: nobody knows those trains still exist,
  and nothing tells me what may or may not go into them.
* `ph-forbidden-apis` and `ph-isorelax` on JDK 8 are de-facto frozen tails already.
* The 17/21/25 CI matrix is me paying the multi-JDK testing cost that JEP 14 explicitly calls
  "unfair" to expect of a library developer -- and paying it on the *tip*, where it buys the least.

So the change is mostly one of naming, promises and tooling, not of process invention.

---

## 3. The core asymmetry: who owns the runtime

The reason libraries must be conservative and applications may be cutting edge is not taste. It is
who chooses the JVM the code runs on.

| | Library (`ph-commons`, `peppol-commons`, `phase4-lib`, `phive`, ...) | Application (`phoss-smp-webapp-*`, `phoss-ap-webapp`, `phoss-directory-publisher`, `peppol-practical`, `kaltblut-cli`, `ph-redact-cli`, `phoss-peppol-mcp-server`) |
| --- | --- | --- |
| Who picks the JVM | The consumer. Unknown, plural, often a bank or a public administration. | I do -- via the Docker image, the bundled runtime, or the documented install requirement. |
| Cost of a baseline bump | Locks out every consumer that cannot move. Support load, angry issues, forks. | A one-line change in a Dockerfile that I control. |
| Blast radius of a mistake | Every downstream project and their users. | One deployable, rolled back by redeploying the previous tag. |
| Therefore | **Baseline on LTS only. Bump rarely and deliberately.** | **Baseline on the current GA feature release. Bump routinely.** |

Corollary: the interesting boundary is not "phax project vs other project", it is **published for
third-party compilation vs shipped as a running unit**. A `JAVA_LIBRARY` inside an application's
reactor (e.g. `phoss-smp-webapp`, which is typed as a library but only exists to be assembled into
the three `phoss-smp-webapp-*` wars) follows the *application* rules, because nobody outside
compiles against it. That distinction is currently invisible in `EProject` -- see §8.

---

## 4. Library rules

**L1 -- Tip baseline is LTS-only.** Never baseline a published library on a non-LTS feature release.
JEP 14 permits it; my consumer base does not. The permitted ladder is 17 -> 21 -> 25 -> (29,
expected 2027). `EJDK.JDK24` exists in the enum today and should never be used for a library.

**L2 -- Bump the baseline only when a JDK feature pays for itself.** Not "because 25 is out".
Concretely, an acceptable justification names an API or language feature and the code it deletes,
for example:
  * virtual threads (21) replacing a thread pool in `phase4-lib` / `ph-schedule`;
  * pattern matching for `switch` + sealed interfaces (21) collapsing visitor code in `ph-ubl` /
    `ph-cii` mapping layers;
  * the Foreign Function & Memory API (22, final) -- no current use case in my stack;
  * `HttpClient` / `HexFormat` / `ByteBuffer` improvements that let me delete hand-rolled helpers
    from `ph-base` / `ph-io`.

"CI is green on 25 anyway" is not a justification. Neither is "Spring did it".

**L3 -- A baseline bump is a major version bump, and it forks a tail.** The version at which the
baseline changes is the point where a tail is created from the previous major. This makes the
baseline discoverable from the version number, which my consumers already read.

**L4 -- Tails carry critical fixes and security patches only.** My working definition of *critical*,
to be pasted into each project wiki:

> A backport candidate is critical if, without it, a correctly written consumer can (a) produce or
> accept wrong business data (wrong amounts, wrong identifiers, silently dropped content),
> (b) fail to interoperate with a mandated profile (Peppol, eDelivery, EN 16931, ...) whose rules
> changed by external mandate, (c) be exposed to a published CVE, or (d) lose or corrupt persisted
> data. Everything else -- new features, convenience overloads, refactorings, performance work,
> cosmetic API additions, dependency upgrades for their own sake -- goes to the tip only.

Note (b): my domain has a genuine extra category the JEP does not contemplate. Peppol code lists,
validation artefacts and specification versions change on externally mandated dates, and a
deployed AP or SMP on a tail *must* be able to follow them or it becomes non-compliant. See L6.

**L5 -- Tails depend on tails.** A `peppol-commons` tail must resolve a `ph-commons` tail, not the
`ph-commons` tip. This is the single hardest rule in my stack (§7) and the one most likely to be
violated by reflex, because `mvn versions:display-dependency-updates` and my own habit both push
everything to newest.

**L6 -- Externally mandated data is the one legitimate exception, and it must be isolated.**
Code-list and validation-artefact updates are not "features" but they are also not "critical bug
fixes" -- they are a third thing: compliance data with a legal deadline. The clean answer is not
to backport them but to keep them in artefacts whose version is independent of the code baseline,
so a tail consumer can upgrade data without upgrading code. Candidates: `peppol-codelists`-style
artefacts, `phive-rules-*` rule sets, `ddd` mapping rules. Where that separation does not exist
yet, creating it is worth more than any backporting policy.
*This is the item I am least sure about -- see §12.*

**L7 -- One CI matrix per train, and it is small.** The tip builds on its own baseline plus the
next LTS (early warning). A tail builds on its own baseline only. Today every repo builds
17/21/25 on the tip, which is three times the cost for information I mostly do not act on.

| Train | Baseline | CI matrix | Deploy |
| --- | --- | --- | --- |
| tip | current chosen LTS (17 today, 25 next) | baseline + next LTS | snapshot on baseline |
| tail | its frozen LTS | baseline only | on release only |

**L8 -- No preview features, ever, in a published library.** Preview APIs require
`--enable-preview` at both compile and run time, and the flag is version-locked: a class compiled
with preview on JDK N refuses to load on JDK N+1. Putting that into an artefact on Maven Central
transfers an unbounded upgrade obligation to strangers. Incubator modules likewise.

---

## 5. Application rules

**A1 -- Baseline on the current GA feature release.** For anything I deploy or ship as a runnable
unit, `<java.version>` tracks the newest GA JDK, because I also ship the JVM (container image,
documented runtime, or bundled JRE). At the time of writing that is JDK 26; verify the current GA
at <https://openjdk.org/projects/jdk/> before bumping.

**A2 -- Ride the JDK tip properly: N, N.0.1, N.0.2, then N+1.** JEP 14 describes exactly this
pattern for users who want to stay current. Two update releases per feature release, then move on.
That is one baseline bump every six months per application -- a scheduled 30-minute job (bump the
property, bump the base image, rerun CI), not a project.

**A3 -- An application may not be more modern than the libraries it consumes allow.** An app on
JDK 26 can consume libraries baselined on 17 without any problem; the reverse is impossible. So A1
is always safe, and no coordination with §4 is needed in that direction. What *is* needed: when
`phoss-ap` (JDK 21 today) wants a JDK 25 language feature, nothing stops it -- but if it also wants
a *library* change that requires JDK 25, that library change belongs in the library's next tip
major, and phoss-ap waits for it or vendors the code locally.

**A4 -- Preview features: allowed, opt-in, never in a library, and never across a support boundary.**
For an application whose runtime I fully control and whose deployment I can recompile at will, the
cost of `--enable-preview` is bounded: recompile on every JDK bump, which A2 makes me do anyway.
My recommendation is to allow it for demos, benchmarks and internal tools
(`phase2-demo-spring-boot`, `ph-oton-bootstrap5-demo`, `phoss-ap-testsender`, `phoss-ap-testbackend`, `bozoo`) and to keep it
out of anything a third party operates -- notably `phoss-smp-webapp-*` and `phoss-ap-webapp`, where
an operator may run a JVM I did not pick despite everything above. *Open decision -- §12.*

**A5 -- Applications get no tails, with named exceptions.** Where an application is operated by
third parties on their own schedule (`phoss-smp`, `phoss-ap`), it is in the same position as a
library and needs a tail: a maintained older major that receives security patches, critical fixes,
and (per L6) compliance-data updates. Where I am the only operator, there is no tail -- roll
forward.

---

## 6. Version numbering

I do not need a new scheme; I need to attach meaning to the digits I already use.

| Change | Version effect | Baseline effect |
| --- | --- | --- |
| JDK baseline bump | major +1 (`ph-commons` 12.x -> 13.x) | new baseline; previous major becomes a tail |
| Breaking API change | major +1 | unchanged |
| New feature, additive API | minor +1 on the tip | unchanged |
| Critical fix / security patch on the tip | patch +1 on the tip | unchanged |
| Critical fix / security patch backported | patch +1 on the tail (`12.3.5` -> `12.3.6`) | frozen |

Invariant worth enforcing in tooling: **within one major version, the baseline never changes.**
That is the promise that makes a tail trustworthy, and it is mechanically checkable (§8).

Consequence for the "News and noteworthy" rule in my own working rules: a tail release's news entry
may only ever contain *Fixed* / *Security* / (per L6) *Updated data* bullets. If a tail entry grows
a *Added* or *Changed* bullet, the discipline has already been broken.

---

## 7. The hard part: dependency order in the ph-* stack

JEP 14's cascade of doom is not hypothetical for me -- my stack is a deep single-rooted tree, and
`EProject` is literally declared in dependency order.

```
ph-parent-pom            <- sets java.version for everything below
  ph-commons             <- 12.3.5, JDK 17
    ph-xml, ph-io, ph-json, ph-datetime, ph-web, ph-css, ph-schematron, ph-ubl, ph-cii, ...
      peppol-commons, phive, phase4-lib, ph-oton, ph-diver, ...
        phoss-smp, phoss-directory, phoss-ap, peppol-practical, phase4-server-webapp
```

Implications:

1. **A baseline bump in `ph-commons` is an ecosystem event, not a project decision.** Everything
   above it must either follow within one wave or pin to the `ph-commons` tail forever. So baseline
   bumps happen as **waves**, planned top-down from `ph-parent-pom`, ideally once per LTS -- i.e.
   roughly every two years, not continuously.
2. **A wave means a major bump nearly everywhere at once.** That is expensive in release
   choreography but cheap in thinking, and it keeps the whole stack internally consistent: one
   baseline per wave, one tail generation per wave.
3. **Between waves, `ph-parent-pom` must be able to express two baselines**, because the tail wave
   and the tip wave are alive simultaneously. Simplest implementation: the tail generation stays on
   `parent-pom` 3.x (java.version 17) and the tip wave moves to `parent-pom` 4.x
   (java.version 25). The parent POM version becomes the carrier of the baseline, which is honest
   and already how `phoss-ap` opts out today.
4. **Applications are exempt from wave timing** (A1/A3): they can be on JDK 26 while consuming a
   JDK 17-baselined stack. This is what makes "libraries boring, applications cutting edge"
   actually affordable -- the two halves are decoupled by design, not by discipline.

---

## 8. What the `meta` project needs to support this

Current gaps, all in `src/main/java/com/helger/meta/project/`:

| Gap | Proposal |
| --- | --- |
| `EJDK` lacks `JDK25`, has non-LTS `JDK24` | Add `JDK25` (and future GA levels for applications). Add `isLTS()` so a checker can enforce L1. |
| `EProject` models exactly one version + one JDK per project | A project in tip & tail has 1 tip + N tails. Either add a train list per project, or introduce `EProjectTail` alongside `EProjectDeprecated`. Needs a decision (§12) before any tooling is written. |
| No notion of "published for third parties" vs "assembled into an app" | Add a flag (e.g. `EPublishedFor.THIRD_PARTIES` / `OWN_APPS`) so L*/A* rules can be applied automatically. `phoss-smp-webapp` is the motivating example. |
| Nothing checks that a major version keeps its baseline | New `MainCheckJDKBaselineStable`: for each project, compare `<java.version>` in the POM against the baseline recorded for that major in `EProject`. |
| Nothing checks tail-depends-on-tail (L5) | New `MainCheckTailDependencies`: for a tail POM, assert every `com.helger*` dependency resolves to a tail version, not the tip. This is the highest-value new checker on this list. |
| CI matrix is uniform 17/21/25 everywhere | `MainCheckGitHubActionVersions` is the natural place to also enforce the per-train matrix from L7. |
| `README.md` generation prints only "Version X - JDK N" | Print the train role and baseline, e.g. `12.3.5 (tail, JDK 17)` / `13.0.0 (tip, JDK 25)`, so the public listing states the promise. |
| Generated `mvn_*.sh` walk one train | A wave in progress needs per-train build scripts, or a train argument. |

---

## 9. Worked example (hypothetical, `ph-commons`)

```
TIP:    12.3.5 -- 12.4.0 -- 13.0.0 -- 13.1.0 -- 13.2.0 -- 14.0.0 ...
         (17)      (17)      (25)      (25)      (25)      (29)
                     \                             \
TAIL 1:               \- 12.4.1 -- 12.4.2 -- 12.4.3 -- 12.4.4 EOL
                          (17)      (17)      (17)      (17)
                                                \
TAIL 2:                                          \- 13.2.1 -- 13.2.2 -- ...
                                                      (25)      (25)
```

* `12.4.0` is the last JDK 17 tip release and the fork point of tail 1.
* `13.0.0` opens the JDK 25 wave -- new major precisely because the baseline moved (L3).
* Tail 1 gets CVE fixes and wrong-data fixes only; the virtual-thread rework that landed in
  `13.1.0` never appears there (L2, L4).
* `peppol-commons`, `phive`, `phase4` each fork their own tail against `ph-commons` 12.4.x (L5).
* Meanwhile `phoss-smp-webapp-xml` runs on JDK 26 against `ph-commons` 12.4.x without anyone
  noticing a conflict (A1, A3).

---

## 10. The discipline: what I have to start saying no to

JEP 14 is explicit that the model only works if backporting stays minimal, and that this means
saying no. The requests I should expect, and the answer:

| Request | Answer |
| --- | --- |
| "Can you backport this new method to 12.x? We can't move to 13." | No. Wanting new API means you are actively developing; use the tip. |
| "It's a tiny fix, surely it can go into the tail." | No, unless it meets the L4 definition. Every backport costs test + release + risk on a train whose entire value is that nothing moves. |
| "Can you bump dependency X in the tail? It has a newer version." | Only if the newer version is itself a tail release and the bump closes a CVE. |
| "The tail is slow, can you backport the performance work?" | No. All stability-focused users can live without it (JEP 14, verbatim reasoning). |
| "12.x fails on JDK 25." | The 12.x tail is tested on JDK 17. Use the 13.x tip, which is baselined on 25. |

The last row is the one that requires the CI matrix change in L7 to be *stated publicly*, otherwise
the expectation of "works on everything" persists.

---

## 11. Rollout, smallest useful steps first

1. Publish this policy per project (wiki page or `README.md` section): baseline, current tip,
   which majors are maintained tails, and the L4 "critical" definition. Costs nothing, and most of
   the benefit of tip & tail is the *promise*, not the mechanics.
2. Add `JDK25` + `isLTS()` to `EJDK`. Stop using `JDK24` for libraries.
3. Decide the tail-representation question in `EProject` (§12) -- everything else in §8 depends on it.
4. Move applications to A1 one at a time, starting with the ones I alone operate
   (`peppol-practical`, `smp-query-webapp`, `bozoo`, `phoss-peppol-mcp-server`). This is the part
   that delivers "cutting edge" immediately and risks nothing.
5. Plan the JDK 25 wave for the library stack: pick the wave date, list the features that justify
   it (L2), fork the JDK 17 tails at the last 17-baselined release of each project.
6. Then, and only then, write `MainCheckTailDependencies` and `MainCheckJDKBaselineStable`.

Steps 1, 2 and 4 are independent and can happen now. Step 5 is the expensive one and should not
start before the L6 question below is answered.

---

## 12. Open decisions -- I want to settle these before writing code

1. **Compliance data on tails (L6).** Do externally mandated code lists / validation artefacts get
   backported into tails, or do I first split them into separately versioned artefacts so tails can
   consume new data without new code? The second is correct and more work. Which one, and for which
   projects?
2. **Tail representation in `EProject`.** Trains as a list inside `EProject`, or a separate
   `EProjectTail` enum? This decides the shape of every generator, checker and script in `meta`.
3. **Preview features in third-party-operated applications (A4).** My recommendation is no for
   `phoss-smp-webapp-*` and `phoss-ap-webapp`, yes for demos and internal tools. Agreed?
4. **How many tails do I promise, and for how long?** JEP 14 leaves this open. A defensible default:
   one tail generation (the previous LTS baseline), supported until the next wave lands -- i.e.
   roughly two years and never two tails at once. Accept, or promise more?
5. **`phoss-ap` on JDK 21.** Under A1 it should be on the current GA; under A5 it is
   third-party-operated and behaves like a library, so LTS-only (25). I read it as A5 -> move to 25
   at the wave, not to 26 now. Confirm?

---

## References

* [JEP 14: The Tip & Tail Model of Library Development](https://openjdk.org/jeps/14)
* [JDK Project -- current releases and schedule](https://openjdk.org/projects/jdk/)
* [DevelopmentProcess.md](DevelopmentProcess.md) -- how the work gets done today
* [CodingStyleguide.md](CodingStyleguide.md) -- how the code should look
* `src/main/java/com/helger/meta/project/EProject.java` -- the project registry this policy must be encoded in
