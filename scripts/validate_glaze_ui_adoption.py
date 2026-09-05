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
    web_contract = json.loads(read("contracts/web-distribution.json"))
    require(adoption.get("status") == "adoption-candidate", "status must remain adoption-candidate")
    require(adoption.get("targetVersion") == VERSION, "targetVersion mismatch")
    require(adoption.get("requiredTargetVersion") == VERSION, "requiredTargetVersion mismatch")
    require(adoption.get("stableReleaseTag") == TAG, "release tag mismatch")
    require(adoption.get("stableReleaseRevision") == REVISION, "release revision mismatch")
    acceptance = adoption.get("acceptance", {})
    require(acceptance.get("productionEligible") is False, "must not be production eligible")
    require(acceptance.get("conformanceAccepted") is False, "conformance must remain unaccepted")
    for key in ("renderedAcceptance", "nativeAccessibilityAcceptance", "representativePhysicalDeviceAcceptance", "supportedFormFactorAcceptance", "humanVisualExcellence", "webRenderedAcceptance", "webAccessibilityAcceptance", "webRepresentativeTargetAcceptance"):
        require(acceptance.get(key) == "pending", f"{key} must remain pending")

    native = adoption.get("nativeMapping", {})
    require(native.get("deepDarkSourceMapping") is True, "Android Deep Dark source mapping missing")
    require(native.get("environmentalSampling") is False, "Android environmental sampling must remain off")

    web = adoption.get("webMapping", {})
    require(web.get("platform") == "Web", "Web mapping missing")
    require(web.get("generalTargetFloorPx") == 48, "Web interaction floor mismatch")
    require(web.get("deepDarkSourceMapping") is True, "Web Deep Dark source mapping missing")
    require(web.get("deepDarkRuntimeSelection") == "pending-policy", "Web Deep Dark runtime policy must remain pending")
    require(web.get("environmentalSampling") is False, "Web environmental sampling must remain off")
    require(web.get("externalRuntimeDependencies") is False, "Web mapping must not add third-party runtime dependencies")

    glaze = integrations.get("integrations", {}).get("glazeUi", {})
    require(glaze.get("target") == VERSION, "platform target mismatch")
    require(glaze.get("stableReleaseRevision") == REVISION, "platform revision mismatch")
    require(integrations.get("productionAcceptance") is False, "productionAcceptance must remain false")
    require(web_contract.get("glazeUi", {}).get("target") == VERSION, "Web distribution target mismatch")
    require(web_contract.get("productionAcceptance") is False, "Web production acceptance must remain false")

    gateways = read("app/src/main/java/com/goreecloud/appstore/platform/PlatformGateways.kt")
    for literal in (VERSION, TAG, REVISION, "CONFORMANCE_ACCEPTED = false"):
        require(literal in gateways, f"PlatformGateways.kt missing {literal}")
    theme = read("app/src/main/java/com/goreecloud/appstore/ui/GlazeTheme.kt")
    for literal in ("0xFF0F6B6F", "0xFFD9A35F", "0xFF05070A", "DEEP_DARK"):
        require(literal in theme, f"Android V1.1 source mapping missing {literal}")
    web_styles = read("web/styles.css")
    for literal in ("#0F6B6F", "#D9A35F", "#05070A", "--target-min: 48px"):
        require(literal in web_styles, f"Web V1.1 source mapping missing {literal}")

    gradle = read("app/build.gradle.kts")
    require(f'versionName = "{APP_VERSION}"' in gradle, "versionName mismatch")
    require(f"versionCode = {APP_VERSION_CODE}" in gradle, "versionCode mismatch")
    print(f"GLAZE UI V1.1 source mapping validated for Android + Web: {VERSION} @ {REVISION}; conformance=false production=false")


if __name__ == "__main__":
    main()
