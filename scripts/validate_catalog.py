#!/usr/bin/env python3
"""Fail-closed validation for the shared GoreeCloud App Store catalog."""

from __future__ import annotations

import json
import re
from pathlib import Path
from urllib.parse import urlparse

ROOT = Path(__file__).resolve().parents[1]
CATALOG = ROOT / "catalog" / "development-catalog.json"
LEGACY_ANDROID_CATALOG = ROOT / "app" / "src" / "main" / "assets" / "catalog" / "development-catalog.json"
SHA256 = re.compile(r"^[0-9a-f]{64}$")
SHA40 = re.compile(r"^[0-9a-f]{40}$")
EXPECTED_FORMATS = {"deb", "flatpak"}


def fail(message: str) -> None:
    raise SystemExit(f"App Store catalog validation failed: {message}")


def require(condition: bool, message: str) -> None:
    if not condition:
        fail(message)


def valid_https(url: str) -> bool:
    parsed = urlparse(url)
    return parsed.scheme == "https" and bool(parsed.netloc) and not parsed.username and not parsed.password


def main() -> None:
    require(CATALOG.is_file(), f"missing shared catalog: {CATALOG.relative_to(ROOT)}")
    require(not LEGACY_ANDROID_CATALOG.exists(), "legacy Android-only catalog copy must not be committed")

    data = json.loads(CATALOG.read_text(encoding="utf-8"))
    require(data.get("schemaVersion") == 2, "schemaVersion must be 2")
    require(data.get("environment") == "development-fixture", "development catalog environment mismatch")
    require(data.get("authoritative") is False, "development catalog must remain non-authoritative")
    require(isinstance(data.get("shareUrl"), str), "shareUrl must be a string")
    if data["shareUrl"]:
        require(valid_https(data["shareUrl"]), "non-empty shareUrl must be HTTPS without embedded credentials")

    items = data.get("items")
    require(isinstance(items, list) and items, "catalog must contain at least one item")
    seen_ids: set[str] = set()

    for item in items:
        item_id = item.get("id")
        require(isinstance(item_id, str) and item_id, "item id is required")
        require(item_id not in seen_ids, f"duplicate item id: {item_id}")
        seen_ids.add(item_id)
        require(item.get("type") in {"application", "service"}, f"{item_id}: invalid type")
        require(item.get("releaseChannel") in {"stable", "beta", "development"}, f"{item_id}: invalid release channel")

        access = item.get("access")
        require(isinstance(access, dict), f"{item_id}: access object is required")
        require(isinstance(access.get("requireSignedIn"), bool), f"{item_id}: requireSignedIn must be boolean")
        audiences = access.get("anyAudience")
        require(isinstance(audiences, list), f"{item_id}: anyAudience must be a list")
        require(len(audiences) == len(set(audiences)), f"{item_id}: duplicate audience")

        artifacts = item.get("artifacts", {}).get("linux")
        require(isinstance(artifacts, list), f"{item_id}: Linux artifacts are required")
        formats = {artifact.get("format") for artifact in artifacts}
        require(formats == EXPECTED_FORMATS, f"{item_id}: expected exactly Debian and Flatpak artifact slots")

        for artifact in artifacts:
            fmt = artifact.get("format")
            status = artifact.get("status")
            require(status in {"unpublished", "published", "blocked", "withdrawn"}, f"{item_id}/{fmt}: invalid status")
            expected_role = "application" if item.get("type") == "application" else "service-client"
            require(artifact.get("role") == expected_role, f"{item_id}/{fmt}: role does not match catalog item type")
            require(bool(artifact.get("architecture")), f"{item_id}/{fmt}: architecture is required")
            require(isinstance(artifact.get("signed"), bool), f"{item_id}/{fmt}: signed must be boolean")
            require(isinstance(artifact.get("wardveilAccepted"), bool), f"{item_id}/{fmt}: wardveilAccepted must be boolean")

            if status == "published":
                require(bool(artifact.get("packageId")), f"{item_id}/{fmt}: published artifact requires packageId")
                require(valid_https(artifact.get("downloadUrl", "")), f"{item_id}/{fmt}: published artifact requires HTTPS URL")
                require(bool(SHA256.fullmatch(artifact.get("sha256", ""))), f"{item_id}/{fmt}: published artifact requires SHA-256")
                require(bool(SHA40.fullmatch(artifact.get("sourceRevision", ""))), f"{item_id}/{fmt}: published artifact requires exact source revision")
                require(artifact.get("signed") is True, f"{item_id}/{fmt}: published artifact must have signing evidence")
                require(artifact.get("wardveilAccepted") is True, f"{item_id}/{fmt}: published artifact must have Wardveil acceptance")
            else:
                for key in ("packageId", "downloadUrl", "sha256", "sourceRevision"):
                    require(artifact.get(key, "") == "", f"{item_id}/{fmt}: {status} artifact must not expose {key}")
                require(artifact.get("signed") is False, f"{item_id}/{fmt}: non-published artifact cannot claim signing acceptance")
                require(artifact.get("wardveilAccepted") is False, f"{item_id}/{fmt}: non-published artifact cannot claim Wardveil acceptance")

    print(
        "GoreeCloud App Store catalog validated: "
        f"schema=2 items={len(items)} sharedSource=catalog/development-catalog.json "
        "linuxFormats=deb,flatpak authoritative=false"
    )


if __name__ == "__main__":
    main()
