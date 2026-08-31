# GoreeCloud App Store User Manual

## Current status

GoreeCloud App Store is currently an **active-development Android application**. It is not production-ready and does not yet install applications or open GoreeCloud services.

The present application validates the native store experience, multi-user catalog behavior, first-party GoreeCloud artwork consumption, responsive store presentation, current Glaze UI adoption, and GoreeCloud platform integration boundaries before real distribution is enabled.

`productionAcceptance` remains `false`.

## Development APKs

There is no production or Stable end-user App Store release yet.

The repository CI can produce a development/debug APK for an exact source revision after Glaze UI adoption contract validation, unit tests, Android lint, APK assembly, package/version/application-label verification, development signing-certificate verification, SHA-256 evidence generation, and artifact publication succeed. Treat those artifacts as test builds only.

Current development/debug builds use:

- application ID `com.goreecloud.appstore.dev`;
- Android label **GoreeCloud App Store Dev**;
- development version line `0.1.4-dev` / version code `5` at this checkpoint;
- the repository-managed, development-only signing certificate documented in `development/signing/README.md`.

The development package is intentionally separate from the reserved future production application ID `com.goreecloud.appstore`. The development signing identity is non-production test material and must never sign the production package or a Stable artifact.

### If an older bootstrap is still installed

Early bootstrap CI APKs used the production-reserved package name `com.goreecloud.appstore` while Android CI generated a different ephemeral debug certificate on each runner. Android therefore cannot replace one of those old bootstrap installations with a later CI APK signed by another runner.

If the device still shows the large **Development identity adapter** panel, letter-only G/I/M artwork, text-glyph bottom navigation, **Search what is available to you**, or the large **Platform integration checkpoint** inside normal browsing, that is the old bootstrap application.

Remove that older bootstrap from the test device, or leave it installed only if you intentionally want to compare it. Install and launch **GoreeCloud App Store Dev** for current testing. The `.dev` package can coexist with the old package because they have different Android application IDs.

Validated `.dev` artifacts retain the same development package and signing identity so a newer build can update an earlier `.dev` installation when its Android version code advances normally.

## Development account switcher

The account menu offers development-only identities such as **Standard demo**, **Administrator demo**, **Developer demo**, and **Signed out**.

These are not real GoreeCloud accounts, groups, or production roles. They are local fixtures used to demonstrate how different logins can receive different App Store catalogs while production GoreeCloud Identity integration is still pending.

Changing the development identity immediately recalculates which catalog entries are visible and returns the current section to its top. An entry for which the active session is not entitled is concealed from visible lists and search results.

For compact phone headers, the active fixture is presented with a shorter label such as **Standard**, **Administrator**, or **Developer**. The complete fixture names remain visible in the account menu. This keeps the account icon and menu affordance readable without changing the underlying identity session.

## Store sections

### Discover

Shows all development catalog entries currently available to the active development identity. A compact development-status notice is shown instead of embedding platform diagnostics throughout the catalog.

The Discover hero is intentionally compact so catalog content appears sooner while still identifying the store and showing the number of entries available to the active development identity.

The available-item count is presented below the section heading so compact-width and larger-text layouts do not force the count over the heading. Singular and plural labels are handled separately.

### Apps

Shows only entitled installable-application entries. Installation is currently disabled until secure release ingestion, artifact provenance, Wardveil verification, and Android package-delivery acceptance are implemented.

### Services

Shows only entitled GoreeCloud service entries. Opening services is currently disabled until production GoreeCloud Identity authorization and approved service-endpoint policy are connected.

### Updates

Shows a dedicated development unavailable state. Update discovery and delivery have not yet been connected.

### Library

Shows a dedicated development unavailable state. Per-identity installed/library history and Everkeep-backed recovery have not yet been connected.

## Search

Search operates only on entries already available to the active development identity. It does not reveal entries that were filtered out by entitlement rules.

The prompt is section-specific:

- Discover: **Search apps and services**
- Apps: **Search apps**
- Services: **Search services**

Search matches the visible catalog by application/service name, summary, or category.

## Application and service artwork

Where approved assets exist, the Android client uses native VectorDrawable derivatives tied to canonical assets in `GoreeCloud/goreecloud-branding-assets`.

The branding repository remains authoritative. Copies in this App Store repository are implementation derivatives only. See `BRANDING.md` for exact canonical mappings.

The App Store itself does not yet have an approved product-specific official icon/logo. Until that identity is established through the canonical branding path and stored in this repository, the development build must not present a placeholder as an approved production App Store identity. This remains a production-readiness blocker.

## Catalog cards and release channels

Catalog-list cards show artwork, name, summary, type/category metadata, and a product-navigation affordance.

The development release channel is not repeated as a capsule on every list card. The channel remains visible in product details where it is materially useful.

Product titles may use up to two lines when needed; summaries and metadata use bounded truncation instead of single-character vertical wrapping.

## Product details

Select an application or service card to open its store-style development detail sheet. The sheet shows approved artwork where available, type/category, development release channel, version information, access state, and the unavailable primary action.

Detail metadata uses vertically stacked label/value presentation so long values remain readable on compact widths instead of competing with their labels in one horizontal row.

The **Install** or **Open** action remains disabled because package/service delivery is not yet trusted or connected.

## Glaze UI 2.1 status

The current required GoreeCloud design-system target is **Glaze UI 2.1.0 Stable**. The App Store pins the reviewed Stable release tag `v2.1.0` and revision `c49113eb8b93c267613fdf1bbca1f814495acad7`.

The application is currently a **Glaze UI 2.1 adoption candidate**, not a conformant or production-eligible consumer. The source mapping follows the 2.1 rules **Make interaction feel tangible**, **Content is solid. Interaction is glazed**, and **Nothing teleports**. Light and Dark native material-role mappings are implemented in the Compose theme, with durable content on solid Canvas/Surface roles and Glaze roles available for interaction/status chrome.

This does **not** establish application conformance. Deep Dark, Reduced Transparency/Solid, Increased Contrast, 200% Large Text, Touch Assistance, TalkBack/switch/keyboard-focus behavior, supported-form-factor acceptance, and representative physical-device acceptance remain pending. Glaze Motion is not consumed.

The machine-readable adoption record is `contracts/glaze-ui-adoption.json`; the explanatory record is `docs/GLAZE_UI_ADOPTION.md`. CI runs `scripts/validate_glaze_ui_adoption.py` before Android build validation.

## Development status and integral GoreeCloud systems

Open the account menu and choose **Development status**, or use the development-status affordance on Discover, to inspect current integration boundaries. These diagnostics are development state, not production trust badges.

The status sheet keeps its **Development status** heading and explicit **Close** action visible while the diagnostic body scrolls. Status names receive flexible width while state capsules remain bounded and single-line.

The status surface covers:

- **Glaze UI** — 2.1.0 Stable is the current target; adoption is source/contract-level only and conformance remains pending.
- **GoreeCloud Identity** — production authentication/authorization integration is not connected.
- **Wardveil Security** — package trust and verification integration is not connected.
- **Privacy Shield** — production privacy-policy integration is not connected; development analytics are off.
- **Everkeep** — library/history recovery integration is not connected.
- **GoreeCloud Mesh** — lifecycle/catalog coordination transport is not connected.

## Privacy and security behavior

The development client does not collect analytics. Cleartext application traffic is disabled. It does not request Android package-install authority.

The development account selector, audience labels, versions, catalog package names, and service endpoints are not production policy or release metadata. Production package identities and endpoints will be populated only from approved authoritative sources.

Client-side catalog filtering is not the future sole authorization boundary. Production artifact access and service launch must be re-authorized by the responsible backend.

## Current limitations

The application currently has no production login, server-authoritative production catalog service, APK download/install flow, service-launch flow, production update delivery, installed-library reconciliation, production signing, Wardveil package-verification acceptance, Privacy Shield runtime acceptance, Everkeep runtime recovery acceptance, Mesh runtime event transport, approved App Store-specific official identity, or accepted current-Stable Glaze UI application conformance.

The responsive corrections are being validated from real-device screenshot review, but existing phone screenshots do not establish acceptance across the supported screen-size, orientation, font-scale, accessibility, or Android runtime matrix.

These limitations are deliberate fail-closed boundaries, not hidden features.

## Build requirements for developers

The application is configured for JDK 17, Gradle 9.5.0, Android Gradle Plugin 9.3.0, compileSdk 37, targetSdk 36, minSdk 26, Kotlin/Compose compiler plugin 2.4.10, and Jetpack Compose BOM 2026.08.00.

A Gradle wrapper is not yet committed. With the required SDK and Gradle available, validate with:

```bash
python3 scripts/validate_glaze_ui_adoption.py
gradle :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

## Support boundary

Until a GoreeCloud App Store release is formally accepted, this manual describes the development build only. Production installation, upgrade, signing, account, recovery, Glaze UI conformance, and service-access instructions will be added only when those behaviors actually exist and are validated.
