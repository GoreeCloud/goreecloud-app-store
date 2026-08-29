# GoreeCloud App Store Architecture

## Design principle

The store is a **policy-mediated catalog and delivery client**, not the authority for every platform decision it displays.

```text
GoreeCloud Identity
      │ authenticated actor + approved claims
      ▼
App Store entitlement service / policy boundary
      │ personalized authorized catalog
      ▼
Native App Store client
      ├── Discover / Apps / Services / Search
      ├── Updates / Library
      └── Product details + evidence presentation
             │
             ├── application artifact request ──► delivery service ──► Wardveil verification ──► Android installer
             └── service launch request ───────► service authorization ──► approved endpoint

Privacy Shield governs permitted data use.
Everkeep governs protection/recovery evidence.
GoreeCloud Mesh coordinates capabilities/events.
Glaze UI governs presentation/interaction semantics.
```

## Authority matrix

| Decision | Authority | App Store responsibility |
| --- | --- | --- |
| Who is acting? | GoreeCloud Identity | Consume validated session/claims |
| What catalog items may this user see? | App Store domain policy using approved Identity inputs | Evaluate and conceal non-entitled items |
| May this artifact be downloaded? | Delivery backend + applicable Identity/App Store policy | Request with current authorization; never rely on UI state |
| Is this package trusted/safe enough for install? | Wardveil Security | Present and enforce authoritative result |
| May catalog/search/library data be collected or used? | Privacy Shield | Minimize data and honor policy/consent |
| Is library/history state recoverable? | Everkeep | Preserve required state and present evidence truthfully |
| How are lifecycle/capability events coordinated? | GoreeCloud Mesh | Publish/consume minimized approved events |
| How is state represented and interacted with? | Glaze UI | Implement current consumer contract |

## Entitlement evaluation

The current pure `EntitlementEngine` is deliberately small. It accepts an `IdentitySession` plus `AccessRule` and returns only visible catalog items. It contains no implicit administrator override.

In production, normalized policy inputs should come from a server-authoritative catalog/entitlement boundary. The client may repeat the filter for defense in depth and offline-safe rendering, but the client is not the sole access-control enforcement point.

## Production catalog direction

A production catalog service should provide an authenticated snapshot containing:

- catalog revision and generation time;
- item metadata and lifecycle state;
- entitlement result or opaque policy binding appropriate for the caller;
- compatible releases;
- immutable artifact identifiers and digests;
- signing/provenance metadata;
- evidence references for platform systems;
- cache expiry and rollback/revocation information.

For privacy and security, a user should preferably receive only entitled entries rather than downloading a complete secret catalog and hiding items locally.

## Package-delivery boundary

No installer permission is requested in the bootstrap. Before Android installation is enabled, the implementation needs:

1. a protected artifact endpoint with backend re-authorization;
2. immutable artifact identity and SHA-256 digest;
3. application signing-certificate expectations/provenance;
4. Wardveil verification policy and fail-closed result handling;
5. download integrity checking;
6. explicit Android user consent for installation;
7. install/update result reconciliation;
8. recovery/rollback behavior and audit evidence;
9. package-install permission only when required by the accepted implementation.

## Service-launch boundary

A service catalog entry may expose an approved HTTPS/deep-link destination, but the service must enforce its own authentication and authorization. Redirects and external-app handoff must use an allowlisted policy rather than arbitrary catalog-provided URLs.

## Offline behavior

A future offline cache may show a previously authorized catalog only within a defined freshness window and must clearly distinguish cached state from current authority. Protected downloads, new service grants, or stale elevated access must not be inferred solely from cached authorization.

## Current bootstrap boundary

Implemented now: native Android shell, development catalog loader, development identity adapter, entitlement filtering, application/service views, explicit platform status, tests, CI.

Not connected now: production Identity, production catalog service, package download/install, service launch, updates, installed library, Wardveil runtime, Privacy Shield runtime, Everkeep runtime, Mesh runtime. `productionAcceptance` therefore remains `false`.
