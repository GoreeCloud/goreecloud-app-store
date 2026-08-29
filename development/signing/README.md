# GoreeCloud App Store Development Signing

This directory contains the stable signing identity used only for GoreeCloud App Store development/debug APKs.

## Boundary

- Development application ID: `com.goreecloud.appstore.dev`
- Development label: `GoreeCloud App Store Dev`
- Keystore type: PKCS12
- Alias: `goreecloud-development`
- Certificate SHA-256: `38:D5:43:BB:C1:37:DB:F4:E7:A2:99:FF:2D:2D:D0:65:55:A5:C2:0D:11:0A:9A:DA:4D:E5:E9:E0:D9:05:51:45`

The development keystore is intentionally repository-managed and must be treated as non-secret test material. It exists so exact-source CI development APKs can upgrade earlier development installations instead of receiving a new ephemeral Android debug certificate on every runner.

It MUST NOT sign the reserved production package `com.goreecloud.appstore`, a production release, or any artifact represented as Stable or production-approved. Production signing requires a separate controlled signing identity, custody policy, recovery plan, and explicit production acceptance.

The CI workflow verifies the development package identity, application label, and expected signing-certificate fingerprint before publishing the development artifact.
