#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import re
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / ".artifacts" / "web" / "site"
ICON = ROOT / "linux" / "resources" / "com.goreecloud.AppStore.Development.svg"
EXPECTED_ICON_BLOB = "05c66a2a4c8edcc194183bb8ffb10ca90d8eaeef"
STATIC_FILES = ("index.html", "styles.css", "app.mjs", "entitlements.mjs")


def git_blob_sha(data: bytes) -> str:
    return hashlib.sha1(f"blob {len(data)}\0".encode("ascii") + data).hexdigest()


def main() -> None:
    parser = argparse.ArgumentParser(description="Build the GoreeCloud App Store Web Development static artifact")
    parser.add_argument("--revision", required=True)
    args = parser.parse_args()
    if not re.fullmatch(r"[0-9a-f]{40}", args.revision):
        raise SystemExit("--revision must be an exact 40-character lowercase Git SHA")

    icon_bytes = ICON.read_bytes()
    if git_blob_sha(icon_bytes) != EXPECTED_ICON_BLOB:
        raise SystemExit("Approved App Store SVG provenance mismatch")

    if OUT.exists():
        shutil.rmtree(OUT)
    (OUT / "catalog").mkdir(parents=True)
    (OUT / "assets").mkdir(parents=True)

    for name in STATIC_FILES:
        shutil.copyfile(ROOT / "web" / name, OUT / name)
    shutil.copyfile(ROOT / "catalog" / "development-catalog.json", OUT / "catalog" / "development-catalog.json")
    shutil.copyfile(ICON, OUT / "assets" / "app-icon.svg")

    build_info = {
        "application": "goreecloud-app-store",
        "clientVersion": "0.1.0-dev",
        "lifecycle": "development",
        "productionAcceptance": False,
        "sourceRevision": args.revision,
    }
    (OUT / "build-info.json").write_text(json.dumps(build_info, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    print(f"Built Web Development client for exact source {args.revision} at {OUT.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
