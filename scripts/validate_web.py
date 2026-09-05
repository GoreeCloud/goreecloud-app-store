#!/usr/bin/env python3
from __future__ import annotations

import hashlib
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
EXPECTED_ICON_BLOB = "05c66a2a4c8edcc194183bb8ffb10ca90d8eaeef"


def require(value: bool, message: str) -> None:
    if not value:
        raise SystemExit(f"Web Development validation failed: {message}")


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def git_blob_sha(path: str) -> str:
    data = (ROOT / path).read_bytes()
    return hashlib.sha1(f"blob {len(data)}\0".encode("ascii") + data).hexdigest()


def main() -> None:
    contract = json.loads(read("contracts/web-distribution.json"))
    catalog = json.loads(read("catalog/development-catalog.json"))
    adoption = json.loads(read("contracts/glaze-ui-adoption.json"))

    require(contract["schemaVersion"] == 1, "contract schemaVersion mismatch")
    require(contract["application"] == "goreecloud-app-store", "application mismatch")
    require(contract["platform"] == "web", "platform mismatch")
    require(contract["lifecycle"] == "development", "lifecycle must remain development")
    require(contract["client"]["version"] == "0.1.0-dev", "web client version mismatch")
    require(contract["client"]["externalRuntimeDependencies"] is False, "external runtime dependencies must remain disabled")
    require(contract["catalog"]["sharedSource"] == "catalog/development-catalog.json", "web must use the shared catalog")
    require(contract["catalog"]["itemCount"] == 12, "contract item count mismatch")
    require(contract["catalog"]["authoritative"] is False, "Development catalog must remain non-authoritative")
    require(contract["security"]["analyticsEnabled"] is False, "analytics must remain disabled")
    require(contract["security"]["thirdPartyRuntimeCode"] is False, "third-party runtime code must remain disabled")
    require(contract["security"]["packageInstallationEnabled"] is False, "package installation must remain disabled")
    require(contract["security"]["serviceLaunchEnabled"] is False, "service launch must remain disabled")
    require(contract["productionAcceptance"] is False, "productionAcceptance must remain false")
    require(contract["glazeUi"]["target"] == "1.1.0", "GLAZE target mismatch")
    require(contract["glazeUi"]["conformanceAccepted"] is False, "GLAZE conformance must remain unaccepted")

    require(catalog["schemaVersion"] == 2 and catalog["authoritative"] is False, "shared Development catalog mismatch")
    require(len(catalog["items"]) == 12, "reviewed Development catalog must contain 12 entries")
    require(git_blob_sha(contract["branding"]["repositorySource"]) == EXPECTED_ICON_BLOB, "approved App Store SVG provenance mismatch")

    html = read("web/index.html")
    styles = read("web/styles.css")
    app = read("web/app.mjs")
    entitlements = read("web/entitlements.mjs")
    require("Content-Security-Policy" in html, "CSP source declaration missing")
    require("script-src 'self'" in html and "connect-src 'self'" in html, "CSP must keep runtime and catalog local")
    require("https://" not in html and "http://" not in html, "web entrypoint must not load external URLs")
    require("./catalog/development-catalog.json" in app, "client must load the built shared Development catalog")
    require("textContent" in app, "catalog presentation must use text-safe DOM assignment")
    require("visibleItems(state.catalog, state.identity)" in app, "entitlement filtering must precede discovery filtering")
    require("allowed.some" in entitlements, "explicit audience matching missing")
    require("--target-min: 48px" in styles, "48px interaction floor missing")
    require(":focus-visible" in styles, "keyboard focus styling missing")
    require("prefers-reduced-motion: reduce" in styles, "Reduced Motion mapping missing")
    require("forced-colors: active" in styles, "Forced Colors mapping missing")
    for literal in ("#0F6B6F", "#D9A35F", "#05070A"):
        require(literal in styles, f"GLAZE V1.1 source primitive missing: {literal}")

    web_mapping = adoption.get("webMapping", {})
    require(web_mapping.get("platform") == "Web", "GLAZE adoption web mapping missing")
    require(web_mapping.get("externalRuntimeDependencies") is False, "GLAZE web mapping must remain dependency-light")
    require(web_mapping.get("generalTargetFloorPx") == 48, "GLAZE web target floor mismatch")
    print("Web Development source contract validated: shared 12-item entitlement-safe catalog, local runtime, GLAZE UI V1.1 source mapping, production=false")


if __name__ == "__main__":
    main()
