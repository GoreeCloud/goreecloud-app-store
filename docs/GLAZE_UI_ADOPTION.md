# Glaze UI 2.1 Adoption — GoreeCloud App Store

## Status

- Application: GoreeCloud App Store
- Native clients: Android / Jetpack Compose and Linux / GTK 4 + libadwaita
- Current required Glaze UI target: **2.1.0 Stable**
- Canonical release tag: `v2.1.0`
- Reviewed Stable release revision: `c49113eb8b93c267613fdf1bbca1f814495acad7`
- Adoption status: **adoption-candidate**
- Glaze UI conformance accepted: **false**
- Production eligible from Glaze UI evidence: **false**

The prior Glaze UI 2.0 work is historical migration input only. This record maps both current native clients to the 2.1 Stable contract without claiming rendered or accessibility acceptance. The machine-readable companion is `contracts/glaze-ui-adoption.json`.

## Governing semantics

Both native clients adopt the 2.1 rules:

- **Make interaction feel tangible.**
- **Content is solid. Interaction is glazed.**
- **Nothing teleports.**

The material hierarchy is **Canvas → Surface → Soft Glaze → Glaze → Deep Glaze → Live Glaze**. Durable catalog content stays on solid Canvas/Surface equivalents. Glaze roles apply to interaction/status chrome according to function and accessibility state, not as decorative translucency.

## Android native mapping

`app/src/main/java/com/goreecloud/appstore/ui/GlazeTheme.kt` provides the repository-local Compose Material 3 mapping. It separates canvas, solid surface, raised surface, Glaze interaction, accent, border, positive, and warning roles for Light and Dark appearance.

Android platform controls remain native Compose controls. The current 48 dp interaction floor and recorded 56 dp Touch Assistance target do not by themselves establish Touch Assistance acceptance.

Pending Android acceptance includes Deep Dark, Reduced Transparency/Solid, Increased Contrast/forced-color-equivalent behavior where applicable, 200% Large Text, Touch Assistance, TalkBack/switch/keyboard-focus behavior, supported Android form factors, and representative physical-device evidence.

## Linux native mapping

`linux/resources/style.css` provides the native GTK 4/libadwaita mapping. It uses host theme roles such as `@card_bg_color`, `@accent_bg_color`, borders, warning, and success roles rather than hard-coding an independent visual theme that could conflict with desktop appearance/accessibility settings.

The current mapping includes Glaze-oriented header, navigation, search, card, artifact-row, status, and selected-navigation treatments while keeping primary catalog content on solid card/surface backgrounds.

This Linux source mapping is not desktop conformance evidence. Pending Linux acceptance includes:

- rendered Light/Dark and supported host-theme behavior;
- Reduced Transparency/Solid-equivalent behavior where applicable;
- Increased Contrast/high-contrast desktop behavior;
- 200% text scaling and reflow;
- keyboard navigation, focus visibility, screen-reader semantics, and pointer behavior;
- supported desktop/window-size and Wayland/X11 behavior;
- representative Linux runtime/device acceptance.

No Linux interaction target-floor claim is inferred merely from CSS dimensions. Applicable Glaze target requirements must be validated against the current native desktop contract and rendered behavior.

## Accessibility and resilience boundary

Application-wide Glaze acceptance remains pending for rendered appearance, native accessibility, representative physical devices/desktops, and supported form factors. Accessibility settings take precedence over decorative material effects.

Glaze Motion is not consumed by either App Store client and remains separately governed.

## Automated contract

`scripts/validate_glaze_ui_adoption.py` fails closed when the current Stable target, release anchor, Android native mapping, Linux native mapping, platform integration record, Android application version boundary, or acceptance state drifts.

The validator checks representative Android theme anchors and Linux GTK/libadwaita mapping markers. Android and Linux CI run the contract before their platform build acceptance work.

This is automated **source-level adoption evidence only**. It does not substitute for rendered, native accessibility, human visual, keyboard/focus, supported-form-factor, or representative-device acceptance.

## Production boundary

`productionAcceptance` remains `false`. Glaze UI adoption does not establish production GoreeCloud Identity, authoritative catalog delivery, Wardveil package verification, Privacy Shield runtime acceptance, Everkeep recovery, GoreeCloud Mesh transport, production signing, approved App Store branding, package installation/update authority, deployment, or Stable App Store status.
