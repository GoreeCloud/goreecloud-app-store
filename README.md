# GoreeCloud App Store

GoreeCloud App Store is the first-party GoreeCloud catalog and delivery experience for applications and services an authenticated identity is authorized to access.

## Current Development line

The active successor line remains **Development**, not production or Stable:

- **Android:** original Kotlin / Jetpack Compose client, `0.1.5-dev` / versionCode `6`, package `com.goreecloud.appstore.dev`.
- **Linux:** original Rust / GTK 4 / libadwaita client, `0.1.0-dev`, with fail-closed unpublished Debian and Flatpak Development packaging.
- **Web:** original standards-based HTML/CSS/ES-module client, `0.1.0-dev`, using the same non-authoritative twelve-item Development catalog. Its current source/build contract requires exact-revision Web CI before accepted Web evidence is claimed.

The reserved future production Android package remains `com.goreecloud.appstore` and must use a separately controlled production signing lineage.

## Development catalog and entitlement model

The shared non-authoritative fixture contains twelve entries:

**Applications:** GoreeCloud Browser, Messenger, Location, Contacts, Tasks, Notes, Memos, Launcher, Keyboard, and Manager.

**Services:** Identity Center and Mesh Center.

Development identity fixtures exercise concealment before presentation/search. Administrator status has no implicit bypass; access requires an explicitly matching fixture audience. The Web client derives categories and search results only from the already-entitled subset. Signed-out fixtures see no protected entries.

Catalog membership and fixture audiences do not establish production entitlement taxonomy, package publication, deployment state, or release eligibility. Production delivery must become server-authoritative and backend-re-authorized before restricted metadata or artifacts are exposed.

## Platform clients

### Android

The Compose client provides Discover, Apps, Services, Updates, and Library navigation, search constrained to the entitled fixture catalog, canonical artwork, account fixtures, details, fail-closed unavailable delivery states, package/version/signing validation, and deterministic emulator interaction/rendered evidence. Representative physical-device, complete accessibility, Human Visual Excellence, production integration, signing, release, and Stable acceptance remain open.

### Linux

The Rust/GTK/libadwaita client consumes the same shared catalog and builds Development Debian/Flatpak artifacts. Debian installs the byte-exact approved App Store SVG. Flatpak retains that SVG as provenance and uses a build-time raster derivative for AppStream; CI validates the derivative and generated AppStream catalog/icon. Automatic catalog-product installation and supported production publication remain disabled/unaccepted.

### Web

The first-party Web Development client has no third-party runtime JavaScript and no analytics. It uses a local Content Security Policy source declaration, the shared Development catalog, pure entitlement logic, responsive/focus/reduced-motion/Forced-Colors source handling, and fail-closed Updates/Library/Install/Open states. The deterministic build copies the shared catalog and approved App Store identity into a static artifact rather than creating competing authorities.

This source bootstrap does **not** by itself establish rendered browser, assistive-technology, Human Visual Excellence, representative target environment, production hosting headers, deployment, or release acceptance.

## Security and platform boundaries

Package installation, application updates, library reconciliation, and service opening remain disabled until the responsible production contracts are accepted. The Android Development client does not request `REQUEST_INSTALL_PACKAGES`.

A future install/update path must use backend re-authorization, immutable release/artifact identity, digest and signing provenance, Wardveil verification, secure transport, platform/user authorization, result reconciliation, rollback/failure behavior, and auditable source/release evidence.

GoreeCloud Identity remains authoritative for identity and sessions; Wardveil Security for package trust; Privacy Shield for privacy policy and data use; Everkeep for recovery truth; GoreeCloud Mesh for coordination; GoreeCloud Manager for applicable operational visibility; and GLAZE UI for shared presentation requirements.

## GLAZE UI

Current source mappings target **GLAZE UI V1.1 / 1.1.0**, release tag `v1.1.0`, exact release revision `15cc76d2bcd4065552dc31c77145b63f34d9e7b2`.

Android and Web include inherited Light/Dark structure, explicit Deep Dark source mapping without automatic Deep Dark selection, V1.1 optical geometry/target rules, and bounded Deep Teal + Soft Amber non-semantic atmosphere. Linux maintains its native source mapping. Environmental/user-content sampling is disabled and Glaze Motion is not consumed.

`conformanceAccepted=false` and `productionAcceptance=false` remain explicit. Complete rendered/native/browser accessibility, supported form factors/target environments, Human Visual Excellence, production signing, deployment, and Stable qualification remain pending.

## Build and validation

Android:

```sh
gradle :app:testDebugUnitTest
gradle :app:lintDebug
gradle :app:assembleDebug
```

Linux:

```sh
cargo test --manifest-path linux/Cargo.toml --locked
cargo clippy --manifest-path linux/Cargo.toml --locked --all-targets -- -D warnings
cargo build --manifest-path linux/Cargo.toml --locked --release
```

Web:

```sh
node --test web/tests/*.test.mjs
python3 scripts/validate_web.py
python3 scripts/build_web.py --revision <40-character-git-revision>
```

Repository CI validates exact source identity for the application-specific workflows, shared catalog and branding provenance, GLAZE source contracts, client-specific tests/builds, and bounded Development artifacts. The central reusable Platform Contract workflow currently has a separately tracked pull-request revision-attribution defect and must not be described as exact-head evidence when it evaluates GitHub's synthetic PR merge revision.

## Production status

`productionAcceptance=false`.

No GitHub Release, production signing, supported production deployment, Release Candidate promotion, or Stable lifecycle status is established by Development source or CI alone.
