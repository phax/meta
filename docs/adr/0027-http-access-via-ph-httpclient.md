---
status: accepted
date: long-standing convention
decision-makers: Philip Helger
---

# HTTP access via `ph-httpclient` settings-based wrapper

## Context and Problem Statement

Many projects make outbound HTTP calls (SMP/SML lookups, remote schema/resource fetches,
web-service clients). Configuring Apache HttpClient directly at each call site duplicates
concerns — timeouts, TLS versions, proxy handling, retries, certificate revocation
checking, user agent — and makes it easy to ship insecure or inconsistent defaults.

## Considered Options

* **A shared `ph-httpclient` wrapper** over Apache HttpClient, driven by a
  `HttpClientSettings` object that each domain subclasses to impose its own safe defaults
  (e.g. `SMPHttpClientSettings`).
* **Use Apache HttpClient (or `java.net.http`) directly** at each call site.

## Decision Outcome

Chosen option: **`ph-httpclient` with settings objects**. HTTP behaviour is expressed as a
`HttpClientSettings` value that callers pass in, and domains subclass it to bake in
required, security-relevant defaults. For example, the Peppol SMP client's
`SMPHttpClientSettings` enforces TLS 1.2+, certificate revocation checking, a specific user
agent, and — per Peppol SMP spec — refuses to follow HTTP redirects (a DNS-spoofing
mitigation). Centralising this makes the safe configuration the default rather than
something each call site must remember.

### Consequences

* Good, because networking concerns (timeouts, TLS, proxy, retries, revocation) are
  configured once and reused, and domain-specific security defaults are enforced by
  construction.
* Good, because settings are explicit, testable values rather than scattered imperative
  setup.
* Bad, because it is another abstraction layer over Apache HttpClient that must track the
  underlying library's API and evolution.

## More Information

* Library: `ph-httpclient` (sibling repo) — `HttpClientSettings`.
* Example subclass: peppol-commons `peppol-smp-client`
  `com.helger.smpclient.httpclient.SMPHttpClientSettings` (no-redirect, TLS 1.2+,
  revocation checking).
