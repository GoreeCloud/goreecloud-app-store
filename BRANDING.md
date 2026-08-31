# GoreeCloud App Store Branding Contract

GoreeCloud App Store consumes branding from the canonical `GoreeCloud/goreecloud-branding-assets` repository.

The branding repository remains authoritative. Consumer copies in this repository are implementation derivatives only and do not create new branding authority.

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

## App Store product identity gate

No product-specific GoreeCloud App Store logo or icon is established by this repository. Any future official GoreeCloud App Store artwork must originate in `GoreeCloud/goreecloud-branding-assets` first and then be copied into this application repository with explicit provenance.

This is an **open production-readiness defect**, not permission to improvise an official mark. Before production or Stable qualification, the approved App Store identity must be present in this repository and used by applicable Android manifest/launcher, install/update, release-packaging, documentation, and platform-discovery surfaces. Development placeholders or generic store glyphs must not be represented as approved official branding.
