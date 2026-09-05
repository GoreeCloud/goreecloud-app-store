# GoreeCloud App Store — Repository Specifications

## Status

- Product: GoreeCloud App Store
- Repository: `GoreeCloud/goreecloud-app-store`
- Lifecycle: Development
- Android client: `0.1.5-dev` / versionCode `6` / Kotlin + Jetpack Compose
- Linux client: `0.1.0-dev` / Rust + GTK 4 + libadwaita
- Web client: `0.1.0-dev` / standards HTML + CSS + ES modules
- Development Android package: `com.goreecloud.appstore.dev`
- Reserved production Android package: `com.goreecloud.appstore`
- Production acceptance: false

This repository specification describes the current Development successor line. Client source/build evidence is platform-specific. Release Candidate, Stable, and production claims require separate applicable acceptance evidence.

## Role

The App Store discovers, presents, eventually obtains/updates, manages, and opens GoreeCloud applications and services available to an authorized identity. It is initially first-party rather than a general third-party marketplace.

## Shared catalog and entitlement model

The Development catalog is explicitly non-authoritative. It contains twelve entries and uses taxonomy-neutral audience fixtures only to exercise filtering behavior. Non-entitled entries are concealed before list/search/category presentation. Administrator status provides no implicit authorization bypass.

Applications represented: Browser, Messenger, Location, Contacts, Tasks, Notes, Memos, Launcher, Keyboard, and Manager. Services represented: Identity Center and Mesh Center.

Production catalog delivery must be authenticated, versioned, rollback-aware, cache-bounded, server-authorized, and fail closed. Package identities, endpoints, immutable release IDs, and production release channels remain blank until authoritative metadata exists.

## Android Development architecture

- Kotlin and Jetpack Compose
- Android Gradle Plugin 9.3.0
- Gradle 9.5.0
- JDK 17
- compileSdk 37
- targetSdk 36
- minSdk 26
- Compose BOM 2026.08.00

The active Compose experience provides responsive store navigation, development account switching, search, category discovery, catalog cards, details, and explicit unavailable Updates/Library/delivery states rather than fabricating implementation.

## Linux Development architecture

The Linux client is original Rust software using GTK 4 + libadwaita. It consumes `catalog/development-catalog.json` and retains its own `0.1.0-dev` version history. Development CI validates the locked Rust graph, format/test/clippy/release build, Debian construction, AppStream metadata, Flatpak composition, and evidence artifacts.

The approved App Store SVG remains canonical provenance. Debian installs it directly. Flatpak retains it as provenance and generates a bounded PNG derivative for desktop/AppStream composition. Catalog-product package publication and automatic installation remain unavailable until authoritative release metadata and platform-system acceptance exist.

## Web Development architecture

The Web client is original standards-based HTML/CSS/ES-module software with no third-party runtime JavaScript dependency. It consumes the same shared Development catalog at build time, uses pure fixture entitlement logic before discovery/search/category filtering, and fails closed when the catalog contract cannot be loaded.

The static Development build copies reviewed source files, the shared catalog, and the byte-exact approved App Store SVG into `.artifacts/web/site` and writes source-bound build metadata. Install, update, service launch, and recoverable Library actions remain unavailable. No analytics, persistent search history, or personalization telemetry is enabled.

Exact-revision Web source/unit/static-build/local-HTTP smoke CI is required before the Web bootstrap is accepted as Development evidence. Rendered browser, assistive-technology, Human Visual Excellence, representative target-environment, production hosting/header, deployment, release, and Stable acceptance remain separate pending gates.

## Branding

Every current catalog entry has an explicit reviewed Android VectorDrawable derived from an approved canonical asset in `GoreeCloud/goreecloud-branding-assets`. `BRANDING.md` pins canonical paths/blobs. Identity Center uses its reduced service identity and Mesh Center its Interlace-derived service identity.

The Web build does not create a new branding source: it copies the repository's byte-exact approved App Store SVG source into the generated site and verifies canonical Git blob `05c66a2a4c8edcc194183bb8ffb10ca90d8eaeef`.

## GLAZE UI V1.1

The current required source target is GLAZE UI V1.1 / 1.1.0 at Stable release revision `15cc76d2bcd4065552dc31c77145b63f34d9e7b2`.

Android and Web provide inherited Light/Dark source mappings, separate Deep Dark source values without automatic runtime selection, V1.1 optical/interaction geometry, and bounded Deep Teal + Soft Amber non-semantic atmosphere primitives. Linux has its native source mapping. Environmental/user-content sampling is disabled and Glaze Motion is not consumed.

Runtime Deep Dark policy, complete rendered/native/browser accessibility, representative-device/target-environment, supported-form-factor, and Human Visual Excellence acceptance remain pending. `conformanceAccepted=false` and `productionEligible=false` remain explicit.

## Integral platform boundaries

- GoreeCloud Identity: Development fixture boundaries exist; production authentication, sessions, authorization, disablement/revocation, and server-authoritative entitled-catalog delivery are not connected.
- Wardveil Security: package trust/verification boundary exists; no production install/update is enabled.
- Privacy Shield: Development clients have no analytics; runtime policy/consent/retention acceptance remains pending.
- Everkeep: library/history recovery requirements are documented; recovery acceptance remains pending.
- GoreeCloud Mesh: catalog/lifecycle coordination boundary exists; production transport/registration remains pending.
- GoreeCloud Manager: applicable operational visibility and administrative integration remain pending.

## Delivery and installation

Before installation/update may be enabled, the exact release path must provide backend re-authorization, immutable artifact identity, approved digest verification, expected signing identity, Wardveil acceptance, secure transport, platform/user authorization, install/update reconciliation, rollback/failure behavior, and auditable linkage to approved source/release evidence.

The Android Development app intentionally does not request package-install authority. Linux catalog artifacts remain unpublished. The Web client exposes no install or service-launch authority.

## Current verified pre-Web integration checkpoint

At exact branch head `3a5b6c01e8b86cc8789cd3b547e11da3f9df35d2`:

- Android Development run `33881482655` passed exact-head validation and produced artifact `9940119603`, archive digest `sha256:5124aa3f18b7d9fb6d297a1f1d502058df142cbf0ae0d426b506c7a144953830`.
- Android rendered run `33881482896` passed exact-head Compose/emulator acceptance and produced artifact `9940291603`, digest `sha256:f07c0bfe20e6a0200a664e1ec1dd7f825aaba5985c48c8134cfc9fefe3b1a681`.
- Linux Development run `33881482849` passed exact-head native/package/AppStream validation and produced artifact `9940338423`, digest `sha256:bb721f4bb415bf510f5295b924e7f50f611101a9772c65587c52f62ed0673ca2`.
- Platform Contract run `33881483682` passed only against GitHub synthetic PR merge revision `1a64e0b0ddfec533365cade88824f4b4af4f5e75`, not the branch head, due the separately tracked central revision-attribution defect. It must not be described as exact-head Platform Contract evidence.

Any source change after this checkpoint requires fresh applicable exact-revision client validation before the new head is accepted.

## Promotion gates

Stable/production promotion requires successful applicable CI plus accepted production Identity integration, authenticated catalog delivery, Wardveil package verification, Privacy Shield acceptance, Everkeep recovery evidence, applicable Mesh/Manager integration, current GLAZE UI application conformance, supported Android/Linux/Web runtime/form-factor/accessibility/target-environment validation, protected production signing/key recovery where applicable, Android/Linux delivery and rollback validation, Web production hosting/deployment acceptance, release notes, and reconciled documentation.
