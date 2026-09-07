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

    fun visibleItems(session: IdentitySession, items: List<StoreItem>): List<StoreItem> =
        items.filter { canView(session, it.accessRule) }
}
