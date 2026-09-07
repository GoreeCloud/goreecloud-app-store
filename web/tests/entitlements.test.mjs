import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import test from "node:test";
import {
  IDENTITIES,
  allowedReleaseChannels,
  canUseReleaseChannel,
  deriveCategories,
  filterItems,
  normalizeReleaseChannel,
  visibleItems,
} from "../entitlements.mjs";

const catalog = JSON.parse(await readFile(new URL("../../catalog/development-catalog.json", import.meta.url), "utf8"));

test("signed-out fixture sees no protected catalog entries", () => {
  assert.equal(visibleItems(catalog, IDENTITIES["signed-out"]).length, 0);
});

test("standard fixture sees only its ten explicitly authorized entries", () => {
  const items = visibleItems(catalog, IDENTITIES.standard);
  assert.equal(items.length, 10);
  assert.equal(items.some((item) => item.id === "goreecloud.manager"), false);
  assert.equal(items.some((item) => item.id === "goreecloud.mesh-center"), false);
});

test("administrator fixture sees all twelve explicitly authorized entries", () => {
  assert.equal(visibleItems(catalog, IDENTITIES.administrator).length, 12);
});

test("developer fixture receives Standard apps plus Developer-only Mesh Center without administrator bypass", () => {
  const items = visibleItems(catalog, IDENTITIES.developer);
  assert.equal(items.length, 11);
  assert.equal(items.some((item) => item.id === "goreecloud.mesh-center"), true);
  assert.equal(items.some((item) => item.id === "goreecloud.manager"), false);
});

test("release channels are granted explicitly by login", () => {
  assert.deepEqual(allowedReleaseChannels(IDENTITIES.standard), ["stable"]);
  assert.deepEqual(
    allowedReleaseChannels(IDENTITIES.administrator),
    ["stable", "release-candidate", "beta"],
  );
  assert.deepEqual(
    allowedReleaseChannels(IDENTITIES.developer),
    ["stable", "release-candidate", "beta", "debug"],
  );
  assert.deepEqual(allowedReleaseChannels(IDENTITIES["signed-out"]), []);
});

test("administrator status does not implicitly grant Debug", () => {
  assert.equal(canUseReleaseChannel(IDENTITIES.administrator, "debug"), false);
  assert.equal(canUseReleaseChannel(IDENTITIES.developer, "debug"), true);
});

test("legacy Development catalog token maps to Debug only for authorized download-track interpretation", () => {
  assert.equal(normalizeReleaseChannel("development"), "debug");
});

test("search cannot widen the already-entitled set", () => {
  const items = visibleItems(catalog, IDENTITIES.standard);
  assert.deepEqual(filterItems(items, { query: "Manager" }), []);
});

test("categories are derived only from the already-entitled view", () => {
  const categories = deriveCategories(visibleItems(catalog, IDENTITIES.standard));
  assert.equal(categories.includes("Administration"), false);
  assert.equal(categories.includes("Platform"), false);
  assert.equal(categories.includes("Productivity"), true);
});

test("type, category, and search filters compose without adding entries", () => {
  const entitled = visibleItems(catalog, IDENTITIES.administrator);
  const result = filterItems(entitled, { type: "application", category: "Productivity", query: "native" });
  assert.ok(result.length > 0);
  assert.ok(result.every((item) => item.type === "application" && item.category === "Productivity"));
  assert.ok(result.every((item) => entitled.some((candidate) => candidate.id === item.id)));
});
