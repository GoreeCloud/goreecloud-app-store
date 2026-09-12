package com.goreecloud.appstore.platform

enum class IntegrationState { TARGETED, SOURCE_BOUNDARY, NOT_CONNECTED }

data class PlatformIntegrationStatus(
    val system: String,
    val state: IntegrationState,
    val detail: String,
)

object GlazeUiContract {
    const val VERSION = "1.3.0"
    const val RELEASE_TAG = "v1.3.0"
    const val RELEASE_REVISION = "ff34f232f295c9dcb07e4c681f66d4104d0b9323"
    const val SOURCE_QUALIFICATION_ANCHOR = "fc7cc91d2eace8da2371371c2855c24cbcb326a1"
    const val ROLLBACK_VERSION = "1.2.0"
    const val MATERIAL_RULE = "Neutral glass remains the material foundation. Adaptive expression is contextual, bounded, and subordinate to meaning, accessibility, and task completion."
    const val SYSTEM_SHELL_SCOPE = "Application"
    const val CONFORMANCE_ACCEPTED = false
}

object PlatformIntegrationRegistry {
    val current = listOf(
        PlatformIntegrationStatus(
            system = "GLAZE UI V1.3 — Adaptive Resonance",
            state = IntegrationState.TARGETED,
            detail = "Android, Linux, and Web source mappings target current Stable 1.3.0. Consumer-local rendered, accessibility, representative-target, performance, rollback, and production acceptance remain pending.",
        ),
        PlatformIntegrationStatus(
            system = "GoreeCloud Manager",
            state = IntegrationState.NOT_CONNECTED,
            detail = "Manager registration, operational visibility, and administrative integration are not established.",
        ),
        PlatformIntegrationStatus(
            system = "GoreeCloud Identity",
            state = IntegrationState.SOURCE_BOUNDARY,
            detail = "Identity gateway and entitlement inputs exist; production OIDC/runtime integration is not connected.",
        ),
        PlatformIntegrationStatus(
            system = "Wardveil Security",
            state = IntegrationState.SOURCE_BOUNDARY,
            detail = "Package trust and verification boundaries are reserved; package delivery remains disabled.",
        ),
        PlatformIntegrationStatus(
            system = "Privacy Shield",
            state = IntegrationState.SOURCE_BOUNDARY,
            detail = "The Development client collects no analytics; production privacy-policy and runtime acceptance remain pending.",
        ),
        PlatformIntegrationStatus(
            system = "Everkeep",
            state = IntegrationState.SOURCE_BOUNDARY,
            detail = "Library/history recovery boundaries are defined; production continuity evidence remains pending.",
        ),
        PlatformIntegrationStatus(
            system = "GoreeCloud Mesh",
            state = IntegrationState.SOURCE_BOUNDARY,
            detail = "Catalog/lifecycle coordination boundaries are defined; production event transport is not connected.",
        ),
    )
}

interface PackageDeliveryGateway { val isAvailable: Boolean }
object UnavailablePackageDeliveryGateway : PackageDeliveryGateway {
    override val isAvailable = false
}
