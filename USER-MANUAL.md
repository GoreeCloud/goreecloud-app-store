# GoreeCloud App Store — Development User Manual

This manual describes the Android **0.1.5-dev** candidate. It is not a production release.

## Opening the app

Development builds install as `GoreeCloud App Store Dev` under package `com.goreecloud.appstore.dev`. The reserved production package is separate.

## Navigation

**Discover** shows the items available to the active Development identity and provides search.

**Apps** limits the current authorized catalog to applications.

**Services** limits it to GoreeCloud service entries.

**Updates** intentionally reports unavailable until authenticated production release metadata and package delivery exist.

**Library** intentionally reports unavailable until per-identity installation history and Everkeep-backed recovery are implemented and accepted.

## Development identities

The account control exposes Standard demo, Administrator demo, Developer demo, and Signed out fixtures. These are test inputs, not production GoreeCloud Identity roles/accounts.

The App Store filters entries before presentation. An administrator fixture does not bypass unrelated audience rules.

## Current catalog

The fixture currently represents Browser, Messenger, Location, Contacts, Tasks, Notes, Memos, Launcher, Keyboard, Manager, Identity Center, and Mesh Center. The exact visible subset depends on the active Development identity.

Every entry uses approved first-party GoreeCloud artwork. Catalog presence does not mean the item is production-released or installable.

## Product details

Tap a card to view type, category, Development channel, version fixture, and access state. Install/Open actions remain disabled because production delivery and service-launch contracts are not yet accepted.

## Development status

Open the account menu and choose **Development status** to inspect the current boundaries for GLAZE UI V1.1, GoreeCloud Identity, Wardveil Security, Privacy Shield, Everkeep, and GoreeCloud Mesh.

These rows describe implementation state and must not be interpreted as trust, health, security, privacy, recovery, or production badges.

## Appearance

The source target is GLAZE UI V1.1 / 1.1.0. System Light/Dark behavior is active. A Deep Dark source palette exists but is not automatically selected until product policy and application acceptance are completed. Atmospheric Deep Teal/Soft Amber primitives are non-semantic and are not derived from user content.

## What is not available yet

Production sign-in, server-authoritative catalog delivery, APK download/install/update, Wardveil runtime package verification, production service opening, update reconciliation, recoverable Library history, production signing, deployment, and Stable qualification remain unavailable.
