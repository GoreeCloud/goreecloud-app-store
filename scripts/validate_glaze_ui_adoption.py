#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
VERSION = "1.3.0"
TAG = "v1.3.0"
REVISION = "ff34f232f295c9dcb07e4c681f66d4104d0b9323"
SOURCE_ANCHOR = "fc7cc91d2eace8da2371371c2855c24cbcb326a1"
ROLLBACK_VERSION = "1.2.0"
ROLLBACK_REVISION = "f285b9145e27e6e7027b075c37299d101945c272"
MATERIAL_RULE = (
    "Neutral glass remains the material foundation. Adaptive expression is contextual, "
    "bounded, and subordinate to meaning, accessibility, and task completion."
)
APP_VERSION = "0.1.5-dev"
APP_VERSION_CODE = "6"


def require(value: bool, message: str) -> None:
    if not value:
        raise SystemExit(f"GLAZE UI V1.3 validation failed: {message}")


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
    require(adoption.get("sourceQualificationAnchor") == SOURCE_ANCHOR, "source qualification anchor mismatch")
    require(adoption.get("stableContract") == "GLAZE_UI_V1_3.md", "Stable contract mismatch")
    require(adoption.get("materialRule") == MATERIAL_RULE, "material rule mismatch")
    require(adoption.get("platforms") == ["android", "linux", "web"], "supported Glaze platform set mismatch")

    rollback = adoption.get("rollback", {})
    require(rollback.get("version") == ROLLBACK_VERSION, "rollback version mismatch")
    require(rollback.get("stableReleaseRevision") == ROLLBACK_REVISION, "rollback revision mismatch")
    require(rollback.get("verifiedOnThisConsumerRevision") is False, "rollback must remain unverified until exercised")

    acceptance = adoption.get("acceptance", {})
    require(acceptance.get("productionEligible") is False, "must not be production eligible")
    require(acceptance.get("conformanceAccepted") is False, "conformance must remain unaccepted")
    for key in (
        "renderedAcceptance",
        "supportedFormFactorAcceptance",
        "linuxRenderedAcceptance",
        "webRenderedAcceptance",
    ):
        require(acceptance.get(key) == "pending-v1.3-revalidation", f"{key} must require V1.3 revalidation")
    for key in (
        "nativeAccessibilityAcceptance",
        "representativePhysicalDeviceAcceptance",
        "linuxAccessibilityAcceptance",
        "humanVisualExcellence",
        "webAccessibilityAcceptance",
        "webRepresentativeTargetAcceptance",
        "performanceAcceptance",
        "rollbackAcceptance",
    ):
        require(acceptance.get(key) == "pending", f"{key} must remain pending")

    native = adoption.get("nativeMapping", {})
    require(native.get("systemShellScope") == "Application", "Android shell scope must remain Application")
    require(native.get("generalTargetFloorDp") == 48, "Android interaction floor mismatch")
    require(native.get("touchAssistanceTargetFloorDp") == 56, "Android Touch Assistance floor mismatch")
    require(native.get("deepDarkSourceMapping") is True, "Android Deep Dark source mapping missing")
    require(native.get("neutralMaterial") is True, "Android neutral material mapping missing")
    require(native.get("adaptiveColorCarriesSemanticAuthority") is False, "Android adaptive color must not carry truth authority")
    require(native.get("environmentalSampling") is False, "Android environmental sampling must remain off")

    linux = adoption.get("linuxMapping", {})
    require(linux.get("platform") == "Linux", "Linux mapping missing")
    require(linux.get("generalTargetFloorPx") == 48, "Linux interaction floor mismatch")
    require(linux.get("neutralHostMaterial") is True, "Linux neutral host material mapping missing")
    require(linux.get("adaptiveColorCarriesSemanticAuthority") is False, "Linux adaptive color must not carry truth authority")
    require(linux.get("environmentalSampling") is False, "Linux environmental sampling must remain off")

    web = adoption.get("webMapping", {})
    require(web.get("platform") == "Web", "Web mapping missing")
    require(web.get("systemShellScope") == "Application", "Web shell scope must remain Application")
    require(web.get("generalTargetFloorPx") == 48, "Web interaction floor mismatch")
    require(web.get("deepDarkSourceMapping") is True, "Web Deep Dark source mapping missing")
    require(web.get("deepDarkRuntimeSelection") == "pending-policy", "Web Deep Dark runtime policy must remain pending")
    require(web.get("neutralMaterial") is True, "Web neutral material mapping missing")
    require(web.get("adaptiveColorCarriesSemanticAuthority") is False, "Web adaptive color must not carry truth authority")
    require(web.get("nestedBackdropBlur") is False, "Web nested backdrop blur must remain disabled")
    require(web.get("environmentalSampling") is False, "Web environmental sampling must remain off")
    require(web.get("externalRuntimeDependencies") is False, "Web mapping must not add third-party runtime dependencies")

    glaze = integrations.get("integrations", {}).get("glazeUi", {})
    require(glaze.get("target") == VERSION, "platform target mismatch")
    require(glaze.get("stableReleaseTag") == TAG, "platform release tag mismatch")
    require(glaze.get("stableReleaseRevision") == REVISION, "platform revision mismatch")
    require(glaze.get("sourceQualificationAnchor") == SOURCE_ANCHOR, "platform source anchor mismatch")
    require(glaze.get("rollbackVersion") == ROLLBACK_VERSION, "platform rollback mismatch")
    require(glaze.get("materialRule") == MATERIAL_RULE, "platform material rule mismatch")
    require(glaze.get("systemShellScope") == "Application", "platform shell scope mismatch")
    require(integrations.get("productionAcceptance") is False, "productionAcceptance must remain false")
    manager = integrations.get("integrations", {}).get("goreecloudManager", {})
    require(manager.get("sourceBoundaryImplemented") is False, "Manager source boundary must not be fabricated")
    require(manager.get("productionConnected") is False, "Manager production integration must remain false")

    web_glaze = web_contract.get("glazeUi", {})
    require(web_glaze.get("target") == VERSION, "Web distribution target mismatch")
    require(web_glaze.get("stableReleaseTag") == TAG, "Web distribution tag mismatch")
    require(web_glaze.get("stableReleaseRevision") == REVISION, "Web distribution revision mismatch")
    require(web_glaze.get("sourceQualificationAnchor") == SOURCE_ANCHOR, "Web distribution source anchor mismatch")
    require(web_glaze.get("rollbackVersion") == ROLLBACK_VERSION, "Web rollback mismatch")
    require(web_glaze.get("materialRule") == MATERIAL_RULE, "Web material rule mismatch")
    require(web_glaze.get("environmentalSampling") is False, "Web environmental sampling must remain disabled")
    require(web_glaze.get("adaptiveColorCarriesSemanticAuthority") is False, "Web color must not become truth authority")
    require(web_contract.get("productionAcceptance") is False, "Web production acceptance must remain false")
    web_acceptance = web_contract.get("acceptance", {})
    require(web_acceptance.get("renderedBrowser") == "pending-v1.3-revalidation", "prior Web rendered evidence must not transfer to V1.3")
    require(web_acceptance.get("renderedBrowserEvidence") is None, "prior Web rendered evidence must be cleared")
    for key in ("accessibilityTreeNames", "forcedColorsAutomation", "rtlStructuralResilience", "allViewports200PercentTextReflow"):
        require(web_acceptance.get(key) == "pending-v1.3-revalidation", f"{key} must require V1.3 revalidation")

    gateways = read("app/src/main/java/com/goreecloud/appstore/platform/PlatformGateways.kt")
    for literal in (
        VERSION,
        TAG,
        REVISION,
        SOURCE_ANCHOR,
        ROLLBACK_VERSION,
        MATERIAL_RULE,
        'SYSTEM_SHELL_SCOPE = "Application"',
        "CONFORMANCE_ACCEPTED = false",
        'system = "GoreeCloud Manager"',
        "state = IntegrationState.NOT_CONNECTED",
    ):
        require(literal in gateways, f"PlatformGateways.kt missing {literal}")

    theme = read("app/src/main/java/com/goreecloud/appstore/ui/GlazeTheme.kt")
    for literal in ("GlazeFoundation", "FrostWhite", "Pearl", "IceBlue", "0xFF05070A", "DEEP_DARK"):
        require(literal in theme, f"Android V1.3 source mapping missing {literal}")
    for forbidden in ("DeepTeal", "MineralTeal", "SoftAqua", "SoftAmber", "ChampagneGold"):
        require(forbidden not in theme, f"retired chromatic material mapping returned: {forbidden}")

    linux_styles = read("linux/resources/style.css")
    require("goreecloud_deep_teal" not in linux_styles, "Linux Deep Teal substrate mapping must remain absent")
    require("goreecloud_ice_blue" in linux_styles, "Linux bounded Ice Blue accent mapping missing")
    require("goreecloud_development_amber" in linux_styles, "Linux Development status accent mapping missing")
    require("@card_bg_color" in linux_styles, "Linux neutral host material mapping missing")

    web_styles = read("web/styles.css")
    for literal in (
        "--gc-frost-white: #F7F9FC",
        "--gc-pearl: #EFF2F6",
        "--gc-ice-blue: #8DB5FF",
        "--gc-development-amber: #D9A35F",
        "--target-min: 48px",
    ):
        require(literal in web_styles, f"Web V1.3 source mapping missing {literal}")
    require("--gc-deep-teal" not in web_styles, "historical Deep Teal substrate mapping must remain absent")
    require("backdrop-filter: blur(4px)" not in web_styles, "nested dialog backdrop blur must remain absent")

    gradle = read("app/build.gradle.kts")
    require(f'versionName = "{APP_VERSION}"' in gradle, "versionName mismatch")
    require(f"versionCode = {APP_VERSION_CODE}" in gradle, "versionCode mismatch")

    print(
        f"GLAZE UI V1.3 source mapping validated for Android + Linux + Web: "
        f"{VERSION} @ {REVISION}; conformance=false production=false"
    )


if __name__ == "__main__":
    main()
