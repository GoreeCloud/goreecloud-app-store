import {
  IDENTITIES,
  allowedReleaseChannels,
  deriveCategories,
  filterItems,
  normalizeReleaseChannel,
  visibleItems,
} from "./entitlements.mjs";

const els = {
  identity: document.querySelector("#identitySelect"),
  tabs: [...document.querySelectorAll("[data-tab]")],
  title: document.querySelector("#viewTitle"),
  summary: document.querySelector("#viewSummary"),
  count: document.querySelector("#resultCount"),
  filters: document.querySelector("#filterPanel"),
  search: document.querySelector("#searchInput"),
  category: document.querySelector("#categorySelect"),
  status: document.querySelector("#statusMessage"),
  grid: document.querySelector("#catalogGrid"),
  unavailable: document.querySelector("#unavailablePanel"),
  unavailableTitle: document.querySelector("#unavailableTitle"),
  unavailableText: document.querySelector("#unavailableText"),
  dialog: document.querySelector("#productDialog"),
  dialogType: document.querySelector("#dialogType"),
  dialogTitle: document.querySelector("#dialogTitle"),
  dialogSummary: document.querySelector("#dialogSummary"),
  dialogCategory: document.querySelector("#dialogCategory"),
  dialogVersion: document.querySelector("#dialogVersion"),
  dialogChannel: document.querySelector("#dialogChannel"),
  dialogReleaseChannel: document.querySelector("#dialogReleaseChannel"),
  dialogReleaseStatus: document.querySelector("#dialogReleaseStatus"),
  dialogDownloadAction: document.querySelector("#dialogDownloadAction"),
};

const state = {
  catalog: null,
  identity: IDENTITIES.standard,
  tab: "discover",
  query: "",
  category: "all",
};

let dialogOpener = null;
let dialogItem = null;

const tabMeta = {
  discover: ["Discover", "Browse only the Development items available to the selected fixture identity."],
  applications: ["Apps", "Applications are filtered from the already-entitled Development catalog."],
  services: ["Services", "Services remain subject to their own future backend authorization before launch."],
  updates: ["Updates", "Update delivery remains unavailable until authoritative release, package, identity, Wardveil, and rollback contracts are accepted."],
  library: ["Library", "Recoverable Library state remains unavailable until identity isolation, Privacy Shield, Everkeep, and reconciliation contracts are accepted."],
};

const releaseLabels = Object.freeze({
  stable: "Stable",
  "release-candidate": "Release Candidate (RC)",
  beta: "Beta",
  debug: "Debug",
});

function validateCatalog(value) {
  if (!value || value.schemaVersion !== 2 || value.authoritative !== false || !Array.isArray(value.items)) {
    throw new Error("Development catalog contract mismatch");
  }
  if (value.items.length !== 12) throw new Error("Expected the reviewed 12-item Development catalog");
  return value;
}

async function loadCatalog() {
  const response = await fetch("./catalog/development-catalog.json", { cache: "no-store", credentials: "same-origin" });
  if (!response.ok) throw new Error(`Catalog request failed (${response.status})`);
  return validateCatalog(await response.json());
}

function setText(element, value) {
  element.textContent = value ?? "";
}

function releaseLabel(channel) {
  return releaseLabels[channel] ?? channel ?? "Unknown";
}

function catalogChannelLabel(channel) {
  const value = String(channel ?? "").trim();
  if (!value) return "Unknown";
  return value.charAt(0).toLocaleUpperCase() + value.slice(1);
}

function dialogFocusableElements() {
  const selector = [
    "button:not([disabled])",
    "select:not([disabled])",
    "input:not([disabled])",
    "a[href]",
    "[tabindex]:not([tabindex='-1'])",
  ].join(",");
  return [...els.dialog.querySelectorAll(selector)].filter((element) => element.getClientRects().length > 0);
}

function renderReleaseSelection(item) {
  els.dialogReleaseChannel.replaceChildren();

  if (item.type !== "application") {
    els.dialogReleaseChannel.disabled = true;
    const option = document.createElement("option");
    option.textContent = "Not applicable to service";
    option.value = "";
    els.dialogReleaseChannel.append(option);
    setText(els.dialogReleaseStatus, "Services do not expose downloadable package channels in this Development client.");
    setText(els.dialogDownloadAction, "Open unavailable in Development");
    return;
  }

  const allowed = allowedReleaseChannels(state.identity);
  els.dialogReleaseChannel.disabled = allowed.length === 0;
  for (const channel of allowed) {
    const option = document.createElement("option");
    option.value = channel;
    option.textContent = releaseLabel(channel);
    els.dialogReleaseChannel.append(option);
  }

  if (allowed.length === 0) {
    const option = document.createElement("option");
    option.value = "";
    option.textContent = "No authorized channels";
    els.dialogReleaseChannel.append(option);
  } else {
    const current = normalizeReleaseChannel(item.releaseChannel);
    els.dialogReleaseChannel.value = current && allowed.includes(current) ? current : allowed[0];
  }

  updateReleaseStatus(item);
}

function updateReleaseStatus(item) {
  if (item.type !== "application") return;

  const selected = els.dialogReleaseChannel.value;
  const current = normalizeReleaseChannel(item.releaseChannel);
  if (!selected) {
    setText(
      els.dialogReleaseStatus,
      "This login has no App Store download-channel grant. Unauthorized channel metadata remains unavailable.",
    );
  } else if (selected !== current) {
    setText(
      els.dialogReleaseStatus,
      `No ${releaseLabel(selected)} build is present in this non-authoritative Development fixture. A production catalog must provide an authorized release before download can be offered.`,
    );
  } else {
    setText(
      els.dialogReleaseStatus,
      `${releaseLabel(selected)} is the authorized download interpretation of this Development fixture entry. Download still remains disabled until protected delivery, digest/signing provenance, Wardveil verification, and backend re-authorization are accepted.`,
    );
  }
  setText(els.dialogDownloadAction, `Download ${selected ? releaseLabel(selected) : "release"} unavailable in Development`);
}

function openDetails(item, trigger) {
  dialogOpener = trigger;
  dialogItem = item;
  setText(els.dialogType, item.type === "service" ? "Service" : "Application");
  setText(els.dialogTitle, item.name);
  setText(els.dialogSummary, item.summary);
  setText(els.dialogCategory, item.category);
  setText(els.dialogVersion, item.version || "Not provided");
  setText(els.dialogChannel, catalogChannelLabel(item.releaseChannel));
  renderReleaseSelection(item);
  if (typeof els.dialog.showModal === "function") {
    els.dialog.showModal();
    els.dialog.querySelector(".dialog-close")?.focus();
  }
}

function renderCard(item) {
  const article = document.createElement("article");
  article.className = "store-card";

  const eyebrow = document.createElement("p");
  eyebrow.className = "eyebrow";
  eyebrow.textContent = item.type === "service" ? "Service" : "Application";

  const heading = document.createElement("h3");
  heading.textContent = item.name;

  const summary = document.createElement("p");
  summary.textContent = item.summary;

  const meta = document.createElement("div");
  meta.className = "card-meta";
  for (const value of [item.category, catalogChannelLabel(item.releaseChannel)]) {
    const chip = document.createElement("span");
    chip.className = "chip";
    chip.textContent = value;
    meta.append(chip);
  }

  const button = document.createElement("button");
  button.type = "button";
  button.className = "details-button";
  button.textContent = "View details";
  button.setAttribute("aria-label", `View details for ${item.name}`);
  button.addEventListener("click", () => openDetails(item, button));

  article.append(eyebrow, heading, summary, meta, button);
  return article;
}

function renderCategoryOptions(categories) {
  const previous = state.category;
  els.category.replaceChildren();
  const all = document.createElement("option");
  all.value = "all";
  all.textContent = "All categories";
  els.category.append(all);
  for (const category of categories) {
    const option = document.createElement("option");
    option.value = category;
    option.textContent = category;
    els.category.append(option);
  }
  state.category = categories.includes(previous) ? previous : "all";
  els.category.value = state.category;
}

function render() {
  const [title, summary] = tabMeta[state.tab];
  setText(els.title, title);
  setText(els.summary, summary);
  for (const button of els.tabs) button.setAttribute("aria-current", button.dataset.tab === state.tab ? "page" : "false");

  const unavailable = state.tab === "updates" || state.tab === "library";
  els.filters.hidden = unavailable;
  els.grid.hidden = unavailable;
  els.unavailable.hidden = !unavailable;

  if (unavailable) {
    setText(els.count, "Unavailable");
    setText(els.status, "");
    setText(els.unavailableTitle, `${title} unavailable in Development`);
    setText(els.unavailableText, summary);
    return;
  }

  if (!state.catalog) {
    setText(els.count, "0 items");
    setText(els.status, "Catalog unavailable. The Development client fails closed.");
    els.grid.replaceChildren();
    return;
  }

  const entitled = visibleItems(state.catalog, state.identity);
  const type = state.tab === "applications" ? "application" : state.tab === "services" ? "service" : "all";
  const entitledInView = filterItems(entitled, { type });
  renderCategoryOptions(deriveCategories(entitledInView));
  const filtered = filterItems(entitledInView, { query: state.query, category: state.category });

  setText(els.count, `${filtered.length} ${filtered.length === 1 ? "item" : "items"}`);
  if (!state.identity.signedIn) {
    setText(els.status, "Signed out. Protected Development catalog entries are concealed.");
  } else if (filtered.length === 0) {
    setText(els.status, "No entitled items match the active filters.");
  } else {
    setText(els.status, "");
  }
  els.grid.replaceChildren(...filtered.map(renderCard));
}

els.identity.addEventListener("change", () => {
  state.identity = IDENTITIES[els.identity.value] ?? IDENTITIES["signed-out"];
  state.query = "";
  state.category = "all";
  els.search.value = "";
  render();
});

for (const button of els.tabs) {
  button.addEventListener("click", () => {
    state.tab = button.dataset.tab;
    state.query = "";
    state.category = "all";
    els.search.value = "";
    render();
  });
}

els.search.addEventListener("input", () => {
  state.query = els.search.value;
  render();
});

els.category.addEventListener("change", () => {
  state.category = els.category.value;
  render();
});

els.dialogReleaseChannel.addEventListener("change", () => {
  if (dialogItem) updateReleaseStatus(dialogItem);
});

els.dialog.addEventListener("keydown", (event) => {
  if (event.key !== "Tab" || !els.dialog.open) return;
  const focusable = dialogFocusableElements();
  if (focusable.length === 0) {
    event.preventDefault();
    return;
  }
  const first = focusable[0];
  const last = focusable[focusable.length - 1];
  if (event.shiftKey && document.activeElement === first) {
    event.preventDefault();
    last.focus();
  } else if (!event.shiftKey && document.activeElement === last) {
    event.preventDefault();
    first.focus();
  }
});

els.dialog.addEventListener("cancel", (event) => {
  event.preventDefault();
  els.dialog.close();
});

els.dialog.addEventListener("close", () => {
  if (dialogOpener?.isConnected) dialogOpener.focus();
  dialogOpener = null;
  dialogItem = null;
});

loadCatalog()
  .then((catalog) => {
    state.catalog = catalog;
    render();
  })
  .catch((error) => {
    console.error("GoreeCloud App Store Development catalog load failed", error);
    state.catalog = null;
    render();
  });
