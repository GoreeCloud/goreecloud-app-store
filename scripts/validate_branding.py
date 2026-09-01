#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BRANDING = ROOT / "BRANDING.md"
MANIFEST = ROOT / "app/src/main/AndroidManifest.xml"

if not BRANDING.is_file():
    raise SystemExit("Missing mandatory BRANDING.md")

branding = BRANDING.read_text(encoding="utf-8")
for required in [
    "GoreeCloud/goreecloud-branding-assets",
    "products/app-store/app-icon.svg",
    "concepts/product-identity-round-1/app-store.svg",
    "No App Store-specific logo or icon is approved yet",
    "android:icon",
    "GoreeCloud Launcher",
    "GoreeCloud Search",
]:
    if required not in branding:
        raise SystemExit(f"BRANDING.md missing required current identity boundary: {required}")

manifest = MANIFEST.read_text(encoding="utf-8")
if "android:icon=" in manifest:
    raise SystemExit(
        "App Store declares an Android icon before BRANDING.md records an approved canonical identity and derivative"
    )

print("GoreeCloud App Store branding candidate-state validation passed.")
