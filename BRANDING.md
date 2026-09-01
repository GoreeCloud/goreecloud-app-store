# GoreeCloud App Store Branding Contract

GoreeCloud App Store consumes branding from the canonical `GoreeCloud/goreecloud-branding-assets` repository.

The branding repository remains authoritative. Consumer copies in this repository are implementation derivatives only and do not create new branding authority.

## App Store identity status

No App Store-specific logo or icon is approved yet. The reserved future canonical product path is:

`products/app-store/app-icon.svg`

A first-round approval candidate is under review in `GoreeCloud/goreecloud-branding-assets` pull request #5 at:

`concepts/product-identity-round-1/app-store.svg`

That file is an approval candidate only. It must not be represented as production artwork, added to a release as the official App Store identity, or used to claim Stable visual conformance before the canonical visual-acceptance and promotion gates complete.

The current Android application manifest does not declare `android:icon`. Approved identity migration therefore requires both canonical artwork promotion and a traceable Android launcher derivative wired into the application package. The absence of a launcher icon remains part of the visual-identity production-readiness blocker.

The candidate identity direction is a software-catalog portal with an acquisition path. The final identity must remain distinct from GoreeCloud Launcher and GoreeCloud Search and must not copy Apple App Store, Google Play, F-Droid, shopping-bag, physical-storefront, or generic package-manager identities.

Branding does not establish entitlement, package authorization, artifact verification, successful download/install, security, privacy, continuity, or release truth.

## Current Android derivatives

The following Android VectorDrawable resources are derived from approved canonical SVG assets so the native store can render first-party artwork without network access:

| Store item | Canonical asset | Canonical Git blob | Android derivative |
| --- | --- | --- | --- |
| GoreeCloud Browser | `products/browser/app-icon.svg` | `2a81cc68cb8c1831dfd7bec6c3d0b14e2f421f1f` | `app/src/main/res/drawable/goreecloud_browser_icon.xml` |
| GoreeCloud Messenger | `products/messenger/app-icon.svg` | `01102af91a43e100c66877489b94929165ec0430` | `app/src/main/res/drawable/goreecloud_messenger_icon.xml` |
| GoreeCloud Location | `products/location/app-icon.svg` | `ceb93b6d814c80ece0929022eb5edcdfbc346e2d` | `app/src/main/res/drawable/goreecloud_location_icon.xml` |
| GoreeCloud Identity / Identity Center | `products/identity/app-icon.svg` | `dc8287e385f86767f0105c48a8f234d8440d7623` | `app/src/main/res/drawable/goreecloud_identity_icon.xml` |
| GoreeCloud Manager | `products/manager/app-icon.svg` | `024d82d5b5911e426216dfbd6a19d95cd6d71fc3` | `app/src/main/res/drawable/goreecloud_manager_icon.xml` |

The derivatives preserve the canonical icon geometry and gradient/color intent within Android VectorDrawable constraints.

`Mesh Center` currently uses a neutral platform/service glyph in the development client rather than copying or approximating the more complex GoreeCloud Mesh mark. A future native derivative must reference the approved canonical Mesh asset and be reviewed as a branding consumer update.

## Promotion requirement

When an App Store-specific identity is approved, this contract must be updated with:

- the exact canonical `products/app-store/app-icon.svg` Git blob;
- the Android launcher/adaptive-icon derivative paths;
- the manifest/package references that consume those derivatives;
- supported optical/platform roles;
- any monochrome/adaptive variants required by Android;
- validation that fails closed if the required product identity disappears or drifts.
