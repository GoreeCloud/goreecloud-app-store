# GoreeCloud App Store Architecture

## Design principle

GoreeCloud App Store is a **policy-mediated catalog and delivery client family**, not the authority for every decision it displays.

```text
GoreeCloud Identity
      │ authenticated actor + approved claims
      ▼
App Store entitlement/catalog boundary
      │ personalized authorized catalog
      ▼
Shared normalized catalog model
      ├── Native Android client (Kotlin / Compose)
      │     ├── Discover / Apps / Services / Search
      │     ├── Updates / Library
      │     └── Android artifact/service presentation
      │
      └── Native Linux client (Rust / GTK 4 / libadwaita)
            ├── Discover / Applications / Services / Search
            ├── Linux packages (.deb / Flatpak publication state)
            └── Share catalog surface

Protected artifact request ──► delivery backend ──► current authorization
                                      │
                                      ▼
                               Wardveil trust result
                                      │
                     ┌────────────────┴────────────────┐
                     ▼                                 ▼
              Android install path               Linux package path
              (future accepted)                  (download now only when
                                                  metadata is complete;
                                                  auto-install not accepted)

Privacy Shield governs permitted data use and sharing.
Everkeep governs protection/recovery evidence.
GoreeCloud Mesh coordinates approved capabilities/events.
Glaze UI governs presentation/interaction semantics.
```

## Authority matrix

| Decision | Authority | App Store responsibility |
| --- | --- | --- |
| Who is acting? | GoreeCloud Identity | Consume validated session/claims |
| What catalog items may this identity see? | App Store domain policy using approved Identity inputs | Evaluate and conceal non-entitled items |
| May this artifact be downloaded? | Delivery backend + current Identity/App Store policy | Request with current authorization; never trust UI state alone |
| Is an artifact trusted? | Wardveil Security | Fail closed and present authoritative result |
| May catalog/search/share/library data be collected or exposed? | Privacy Shield | Minimize, isolate identities, and honor policy/consent |
| Is library/history state recoverable? | Everkeep | Preserve required state and present recovery evidence truthfully |
| How are lifecycle/capability events coordinated? | GoreeCloud Mesh | Publish/consume minimized approved events |
| How is state represented/interacted with? | Glaze UI | Implement current native mapping and accessibility requirements |

## Shared catalog

Android and Linux must consume one normalized catalog contract rather than evolving separate product databases. `catalog/development-catalog.json` is the current non-authoritative fixture and is validated against `contracts/store-catalog.schema.json`.

A future production catalog should provide authenticated, versioned snapshots containing item lifecycle state, entitlement result/policy binding, compatible platform releases, immutable artifact identity, digest and signing provenance, evidence references, expiry, and rollback/revocation information.

Privacy/security preference is to return only entries the caller is authorized to know about. Client-side filtering remains defense in depth, not the sole security boundary.

## Entitlement evaluation

Both clients implement the same rule shape: a signed-in requirement plus explicitly allowed audiences. No implicit administrator override exists. Search and package views operate on the already-entitled catalog.

Development identities and audiences are fixtures only. Production decisions must be based on approved GoreeCloud Identity inputs and server-side authorization.

## Linux package readiness

Each Linux artifact carries format, role, publication state, architecture, package identity, download URL, SHA-256, source revision, signing state, and Wardveil acceptance state.

The Linux client treats an artifact as download-ready only when all required fields are affirmative and structurally valid. Incomplete `published` metadata is not silently upgraded to trusted state. Unpublished, blocked, withdrawn, malformed, or incomplete artifacts remain non-actionable.

Current development fixture entries have Debian and Flatpak slots but are unpublished.

## Linux client distribution

The App Store client itself has development packages independent of the product artifacts it catalogs:

```text
Native Linux development client
  app id:  com.goreecloud.AppStore.Development
  binary:  goreecloud-app-store-dev

  Debian
    package: goreecloud-app-store-dev
    version: 0.1.0~dev1
    arch:    amd64

  Flatpak
    app id:  com.goreecloud.AppStore.Development
    runtime: org.gnome.Platform//50
    arch:    x86_64
```

`.deb` is the primary native format for this checkpoint and Flatpak is the primary cross-distribution format. Both are built from the same exact repository revision. Rust dependencies are pinned in committed `linux/Cargo.lock` and CI builds with `--locked`.

These are development App Store packages. Their existence does not mean the catalog's GoreeCloud products have published Linux artifacts.

## Android development identity

Android development/device-review APKs remain isolated from the future production lineage:

```text
Development APK
  package: com.goreecloud.appstore.dev
  signer: repository-managed development-only certificate

Future production APK
  package: com.goreecloud.appstore
  signer: separate controlled production identity
```

## Artifact-delivery boundary

Before any platform treats an artifact as production distributable, the architecture requires:

1. protected artifact endpoint with backend re-authorization where access is protected;
2. immutable artifact and release identity;
3. SHA-256 or stronger integrity evidence;
4. expected signing/provenance metadata;
5. Wardveil verification policy with fail-closed behavior;
6. secure transport;
7. explicit user-visible installation consent/authority appropriate to the platform;
8. install/update reconciliation;
9. rollback/failure/downgrade/recovery behavior;
10. audit linkage from distributed artifact to approved source/release evidence.

The current Linux development client can only open an approved download URI after its local readiness contract succeeds. It does not run `apt`, `dpkg`, `flatpak install`, or other privileged/automatic installers on behalf of the user.

## Shared/public catalog boundary

The Linux UI has a Share catalog control, but the fixture `shareUrl` is empty and the control is disabled.

A future public/shareable catalog endpoint must explicitly classify public versus identity-protected metadata and must not leak concealed product names, protected audience data, credentials, private service endpoints, or private library state. Sharing catalog discovery cannot substitute for delivery re-authorization.

## Service boundary

A service entry may expose an approved HTTPS/deep-link destination or an installable companion client. The service itself must still enforce authentication and authorization. Catalog visibility or possession of a Linux companion package does not grant service access.

## Glaze UI mapping

The App Store pins Glaze UI 2.1.0 Stable. Android maps it through the Compose theme; Linux maps it through GTK/libadwaita CSS roles. The Linux mapping deliberately uses host theme variables rather than pretending a source-level CSS file establishes rendered conformance.

Rendered, contrast, reduced-transparency, large-text, keyboard/focus, accessibility, supported-form-factor, and representative platform acceptance remain pending. `conformanceAccepted=false`.

## Offline behavior

A future offline cache may show previously authorized catalog state only within a defined freshness window and with clear stale/cached status. It must not infer new protected downloads, service grants, elevated access, or package trust solely from cached authorization.

## Current development boundary

Implemented: native Android and Linux client shells; shared catalog/schema; development identity fixtures; entitlement filtering; search; applications/services; Linux package-publication state; catalog-share affordance; Android development signing lineage; Linux `.deb` and Flatpak development packaging; AppStream metadata validation; pinned Rust dependency graph; exact-source CI; Glaze 2.1 source mappings; and explicit development evidence boundaries.

Not production connected/accepted: GoreeCloud Identity runtime, authoritative production catalog, product artifact publication, protected product delivery, automatic installation/update, service launch, installed library, Wardveil runtime package verification, Privacy Shield runtime policy, Everkeep recovery, Mesh production transport, production signing, approved App Store icon/logo, or Glaze UI application conformance. `productionAcceptance` remains `false`.
