# Glaze UI 2.1 Adoption — GoreeCloud App Store

## Status

- Application: GoreeCloud App Store
- Platform: native Android / Jetpack Compose
- Current required Glaze UI target: **2.1.0 Stable**
- Canonical release tag: `v2.1.0`
- Reviewed Stable release revision: `c49113eb8b93c267613fdf1bbca1f814495acad7`
- Adoption status: **adoption-candidate**
- Glaze UI conformance accepted: **false**
- Production eligible from Glaze UI evidence: **false**

The App Store previously targeted Glaze UI 2.0.0. After Glaze UI 2.1.0 became the current Stable release, the 2.0 evidence became historical migration input and can no longer satisfy current application conformance or production-readiness requirements.

This record maps the App Store to the current Stable contract without claiming downstream acceptance. The machine-readable companion is `contracts/glaze-ui-adoption.json`.

## Governing 2.1 semantics

The native implementation adopts the Stable 2.1 governing rules:

- **Make interaction feel tangible.**
- **Content is solid. Interaction is glazed.**
- **Nothing teleports.**

The material hierarchy is **Canvas → Surface → Soft Glaze → Glaze → Deep Glaze → Live Glaze**. Durable catalog content remains on solid Canvas/Surface roles. Interaction and status chrome may use the Glaze material roles according to function and accessibility state rather than using translucency as decoration.

## Native Android mapping

`app/src/main/java/com/goreecloud/appstore/ui/GlazeTheme.kt` provides a repository-local Material 3 mapping derived from the reviewed 2.1 Android Stable reference palette. It separates canvas, solid surface, raised surface, Soft Glaze/Glaze, accent, border, positive, and warning roles for Light and Dark appearance.

The App Store continues to use platform-native Compose controls for navigation, search, menus, buttons, bottom sheets, and scrolling semantics. Native controls are mappings of Glaze UI behavior; they are not an exemption from Glaze UI requirements.

The current development client preserves a minimum 48 dp interaction floor for the bounded account control and uses standard Material 3 control sizing elsewhere. The 56 dp Touch Assistance requirement is recorded but still requires application-specific state implementation and acceptance before it can be claimed complete.

## Accessibility and resilience boundary

The following Glaze UI 2.1 application acceptance remains pending for the App Store:

- Deep Dark application presentation;
- Reduced Transparency / Solid interaction treatment;
- Increased Contrast and Forced Colors-equivalent platform behavior where applicable;
- 200% Large Text and reflow acceptance;
- Touch Assistance 56 dp target-floor acceptance;
- TalkBack, switch-access, keyboard/focus, and semantic-state acceptance;
- Reduced Motion validation for any future nonessential motion;
- representative phone/tablet/foldable and other supported Android window acceptance;
- representative physical-device acceptance.

Glaze Motion is not consumed by the App Store and remains separately governed/Experimental.

## Automated contract

`scripts/validate_glaze_ui_adoption.py` fails closed when the repository-local target, reviewed Stable release anchor, platform integration record, native palette mapping, version metadata, or acceptance boundary becomes inconsistent.

CI runs that validator before Android tests/lint/assembly and publishes its output with the APK evidence. This is source-level and automated adoption evidence only. It does not substitute for rendered, native accessibility, human visual, or representative physical-device acceptance.

## Production boundary

`productionAcceptance` remains `false`. Glaze UI adoption does not establish production GoreeCloud Identity integration, catalog delivery, Wardveil package verification, package installation/update authority, Privacy Shield runtime acceptance, Everkeep recovery acceptance, GoreeCloud Mesh transport, production signing, deployment, or Stable App Store status.
