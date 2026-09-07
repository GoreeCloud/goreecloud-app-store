#!/usr/bin/env python3
"""Bounded keyboard/modal acceptance for GoreeCloud App Store Web Development.

This verifies browser-level focus and Development release-channel concealment only.
It is not screen-reader or other assistive-technology acceptance and does not
establish production acceptance.
"""

from __future__ import annotations

import json
import os
from pathlib import Path

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.select import Select
from selenium.webdriver.support.ui import WebDriverWait

BASE_URL = os.environ.get("GOREECLOUD_APP_STORE_WEB_URL", "http://127.0.0.1:8766").rstrip("/")
EVIDENCE_DIR = Path(os.environ.get("GOREECLOUD_APP_STORE_WEB_EVIDENCE", ".artifacts/web-rendered/evidence"))


def make_driver() -> webdriver.Chrome:
    options = webdriver.ChromeOptions()
    options.add_argument("--headless=new")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--window-size=1280,900")
    return webdriver.Chrome(options=options)


def wait_count(wait: WebDriverWait, expected: str) -> None:
    wait.until(lambda driver: driver.find_element(By.ID, "resultCount").text.strip() == expected)


def assert_inside_dialog(driver: webdriver.Chrome, dialog, context: str) -> None:
    active = driver.switch_to.active_element
    inside = driver.execute_script("return arguments[0].contains(arguments[1]);", dialog, active)
    if not inside:
        raise AssertionError(f"{context}: keyboard focus escaped the open modal dialog")


def release_option_values(driver: webdriver.Chrome) -> list[str]:
    select = driver.find_element(By.ID, "dialogReleaseChannel")
    return [option.get_attribute("value") for option in select.find_elements(By.TAG_NAME, "option")]


def open_first_details(driver: webdriver.Chrome, wait: WebDriverWait):
    first_card = driver.find_element(By.CSS_SELECTOR, ".store-card")
    opener = first_card.find_element(By.CSS_SELECTOR, ".details-button")
    opener.click()
    return opener, wait.until(EC.visibility_of_element_located((By.ID, "productDialog")))


def close_dialog(driver: webdriver.Chrome, wait: WebDriverWait) -> None:
    driver.find_element(By.CSS_SELECTOR, "#productDialog .dialog-close").click()
    wait.until(EC.invisibility_of_element_located((By.ID, "productDialog")))


def assert_release_channel_boundaries(driver: webdriver.Chrome, wait: WebDriverWait) -> None:
    identity = Select(driver.find_element(By.ID, "identitySelect"))

    identity.select_by_value("standard")
    wait_count(wait, "10 items")
    _, dialog = open_first_details(driver, wait)
    if release_option_values(driver) != ["stable"]:
        raise AssertionError("Standard fixture must expose only Stable in the release-channel selector")
    if driver.find_element(By.ID, "dialogChannel").text.strip() != "Development":
        raise AssertionError("legacy catalog lifecycle must remain Development fixture metadata")
    dialog_text = dialog.text.lower()
    for concealed in ("debug", "beta", "release candidate"):
        if concealed in dialog_text:
            raise AssertionError(f"Standard fixture leaked unauthorized release channel name: {concealed}")
    close_dialog(driver, wait)

    identity.select_by_value("administrator")
    wait_count(wait, "12 items")
    _, dialog = open_first_details(driver, wait)
    if release_option_values(driver) != ["stable", "release-candidate", "beta"]:
        raise AssertionError("Administrator fixture must expose Stable, RC, and Beta only")
    if "debug" in dialog.text.lower():
        raise AssertionError("Administrator fixture leaked unauthorized Debug channel metadata")
    close_dialog(driver, wait)

    identity.select_by_value("developer")
    wait_count(wait, "1 item")
    _, _ = open_first_details(driver, wait)
    if release_option_values(driver) != ["stable", "release-candidate", "beta", "debug"]:
        raise AssertionError("Developer fixture must expose Stable, RC, Beta, and Debug")
    close_dialog(driver, wait)

    identity.select_by_value("signed-out")
    wait_count(wait, "0 items")
    if driver.find_elements(By.CSS_SELECTOR, ".details-button"):
        raise AssertionError("Signed-out fixture must not expose product details or release selectors")

    identity.select_by_value("standard")
    wait_count(wait, "10 items")


def main() -> None:
    EVIDENCE_DIR.mkdir(parents=True, exist_ok=True)
    driver = make_driver()
    try:
        driver.get(f"{BASE_URL}/index.html")
        wait = WebDriverWait(driver, 15)
        wait_count(wait, "10 items")

        first_card = driver.find_element(By.CSS_SELECTOR, ".store-card")
        product_name = first_card.find_element(By.TAG_NAME, "h3").text.strip()
        opener = first_card.find_element(By.CSS_SELECTOR, ".details-button")
        expected_name = f"View details for {product_name}"
        if opener.get_attribute("aria-label") != expected_name:
            raise AssertionError(
                f"details button accessible name mismatch: {opener.get_attribute('aria-label')!r}; expected {expected_name!r}"
            )

        opener.send_keys(Keys.ENTER)
        dialog = wait.until(EC.visibility_of_element_located((By.ID, "productDialog")))
        if dialog.get_attribute("aria-describedby") != "dialogSummary":
            raise AssertionError("product dialog must be described by dialogSummary")
        summary = driver.find_element(By.ID, "dialogSummary").text.strip()
        if not summary:
            raise AssertionError("product dialog summary must be populated")

        active = driver.switch_to.active_element
        if "dialog-close" not in (active.get_attribute("class") or ""):
            raise AssertionError("opening product details must move focus to the dialog close control")

        for step in range(6):
            driver.switch_to.active_element.send_keys(Keys.TAB)
            assert_inside_dialog(driver, dialog, f"tab step {step + 1}")

        driver.switch_to.active_element.send_keys(Keys.ESCAPE)
        wait.until(EC.invisibility_of_element_located((By.ID, "productDialog")))
        if driver.switch_to.active_element != opener:
            raise AssertionError("Escape-closing product details must restore focus to the invoking details button")

        opener.send_keys(Keys.ENTER)
        wait.until(EC.visibility_of_element_located((By.ID, "productDialog")))
        close = driver.find_element(By.CSS_SELECTOR, "#productDialog .dialog-close")
        close.click()
        wait.until(EC.invisibility_of_element_located((By.ID, "productDialog")))
        if driver.switch_to.active_element != opener:
            raise AssertionError("close-button dismissal must restore focus to the invoking details button")

        assert_release_channel_boundaries(driver, wait)

        report = {
            "application": "goreecloud-app-store",
            "lifecycle": "development",
            "productionAcceptance": False,
            "browser": str(driver.capabilities.get("browserName", "unknown")),
            "browserVersion": str(driver.capabilities.get("browserVersion", "unknown")),
            "productSpecificDetailsName": True,
            "dialogDescriptionRelationship": True,
            "keyboardEnterOpensDialog": True,
            "dialogInitialFocus": "close-control",
            "modalFocusContainment": True,
            "escapeClosesDialog": True,
            "focusRestoredToOpener": True,
            "releaseChannelConcealment": True,
            "standardReleaseChannels": ["stable"],
            "administratorReleaseChannels": ["stable", "release-candidate", "beta"],
            "developerReleaseChannels": ["stable", "release-candidate", "beta", "debug"],
            "signedOutReleaseChannels": [],
            "screenReaderAcceptance": False,
            "assistiveTechnologyAcceptance": False,
        }
        (EVIDENCE_DIR / "dialog-keyboard-acceptance.json").write_text(
            json.dumps(report, indent=2, sort_keys=True) + "\n", encoding="utf-8"
        )
        print("Web Development dialog keyboard/focus and release-channel concealment acceptance passed")
    finally:
        driver.quit()


if __name__ == "__main__":
    main()
