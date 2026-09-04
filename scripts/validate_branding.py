#!/usr/bin/env python3
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BRANDING = ROOT / "BRANDING.md"
MANIFEST = ROOT / "app/src/main/AndroidManifest.xml"
UI = ROOT / "app/src/main/java/com/goreecloud/appstore/ui/AppStoreExperience.kt"
CATALOG = ROOT / "catalog/development-catalog.json"
DRAWABLE = ROOT / "app/src/main/res/drawable"

expected = {
    "goreecloud.browser": ("goreecloud_browser_icon", "products/browser/app-icon.svg", "2a81cc68cb8c1831dfd7bec6c3d0b14e2f421f1f"),
    "goreecloud.messenger": ("goreecloud_messenger_icon", "products/messenger/app-icon.svg", "01102af91a43e100c66877489b94929165ec0430"),
    "goreecloud.location": ("goreecloud_location_icon", "products/location/app-icon.svg", "ceb93b6d814c80ece0929022eb5edcdfbc346e2d"),
    "goreecloud.contacts": ("goreecloud_contacts_icon", "products/contacts/app-icon.svg", "22e818436ebef790333fcf56efa79d5bdfff5c88"),
    "goreecloud.tasks": ("goreecloud_tasks_icon", "products/tasks/app-icon.svg", "180e162c81b34a0b1dffd20031b36cbb874e2f61"),
    "goreecloud.notes": ("goreecloud_notes_icon", "products/notes/app-icon.svg", "9618b85e29f89990320cc3a101f0f3bf6fffc89f"),
    "goreecloud.memos": ("goreecloud_memos_icon", "products/memos/app-icon.svg", "eb9396c3a1891f6afb96849a29110c6f35e65f19"),
    "goreecloud.launcher": ("goreecloud_launcher_icon", "products/launcher/app-icon.svg", "d6768114e689058f1c911beca4050f33c96bd7c2"),
    "goreecloud.keyboard": ("goreecloud_keyboard_icon", "products/keyboard/app-icon.svg", "9dea51ca5853dc0faf41d94fbc12ee810480c472"),
    "goreecloud.manager": ("goreecloud_manager_icon", "products/manager/app-icon.svg", "024d82d5b5911e426216dfbd6a19d95cd6d71fc3"),
    "goreecloud.identity-center": ("goreecloud_identity_center_icon", "services/identity-center/service-icon.svg", "36922e5a747817267a27f640bb4234b8d59ab2a5"),
    "goreecloud.mesh-center": ("goreecloud_mesh_center_icon", "services/mesh-center/service-icon.svg", "2628ff825549847398e98d9768f8f57b30aa378a"),
}

for path in (BRANDING, MANIFEST, UI, CATALOG):
    if not path.is_file():
        raise SystemExit(f"Missing required branding input: {path.relative_to(ROOT)}")
branding = BRANDING.read_text(encoding="utf-8")
ui = UI.read_text(encoding="utf-8")
manifest = MANIFEST.read_text(encoding="utf-8")

for token in (
    "GoreeCloud/goreecloud-branding-assets",
    "products/app-store/app-icon.svg",
    "05c66a2a4c8edcc194183bb8ffb10ca90d8eaeef",
    "android:icon=\"@drawable/goreecloud_app_store_icon\"",
    "GoreeCloud Launcher",
    "GoreeCloud Search",
):
    if token not in branding:
        raise SystemExit(f"BRANDING.md missing required token: {token}")
if 'android:icon="@drawable/goreecloud_app_store_icon"' not in manifest:
    raise SystemExit("Manifest is not wired to the approved App Store identity")
if "R.drawable.goreecloud_app_store_icon" not in ui:
    raise SystemExit("Discover hero is not wired to the approved App Store identity")

catalog = json.loads(CATALOG.read_text(encoding="utf-8"))
ids = {item.get("id") for item in catalog.get("items", [])}
if ids != set(expected):
    raise SystemExit(f"Catalog identity set changed without branding review: expected {sorted(expected)}, got {sorted(ids)}")

for item_id, (drawable, canonical_path, blob) in expected.items():
    resource = DRAWABLE / f"{drawable}.xml"
    if not resource.is_file():
        raise SystemExit(f"Missing artwork derivative for {item_id}: {resource.name}")
    mapping = f'"{item_id}" -> R.drawable.{drawable}'
    if mapping not in ui:
        raise SystemExit(f"Missing explicit artwork mapping: {mapping}")
    for token in (canonical_path, blob):
        if token not in branding:
            raise SystemExit(f"Missing provenance token for {item_id}: {token}")

if '"goreecloud.identity-center" -> R.drawable.goreecloud_identity_icon' in ui:
    raise SystemExit("Identity Center regressed to the full Identity application icon")
if (DRAWABLE / "goreecloud_identity_icon.xml").exists():
    raise SystemExit("Obsolete full Identity application derivative remains in resource set")

checks = {
    "goreecloud_contacts_icon.xml": ["#2DD4BF", "#2563EB", "M14,45"],
    "goreecloud_tasks_icon.xml": ["#F59E0B", "#7C3AED", "M16,21"],
    "goreecloud_notes_icon.xml": ["#F59E0B", "#EA580C", "M19,14H45V50H19Z"],
    "goreecloud_memos_icon.xml": ["#FBBF24", "#F97316", "M17,17H47V40"],
    "goreecloud_launcher_icon.xml": ["#38BDF8", "#6366F1", "M19,15H25"],
    "goreecloud_keyboard_icon.xml": ["#14B8A6", "#2563EB", "M16,18H48"],
}
for name, tokens in checks.items():
    text = (DRAWABLE / name).read_text(encoding="utf-8")
    for token in tokens:
        if token not in text:
            raise SystemExit(f"{name} drifted from reviewed canonical derivative token: {token}")

print("GoreeCloud App Store approved branding and 12-item shared catalog mapping validation passed.")
