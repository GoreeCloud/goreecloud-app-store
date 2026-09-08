import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import test from "node:test";
import {
  IDENTITIES,
  canAccessReleaseChannel,
  deriveCategories,
  filterItems,
  isItemEntitled,
  visibleItems,
} from "../entitlements.mjs";

const catalog = JSON.parse(await readFile(new URL("../../catalog/development-catalog.json", import.meta.url), "utf8"));

test("signed-out fixture sees no protected catalog entries", () => {
  assert.equal(visibleItems(catalog, IDENTITIES["signed-out"]).length, 0);
});

test("release channel claims are explicit and fail closed", () => {
  assert.equal(canAccessReleaseChannel(IDENTITIES.standard, "stable"), true);
  assert.equal(canAccessReleaseChannel(IDENTITIES.standard, "development"), false);
  assert.equal(canAccessReleaseChannel(IDENTITIES.preview, "beta"), true);
  assert.equal(canAccessReleaseChannel(IDENTITIES.preview, "rc"), true);
  assert.equal(canAccessReleaseChannel(IDENTITIES.preview, "debug"), false);
  assert.equal(canAccessReleaseChannel(IDENTITIES.developer, "debug"), true);
  assert.equal(canAccessReleaseChannel(IDENTITIES.developer, "unknown"), false);
});

test("development-only fixture catalog is hidden from stable-only identities", () => {
  assert.equal(visibleItems(catalog, IDENTITIES.standard).length, 0);
  assert.equal(visibleItems(catalog, IDENTITIES.administrator).length, 0);
});

test("administrator role does not bypass a missing development-channel claim", () => {
  const item = catalog.items.find((candidate) => candidate.id === "goreecloud.manager");
  assert.ok(item);
  assert.equal(isItemEntitled(item, IDENTITIES.administrator), false);
});

test("developer fixture sees development entries permitted by audience", () => {
  const items = visibleItems(catalog, IDENTITIES.developer);
  assert.ok(items.length > 0);
  assert.equal(items.some((item) => item.id === "goreecloud.browser"), true);
  assert.equal(items.some((item) => item.id === "goreecloud.mesh-center"), true);
  assert.equal(items.some((item) => item.id === "goreecloud.manager"), false);
});

test("search cannot widen the already-entitled set", () => {
  const items = visibleItems(catalog, IDENTITIES.developer);
  assert.deepEqual(filterItems(items, { query: "Manager" }), []);
});

test("categories are derived only from the already-entitled view", () => {
  const categories = deriveCategories(visibleItems(catalog, IDENTITIES.developer));
  assert.equal(categories.includes("Administration"), false);
  assert.equal(categories.includes("Productivity"), true);
});

test("type, category, and search filters compose without adding entries", () => {
  const entitled = visibleItems(catalog, IDENTITIES.developer);
  const result = filterItems(entitled, { type: "application", category: "Productivity", query: "native" });
  assert.ok(result.length > 0);
  assert.ok(result.every((item) => item.type === "application" && item.category === "Productivity"));
  assert.ok(result.every((item) => entitled.some((candidate) => candidate.id === item.id)));
});
