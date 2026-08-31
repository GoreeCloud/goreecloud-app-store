#!/usr/bin/env python3
"""Fail-closed repository contract check for the App Store Glaze UI 2.1 adoption candidate."""

from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
EXPECTED_VERSION = "2.1.0"
EXPECTED_TAG = "v2.1.0"
EXPECTED_REVISION = "c49113eb8b93c267613fdf1bbca1f814495acad7"
EXPECTED_APP_VERSION = "0.1.4-dev"
EXPECTED_APP_VERSION_CODE = "5"


def require(condition: bool, message: str) -> None:
    if not condition:
        raise SystemExit(f"Glaze UI adoption validation failed: {message}")


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def load_json(path: str) -> dict:
    return json.loads(read(path))


def main() -> None:
    adoption = load_json("contracts/glaze-ui-adoption.json")
    integrations = load_json("contracts/platform-integrations.json")

    require(adoption.get("application") == "goreecloud-app-store", "unexpected application identifier")
    require(adoption.get("status") == "adoption-candidate", "status must remain adoption-candidate until acceptance")
    require(adoption.get("targetVersion") == EXPECTED_VERSION, "targetVersion is not current Stable")
    require(adoption.get("requiredTargetVersion") == EXPECTED_VERSION, "requiredTargetVersion mismatch")
    require(adoption.get("stableReleaseTag") == EXPECTED_TAG, "Stable release tag mismatch")
    require(adoption.get("stableReleaseRevision") == EXPECTED_REVISION, "Stable release revision mismatch")
    require(adoption.get("canonicalRepository") == "GoreeCloud/goreecloud-glaze-ui", "canonical Glaze repository mismatch")

    android = adoption.get("nativeMapping", {})
    require(android.get("platform") == "Android", "Android native platform mapping missing")
    require(android.get("framework") == "Jetpack Compose Material 3", "Android native framework mapping mismatch")
    require(android.get("nativeTheme") == "app/src/main/java/com/goreecloud/appstore/ui/GlazeTheme.kt", "Android native theme path mismatch")
    require(android.get("generalTargetFloorDp") == 48, "Android general target floor must be 48 dp")
    require(android.get("touchAssistanceTargetFloorDp") == 56, "Android Touch Assistance target floor must be 56 dp")
    require(android.get("glazeMotion") == "not-consumed", "Android Glaze Motion must not be claimed as Stable consumption")

    linux = adoption.get("linuxNativeMapping", {})
    require(linux.get("platform") == "Linux", "Linux native platform mapping missing")
    require(linux.get("framework") == "GTK 4 + libadwaita", "Linux native framework mapping mismatch")
    require(linux.get("nativeTheme") == "linux/resources/style.css", "Linux native theme path mismatch")
    require(linux.get("hostThemeAdaptive") is True, "Linux mapping must remain host-theme adaptive")
    require(linux.get("contentSurfacesSolid") is True, "Linux content-surface boundary must remain solid")
    require(linux.get("glazeMotion") == "not-consumed", "Linux Glaze Motion must not be claimed as Stable consumption")
    for key in (
        "reducedTransparencySolidFallback",
        "increasedContrast",
        "largeText200Percent",
        "keyboardFocusAccessibility",
        "supportedDesktopFormFactors",
    ):
        require(linux.get(key) == "pending-acceptance", f"Linux {key} must remain pending until evidence exists")

    acceptance = adoption.get("acceptance", {})
    require(acceptance.get("automatedContract") is True, "automated contract must be enabled")
    require(acceptance.get("productionEligible") is False, "Glaze adoption must not imply production eligibility")
    require(acceptance.get("conformanceAccepted") is False, "Glaze conformance must remain unaccepted")
    for key in (
        "renderedAcceptance",
        "nativeAccessibilityAcceptance",
        "representativePhysicalDeviceAcceptance",
        "supportedFormFactorAcceptance",
    ):
        require(acceptance.get(key) == "pending", f"{key} must remain pending until evidence exists")

    require(integrations.get("productionAcceptance") is False, "application productionAcceptance must remain false")
    clients = integrations.get("nativeClients", {})
    require(clients.get("android", {}).get("implemented") is True, "Android native client record missing")
    require(clients.get("linux", {}).get("implemented") is True, "Linux native client record missing")
    require(clients.get("linux", {}).get("framework") == "GTK 4 + libadwaita", "Linux integration framework mismatch")
    glaze = integrations.get("integrations", {}).get("glazeUi", {})
    require(glaze.get("target") == EXPECTED_VERSION, "platform Glaze target mismatch")
    require(glaze.get("stableReleaseTag") == EXPECTED_TAG, "platform Glaze release tag mismatch")
    require(glaze.get("stableReleaseRevision") == EXPECTED_REVISION, "platform Glaze release revision mismatch")
    require(glaze.get("adoptionStatus") == "adoption-candidate", "platform Glaze adoption status mismatch")
    require(glaze.get("automatedContract") is True, "platform Glaze automatedContract must be true")
    require(glaze.get("conformanceAccepted") is False, "platform Glaze conformance must remain false")

    gateways = read("app/src/main/java/com/goreecloud/appstore/platform/PlatformGateways.kt")
    for literal in (EXPECTED_VERSION, EXPECTED_TAG, EXPECTED_REVISION, "CONFORMANCE_ACCEPTED = false"):
        require(literal in gateways, f"PlatformGateways.kt is missing {literal!r}")

    android_theme = read("app/src/main/java/com/goreecloud/appstore/ui/GlazeTheme.kt")
    for palette_literal in (
        "0xFFF4F7FD",
        "0xFFE8EEFF",
        "0xFF3F57D6",
        "0xFF0C0E14",
        "0xFF262F43",
        "0xFF93A6FF",
    ):
        require(palette_literal in android_theme, f"Android Glaze 2.1 palette mapping is missing {palette_literal}")
    require("Content planes stay solid" in android_theme, "Android material-role boundary is undocumented")

    linux_theme = read("linux/resources/style.css")
    for marker in (
        ".glaze-header",
        ".glaze-navigation",
        ".glaze-search",
        ".store-card",
        "@card_bg_color",
        "@accent_bg_color",
    ):
        require(marker in linux_theme, f"Linux Glaze mapping is missing {marker!r}")

    gradle = read("app/build.gradle.kts")
    require(f'versionName = "{EXPECTED_APP_VERSION}"' in gradle, "Android development versionName mismatch")
    require(f"versionCode = {EXPECTED_APP_VERSION_CODE}" in gradle, "Android development versionCode mismatch")

    documentation = {
        "README.md": read("README.md"),
        "SPECIFICATIONS.md": read("SPECIFICATIONS.md"),
        "USER-MANUAL.md": read("USER-MANUAL.md"),
        "docs/GLAZE_UI_ADOPTION.md": read("docs/GLAZE_UI_ADOPTION.md"),
    }
    for path, text in documentation.items():
        require(EXPECTED_VERSION in text, f"{path} does not record current Stable Glaze UI {EXPECTED_VERSION}")

    stale_current_claims = (
        "Glaze UI 2.0.0 consumer target",
        "Current consumer target: **2.0.0 Stable**",
        "current design-system target is 2.0.0",
    )
    for path, text in documentation.items():
        for phrase in stale_current_claims:
            require(phrase not in text, f"{path} still contains stale current-target wording: {phrase}")

    print(
        "Glaze UI 2.1 adoption contract validated: "
        f"status=adoption-candidate target={EXPECTED_VERSION} release={EXPECTED_TAG} "
        "nativeMappings=android,linux conformance=false productionEligible=false"
    )


if __name__ == "__main__":
    main()
