export const IDENTITIES = Object.freeze({
  standard: Object.freeze({
    id: "standard",
    signedIn: true,
    audiences: Object.freeze(["audience:standard", "channel:stable"]),
  }),
  preview: Object.freeze({
    id: "preview",
    signedIn: true,
    audiences: Object.freeze(["audience:standard", "channel:stable", "channel:beta", "channel:rc"]),
  }),
  administrator: Object.freeze({
    id: "administrator",
    signedIn: true,
    audiences: Object.freeze(["audience:standard", "audience:administrator", "channel:stable"]),
  }),
  developer: Object.freeze({
    id: "developer",
    signedIn: true,
    audiences: Object.freeze([
      "audience:standard",
      "audience:developer",
      "channel:stable",
      "channel:rc",
      "channel:beta",
      "channel:development",
      "channel:debug",
    ]),
  }),
  "signed-out": Object.freeze({ id: "signed-out", signedIn: false, audiences: Object.freeze([]) }),
});

const CHANNEL_CLAIMS = Object.freeze({
  stable: "channel:stable",
  rc: "channel:rc",
  beta: "channel:beta",
  development: "channel:development",
  debug: "channel:debug",
});

export function canAccessReleaseChannel(identity, releaseChannel) {
  if (!identity?.signedIn) return false;
  const claim = CHANNEL_CLAIMS[String(releaseChannel ?? "").toLocaleLowerCase()];
  if (!claim) return false;
  const held = new Set(Array.isArray(identity?.audiences) ? identity.audiences : []);
  return held.has(claim);
}

export function isItemEntitled(item, identity) {
  const access = item?.access ?? {};
  if (access.requireSignedIn && !identity?.signedIn) return false;

  const allowed = Array.isArray(access.anyAudience) ? access.anyAudience : [];
  const held = new Set(Array.isArray(identity?.audiences) ? identity.audiences : []);
  const audienceAllowed = allowed.length === 0 || allowed.some((audience) => held.has(audience));
  if (!audienceAllowed) return false;

  return canAccessReleaseChannel(identity, item?.releaseChannel);
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
