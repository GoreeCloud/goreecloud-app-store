import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import test from "node:test";
import { IDENTITIES, deriveCategories, filterItems, visibleItems } from "../entitlements.mjs";

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

test("developer fixture receives only Mesh Center and no administrator bypass", () => {
  const items = visibleItems(catalog, IDENTITIES.developer);
  assert.deepEqual(items.map((item) => item.id), ["goreecloud.mesh-center"]);
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
