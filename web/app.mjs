import { IDENTITIES, deriveCategories, filterItems, visibleItems } from "./entitlements.mjs";

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
};

const state = {
  catalog: null,
  identity: IDENTITIES.standard,
  tab: "discover",
  query: "",
  category: "all",
};

const tabMeta = {
  discover: ["Discover", "Browse only the Development items available to the selected fixture identity."],
  applications: ["Apps", "Applications are filtered from the already-entitled Development catalog."],
  services: ["Services", "Services remain subject to their own future backend authorization before launch."],
  updates: ["Updates", "Update delivery remains unavailable until authoritative release, package, identity, Wardveil, and rollback contracts are accepted."],
  library: ["Library", "Recoverable Library state remains unavailable until identity isolation, Privacy Shield, Everkeep, and reconciliation contracts are accepted."],
};

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

function openDetails(item) {
  setText(els.dialogType, item.type === "service" ? "Service" : "Application");
  setText(els.dialogTitle, item.name);
  setText(els.dialogSummary, item.summary);
  setText(els.dialogCategory, item.category);
  setText(els.dialogVersion, item.version);
  setText(els.dialogChannel, item.releaseChannel);
  if (typeof els.dialog.showModal === "function") els.dialog.showModal();
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
  for (const value of [item.category, item.releaseChannel]) {
    const chip = document.createElement("span");
    chip.className = "chip";
    chip.textContent = value;
    meta.append(chip);
  }

  const button = document.createElement("button");
  button.type = "button";
  button.className = "details-button";
  button.textContent = "View details";
  button.addEventListener("click", () => openDetails(item));

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
