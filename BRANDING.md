# GoreeCloud App Store Branding Contract

GoreeCloud App Store consumes product and service identity from `GoreeCloud/goreecloud-branding-assets`. Canonical SVGs and pinned Git blobs remain authoritative; Android resources here are implementation derivatives only.

## App Store identity

- `products/app-store/app-icon.svg` — `05c66a2a4c8edcc194183bb8ffb10ca90d8eaeef`
- Android derivative: `app/src/main/res/drawable/goreecloud_app_store_icon.xml`
- Manifest mapping: `android:icon="@drawable/goreecloud_app_store_icon"`

The App Store identity remains distinct from GoreeCloud Launcher, GoreeCloud Search, Google Play, Apple App Store, F-Droid, generic storefronts, shopping bags, and package-manager glyphs.

## Catalog identity provenance

| Store item | Canonical asset | Canonical Git blob | Android derivative |
| --- | --- | --- | --- |
| GoreeCloud Browser | `products/browser/app-icon.svg` | `2a81cc68cb8c1831dfd7bec6c3d0b14e2f421f1f` | `goreecloud_browser_icon.xml` |
| GoreeCloud Messenger | `products/messenger/app-icon.svg` | `01102af91a43e100c66877489b94929165ec0430` | `goreecloud_messenger_icon.xml` |
| GoreeCloud Location | `products/location/app-icon.svg` | `ceb93b6d814c80ece0929022eb5edcdfbc346e2d` | `goreecloud_location_icon.xml` |
| GoreeCloud Contacts | `products/contacts/app-icon.svg` | `22e818436ebef790333fcf56efa79d5bdfff5c88` | `goreecloud_contacts_icon.xml` |
| GoreeCloud Tasks | `products/tasks/app-icon.svg` | `180e162c81b34a0b1dffd20031b36cbb874e2f61` | `goreecloud_tasks_icon.xml` |
| GoreeCloud Notes | `products/notes/app-icon.svg` | `9618b85e29f89990320cc3a101f0f3bf6fffc89f` | `goreecloud_notes_icon.xml` |
| GoreeCloud Memos | `products/memos/app-icon.svg` | `eb9396c3a1891f6afb96849a29110c6f35e65f19` | `goreecloud_memos_icon.xml` |
| GoreeCloud Launcher | `products/launcher/app-icon.svg` | `d6768114e689058f1c911beca4050f33c96bd7c2` | `goreecloud_launcher_icon.xml` |
| GoreeCloud Keyboard | `products/keyboard/app-icon.svg` | `9dea51ca5853dc0faf41d94fbc12ee810480c472` | `goreecloud_keyboard_icon.xml` |
| GoreeCloud Manager | `products/manager/app-icon.svg` | `024d82d5b5911e426216dfbd6a19d95cd6d71fc3` | `goreecloud_manager_icon.xml` |
| Identity Center | `services/identity-center/service-icon.svg` | `36922e5a747817267a27f640bb4234b8d59ab2a5` | `goreecloud_identity_center_icon.xml` |
| Mesh Center | `services/mesh-center/service-icon.svg` | `2628ff825549847398e98d9768f8f57b30aa378a` | `goreecloud_mesh_center_icon.xml` |

Identity Center must not reuse the complete GoreeCloud Identity application icon. Mesh Center must retain the approved Interlace-derived service identity instead of a generic cloud/network glyph.

## Rules

- Stable identity artwork never communicates installed, connected, healthy, secure, authorized, synchronized, or verified state.
- Generic framework glyphs may be used for navigation and unlabeled affordances, not as substitutes for registered catalog identities.
- Catalog expansion requires explicit artwork mapping and provenance review.
- Canonical asset changes require explicit derivative regeneration and provenance updates.
- Branding acceptance does not establish package delivery, runtime authorization, security/privacy/continuity acceptance, production signing, deployment, or Stable release status.
