package com.goreecloud.appstore.domain

enum class StoreItemType {
    APPLICATION,
    SERVICE,
}

enum class ReleaseChannel {
    STABLE,
    BETA,
    DEVELOPMENT,
}

/**
 * Download-track authorization is separate from the legacy Development catalog lifecycle label.
 * This lets the App Store gate Stable, RC, Beta, and Debug artifacts per login without treating
 * the current non-authoritative Development catalog as a production release manifest.
 */
enum class DownloadReleaseChannel {
    STABLE,
    RELEASE_CANDIDATE,
    BETA,
    DEBUG,
}

data class AccessRule(
    val requireSignedIn: Boolean = true,
    val anyAudience: Set<String> = emptySet(),
)

data class StoreItem(
    val id: String,
    val name: String,
    val summary: String,
    val type: StoreItemType,
    val category: String,
    val version: String?,
    val releaseChannel: ReleaseChannel,
    val packageName: String?,
    val serviceUrl: String?,
    val accessRule: AccessRule,
)

data class IdentitySession(
    val subjectId: String,
    val displayName: String,
    val audiences: Set<String>,
    val isAuthenticated: Boolean,
    /**
     * Development-only fixture grants. Production values must be supplied from an approved
     * GoreeCloud Identity / App Store authorization contract and re-authorized by delivery.
     */
    val allowedReleaseChannels: Set<DownloadReleaseChannel> = emptySet(),
)
