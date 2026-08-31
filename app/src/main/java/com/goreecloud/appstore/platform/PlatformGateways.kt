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

object GlazeUiContract {
    const val CURRENT_STABLE_VERSION = "2.1.0"
    const val STABLE_RELEASE_TAG = "v2.1.0"
    const val STABLE_RELEASE_REVISION = "c49113eb8b93c267613fdf1bbca1f814495acad7"
    const val ADOPTION_EVIDENCE = "contracts/glaze-ui-adoption.json"
    const val CONFORMANCE_ACCEPTED = false
}

object PlatformIntegrationRegistry {
    val current = listOf(
        PlatformIntegrationStatus(
            system = "Glaze UI",
            state = IntegrationState.TARGETED,
            detail = "UI targets Glaze UI ${GlazeUiContract.CURRENT_STABLE_VERSION} Stable (${GlazeUiContract.STABLE_RELEASE_TAG}, ${GlazeUiContract.STABLE_RELEASE_REVISION.take(12)}) through repository-local adoption evidence; application conformance remains pending rendered, native, accessibility, and representative-device acceptance.",
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
