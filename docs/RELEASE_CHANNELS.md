# GoreeCloud App Store Release Channels

Status: Development design and authorization scaffold. Production download delivery is not accepted or enabled.

## Channels

GoreeCloud App Store supports four download tracks:

1. **Stable** — production-approved releases intended for normal users.
2. **Release Candidate (RC)** — near-final builds undergoing final release acceptance.
3. **Beta** — pre-release builds intended for explicitly authorized testers.
4. **Debug** — development/debug artifacts intended only for explicitly authorized development identities.

`Development` remains the lifecycle label used by the current non-authoritative App Store fixture catalog. It is not itself a production download track and must not be confused with the Debug artifact channel.

## Authorization model

Release-channel access is login-dependent and must be granted explicitly. Administrative status is not an implicit bypass.

The current Development identity adapter uses these fixtures only to exercise the rule:

| Development login | Explicit download-channel grants |
| --- | --- |
| Standard demo | Stable |
| Administrator demo | Stable, RC, Beta |
| Developer demo | Stable, RC, Beta, Debug |
| Signed out | None |

These names and grants are not the production GoreeCloud Identity role taxonomy. Production grants must come from an approved Identity/App Store authorization contract.

## Product behavior

The client may present only release channels authorized for the active identity. It must not reveal unauthorized version numbers, release notes, artifact names, download URLs, checksums, signing metadata, or hidden-channel availability through search, counts, caches, deep links, update checks, or error messages.

When a channel is authorized but the catalog contains no approved release for that channel, the App Store must show a clear unavailable state rather than fabricating a version.

Changing accounts must immediately recalculate visible channels and clear any selected channel that the new identity is not authorized to use.

## Download security boundary

A client-side channel picker is not an authorization boundary. Every artifact request must be re-authorized by the responsible backend against the active identity, application, exact release/artifact identity, requested channel, device/platform compatibility, and current policy.

Before a download/install can be enabled, the release record must provide accepted immutable artifact identity, digest, signing/provenance expectations, secure transport, required Wardveil verification state, and rollback/failure behavior. Revoked sessions, disabled accounts, withdrawn releases, or removed channel grants must fail closed.

## Catalog evolution

The current schema exposes one fixture `version` and `releaseChannel` per catalog item and is not yet a production multi-version release manifest. The production catalog contract must evolve so one application can expose multiple release records, each with at least:

- immutable release identifier;
- semantic/display version;
- channel: Stable, RC, Beta, or Debug;
- publication/withdrawal state;
- platform and compatibility constraints;
- artifact identity and secure delivery reference;
- digest and signing/provenance evidence;
- release notes and source revision where applicable;
- channel/release-specific authorization inputs;
- Wardveil acceptance state or required verification contract.

The backend should preferably return only the releases the active identity is entitled to know about. Client filtering remains defense in depth.

## Current Development implementation

The Android domain model now carries explicit per-session download-channel grants and unit-test coverage. The first-party Web Development client exposes a release-channel picker in product details and limits its choices to the active fixture login's explicit grants. The current shared Development catalog does not contain real Stable, RC, or Beta release manifests, and package delivery remains disabled, so selecting a channel cannot enable a download yet.

Production Acceptance remains false.
