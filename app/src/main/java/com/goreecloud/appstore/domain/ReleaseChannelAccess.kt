package com.goreecloud.appstore.domain

/**
 * Development policy adapter for release-channel visibility.
 *
 * Production channel authorization must ultimately come from approved GoreeCloud Identity
 * claims/policy. These explicit fixture claims keep channel access separate from broad roles so
 * administrator status never becomes an implicit pre-release bypass.
 */
object ReleaseChannelAccess {
    private const val STABLE = "channel:stable"
    private const val RC = "channel:rc"
    private const val BETA = "channel:beta"
    private const val DEVELOPMENT = "channel:development"
    private const val DEBUG = "channel:debug"

    fun canAccess(session: IdentitySession, channel: ReleaseChannel): Boolean {
        if (!session.isAuthenticated) return false

        val requiredClaim = when (channel) {
            ReleaseChannel.STABLE -> STABLE
            ReleaseChannel.RC -> RC
            ReleaseChannel.BETA -> BETA
            ReleaseChannel.DEVELOPMENT -> DEVELOPMENT
            ReleaseChannel.DEBUG -> DEBUG
        }

        return requiredClaim in session.audiences
    }

    fun visibleChannels(session: IdentitySession): Set<ReleaseChannel> =
        ReleaseChannel.entries.filterTo(linkedSetOf()) { canAccess(session, it) }
}
