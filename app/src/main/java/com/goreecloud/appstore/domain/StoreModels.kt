package com.goreecloud.appstore.domain

enum class StoreItemType {
    APPLICATION,
    SERVICE,
}

enum class ReleaseChannel {
    STABLE,
    RELEASE_CANDIDATE,
    BETA,
    DEBUG,
    ;

    companion object {
        /**
         * The shared Development catalog still uses the legacy `development` token.
         * Treat it as the Debug channel without changing the non-authoritative fixture in place.
         */
        fun fromCatalog(value: String): ReleaseChannel = when (value.lowercase()) {
            "stable" -> STABLE
            "release-candidate", "release_candidate", "rc" -> RELEASE_CANDIDATE
            "beta" -> BETA
            "debug", "development" -> DEBUG
            else -> throw IllegalArgumentException("Unknown release channel: $value")
        }
    }
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
    val allowedReleaseChannels: Set<ReleaseChannel> = emptySet(),
)
