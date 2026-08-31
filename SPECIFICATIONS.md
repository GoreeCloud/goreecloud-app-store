# GoreeCloud App Store — Repository Specifications

## Product role

GoreeCloud App Store is the official GoreeCloud distribution and discovery surface for GoreeCloud applications and services. Initial scope is first-party GoreeCloud software and services rather than a general third-party marketplace.

## Development model

The App Store must be original GoreeCloud-owned native software. Store patterns may be informed by Google Play, Apple App Store, and F-Droid, but those products are not implementation foundations.

## Native clients

The repository supports two first-party native clients:

- **Android:** Kotlin + Jetpack Compose.
- **Linux:** Rust + GTK 4 + libadwaita.

Both consume the same normalized catalog/entitlement model. Platform frameworks, package systems, cryptographic libraries, and transport primitives are supporting foundations rather than substitutes for GoreeCloud product implementation.

## Development identities and package boundaries

### Android

- Development application ID: `com.goreecloud.appstore.dev`.
- Reserved future production application ID: `com.goreecloud.appstore`.
- Development APKs use the repository-managed non-production certificate documented in `development/signing/README.md`.
- Production signing must use separate controlled custody, provenance, recovery, and acceptance.

### Linux

- Development application ID: `com.goreecloud.AppStore.Development`.
- Development binary: `goreecloud-app-store-dev`.
- Debian development package: `goreecloud-app-store-dev`, `0.1.0~dev1`, amd64.
- Flatpak development application: `com.goreecloud.AppStore.Development`, x86_64, GNOME Platform 50.
- `.deb` is the primary native package format and Flatpak is the primary cross-distribution package format for this checkpoint.
- AppImage remains a planned portable third format, not an implemented current package.
- Linux production package signing/trust acceptance is not established.

## Multi-user and entitlement requirements

- Every production session must originate from GoreeCloud Identity or an explicitly approved local/offline identity path.
- The catalog must be personalized from authoritative policy inputs associated with the active identity.
- Different identities may receive different applications, services, channels, versions, artifacts, or administrative tools.
- Concealed items must not leak through search, recommendations, counts, update lists, deep links, package views, caches, or service launch affordances.
- Administrator status creates no implicit bypass. Access must be explicitly granted by applicable policy.
- The delivery service must re-authorize artifact download or service launch independently of client rendering.
- Disablement and session revocation must invalidate protected catalog/delivery access according to the Identity contract.
- Multi-account library/history state must remain isolated by identity.

Current development audience labels are fixtures only and do not establish production GoreeCloud taxonomy.

## Shared catalog model

One normalized catalog source must support all clients without permitting platform-specific metadata to drift into competing stores. Each item declares at minimum:

- stable GoreeCloud item identifier;
- application or service type;
- display metadata and category;
- lifecycle/release channel;
- entitlement requirements or approved policy binding;
- application package identity and/or service endpoint identity where applicable;
- platform-specific artifact collections;
- artifact/version provenance when delivery is enabled;
- privacy, security, continuity, and platform-integration evidence references when authoritative data exists.

The current fixture lives at `catalog/development-catalog.json`, is schema version 2, and is explicitly non-authoritative. Production catalog metadata must be authenticated, versioned, expiry-aware, rollback/revocation-aware, and transported over approved secure channels.

## Applications and Linux artifacts

Application entries may expose compatible platform releases. Linux product artifacts support Debian and Flatpak metadata slots.

A Linux artifact MUST remain non-downloadable unless all required evidence is present and structurally valid:

- publication state is `published`;
- download URL uses HTTPS;
- package/application identity is present;
- SHA-256 digest is present and valid;
- exact source revision is present;
- signing/provenance evidence is affirmative;
- Wardveil acceptance is affirmative.

`blocked`, `withdrawn`, unpublished, incomplete, malformed, or unverifiable metadata fails closed. Current development catalog artifacts are unpublished.

The Linux development client may open an approved download destination when the artifact contract is complete. Automatic installation, privileged package-manager control, unattended update behavior, and rollback automation are separate future capabilities and are disabled at this checkpoint.

Android installation remains separately gated by Android package identity/signing, Wardveil acceptance, explicit user consent, package-install authority, and runtime reconciliation.

## Services

Service entries represent GoreeCloud capabilities whose service-side authorization remains independent of catalog visibility. A service may eventually have an installable Linux companion/client artifact, but possessing that package does not itself authorize the underlying service.

Approved service launch must use allowlisted destinations and current authentication/authorization.

## Public/shared catalog

The Linux client includes a **Share catalog** surface. It MUST remain disabled while no approved public HTTPS catalog URL exists.

A future shared catalog endpoint must distinguish public metadata from identity-protected catalog data. Sharing must never expose concealed entries, protected entitlement metadata, credentials, private service destinations, or otherwise bypass Privacy Shield/Identity policy. Protected artifact delivery remains re-authorized after catalog discovery.

## Integral platform systems

### Glaze UI

Current required consumer target: **Glaze UI 2.1.0 Stable**, release `v2.1.0` at revision `c49113eb8b93c267613fdf1bbca1f814495acad7`.

The App Store is an **adoption candidate**, not a conformant or production-eligible consumer. Android maps Glaze through the Compose theme and Linux maps it through `linux/resources/style.css` with GTK/libadwaita host-theme roles. Both clients must keep durable content on solid surfaces and map interaction/status chrome according to the Glaze hierarchy.

Source mapping and automated validation do not establish rendered, accessibility, supported-form-factor, contrast, reduced-transparency, large-text, keyboard/focus, or physical-device/desktop acceptance. Those remain pending. Glaze Motion is not consumed.

### GoreeCloud Identity

Identity owns authentication, accounts, sessions, devices, credentials, and platform authority. The App Store owns store-domain entitlement decisions using approved Identity inputs. Production integration must validate login/logout, redirects where applicable, session expiry, mapping, disablement, failure behavior, and rollback.

### Wardveil Security

Wardveil owns package/security trust outcomes. Artifact download/install/update flows must fail closed when required provenance or Wardveil evidence is absent, stale, malformed, unavailable, revoked, blocked, or negative.

### Privacy Shield

Privacy Shield owns consent, minimization, data-use, retention, sharing, and user control. Development analytics remain off. Search/history/recommendation telemetry, public sharing, diagnostics, and cross-device library state require documented purpose and Privacy Shield treatment before production collection/use.

### Everkeep

Everkeep owns continuity and recoverability truth. Library/history/catalog configuration must have an application-specific protection contract before recovery claims are made. Sync and backup remain distinct concepts.

### GoreeCloud Mesh

Mesh owns platform coordination, capability discovery, governance, and events. The App Store may use approved Mesh contracts for minimized lifecycle/catalog coordination without transferring Identity, Privacy, Wardveil, Everkeep, or Glaze authority to Mesh.

## Store UX

The product should provide authorized Discover/home content, Apps, Services, search, Updates, Library, product details, and platform package views appropriate to each client. Linux additionally provides desktop package-publication state and catalog sharing controls.

UX requirements include:

- search constrained to the already-entitled catalog;
- clear active-identity switching with no metadata leakage;
- truthful unknown/unpublished/blocked states instead of fabricated positive badges;
- readable responsive/adaptive layouts;
- keyboard/focus and accessibility behavior appropriate to the host platform;
- product details that expose release, compatibility, privacy, security/provenance, continuity, source/license, and support information only when authoritative.

## Branding and official identity

`GoreeCloud/goreecloud-branding-assets` remains canonical. An approved product-specific App Store icon/logo does not yet exist and is a production-readiness blocker.

No placeholder may be represented as official. Flatpak repository-level AppStream composition is deliberately deferred until that icon exists; the development metainfo document is independently validated in CI.

## Build provenance

Linux Rust dependency resolution must be pinned by committed `linux/Cargo.lock`; validation/build commands use `--locked`. Linux CI records exact source revision, toolchain versions, lockfile digest, executable digest, Debian package metadata/digest, Flatpak bundle digest, catalog validation, Glaze contract validation, and the development acceptance boundary.

Android CI separately records exact source, APK identity/version/label/signing evidence and SHA-256.

## Stable and production gates

Stable qualification requires exact-release-revision evidence for all accepted platform scopes, including:

- controlled/reproducible build provenance;
- passing CI, tests, lint/static analysis, catalog validation, package validation, and Glaze contract validation;
- committed/pinned dependency inputs where applicable;
- approved product-specific App Store identity;
- controlled production signing identities for each package ecosystem;
- production GoreeCloud Identity and entitlement acceptance;
- authenticated production catalog delivery;
- backend re-authorization for protected artifact/service access;
- Wardveil package verification acceptance;
- Privacy Shield acceptance;
- Everkeep protection/recovery acceptance;
- GoreeCloud Mesh integration where required by release scope;
- current Glaze UI 2.1 rendered/native/accessibility/platform acceptance;
- Android runtime/form-factor acceptance for Android scope;
- supported Linux distribution/runtime, Debian installation/upgrade/removal, Flatpak installation/upgrade/removal, desktop accessibility, Wayland/X11 fallback, and package failure/rollback behavior for Linux scope;
- canonical project specification, changelog, README, and user documentation reconciled to the validated revision.

`productionAcceptance` remains `false` until those gates are satisfied by evidence rather than declaration.
