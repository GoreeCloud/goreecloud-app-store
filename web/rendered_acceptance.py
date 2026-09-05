#!/usr/bin/env python3
"""Bounded actual-browser acceptance for GoreeCloud App Store Web Development.

This is Development evidence only. It validates durable rendered behavior in a
GitHub-hosted Chrome environment without claiming screen-reader, human visual,
production-hosting, cross-browser, or representative physical-device acceptance.
"""

from __future__ import annotations

import base64
import json
import os
from dataclasses import dataclass
from pathlib import Path

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.select import Select
from selenium.webdriver.support.ui import WebDriverWait

BASE_URL = os.environ.get("GOREECLOUD_APP_STORE_WEB_URL", "http://127.0.0.1:8766").rstrip("/")
EVIDENCE_DIR = Path(os.environ.get("GOREECLOUD_APP_STORE_WEB_EVIDENCE", ".artifacts/web-rendered/evidence"))
MIN_TARGET_PX = 48


@dataclass(frozen=True)
class Viewport:
    name: str
    width: int
    height: int


@dataclass(frozen=True)
class Appearance:
    name: str
    scheme: str
    expected_canvas: str


VIEWPORTS = (
    Viewport("compact", 390, 844),
    Viewport("expanded", 1280, 900),
)
APPEARANCES = (
    Appearance("light", "light", "rgb(244, 248, 248)"),
    Appearance("dark", "dark", "rgb(16, 26, 32)"),
)


def make_driver(viewport: Viewport) -> webdriver.Chrome:
    options = webdriver.ChromeOptions()
    options.add_argument("--headless=new")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument(f"--window-size={viewport.width},{viewport.height}")
    options.add_argument("--force-device-scale-factor=1")
    driver = webdriver.Chrome(options=options)
    driver.execute_cdp_cmd(
        "Emulation.setDeviceMetricsOverride",
        {
            "width": viewport.width,
            "height": viewport.height,
            "deviceScaleFactor": 1,
            "mobile": False,
            "screenWidth": viewport.width,
            "screenHeight": viewport.height,
        },
    )
    return driver


def set_media(driver: webdriver.Chrome, appearance: Appearance, reduced_motion: bool = False) -> None:
    driver.execute_cdp_cmd(
        "Emulation.setEmulatedMedia",
        {
            "media": "screen",
            "features": [
                {"name": "prefers-color-scheme", "value": appearance.scheme},
                {
                    "name": "prefers-reduced-motion",
                    "value": "reduce" if reduced_motion else "no-preference",
                },
            ],
        },
    )


def capture(driver: webdriver.Chrome, name: str) -> None:
    EVIDENCE_DIR.mkdir(parents=True, exist_ok=True)
    metrics = driver.execute_cdp_cmd("Page.getLayoutMetrics", {})
    size = metrics["cssContentSize"]
    width = max(1, min(int(size["width"]), 4096))
    height = max(1, min(int(size["height"]), 16384))
    screenshot = driver.execute_cdp_cmd(
        "Page.captureScreenshot",
        {
            "format": "png",
            "captureBeyondViewport": True,
            "fromSurface": True,
            "clip": {"x": 0, "y": 0, "width": width, "height": height, "scale": 1},
        },
    )
    (EVIDENCE_DIR / f"{name}.png").write_bytes(base64.b64decode(screenshot["data"]))


def wait_count(wait: WebDriverWait, expected: str) -> None:
    wait.until(lambda driver: driver.find_element(By.ID, "resultCount").text.strip() == expected)


def clear_search(driver: webdriver.Chrome, element) -> None:
    """Clear the search control through the same input event consumed by app.mjs."""
    driver.execute_script(
        """
        const input = arguments[0];
        input.value = '';
        input.dispatchEvent(new Event('input', {bubbles: true}));
        """,
        element,
    )


def assert_no_document_overflow(driver: webdriver.Chrome, context: str) -> None:
    scroll_width, client_width = driver.execute_script(
        "return [document.documentElement.scrollWidth, document.documentElement.clientWidth];"
    )
    if scroll_width > client_width + 2:
        raise AssertionError(
            f"{context}: document overflows horizontally: scrollWidth={scroll_width}, clientWidth={client_width}"
        )


def assert_target(element, context: str) -> None:
    rect = element.rect
    width = float(rect.get("width", 0))
    height = float(rect.get("height", 0))
    if width + 0.5 < MIN_TARGET_PX or height + 0.5 < MIN_TARGET_PX:
        raise AssertionError(
            f"{context}: target is {width:.1f}x{height:.1f}px; expected at least {MIN_TARGET_PX}x{MIN_TARGET_PX}px"
        )


def assert_visible_targets(driver: webdriver.Chrome, context: str) -> None:
    selectors = (
        ".tabs button",
        "#identitySelect",
        "#searchInput",
        "#categorySelect",
        ".details-button",
    )
    for selector in selectors:
        for element in driver.find_elements(By.CSS_SELECTOR, selector):
            if element.is_displayed():
                assert_target(element, f"{context} {selector}")


def assert_ax_names(driver: webdriver.Chrome, context: str) -> None:
    driver.execute_cdp_cmd("Accessibility.enable", {})
    nodes = driver.execute_cdp_cmd("Accessibility.getFullAXTree", {}).get("nodes", [])
    interactive_roles = {"button", "combobox", "searchbox", "link", "dialog"}
    missing: list[str] = []
    seen_roles: set[str] = set()
    for node in nodes:
        if node.get("ignored"):
            continue
        role = str((node.get("role") or {}).get("value") or "")
        if role not in interactive_roles:
            continue
        seen_roles.add(role)
        name = str((node.get("name") or {}).get("value") or "").strip()
        if not name:
            missing.append(role)
    if missing:
        raise AssertionError(f"{context}: unnamed accessibility-tree controls: {missing}")
    for required in ("button", "combobox", "searchbox"):
        if required not in seen_roles:
            raise AssertionError(f"{context}: expected accessibility-tree role {required!r} is absent")


def click_tab(driver: webdriver.Chrome, wait: WebDriverWait, tab: str, count: str) -> None:
    button = driver.find_element(By.CSS_SELECTOR, f'[data-tab="{tab}"]')
    button.click()
    wait.until(lambda d: button.get_attribute("aria-current") == "page")
    wait_count(wait, count)


def assert_identity_boundaries(driver: webdriver.Chrome, wait: WebDriverWait, context: str) -> None:
    identity = Select(driver.find_element(By.ID, "identitySelect"))

    identity.select_by_value("signed-out")
    wait_count(wait, "0 items")
    if driver.find_elements(By.CSS_SELECTOR, ".store-card"):
        raise AssertionError(f"{context}: signed-out fixture leaked protected catalog cards")
    if "concealed" not in driver.find_element(By.ID, "statusMessage").text.lower():
        raise AssertionError(f"{context}: signed-out concealment state is not explicit")

    identity.select_by_value("developer")
    wait_count(wait, "1 item")
    cards = driver.find_elements(By.CSS_SELECTOR, ".store-card h3")
    if [card.text for card in cards] != ["Mesh Center"]:
        raise AssertionError(f"{context}: developer fixture received unexpected catalog: {[card.text for card in cards]}")

    identity.select_by_value("administrator")
    wait_count(wait, "12 items")

    identity.select_by_value("standard")
    wait_count(wait, "10 items")


def assert_discovery_interactions(driver: webdriver.Chrome, wait: WebDriverWait, context: str) -> None:
    click_tab(driver, wait, "applications", "9 items")
    click_tab(driver, wait, "services", "1 item")
    click_tab(driver, wait, "discover", "10 items")

    search = driver.find_element(By.ID, "searchInput")
    clear_search(driver, search)
    search.send_keys("Notes")
    wait_count(wait, "1 item")
    names = [element.text for element in driver.find_elements(By.CSS_SELECTOR, ".store-card h3")]
    if names != ["GoreeCloud Notes"]:
        raise AssertionError(f"{context}: search returned unexpected entries: {names}")

    clear_search(driver, search)
    wait_count(wait, "10 items")
    category = Select(driver.find_element(By.ID, "categorySelect"))
    category.select_by_value("Productivity")
    wait.until(lambda d: d.find_element(By.ID, "resultCount").text.strip().endswith(("item", "items")))
    productivity_cards = driver.find_elements(By.CSS_SELECTOR, ".store-card")
    if not productivity_cards:
        raise AssertionError(f"{context}: entitled Productivity category unexpectedly empty")
    for card in productivity_cards:
        chips = [chip.text for chip in card.find_elements(By.CSS_SELECTOR, ".chip")]
        if "Productivity" not in chips:
            raise AssertionError(f"{context}: category filter widened outside Productivity")

    category.select_by_value("all")
    wait_count(wait, "10 items")


def assert_dialog_and_unavailable_states(driver: webdriver.Chrome, wait: WebDriverWait, context: str) -> None:
    first_details = driver.find_element(By.CSS_SELECTOR, ".details-button")
    first_details.click()
    dialog = wait.until(EC.visibility_of_element_located((By.ID, "productDialog")))
    if not dialog.get_attribute("open"):
        raise AssertionError(f"{context}: product details did not open as a modal dialog")
    disabled_action = dialog.find_element(By.CSS_SELECTOR, ".primary-action")
    if disabled_action.is_enabled():
        raise AssertionError(f"{context}: Development Install/Open action became enabled")
    assert_target(dialog.find_element(By.CSS_SELECTOR, ".dialog-close"), f"{context} dialog close")
    assert_ax_names(driver, f"{context} dialog")
    capture(driver, f"{context}-dialog")
    dialog.find_element(By.CSS_SELECTOR, ".dialog-close").click()
    wait.until(EC.invisibility_of_element_located((By.ID, "productDialog")))

    click_tab(driver, wait, "updates", "Unavailable")
    panel = driver.find_element(By.ID, "unavailablePanel")
    if not panel.is_displayed() or "unavailable" not in panel.text.lower():
        raise AssertionError(f"{context}: Updates did not remain fail-closed")

    click_tab(driver, wait, "library", "Unavailable")
    if not driver.find_element(By.ID, "unavailablePanel").is_displayed():
        raise AssertionError(f"{context}: Library did not remain fail-closed")

    click_tab(driver, wait, "discover", "10 items")


def assert_keyboard_and_resilience(driver: webdriver.Chrome, wait: WebDriverWait, context: str) -> None:
    driver.get(f"{BASE_URL}/index.html")
    wait_count(wait, "10 items")
    body = driver.find_element(By.TAG_NAME, "body")
    body.send_keys(Keys.TAB)
    active = driver.switch_to.active_element
    if "skip-link" not in (active.get_attribute("class") or ""):
        raise AssertionError(f"{context}: first keyboard stop is not the visible skip link")
    if "skip to catalog" not in (active.text or "").lower():
        raise AssertionError(f"{context}: skip link lacks a stable accessible name")

    driver.execute_script("document.documentElement.style.fontSize = '200%';")
    assert_no_document_overflow(driver, f"{context} 200%-text")
    assert_visible_targets(driver, f"{context} 200%-text")
    capture(driver, f"{context}-200pct-text")
    driver.execute_script("document.documentElement.style.fontSize = ''; if (document.activeElement) document.activeElement.blur();")


def run_case(viewport: Viewport, appearance: Appearance) -> dict[str, str]:
    context = f"{viewport.name}-{appearance.name}"
    driver = make_driver(viewport)
    try:
        set_media(driver, appearance)
        driver.get(f"{BASE_URL}/index.html")
        wait = WebDriverWait(driver, 15)
        wait_count(wait, "10 items")

        canvas = driver.execute_script("return getComputedStyle(document.documentElement).backgroundColor;")
        if canvas != appearance.expected_canvas:
            raise AssertionError(f"{context}: rendered canvas {canvas!r}; expected {appearance.expected_canvas!r}")
        if bool(driver.execute_script("return matchMedia('(prefers-color-scheme: dark)').matches;")) != (
            appearance.scheme == "dark"
        ):
            raise AssertionError(f"{context}: color-scheme media emulation did not apply")

        assert_no_document_overflow(driver, context)
        assert_visible_targets(driver, context)
        assert_ax_names(driver, context)
        assert_identity_boundaries(driver, wait, context)
        assert_discovery_interactions(driver, wait, context)
        assert_dialog_and_unavailable_states(driver, wait, context)

        set_media(driver, appearance, reduced_motion=True)
        if not driver.execute_script("return matchMedia('(prefers-reduced-motion: reduce)').matches;"):
            raise AssertionError(f"{context}: Reduced Motion media state was not activated")
        assert_no_document_overflow(driver, f"{context} reduced-motion")

        if viewport.name == "compact":
            assert_keyboard_and_resilience(driver, wait, context)

        driver.get(f"{BASE_URL}/index.html")
        wait_count(wait, "10 items")
        capture(driver, f"{context}-discover")
        return {
            "case": context,
            "browser": str(driver.capabilities.get("browserName", "unknown")),
            "browserVersion": str(driver.capabilities.get("browserVersion", "unknown")),
            "platform": str(driver.capabilities.get("platformName", "unknown")),
            "result": "passed",
        }
    except Exception:
        try:
            capture(driver, f"{context}-failure")
        except Exception:
            pass
        raise
    finally:
        driver.quit()


def main() -> None:
    EVIDENCE_DIR.mkdir(parents=True, exist_ok=True)
    results: list[dict[str, str]] = []
    for viewport in VIEWPORTS:
        for appearance in APPEARANCES:
            results.append(run_case(viewport, appearance))

    report = {
        "application": "goreecloud-app-store",
        "clientVersion": "0.1.0-dev",
        "lifecycle": "development",
        "productionAcceptance": False,
        "scope": {
            "actualBrowser": "GitHub-hosted Chrome headless",
            "viewports": [viewport.__dict__ for viewport in VIEWPORTS],
            "appearances": [appearance.name for appearance in APPEARANCES],
            "automatedAccessibilityTreeNames": True,
            "keyboardSkipLink": True,
            "minimumTargetPx": MIN_TARGET_PX,
            "reducedMotion": True,
            "compact200PercentTextReflow": True,
            "screenReaderAcceptance": False,
            "humanVisualExcellence": False,
            "productionHostingAcceptance": False,
            "crossBrowserAcceptance": False,
        },
        "results": results,
    }
    (EVIDENCE_DIR / "acceptance-report.json").write_text(
        json.dumps(report, indent=2, sort_keys=True) + "\n", encoding="utf-8"
    )
    print(f"Web rendered Development acceptance passed across {len(results)} browser cases")


if __name__ == "__main__":
    main()
