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
)
