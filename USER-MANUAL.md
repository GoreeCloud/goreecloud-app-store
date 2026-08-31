# GoreeCloud App Store User Manual

## Current status

GoreeCloud App Store currently has **active-development native Android and Linux clients**. Neither is a production/Stable App Store release. `productionAcceptance` remains `false`.

The clients validate store UX, multi-user entitlement behavior, a shared application/service catalog, Glaze UI 2.1 adoption, package metadata boundaries, and integral GoreeCloud platform boundaries before real product distribution is enabled.

## Development identity fixtures

Both clients currently use local development identities such as **Standard demo**, **Administrator demo**, **Developer demo**, and **Signed out**.

These are not real GoreeCloud accounts, groups, or production roles. They demonstrate different entitled catalogs while production GoreeCloud Identity integration is pending. Changing the active fixture recalculates visible catalog entries. An item for which the active identity is not entitled is concealed from lists and search.

Administrator status does not grant an implicit bypass.

## Shared development catalog

Android and Linux consume the same normalized development fixture at `catalog/development-catalog.json`. It is explicitly non-authoritative.

Current audience labels, product versions, package identities, service endpoints, publication states, and package URLs are development data unless separately backed by approved release/service evidence.

## Android development client

Current Android development identity:

- application ID: `com.goreecloud.appstore.dev`
- label: **GoreeCloud App Store Dev**
- version: `0.1.4-dev`
- version code: `5`

The Android development certificate is non-production test material. The reserved future production application ID is `com.goreecloud.appstore` and requires a separate controlled signer.

Android sections are Discover, Apps, Services, Updates, and Library. Search only examines entries already entitled to the active identity. Package installation, service opening, production updates, and installed-library reconciliation are deliberately unavailable at this checkpoint.

## Linux development client

The Linux application is native Rust + GTK 4 + libadwaita software, not an Android wrapper.

Development identity:

- application ID: `com.goreecloud.AppStore.Development`
- binary: `goreecloud-app-store-dev`
- client version: `0.1.0-dev`

Repository CI produces two development package formats from the same exact source revision:

- `GoreeCloud-App-Store-0.1.0-dev1-amd64.deb`
- `GoreeCloud-App-Store-0.1.0-dev-x86_64.flatpak`

Treat both as test/development packages. They are not production-signed or Stable releases.

### Installing a validated development Debian package

From the directory containing the package:

```bash
sudo apt install ./GoreeCloud-App-Store-0.1.0-dev1-amd64.deb
```

This installs the development binary and desktop metadata. The package currently targets amd64 and depends on GTK 4.6+ and libadwaita 1.1+.

### Installing a validated development Flatpak bundle

A compatible Flatpak installation with the required GNOME runtime can install the CI bundle with:

```bash
flatpak install --user ./GoreeCloud-App-Store-0.1.0-dev-x86_64.flatpak
```

The development Flatpak application ID is `com.goreecloud.AppStore.Development` and the current bundle targets x86_64 with GNOME Platform 50.

The App Store itself does not silently install the GoreeCloud products it lists.

## Linux sections

### Discover

Shows all development catalog entries available to the active development identity and matching the search query.

### Applications

Shows only entitled application entries.

### Services

Shows only entitled service entries. A service may eventually have an installable Linux companion/client, but package possession does not authorize the underlying service.

### Linux packages

Shows the Debian and Flatpak publication state attached to each visible product. Current fixture artifacts are **Not published yet**.

A product Download button becomes eligible only when its metadata is `published` and includes all required delivery evidence: HTTPS URL, package identity, valid SHA-256, exact source revision, affirmative signing/provenance state, and affirmative Wardveil acceptance. Missing or incomplete evidence remains non-actionable.

The client currently opens an approved package download location rather than running `apt`, `dpkg`, or `flatpak install` automatically. Automatic/privileged package-manager control is not accepted at this checkpoint.

## Search

Search is applied after entitlement filtering. It matches visible catalog names, summaries, and categories and must not reveal concealed items.

## Share catalog

The Linux header includes **Share catalog**. It is currently disabled because the development catalog has no approved public HTTPS `shareUrl`.

When an approved sharing endpoint is introduced, it must expose only data classified for sharing and must not reveal concealed products, protected entitlement metadata, credentials, private service endpoints, or private library state. A shared catalog link will not substitute for backend authorization to download protected artifacts.

## Linux package status meanings

- **Not published yet** — no approved downloadable artifact is represented by this metadata.
- **Ready to download** — all client-side publication/integrity/provenance/Wardveil readiness fields are structurally complete. Production backend authorization still remains authoritative.
- **Published metadata incomplete** — publication was asserted but required evidence is missing or malformed, so the client fails closed.
- **Blocked** — the artifact must not be offered.
- **Withdrawn** — the artifact is no longer offered.

## App Store icon and AppStream boundary

An approved product-specific GoreeCloud App Store icon/logo has not yet been established through the canonical branding process. No placeholder is treated as official.

The development Linux metainfo XML is validated in CI, but Flatpak repository-level AppStream composition is intentionally deferred until the approved product icon exists. This is a known production-readiness blocker, not an omitted hidden feature.

## Glaze UI 2.1 status

The current required design-system target is **Glaze UI 2.1.0 Stable**, release `v2.1.0` at revision `c49113eb8b93c267613fdf1bbca1f814495acad7`.

Android maps Glaze through its Compose theme. Linux maps Glaze through `linux/resources/style.css` using GTK/libadwaita host-theme roles. Durable store content remains on solid surfaces; interaction/status chrome receives the Glaze-oriented treatment.

The App Store remains an **adoption candidate**, not a conformant consumer. Rendered acceptance, native accessibility, contrast/reduced-transparency behavior, 200% large text, keyboard/focus behavior, supported form factors, and representative platform/device acceptance remain pending. Glaze Motion is not consumed.

## Privacy and security behavior

Development analytics are off. Client-side entitlement filtering is not the future sole authorization boundary. Production artifact delivery and service launch must be re-authorized by the responsible backend.

Linux product download readiness fails closed on missing integrity/provenance/Wardveil fields. This does not mean Wardveil runtime integration is production connected; current catalog Wardveil values are development metadata and all fixture product artifacts remain unpublished.

## Current limitations

The App Store currently has no production GoreeCloud Identity login, authoritative production catalog, public sharing endpoint, published Linux product artifacts, automatic Linux install/update flow, Android APK delivery/install flow, production service launch, production update delivery, installed-library reconciliation, production signing, Wardveil runtime package verification, Privacy Shield runtime policy, Everkeep recovery, Mesh production event transport, approved App Store-specific official icon/logo, or accepted Glaze UI application conformance.

These limitations are deliberate fail-closed boundaries.

## Developer validation

Shared checks:

```bash
python3 scripts/validate_catalog.py
python3 scripts/validate_glaze_ui_adoption.py
```

Android:

```bash
gradle :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

Linux:

```bash
cargo test --manifest-path linux/Cargo.toml --locked
cargo clippy --manifest-path linux/Cargo.toml --locked --all-targets -- -D warnings
cargo build --manifest-path linux/Cargo.toml --locked --release
```

The committed `linux/Cargo.lock` is part of Linux build provenance. CI additionally validates Debian metadata, AppStream metainfo, Flatpak bundling, SHA-256 evidence, and exact source revision.

## Support boundary

Until a GoreeCloud App Store release is formally accepted, this manual describes development clients only. Production installation, upgrade, signing, account, recovery, package delivery, sharing, Glaze conformance, and service-access instructions will be added only when those behaviors exist and have accepted evidence.
