#!/usr/bin/env python3
from __future__ import annotations
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
VERSION = "1.1.0"
TAG = "v1.1.0"
REVISION = "15cc76d2bcd4065552dc31c77145b63f34d9e7b2"
APP_VERSION = "0.1.5-dev"
APP_VERSION_CODE = "6"

def require(value: bool, message: str) -> None:
    if not value:
        raise SystemExit(f"GLAZE UI V1.1 validation failed: {message}")

def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")

def main() -> None:
    adoption = json.loads(read("contracts/glaze-ui-adoption.json"))
    integrations = json.loads(read("contracts/platform-integrations.json"))
    require(adoption.get("status") == "adoption-candidate", "status must remain adoption-candidate")
    require(adoption.get("targetVersion") == VERSION, "targetVersion mismatch")
    require(adoption.get("requiredTargetVersion") == VERSION, "requiredTargetVersion mismatch")
    require(adoption.get("stableReleaseTag") == TAG, "release tag mismatch")
    require(adoption.get("stableReleaseRevision") == REVISION, "release revision mismatch")
    acceptance = adoption.get("acceptance", {})
    require(acceptance.get("productionEligible") is False, "must not be production eligible")
    require(acceptance.get("conformanceAccepted") is False, "conformance must remain unaccepted")
    for key in ("renderedAcceptance", "nativeAccessibilityAcceptance", "representativePhysicalDeviceAcceptance", "supportedFormFactorAcceptance", "humanVisualExcellence"):
        require(acceptance.get(key) == "pending", f"{key} must remain pending")
    native = adoption.get("nativeMapping", {})
    require(native.get("deepDarkSourceMapping") is True, "Deep Dark source mapping missing")
    require(native.get("environmentalSampling") is False, "environmental sampling must remain off")
    glaze = integrations.get("integrations", {}).get("glazeUi", {})
    require(glaze.get("target") == VERSION, "platform target mismatch")
    require(glaze.get("stableReleaseRevision") == REVISION, "platform revision mismatch")
    require(integrations.get("productionAcceptance") is False, "productionAcceptance must remain false")
    gateways = read("app/src/main/java/com/goreecloud/appstore/platform/PlatformGateways.kt")
    for literal in (VERSION, TAG, REVISION, "CONFORMANCE_ACCEPTED = false"):
        require(literal in gateways, f"PlatformGateways.kt missing {literal}")
    theme = read("app/src/main/java/com/goreecloud/appstore/ui/GlazeTheme.kt")
    for literal in ("0xFF0F6B6F", "0xFFD9A35F", "0xFF05070A", "DEEP_DARK"):
        require(literal in theme, f"V1.1 source mapping missing {literal}")
    gradle = read("app/build.gradle.kts")
    require(f'versionName = "{APP_VERSION}"' in gradle, "versionName mismatch")
    require(f"versionCode = {APP_VERSION_CODE}" in gradle, "versionCode mismatch")
    print(f"GLAZE UI V1.1 source mapping validated: {VERSION} @ {REVISION}; conformance=false production=false")

if __name__ == "__main__":
    main()
