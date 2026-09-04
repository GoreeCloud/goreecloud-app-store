# GoreeCloud App Store for Linux

This directory contains the original native Linux development client for GoreeCloud App Store.

## Current scope

- Rust application code.
- GTK 4 + libadwaita desktop interface mapped to the current GLAZE UI V1.1 / 1.1.0 Stable source contract.
- Shared twelve-item Development catalog from `../catalog/development-catalog.json`, consumed by both Android and Linux on this reconciliation line.
- Development entitlement fixtures matching the Android catalog visibility model.
- Debian package build path (`.deb`).
- Flatpak bundle build path using `org.gnome.Platform//50`.
- Fail-closed package actions: a product artifact is downloadable only when the catalog marks it published and supplies HTTPS location, SHA-256, exact source revision, signing evidence, and Wardveil acceptance.
- No automatic package installation or privileged package-manager invocation.
- Deep Teal + Soft Amber are bounded non-semantic V1.1 atmosphere; host semantic colors retain authority and no environmental content sampling or Glaze Motion is enabled.

The Linux client is Development-only. Production GoreeCloud Identity, server-authoritative catalog delivery, Wardveil runtime verification, Privacy Shield runtime policy, Everkeep recovery, Mesh lifecycle transport, production signing, representative Linux desktop acceptance, and Glaze UI application conformance remain unconnected or unaccepted.

## Build locally

Install Rust plus GTK 4 and libadwaita development packages, then:

```bash
cd linux
cargo test
cargo build --release
```

The development executable is `target/release/goreecloud-app-store-dev`.

## Debian development package

From the repository root:

```bash
./linux/packaging/debian/build-deb.sh
```

The script packages the exact locally built development binary and the shared catalog. CI records SHA-256 evidence for the resulting `.deb`.

## Flatpak development bundle

The repository manifest is `packaging/flatpak/com.goreecloud.AppStore.Development.yml` and targets GNOME Platform 50. CI stages the exact release binary, desktop metadata, AppStream metadata, and shared catalog into the manifest before creating the Development `.flatpak` bundle.

## Sharing and catalog publication

The UI contains a Share catalog action, but it remains disabled while the shared catalog's `shareUrl` is blank. A public URL may be enabled only when a real HTTPS catalog endpoint is established. The Development catalog must not fabricate public package URLs.

Each of the twelve application/service entries has Debian and Flatpak artifact slots. Until a real artifact is approved, those slots remain `unpublished` and the Linux UI displays `Not published yet`. A catalog listing is not package-publication, signing, Wardveil, or release evidence.
