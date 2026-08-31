# GoreeCloud App Store

GoreeCloud App Store is the official GoreeCloud-owned application for discovering, obtaining, updating, and opening GoreeCloud applications and services an authenticated identity is authorized to access.

This repository contains **original native GoreeCloud clients for Android and Linux**. Google Play, Apple App Store, and F-Droid inform product goals only; their code, architecture, branding, and UI are not the implementation foundation.

## Current checkpoint

Status: **Active Development — native Android and Linux applications**  
Production acceptance: **false**  
Glaze UI status: **2.1.0 Stable adoption candidate; application conformance not accepted**

The repository now establishes:

- a native Android client using Kotlin and Jetpack Compose;
- a native Linux desktop client using Rust, GTK 4, and libadwaita;
- one shared, schema-validated development catalog at `catalog/development-catalog.json`;
- entitlement filtering before presentation, with no implicit administrator bypass;
- development-only identity fixtures for multi-user behavior while production GoreeCloud Identity remains unconnected;
- distinct application and service catalog entries;
- platform-specific artifact metadata, including Debian and Flatpak slots for Linux;
- Linux Discover, Applications, Services, Linux packages, search, identity switching, and catalog-sharing surfaces;
- fail-closed Linux download readiness: a package action is enabled only for published HTTPS metadata carrying package identity, SHA-256, exact source revision, signing evidence, and Wardveil acceptance;
- exact-source Android and Linux CI with explicit development acceptance boundaries;
- Glaze UI 2.1 native mappings for Android and Linux without a conformance claim;
- source boundaries for GoreeCloud Identity, Wardveil Security, Privacy Shield, Everkeep, and GoreeCloud Mesh.

All current product `.deb` and Flatpak entries in the development catalog are deliberately **unpublished**. The Linux store shows truthful negative states rather than invented download links.

## Native clients

### Android

Development builds install as `com.goreecloud.appstore.dev` with the label **GoreeCloud App Store Dev**. The current Android development line is `0.1.4-dev` / version code `5`. A repository-managed development-only certificate provides a stable test update lineage. The reserved future production application ID remains `com.goreecloud.appstore` and must use separate controlled production signing.

Android package download/install, service launch, production update delivery, and installed-library reconciliation remain unavailable until their authoritative integrations are implemented and accepted.

### Linux

The Linux client is original native software written in Rust with GTK 4 and libadwaita.

Development identity:

- application ID: `com.goreecloud.AppStore.Development`
- binary: `goreecloud-app-store-dev`
- client version: `0.1.0-dev`
- Debian development package: `goreecloud-app-store-dev`, version `0.1.0~dev1`, amd64
- Flatpak development application: `com.goreecloud.AppStore.Development`, x86_64, GNOME Platform 50

Linux CI produces a development `.deb` and a development Flatpak bundle from the same exact source revision. Rust dependency resolution is pinned by the committed `linux/Cargo.lock` and builds use `--locked`.

The Linux client does **not** automatically install product packages. When authoritative product metadata eventually satisfies all delivery gates, the current development behavior can expose an approved download action; installation/update authority remains a separate future acceptance area.

The **Share catalog** control remains disabled while the catalog has no approved public HTTPS share URL. A public/shared catalog must not bypass user entitlement or backend delivery authorization.

## Shared catalog and authorization

The App Store separates three decisions:

1. **Identity authentication and platform authority** — owned by GoreeCloud Identity.
2. **Catalog entitlement** — an App Store domain decision using approved Identity inputs. Non-entitled items are concealed, not merely disabled.
3. **Artifact/service authorization** — enforced again by the responsible delivery backend or service. Client rendering is defense in depth, not the security boundary.

The development catalog is non-authoritative fixture data. Its audience labels, package identities, service endpoints, versions, and artifact publication states do not establish production GoreeCloud policy.

## Linux package publication contract

A Linux catalog artifact is not download-ready merely because a filename exists. The development client requires all of the following before enabling the action:

- artifact status `published`;
- HTTPS download location;
- package/application identity;
- SHA-256 digest;
- exact 40-character source revision;
- signing/provenance evidence;
- accepted Wardveil result.

Missing, malformed, stale, blocked, withdrawn, or incomplete metadata fails closed.

## Glaze UI 2.1 adoption

Glaze UI **2.1.0 Stable** is the current design-system target, pinned to release `v2.1.0` at revision `c49113eb8b93c267613fdf1bbca1f814495acad7`.

Android maps the contract through `app/src/main/java/com/goreecloud/appstore/ui/GlazeTheme.kt`. Linux maps it through `linux/resources/style.css` using GTK/libadwaita theme roles. Both mappings keep durable content on solid surfaces and reserve Glaze treatment for interaction/status roles.

This remains **source-level adoption evidence only**. Rendered acceptance, native accessibility acceptance, supported form factors, high-contrast/reduced-transparency behavior, large-text behavior, representative device/desktop acceptance, and other applicable Glaze gates remain pending. `conformanceAccepted` and `productionEligible` remain false.

## Branding boundary

`GoreeCloud/goreecloud-branding-assets` is the canonical brand authority. An approved product-specific GoreeCloud App Store icon/logo has not yet been established.

No placeholder is represented as official identity. This intentionally blocks production release and also means Flatpak repository-level AppStream composition is deferred; the development metainfo file is still validated independently in CI.

## Build foundations

Android development uses JDK 17, Gradle 9.5.0, Android Gradle Plugin 9.3.0, compileSdk 37, targetSdk 36, minSdk 26, Kotlin/Compose compiler plugin 2.4.10, and Jetpack Compose BOM 2026.08.00.

Linux development uses Rust 1.90.0, GTK 4, libadwaita, Debian packaging tools, Flatpak Builder, a committed Cargo lockfile, and GNOME Platform/SDK 50 for the Flatpak development bundle. The native Debian compatibility checkpoint is exercised on Ubuntu 22.04-class libraries (GTK 4.6 / libadwaita 1.1 or newer).

Repository validation entry points include:

```bash
python3 scripts/validate_catalog.py
python3 scripts/validate_glaze_ui_adoption.py
gradle :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
cargo test --manifest-path linux/Cargo.toml --locked
cargo clippy --manifest-path linux/Cargo.toml --locked --all-targets -- -D warnings
cargo build --manifest-path linux/Cargo.toml --locked --release
```

## Important production boundary

Production GoreeCloud Identity, authoritative catalog delivery, protected artifact delivery, Wardveil runtime verification, Privacy Shield runtime policy, Everkeep recovery, GoreeCloud Mesh production transport, production signing, automatic package installation, approved App Store product identity, and accepted Glaze UI conformance are not established. `productionAcceptance` remains `false`.

## Repository records

- `SPECIFICATIONS.md` — product and engineering requirements
- `ARCHITECTURE.md` — authority boundaries and runtime design
- `FEATURES.md` — implemented and planned capabilities
- `BENEFITS.md` — intended user/platform value
- `COMPETITIVE-OBJECTIVES.md` — inspiration translated into GoreeCloud-native objectives
- `BRANDING.md` — canonical branding-consumer mappings and App Store identity gap
- `USER-MANUAL.md` — development user/developer behavior and limitations
- `docs/GLAZE_UI_ADOPTION.md` — Android + Linux Glaze UI 2.1 adoption mapping
- `contracts/glaze-ui-adoption.json` — machine-readable Glaze adoption evidence
- `contracts/platform-integrations.json` — current multi-client platform-integration truth
- `contracts/linux-distribution.json` — Linux development distribution contract
- `contracts/store-catalog.schema.json` — shared catalog schema
- `catalog/development-catalog.json` — non-authoritative shared development fixture

## License

GNU Affero General Public License v3.0. See `LICENSE`.
