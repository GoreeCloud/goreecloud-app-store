package com.goreecloud.appstore.platform

enum class IntegrationState {
    TARGETED,
    SOURCE_BOUNDARY,
    NOT_CONNECTED,
}

data class PlatformIntegrationStatus(
    val system: String,
    val state: IntegrationState,
    val detail: String,
)

object PlatformIntegrationRegistry {
    val current = listOf(
        PlatformIntegrationStatus(
            system = "Glaze UI",
            state = IntegrationState.TARGETED,
            detail = "UI targets the current Stable 2.0.0 consumer contract; conformance is not yet claimed.",
        ),
        PlatformIntegrationStatus(
            system = "GoreeCloud Identity",
            state = IntegrationState.SOURCE_BOUNDARY,
            detail = "Identity gateway and entitlement inputs exist; production OIDC/runtime integration is not connected.",
        ),
        PlatformIntegrationStatus(
            system = "Wardveil Security",
            state = IntegrationState.SOURCE_BOUNDARY,
            detail = "Package trust/verification boundary is reserved; package delivery is not connected.",
        ),
        PlatformIntegrationStatus(
            system = "Privacy Shield",
            state = IntegrationState.SOURCE_BOUNDARY,
            detail = "Development client collects no analytics; production privacy-policy integration remains pending.",
        ),
        PlatformIntegrationStatus(
            system = "Everkeep",
            state = IntegrationState.SOURCE_BOUNDARY,
            detail = "Install/library history recovery boundary is defined; production continuity acceptance remains pending.",
        ),
        PlatformIntegrationStatus(
            system = "GoreeCloud Mesh",
            state = IntegrationState.SOURCE_BOUNDARY,
            detail = "Catalog/lifecycle event boundary is defined; production event transport is not connected.",
        ),
    )
}

interface PackageDeliveryGateway {
    val isAvailable: Boolean
}

object UnavailablePackageDeliveryGateway : PackageDeliveryGateway {
    override val isAvailable: Boolean = false
}
