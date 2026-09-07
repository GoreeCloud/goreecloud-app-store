package com.goreecloud.appstore.domain

enum class ReleasePublicationState {
    DRAFT,
    PUBLISHED,
    BLOCKED,
    WITHDRAWN,
}

data class StoreRelease(
    val releaseId: String,
    val itemId: String,
    val version: String,
    val channel: DownloadReleaseChannel,
    val publicationState: ReleasePublicationState,
)

/**
 * Minimal client-side release view used to model channel filtering without treating the current
 * Development catalog as an authoritative release manifest. Artifact URLs, digests, signing
 * evidence, and Wardveil state belong to the protected delivery contract and are intentionally
 * not represented here until that contract is accepted.
 */
