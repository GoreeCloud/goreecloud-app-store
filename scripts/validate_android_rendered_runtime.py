#!/usr/bin/env python3
"""Rendered/native Android emulator evidence for GoreeCloud App Store development.

This gate executes the exact checked-out development APK through adb and records
bounded responsive/accessibility evidence. Emulator evidence is deliberately
not treated as TalkBack, physical-device, production-signing, Glaze UI
conformance, human Visual Excellence, release, or production acceptance.
"""
from __future__ import annotations

import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / ".artifacts" / "android-rendered"
PACKAGE = "com.goreecloud.appstore.dev"
ACTIVITY = f"{PACKAGE}/com.goreecloud.appstore.MainActivity"
EXPECTED_VERSION = "0.1.4-dev"
SHA40 = re.compile(r"^[0-9a-f]{40}$")
BOUNDS = re.compile(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]")
SIZE = re.compile(r"(?:Override|Physical) size:\s*(\d+)x(\d+)")


def run(*args: str, text: bool = True, check: bool = True) -> subprocess.CompletedProcess:
    return subprocess.run(args, check=check, text=text, capture_output=True)


def adb(serial: str, *args: str, text: bool = True, check: bool = True) -> subprocess.CompletedProcess:
    return run("adb", "-s", serial, *args, text=text, check=check)


def exact_source_revision() -> str:
    revision = run("git", "-C", str(ROOT), "rev-parse", "HEAD").stdout.strip()
    if not SHA40.fullmatch(revision):
        raise SystemExit(f"could not resolve exact checked-out source revision: {revision!r}")
    expected = os.environ.get("APP_STORE_SOURCE_REVISION", "").strip()
    if expected:
        if not SHA40.fullmatch(expected):
            raise SystemExit(f"invalid expected App Store source revision: {expected!r}")
        if expected != revision:
            raise SystemExit(
                f"checked-out source revision {revision} does not match expected exact head {expected}"
            )
    return revision


def serial_from_adb() -> str:
    explicit = os.environ.get("ANDROID_SERIAL", "").strip()
    if explicit:
        return explicit
    result = run("adb", "devices")
    devices: list[str] = []
    for line in result.stdout.splitlines()[1:]:
        cols = line.split()
        if len(cols) >= 2 and cols[1] == "device":
            devices.append(cols[0])
    if len(devices) != 1:
        raise SystemExit(f"expected exactly one ready Android target, found {devices}")
    return devices[0]


def prop(serial: str, name: str) -> str:
    return adb(serial, "shell", "getprop", name).stdout.strip()


def current_density(serial: str) -> int:
    result = adb(serial, "shell", "wm", "density").stdout
    matches = re.findall(r"(?:Override|Physical) density:\s*(\d+)", result)
    if matches:
        return int(matches[-1])
    raw = prop(serial, "ro.sf.lcd_density")
    if raw.isdigit():
        return int(raw)
    raise SystemExit(f"could not resolve Android density from: {result!r}")


def current_size(serial: str) -> tuple[int, int]:
    output = adb(serial, "shell", "wm", "size").stdout
    matches = SIZE.findall(output)
    if not matches:
        raise SystemExit(f"could not resolve Android display size from: {output!r}")
    width, height = matches[-1]
    return int(width), int(height)


def configure_display(serial: str, width: int, height: int, density: int = 160) -> None:
    adb(serial, "shell", "wm", "size", f"{width}x{height}")
    adb(serial, "shell", "wm", "density", str(density))
    time.sleep(0.8)
    actual = current_size(serial)
    actual_density = current_density(serial)
    if actual != (width, height) or actual_density != density:
        raise SystemExit(
            f"display override failed: requested {(width, height, density)}, "
            f"resolved {(actual[0], actual[1], actual_density)}"
        )


def set_font_scale(serial: str, scale: float) -> None:
    adb(serial, "shell", "settings", "put", "system", "font_scale", str(scale))


def set_night(serial: str, mode: str) -> None:
    if mode not in {"yes", "no"}:
        raise ValueError(mode)
    adb(serial, "shell", "cmd", "uimode", "night", mode)
    time.sleep(0.5)


def launch(serial: str) -> None:
    adb(serial, "shell", "am", "force-stop", PACKAGE)
    result = adb(serial, "shell", "am", "start", "-W", "-n", ACTIVITY).stdout
    if "Status: ok" not in result:
        raise SystemExit(f"activity launch failed:\n{result}")
    time.sleep(1.0)


def dump_ui(serial: str, name: str | None = None) -> ET.Element:
    remote = "/sdcard/goreecloud-app-store-ui.xml"
    adb(serial, "shell", "uiautomator", "dump", remote)
    raw = adb(serial, "exec-out", "cat", remote).stdout
    if name:
        (OUT / name).write_text(raw, encoding="utf-8")
    return ET.fromstring(raw)


def all_nodes(root: ET.Element) -> list[ET.Element]:
    return list(root.iter("node"))


def find_text(root: ET.Element, value: str) -> ET.Element | None:
    for node in all_nodes(root):
        if node.attrib.get("text") == value:
            return node
    return None


def find_fragment(root: ET.Element, fragment: str) -> ET.Element | None:
    for node in all_nodes(root):
        if fragment in node.attrib.get("text", ""):
            return node
    return None


def require_fragment(root: ET.Element, fragment: str) -> ET.Element:
    node = find_fragment(root, fragment)
    if node is None:
        raise SystemExit(f"required UI fragment not found: {fragment}")
    return node


def bounds(node: ET.Element) -> tuple[int, int, int, int]:
    match = BOUNDS.fullmatch(node.attrib.get("bounds", ""))
    if not match:
        raise SystemExit(f"invalid/missing UI bounds: {node.attrib.get('bounds')!r}")
    return tuple(map(int, match.groups()))


def nearest_clickable(root: ET.Element, node: ET.Element) -> ET.Element:
    if node.attrib.get("clickable") == "true":
        return node
    parents = {child: parent for parent in root.iter() for child in parent}
    current = node
    while current in parents:
        current = parents[current]
        if current.attrib.get("clickable") == "true":
            return current
    raise SystemExit(f"no clickable semantic ancestor for UI text: {node.attrib.get('text')!r}")


def target_size_dp(node: ET.Element, dpi: int) -> tuple[float, float]:
    x1, y1, x2, y2 = bounds(node)
    factor = 160.0 / dpi
    return (x2 - x1) * factor, (y2 - y1) * factor


def assert_target_floor(root: ET.Element, text: str, dpi: int, floor: float = 48.0) -> dict[str, float]:
    # ElementTree leaf elements are falsey even when they are valid matches, so
    # select exact text explicitly before falling back to a substring match.
    node = find_text(root, text)
    if node is None:
        node = find_fragment(root, text)
    if node is None:
        raise SystemExit(f"target label not found: {text}")
    target = nearest_clickable(root, node)
    width, height = target_size_dp(target, dpi)
    if width < floor - 1.0 or height < floor - 1.0:
        raise SystemExit(
            f"{text!r} target below {floor:.0f} dp floor: {width:.2f} x {height:.2f} dp"
        )
    return {"widthDp": round(width, 2), "heightDp": round(height, 2)}


def screenshot(serial: str, name: str) -> str:
    path = OUT / name
    result = adb(serial, "exec-out", "screencap", "-p", text=False)
    path.write_bytes(result.stdout)
    payload = path.read_bytes()
    if not payload.startswith(b"\x89PNG\r\n\x1a\n"):
        raise SystemExit(f"invalid screenshot PNG: {path}")
    return hashlib.sha256(payload).hexdigest()


def tap_node(serial: str, node: ET.Element) -> None:
    x1, y1, x2, y2 = bounds(node)
    adb(serial, "shell", "input", "tap", str((x1 + x2) // 2), str((y1 + y2) // 2))
    time.sleep(0.5)


def tap_text(serial: str, value: str) -> None:
    root = dump_ui(serial)
    node = find_text(root, value)
    if node is None:
        raise SystemExit(f"cannot tap missing UI text: {value}")
    try:
        node = nearest_clickable(root, node)
    except SystemExit:
        # Text rendered inside a platform popup can expose the text bounds without
        # a separate clickable ancestor; tapping the text center remains a bounded
        # native interaction check rather than a semantic-target assertion.
        pass
    tap_node(serial, node)


def swipe_forward(serial: str) -> None:
    width, height = current_size(serial)
    x = width // 2
    adb(
        serial,
        "shell",
        "input",
        "swipe",
        str(x),
        str(int(height * 0.78)),
        str(x),
        str(int(height * 0.34)),
        "280",
    )
    time.sleep(0.35)


def visible_after_scroll(serial: str, fragment: str, attempts: int = 8) -> tuple[ET.Element, ET.Element]:
    for _ in range(attempts + 1):
        root = dump_ui(serial)
        node = find_fragment(root, fragment)
        if node is not None:
            return root, node
        swipe_forward(serial)
    raise SystemExit(f"UI fragment did not become reachable after scrolling: {fragment}")


def switch_session(serial: str, current_compact: str, menu_name: str, expected_compact: str) -> None:
    tap_text(serial, current_compact)
    menu = dump_ui(serial)
    require_fragment(menu, menu_name)
    tap_text(serial, menu_name)
    root = dump_ui(serial)
    require_fragment(root, expected_compact)


def rects_overlap(a: tuple[int, int, int, int], b: tuple[int, int, int, int]) -> bool:
    ax1, ay1, ax2, ay2 = a
    bx1, by1, bx2, by2 = b
    return max(ax1, bx1) < min(ax2, bx2) and max(ay1, by1) < min(ay2, by2)


def assert_texts_do_not_overlap(root: ET.Element, first: str, second: str) -> None:
    a = find_text(root, first)
    b = find_text(root, second)
    if a is None or b is None:
        raise SystemExit(f"overlap assertion labels missing: {first!r}, {second!r}")
    if rects_overlap(bounds(a), bounds(b)):
        raise SystemExit(f"UI text bounds overlap: {first!r} and {second!r}")


def assert_navigation_no_overlap(root: ET.Element) -> None:
    labels = ["Discover", "Apps", "Services", "Updates", "Library"]
    nodes = []
    for label in labels:
        node = find_text(root, label)
        if node is None:
            raise SystemExit(f"navigation label missing: {label}")
        nodes.append((label, bounds(node)))
    for index, (left_label, left) in enumerate(nodes):
        for right_label, right in nodes[index + 1 :]:
            if rects_overlap(left, right):
                raise SystemExit(f"navigation label overlap: {left_label!r} and {right_label!r}")


def base_assertions(root: ET.Element) -> None:
    require_fragment(root, "GoreeCloud")
    require_fragment(root, "App Store")
    require_fragment(root, "Development")


def case_phone_light(serial: str) -> dict:
    configure_display(serial, 390, 844)
    set_font_scale(serial, 1.0)
    set_night(serial, "no")
    launch(serial)
    root = dump_ui(serial, "phone-light-standard.xml")
    base_assertions(root)
    require_fragment(root, "Standard")
    require_fragment(root, "4 available")
    require_fragment(root, "Search apps and services")
    dpi = current_density(serial)
    targets = {
        "account": assert_target_floor(root, "Standard", dpi),
        "discover": assert_target_floor(root, "Discover", dpi),
        "apps": assert_target_floor(root, "Apps", dpi),
        "services": assert_target_floor(root, "Services", dpi),
        "updates": assert_target_floor(root, "Updates", dpi),
        "library": assert_target_floor(root, "Library", dpi),
    }
    browser_root, _ = visible_after_scroll(serial, "GoreeCloud Browser")
    targets["browserCard"] = assert_target_floor(browser_root, "GoreeCloud Browser", dpi)
    sha = screenshot(serial, "phone-light-standard.png")
    return {
        "id": "phone-light-standard",
        "sizeDp": [390, 844],
        "fontScale": 1.0,
        "nightMode": False,
        "targets": targets,
        "screenshotSha256": sha,
    }


def case_phone_dark_admin(serial: str) -> dict:
    configure_display(serial, 390, 844)
    set_font_scale(serial, 1.0)
    set_night(serial, "yes")
    launch(serial)
    switch_session(serial, "Standard", "Administrator demo", "Administrator")
    root = dump_ui(serial, "phone-dark-administrator.xml")
    base_assertions(root)
    require_fragment(root, "Administrator")
    require_fragment(root, "6 available")
    dpi = current_density(serial)
    account = assert_target_floor(root, "Administrator", dpi)
    manager_root, _ = visible_after_scroll(serial, "GoreeCloud Manager", attempts=12)
    manager_target = assert_target_floor(manager_root, "GoreeCloud Manager", dpi)
    sha = screenshot(serial, "phone-dark-administrator.png")
    return {
        "id": "phone-dark-administrator",
        "sizeDp": [390, 844],
        "fontScale": 1.0,
        "nightMode": True,
        "targets": {"account": account, "managerCard": manager_target},
        "screenshotSha256": sha,
    }


def case_phone_large_text(serial: str) -> dict:
    configure_display(serial, 390, 844)
    set_font_scale(serial, 2.0)
    set_night(serial, "no")
    launch(serial)
    root = dump_ui(serial, "phone-large-text.xml")
    base_assertions(root)
    require_fragment(root, "Standard")
    assert_texts_do_not_overlap(root, "App Store", "Standard")
    assert_navigation_no_overlap(root)
    dpi = current_density(serial)
    targets = {
        "account": assert_target_floor(root, "Standard", dpi),
        "discover": assert_target_floor(root, "Discover", dpi),
        "library": assert_target_floor(root, "Library", dpi),
    }
    sha = screenshot(serial, "phone-large-text.png")
    return {
        "id": "phone-large-text-200-percent",
        "sizeDp": [390, 844],
        "fontScale": 2.0,
        "nightMode": False,
        "targets": targets,
        "screenshotSha256": sha,
    }


def case_tablet(serial: str) -> dict:
    configure_display(serial, 820, 1180)
    set_font_scale(serial, 1.0)
    set_night(serial, "no")
    launch(serial)
    root = dump_ui(serial, "tablet-light-standard.xml")
    base_assertions(root)
    require_fragment(root, "Standard")
    require_fragment(root, "4 available")
    require_fragment(root, "Search apps and services")
    dpi = current_density(serial)
    targets = {
        "account": assert_target_floor(root, "Standard", dpi),
        "discover": assert_target_floor(root, "Discover", dpi),
    }
    visible_after_scroll(serial, "GoreeCloud Browser", attempts=4)
    sha = screenshot(serial, "tablet-light-standard.png")
    return {
        "id": "tablet-light-standard",
        "sizeDp": [820, 1180],
        "fontScale": 1.0,
        "nightMode": False,
        "targets": targets,
        "screenshotSha256": sha,
    }


def main() -> int:
    OUT.mkdir(parents=True, exist_ok=True)
    source_revision = exact_source_revision()
    serial = serial_from_adb()
    original_font_scale = adb(serial, "shell", "settings", "get", "system", "font_scale").stdout.strip() or "1.0"

    try:
        cases = [
            case_phone_light(serial),
            case_phone_dark_admin(serial),
            case_phone_large_text(serial),
            case_tablet(serial),
        ]
    finally:
        adb(serial, "shell", "settings", "put", "system", "font_scale", original_font_scale, check=False)
        adb(serial, "shell", "cmd", "uimode", "night", "auto", check=False)
        adb(serial, "shell", "wm", "size", "reset", check=False)
        adb(serial, "shell", "wm", "density", "reset", check=False)
        adb(serial, "shell", "am", "force-stop", PACKAGE, check=False)

    evidence = {
        "schemaVersion": 1,
        "application": "GoreeCloud App Store Dev",
        "applicationVersion": EXPECTED_VERSION,
        "sourceRevision": source_revision,
        "glazeUiTarget": "2.1.0",
        "platform": "android-emulator-rendered",
        "physicalDevice": False,
        "talkBackAccepted": False,
        "switchAccessAccepted": False,
        "humanVisualExcellenceAccepted": False,
        "glazeUiConformanceAccepted": False,
        "productionAcceptance": False,
        "device": {
            "serial": serial,
            "model": prop(serial, "ro.product.model"),
            "release": prop(serial, "ro.build.version.release"),
            "sdk": prop(serial, "ro.build.version.sdk"),
            "fingerprint": prop(serial, "ro.build.fingerprint"),
        },
        "cases": cases,
        "boundary": (
            "Emulator install/launch/responsive/target-floor/fixture-state evidence only. "
            "It is not representative physical-device, TalkBack, switch-access, OEM, "
            "production-signing, distribution, human Visual Excellence, full Glaze UI "
            "conformance, release, or production acceptance."
        ),
    }
    (OUT / "android-rendered-evidence.json").write_text(
        json.dumps(evidence, indent=2) + "\n", encoding="utf-8"
    )
    print(
        f"GoreeCloud App Store Android rendered emulator evidence passed: "
        f"{len(cases)} cases at {source_revision}"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
