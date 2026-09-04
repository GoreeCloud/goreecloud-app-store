# GoreeCloud App Store — Repository Specifications

## Status

- Product: GoreeCloud App Store
- Repository: `GoreeCloud/goreecloud-app-store`
- Lifecycle: Development
- Current candidate: `0.1.5-dev` / versionCode `6`
- Primary client: native Android / Kotlin / Jetpack Compose
- Development package: `com.goreecloud.appstore.dev`
- Reserved production package: `com.goreecloud.appstore`
- Production acceptance: false

This repository specification describes the current Development source candidate. Stable and production claims require separate acceptance evidence.

## Role

The App Store discovers, presents, eventually obtains/updates, manages, and opens GoreeCloud applications and services available to an authorized identity. It is initially first-party rather than a general third-party marketplace.

## Catalog and entitlement model

The Development catalog is explicitly non-authoritative. It contains twelve entries and uses taxonomy-neutral audience fixtures only to exercise filtering behavior. Non-entitled entries are concealed before list/search presentation. Administrator status provides no implicit authorization bypass.

Applications currently represented: Browser, Messenger, Location, Contacts, Tasks, Notes, Memos, Launcher, Keyboard, and Manager. Services currently represented: Identity Center and Mesh Center.

Production catalog delivery must be authenticated, versioned, rollback-aware, cache-bounded, server-authorized, and fail closed. Package identities, endpoints, immutable release IDs, and production release channels remain blank until authoritative metadata exists.

## Native Android architecture

- Kotlin and Jetpack Compose
- Android Gradle Plugin 9.3.0
- Gradle 9.5.0
- JDK 17
- compileSdk 37
- targetSdk 36
- minSdk 26
- Compose BOM 2026.08.00

The active Compose experience provides responsive store navigation, development account switching, search, catalog cards, details, and explicit unavailable states for Updates/Library rather than fabricating implementation.

## Branding

Every current catalog entry has an explicit Android VectorDrawable derived from an approved canonical asset in `GoreeCloud/goreecloud-branding-assets`. `BRANDING.md` pins the exact canonical path and Git blob for each item. Identity Center uses its reduced service identity, and Mesh Center uses its Interlace-derived service identity.

## GLAZE UI V1.1

The current required source target is GLAZE UI V1.1 / 1.1.0 at Stable release revision `15cc76d2bcd4065552dc31c77145b63f34d9e7b2`.

The source candidate provides inherited Light/Dark structural mappings, a separate Deep Dark source palette, V1.1 optical radius tiers, and bounded Deep Teal + Soft Amber non-semantic atmosphere primitives. Runtime Deep Dark policy, rendered/native accessibility, representative-device, supported-form-factor, and Human Visual Excellence acceptance remain pending. Glaze Motion is not consumed.

## Integral platform boundaries

GoreeCloud Identity: source boundary exists; production application authentication/authorization is not connected.

Wardveil Security: package trust/verification boundary exists; no install/update is enabled.

Privacy Shield: the Development client has no analytics; runtime policy/consent acceptance remains pending.

Everkeep: library/history recovery boundary is defined; recovery acceptance remains pending.

GoreeCloud Mesh: catalog/lifecycle coordination boundary is defined; production transport remains pending.

## Delivery and installation

Before installation/update may be enabled, the exact release path must provide backend re-authorization, immutable artifact identity, approved digest verification, expected signing identity, Wardveil acceptance, secure transport, Android user authorization, install/update reconciliation, rollback/failure behavior, and auditable linkage to approved source/release evidence.

The Development app intentionally does not request Android package-install authority.

## CI and evidence

Pull-request CI validates:

1. exact checked-out source revision;
2. canonical branding/provenance and complete catalog artwork mapping;
3. GLAZE UI V1.1 source mapping and non-production status;
4. unit tests and compiled Android instrumentation tests;
5. Android lint;
6. Development APK assembly;
7. package/version/label identity;
8. Development signing-certificate identity;
9. SHA-256 artifact evidence.

Passing CI validates that Development artifact only. It does not establish rendered acceptance, physical-device acceptance, production signing, deployment, or Stable status.

## Promotion gates

Stable/production promotion requires successful CI plus accepted production Identity integration, authenticated catalog delivery, Wardveil package verification, Privacy Shield acceptance, Everkeep recovery evidence, applicable Mesh integration, current GLAZE UI application conformance, supported Android/runtime/form-factor/accessibility validation, protected production signing and key recovery, rollback validation, release notes, and reconciled documentation.
