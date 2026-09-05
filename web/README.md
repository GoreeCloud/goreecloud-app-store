# GoreeCloud App Store Web Development Client

This directory contains an original first-party, dependency-light web Development client for GoreeCloud App Store.

## Development boundary

- Client version: `0.1.0-dev`.
- Lifecycle: Development.
- Shared catalog source: `catalog/development-catalog.json`.
- The local catalog is a non-authoritative fixture and must not be treated as a production entitlement source.
- Development identity fixtures exist only to test concealment and discovery behavior.
- Install, update, service launch, and recoverable Library workflows remain unavailable.
- No analytics or third-party runtime JavaScript is included.
- Production GoreeCloud Identity, server-authoritative catalog delivery, Wardveil verification, Privacy Shield acceptance, Everkeep recovery, Mesh/Manager integration, protected signing, deployment, and Stable acceptance remain open.

## Local validation

```sh
node --test web/tests/*.test.mjs
python3 scripts/validate_web.py
python3 scripts/build_web.py --revision <40-character-git-revision>
python3 -m http.server 8765 --directory .artifacts/web/site
```

The deterministic build copies the reviewed shared Development catalog and the byte-exact approved App Store SVG into the generated static site. The source tree does not maintain a competing catalog or branding authority.

## Rendered Development acceptance

`.github/workflows/web-rendered.yml` adds bounded actual-browser acceptance using the same pinned Selenium/Chrome pattern already used by GoreeCloud Search. `web/rendered_acceptance.py` exercises compact 390×844 and expanded 1280×900 browser surfaces in Light and Dark, fixture entitlement concealment, Apps/Services/search/category discovery, fail-closed Updates/Library/Install/Open behavior, modal details, rendered 48 px targets, keyboard skip-link focus, document overflow safety, Reduced Motion, compact 200% text reflow, and accessible names exposed through Chrome's accessibility tree. Screenshots and a machine-readable acceptance report are retained as exact-revision CI evidence.

This automation is deliberately bounded. A green rendered workflow does **not** establish screen-reader or other assistive-technology acceptance, human visual/optical approval, cross-browser acceptance, representative physical hardware, production hosting/security headers, deployment, release approval, Release Candidate promotion, or Stable qualification.

Current GLAZE UI V1.1 source mapping lives in `web/styles.css`. Light and Dark follow platform preference. Deep Dark remains source-only pending the product-wide runtime policy. Human/assistive-technology, production hosting/header, and representative target-environment acceptance require separate evidence.
