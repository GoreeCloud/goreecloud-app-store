# GoreeCloud App Store Features

## Implemented development capabilities

### Shared product model

- One schema-validated development catalog shared by Android and Linux.
- Application and service item types.
- Platform-specific artifact metadata.
- Development multi-user identity fixtures.
- Entitlement filtering before presentation.
- No implicit administrator bypass.
- Search constrained to the already-entitled catalog.
- Explicit unpublished/blocked/withdrawn/incomplete artifact states.

### Native Android client

- Kotlin + Jetpack Compose application shell.
- Discover, Apps, Services, Updates, and Library navigation.
- Store-style cards and product detail surfaces.
- Canonical first-party artwork derivatives where approved assets exist.
- Development status/integration diagnostics.
- Stable `.dev` development package identity and development signing certificate.
- Android tests, lint, exact-source build validation, signing verification, and APK evidence publication.

### Native Linux client

- Original Rust + GTK 4 + libadwaita desktop application.
- Discover, Applications, Services, and Linux packages navigation.
- Desktop search and development identity selector.
- Debian and Flatpak publication rows for every development catalog item.
- Disabled **Share catalog** control until an approved public HTTPS catalog URL exists.
- Download readiness that requires published HTTPS metadata, package identity, SHA-256, exact source revision, signing evidence, and Wardveil acceptance.
- No automatic product installation or privileged package-manager execution.
- Development `.deb` packaging for amd64.
- Development Flatpak bundle for x86_64 / GNOME Platform 50.
- AppStream metainfo validation.
- Committed `linux/Cargo.lock` and `--locked` Rust builds.
- Rust format, unit tests, strict Clippy, release build, package metadata checks, SHA-256 evidence, and exact-source artifact publication in Linux CI.

### Glaze UI and platform boundaries

- Glaze UI 2.1.0 Stable adoption contract.
- Android native mapping through Compose Material roles.
- Linux native mapping through GTK/libadwaita host-theme roles.
- Explicit source boundaries for GoreeCloud Identity, Wardveil Security, Privacy Shield, Everkeep, and GoreeCloud Mesh.
- Development analytics disabled.
- Glaze conformance and production acceptance remain false.

## Current deliberate limitations

- No production GoreeCloud Identity login/session integration.
- No server-authoritative production catalog.
- No published GoreeCloud product `.deb` or Flatpak artifacts in the fixture catalog.
- No Android product APK delivery/install flow.
- No automatic Linux package installation/update flow.
- No production service launch.
- No installed Library reconciliation/recovery.
- No Wardveil runtime package-verification integration.
- No Privacy Shield runtime policy integration.
- No Everkeep recovery acceptance.
- No Mesh production lifecycle transport.
- No production package signing.
- No approved App Store-specific icon/logo.
- Flatpak repository-level AppStream composition is deferred until the approved product icon exists.
- No accepted rendered/native accessibility Glaze UI conformance.

## Next functional milestones

- Production GoreeCloud Identity OIDC/session adapter and account switching.
- Server-authoritative entitlement/catalog API with authenticated snapshots, expiry, rollback, and revocation semantics.
- Approved public/shareable catalog endpoint with Privacy Shield and entitlement-safe metadata classification.
- GoreeCloud application/service release ingestion supporting real Linux `.deb` and Flatpak artifacts.
- Immutable artifact IDs, checksums, signing/provenance, and Wardveil verification evidence.
- Protected delivery re-authorization.
- Linux download manager with integrity recheck and explicit handoff to approved installation UX.
- Platform-appropriate Debian/Flatpak install/update/remove reconciliation after authorization and Wardveil acceptance are production-ready.
- Android secure APK delivery and explicit installation consent.
- Update discovery, release notes, staged downloads, failure recovery, rollback, and signing-key-change warnings.
- Installed Library scoped by identity/device and Everkeep protection/recovery evidence.
- Approved service endpoint/deep-link launch with service-side authorization.
- Privacy Shield policies for search/history/recommendations/diagnostics/sharing.
- GoreeCloud Mesh lifecycle/capability events where approved.
- Rich product pages: screenshots, changelog, source/license, permissions, compatibility, privacy facts, security/provenance, continuity, support, and package architecture.
- Categories, collections, editorial surfaces, recommendations, save-for-later, and notifications where privacy policy permits.
- Multiple release channels with per-user/channel entitlements.
- Device/OS/architecture compatibility filtering.
- Concurrent download/update queue where safe.
- Accessibility and responsive acceptance for Android phones/tablets/foldables and Linux desktop/window sizes, including keyboard/focus, large text, contrast, and reduced-transparency behavior.
- Approved canonical GoreeCloud App Store icon/logo and the resulting desktop/Flatpak AppStream identity integration.
