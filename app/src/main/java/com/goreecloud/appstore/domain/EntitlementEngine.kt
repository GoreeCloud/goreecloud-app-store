package com.goreecloud.appstore.domain

object EntitlementEngine {
    fun canView(session: IdentitySession, rule: AccessRule): Boolean {
        if (rule.requireSignedIn && !session.isAuthenticated) return false
        if (rule.anyAudience.isEmpty()) return true
        return session.audiences.any(rule.anyAudience::contains)
    }

    fun canAccessReleaseChannel(session: IdentitySession, channel: DownloadReleaseChannel): Boolean =
        session.isAuthenticated && channel in session.allowedReleaseChannels

    fun allowedReleaseChannels(session: IdentitySession): List<DownloadReleaseChannel> =
        DownloadReleaseChannel.entries.filter { canAccessReleaseChannel(session, it) }

    fun canViewRelease(session: IdentitySession, item: StoreItem, release: StoreRelease): Boolean =
        release.itemId == item.id &&
            release.publicationState == ReleasePublicationState.PUBLISHED &&
            canView(session, item.accessRule) &&
            canAccessReleaseChannel(session, release.channel)

    fun visibleReleases(
        session: IdentitySession,
        item: StoreItem,
        releases: List<StoreRelease>,
    ): List<StoreRelease> = releases.filter { canViewRelease(session, item, it) }

    fun visibleItems(session: IdentitySession, items: List<StoreItem>): List<StoreItem> =
        items.filter { canView(session, it.accessRule) }
}
