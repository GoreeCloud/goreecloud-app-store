# GoreeCloud App Store

GoreeCloud App Store is the first-party GoreeCloud catalog and delivery client for applications and services an authenticated identity is authorized to access.

## Current development candidate

The active successor development line is **0.1.5-dev** (`versionCode 6`) for Android. It is a Development candidate, not a production or Stable release.

The Android client is written in Kotlin with Jetpack Compose and currently provides:

- Discover, Apps, Services, Updates, and Library navigation;
- search constrained to the already-entitled catalog;
- development-only identity fixtures for entitlement testing;
- concealment of non-entitled catalog entries before presentation/search;
- product/service detail sheets;
- canonical first-party GoreeCloud artwork for every current catalog item;
- an explicit Development status surface showing integration boundaries;
- exact package/version/signing validation in CI;
- GLAZE UI V1.1 / 1.1.0 source mapping with production/conformance acceptance kept false.

## Development catalog

The current non-authoritative fixture contains twelve entries:

**Applications:** GoreeCloud Browser, Messenger, Location, Contacts, Tasks, Notes, Memos, Launcher, Keyboard, and Manager.

**Services:** Identity Center and Mesh Center.

Catalog membership and fixture audiences are development inputs only. They do not establish production entitlement taxonomy, package publication, deployment state, or release eligibility.

## Security and delivery boundary

Package installation, application updates, library reconciliation, and service opening remain disabled until the responsible production contracts are accepted. The client does not request `REQUEST_INSTALL_PACKAGES` in this Development state.

A future install/update path must use backend re-authorization, immutable release/artifact identity, digest and signing provenance, Wardveil verification, secure transport, Android user authorization, result reconciliation, rollback/failure behavior, and auditable source/release evidence.

GoreeCloud Identity remains authoritative for identity and sessions; Wardveil Security for package trust; Privacy Shield for privacy policy and data use; Everkeep for recovery truth; GoreeCloud Mesh for coordination; and GLAZE UI for shared presentation requirements.

## GLAZE UI

The current source mapping targets **GLAZE UI V1.1 / 1.1.0**, release tag `v1.1.0`, exact release revision `15cc76d2bcd4065552dc31c77145b63f34d9e7b2`.

The App Store implements inherited Light/Dark structure, an explicit Deep Dark source palette, V1.1 optical geometry, and bounded Deep Teal + Soft Amber atmosphere primitives. Atmosphere is non-semantic and does not observe user content or establish security, privacy, identity, recovery, authorization, or availability state.

Application-specific rendered acceptance, native accessibility, supported form factors, representative physical devices, Human Visual Excellence, production signing, deployment, and Stable qualification remain pending.

## Android identities

- Development package: `com.goreecloud.appstore.dev`
- Reserved production package: `com.goreecloud.appstore`
- Development label: `GoreeCloud App Store Dev`
- Minimum Android API: 26
- Target API: 36
- Compile API: 37

The repository-managed Development signing key is test material only and must never sign a production/Stable artifact.

## Build

```sh
gradle :app:testDebugUnitTest
gradle :app:lintDebug
gradle :app:assembleDebug
```

Repository CI additionally validates exact source revision, branding provenance, the GLAZE UI V1.1 source contract, package/version identity, Development signing identity, and artifact SHA-256 evidence.

## Production status

`productionAcceptance=false`.

No GitHub Release, production signing, production deployment, or Stable lifecycle status is established by Development source or CI alone.
