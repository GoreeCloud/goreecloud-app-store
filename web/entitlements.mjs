export const IDENTITIES = Object.freeze({
  standard: Object.freeze({ id: "standard", signedIn: true, audiences: Object.freeze(["audience:standard"]) }),
  administrator: Object.freeze({ id: "administrator", signedIn: true, audiences: Object.freeze(["audience:administrator"]) }),
  developer: Object.freeze({ id: "developer", signedIn: true, audiences: Object.freeze(["audience:developer"]) }),
  "signed-out": Object.freeze({ id: "signed-out", signedIn: false, audiences: Object.freeze([]) }),
});

export function isItemEntitled(item, identity) {
  const access = item?.access ?? {};
  if (access.requireSignedIn && !identity?.signedIn) return false;

  const allowed = Array.isArray(access.anyAudience) ? access.anyAudience : [];
  if (allowed.length === 0) return true;

  const held = new Set(Array.isArray(identity?.audiences) ? identity.audiences : []);
  return allowed.some((audience) => held.has(audience));
}

export function visibleItems(catalog, identity) {
  if (!catalog || !Array.isArray(catalog.items)) return [];
  return catalog.items.filter((item) => isItemEntitled(item, identity));
}

export function deriveCategories(items) {
  return [...new Set(items.map((item) => item.category).filter(Boolean))]
    .sort((a, b) => a.localeCompare(b));
}

export function filterItems(items, { query = "", type = "all", category = "all" } = {}) {
  const normalizedQuery = query.trim().toLocaleLowerCase();
  return items.filter((item) => {
    if (type !== "all" && item.type !== type) return false;
    if (category !== "all" && item.category !== category) return false;
    if (!normalizedQuery) return true;
    const haystack = [item.name, item.summary, item.category, item.type]
      .filter(Boolean)
      .join(" ")
      .toLocaleLowerCase();
    return haystack.includes(normalizedQuery);
  });
}
