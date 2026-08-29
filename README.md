# GoreeCloud App Store

GoreeCloud App Store is the official GoreeCloud-owned application for discovering, obtaining, updating, and opening GoreeCloud applications and services that an authenticated identity is authorized to access.

This repository is an **original native GoreeCloud implementation**. Google Play, Apple App Store, and F-Droid are product inspirations only; their application code, architecture, branding, and UI are not the implementation foundation.

## Current checkpoint

Status: **Active Development — native Android bootstrap**  
Production acceptance: **false**

The current development branch establishes:

- a native Android/Jetpack Compose application shell;
- a Glaze UI 2.0.0 consumer target with tangible surfaces, capsule navigation, responsive Compose layout, 48dp-class interactive controls, and effects-free behavior;
- a per-session entitlement engine that filters the catalog before presentation;
- development-only multi-user identity fixtures behind an explicit `IdentityGateway` boundary;
- distinct application and service catalog entries;
- Discover, Apps, Services, Updates, and Library surfaces;
- explicit source boundaries for GoreeCloud Identity, Wardveil Security, Privacy Shield, Everkeep, and GoreeCloud Mesh;
- a machine-readable platform-integration record;
- unit tests that prevent implicit administrator bypass of catalog audience rules;
- Android CI for tests, lint, and debug APK assembly.

## Important acceptance boundary

The account switcher is **not** a production GoreeCloud Identity login. It uses development fixtures only so multi-user entitlement behavior can be built and tested while the application-facing GoreeCloud Identity runtime remains unaccepted.

The development catalog also does not assert production package identities, service endpoints, versions, or audience taxonomy. Those values must come from approved authoritative release, service, and Identity metadata.

Package download, APK installation, service launch, update delivery, Wardveil package verification, production Privacy Shield policy evaluation, Everkeep library recovery, and Mesh lifecycle transport are deliberately unavailable until their real integrations are implemented and validated. The UI must not imply otherwise.

## Authorization model

The App Store separates three decisions:

1. **Identity authentication and platform authority** — owned by GoreeCloud Identity.
2. **Catalog entitlement** — an App Store domain decision evaluated from approved Identity claims/policy inputs; an item not entitled to a user is concealed rather than merely disabled.
3. **Artifact/service authorization** — must be enforced again by the package or service delivery backend. Client-side filtering is defense in depth, not the security boundary.

No role receives an undocumented superuser bypass. Administrative access must be explicitly granted by policy.

## Build foundation

The bootstrap is pinned to current stable Android tooling as of August 29, 2026:

- Android Gradle Plugin 9.3.0
- Gradle 9.5.0
- JDK 17
- compileSdk 37
- targetSdk 36
- Kotlin / Compose compiler plugin 2.4.10
- Jetpack Compose BOM 2026.08.00

A Gradle wrapper is not yet committed. With JDK 17 and Gradle 9.5.0 installed:

```bash
gradle :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

CI installs the pinned Gradle distribution directly.

## Repository records

- `SPECIFICATIONS.md` — product and engineering requirements
- `ARCHITECTURE.md` — authority boundaries and runtime design
- `FEATURES.md` — implemented and planned capabilities
- `BENEFITS.md` — intended user/platform value
- `COMPETITIVE-OBJECTIVES.md` — inspiration translated into GoreeCloud-native objectives
- `USER-MANUAL.md` — current user/developer behavior and limitations
- `contracts/platform-integrations.json` — machine-readable current integration truth
- `app/src/main/assets/catalog/development-catalog.json` — non-authoritative development fixture catalog

## License

GNU Affero General Public License v3.0. See `LICENSE`.
