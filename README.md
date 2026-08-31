# GoreeCloud App Store

GoreeCloud App Store is the official GoreeCloud-owned application for discovering, obtaining, updating, and opening GoreeCloud applications and services that an authenticated identity is authorized to access.

This repository is an **original native GoreeCloud implementation**. Google Play, Apple App Store, and F-Droid are product inspirations only; their application code, architecture, branding, and UI are not the implementation foundation.

## Current checkpoint

Status: **Active Development — native Android application**  
Production acceptance: **false**  
Glaze UI status: **2.1.0 Stable adoption candidate; application conformance not accepted**

The current development branch establishes:

- a native Android/Jetpack Compose store application;
- a repository-local Glaze UI 2.1.0 Stable adoption contract pinned to release `v2.1.0` / revision `c49113eb8b93c267613fdf1bbca1f814495acad7`;
- a native Glaze 2.1 material-role mapping that keeps content on Canvas/Surface roles and maps interaction/status chrome to Glaze roles without claiming downstream conformance;
- a persistent App Store/account header and Material bottom navigation;
- a per-session entitlement engine that filters the catalog before presentation;
- development-only multi-user identity fixtures behind an explicit `IdentityGateway` boundary;
- distinct application and service catalog entries;
- Discover, Apps, Services, Updates, and Library surfaces;
- search constrained to the already-entitled catalog;
- store-style application/service cards and product-detail bottom sheets;
- approved first-party artwork derivatives tied to canonical assets in `GoreeCloud/goreecloud-branding-assets`;
- development-status diagnostics separated from ordinary catalog browsing;
- compact-width safeguards for account controls, catalog headings, item metadata, detail metadata, and platform-status rows;
- explicit source boundaries for GoreeCloud Identity, Wardveil Security, Privacy Shield, Everkeep, and GoreeCloud Mesh;
- machine-readable platform-integration and Glaze adoption records;
- unit tests that prevent implicit administrator bypass of catalog audience rules;
- exact-source Android CI for Glaze adoption validation, tests, lint, APK assembly, package/version/application-label validation, signing-certificate verification, SHA-256 evidence, and development artifact publication.

The interface is being iterated using real-device screenshots from Android development builds. Earlier review removed oversized internal diagnostics from normal browsing, replaced placeholder artwork/navigation glyphs, fixed tab/account scroll behavior, corrected pathological compact-width wrapping, and moved item details to store-style sheets. The complete follow-up screenshot set for `0.1.2-dev` confirmed that the stable `.dev` update lineage and multi-user catalog behavior were functioning, while identifying remaining presentation pressure in the header and catalog cards.

The `0.1.3-dev` iteration introduced compact account labels, preserved the full **Development** environment indicator, reduced Discover hero height, added section-specific search prompts, removed redundant release-channel capsules from list cards, and fixed the Development status title/Close affordance while diagnostics scroll. The current `0.1.4-dev` line adds the Glaze UI 2.1 adoption contract and current-Stable material mapping without upgrading the application to a conformance or production-ready state by declaration.

## Glaze UI 2.1 adoption

Glaze UI **2.1.0** is the current Stable design-system target for GoreeCloud-controlled user-facing applications. The App Store pins the reviewed Stable release at:

- release tag: `v2.1.0`;
- release revision: `c49113eb8b93c267613fdf1bbca1f814495acad7`;
- repository-local evidence: `contracts/glaze-ui-adoption.json` and `docs/GLAZE_UI_ADOPTION.md`.

The App Store is an **adoption candidate**, not an aligned/current-conformant consumer. Existing 2.0-era App Store work is retained only as migration history. Rendered acceptance, native accessibility acceptance, Deep Dark, Reduced Transparency/Solid behavior, Increased Contrast, 200% Large Text, Touch Assistance, supported-form-factor validation, and representative physical-device evidence remain pending.

`scripts/validate_glaze_ui_adoption.py` fails closed if the current target, release anchor, native mapping, application version, platform integration record, or acceptance boundary drifts. CI runs that contract before Android build validation and includes its output with build evidence.

## Development APK identity

CI/debug builds install as `com.goreecloud.appstore.dev` with the Android label **GoreeCloud App Store Dev**. They are signed with one repository-managed development-only certificate so successive development builds can update each other instead of receiving a new ephemeral Android debug identity from every CI runner.

The current development version line is `0.1.4-dev` with version code `5`.

The reserved future production application ID remains `com.goreecloud.appstore`. The development signing key MUST NOT sign that production package or any artifact represented as production-approved or Stable. See `development/signing/README.md` for the explicit boundary and certificate fingerprint.

Older bootstrap APKs used `com.goreecloud.appstore` with ephemeral runner-generated debug certificates. Those builds cannot be upgraded in place by later CI APKs and should be removed from test devices before using the new development package.

## Important acceptance boundary

The account switcher is **not** a production GoreeCloud Identity login. It uses development fixtures only so multi-user entitlement behavior can be built and tested while the application-facing GoreeCloud Identity runtime remains unaccepted.

The development catalog also does not assert production package identities, service endpoints, versions, or audience taxonomy. Those values must come from approved authoritative release, service, and Identity metadata.

Package download, APK installation, service launch, production update delivery, Wardveil package-verification acceptance, production Privacy Shield policy evaluation, Everkeep library recovery, and Mesh lifecycle transport are deliberately unavailable until their real integrations are implemented and validated. The UI must not imply otherwise.

## Authorization model

The App Store separates three decisions:

1. **Identity authentication and platform authority** — owned by GoreeCloud Identity.
2. **Catalog entitlement** — an App Store domain decision evaluated from approved Identity claims/policy inputs; an item not entitled to a user is concealed rather than merely disabled.
3. **Artifact/service authorization** — must be enforced again by the package or service delivery backend. Client-side filtering is defense in depth, not the security boundary.

No role receives an undocumented superuser bypass. Administrative access must be explicitly granted by policy.

## Branding contract

`GoreeCloud/goreecloud-branding-assets` is the canonical branding repository. Android VectorDrawable copies in this repository are consumer derivatives only and do not become new branding authorities.

See `BRANDING.md` for the exact canonical asset and Git-blob mappings currently consumed for Browser, Messenger, Location, Identity, and Manager artwork.

An approved product-specific GoreeCloud App Store icon/logo has **not yet been established**. Under GoreeCloud repository policy, that is an outstanding branding and production-readiness defect. The official App Store identity must be approved through the canonical branding path first, then stored in this application repository and used by manifests, launchers, release packaging, documentation, and other applicable surfaces. No placeholder is represented as official identity.

## Build foundation

The application is pinned to current stable Android tooling as of August 29, 2026:

- Android Gradle Plugin 9.3.0
- Gradle 9.5.0
- JDK 17
- compileSdk 37
- targetSdk 36
- minSdk 26
- Kotlin / Compose compiler plugin 2.4.10
- Jetpack Compose BOM 2026.08.00

A Gradle wrapper is not yet committed. With JDK 17 and Gradle 9.5.0 installed:

```bash
python3 scripts/validate_glaze_ui_adoption.py
gradle :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

CI checks out and records the exact source revision, validates the Glaze UI 2.1 adoption contract, validates the generated development APK package/version/application label and development signing certificate, generates SHA-256 evidence, and publishes the development APK/evidence bundle.

## Repository records

- `SPECIFICATIONS.md` — product and engineering requirements
- `ARCHITECTURE.md` — authority boundaries and runtime design
- `FEATURES.md` — implemented and planned capabilities
- `BENEFITS.md` — intended user/platform value
- `COMPETITIVE-OBJECTIVES.md` — inspiration translated into GoreeCloud-native objectives
- `BRANDING.md` — canonical branding-consumer mappings and current App Store identity gap
- `USER-MANUAL.md` — current user/developer behavior and limitations
- `docs/GLAZE_UI_ADOPTION.md` — current Stable 2.1 repository-local adoption mapping and acceptance boundary
- `development/signing/README.md` — development package/signing boundary
- `contracts/glaze-ui-adoption.json` — machine-readable Glaze UI 2.1 adoption evidence
- `contracts/platform-integrations.json` — machine-readable current integration truth
- `app/src/main/assets/catalog/development-catalog.json` — non-authoritative development fixture catalog

## License

GNU Affero General Public License v3.0. See `LICENSE`.
