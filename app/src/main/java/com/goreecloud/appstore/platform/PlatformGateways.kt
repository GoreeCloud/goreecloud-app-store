package com.goreecloud.appstore.platform

enum class IntegrationState { TARGETED, SOURCE_BOUNDARY, NOT_CONNECTED }

data class PlatformIntegrationStatus(
    val system: String,
    val state: IntegrationState,
    val detail: String,
)

object GlazeUiContract {
    const val VERSION = "1.2.0"
    const val RELEASE_TAG = "v1.2.0"
    const val RELEASE_REVISION = "f285b9145e27e6e7027b075c37299d101945c272"
    const val SOURCE_QUALIFICATION_ANCHOR = "b0eadf9a60f73d45caffb62ffc7e9e0334cddc97"
    const val MATERIAL_RULE = "Neutral glass is the material. Color is an accent."
    const val SYSTEM_SHELL_SCOPE = "Application"
    const val CONFORMANCE_ACCEPTED = false
}

object PlatformIntegrationRegistry {
    val current = listOf(
        PlatformIntegrationStatus(
            system = "GLAZE UI V1.2",
            state = IntegrationState.TARGETED,
            detail = "Source mapping targets current Stable 1.2.0 with neutral material and bounded accent; application-specific rendered, accessibility, physical-device, and production acceptance remain pending.",
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
            detail = "The development client collects no analytics; production privacy-policy acceptance remains pending.",
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
