package com.goreecloud.appstore.identity

import com.goreecloud.appstore.domain.DownloadReleaseChannel
import com.goreecloud.appstore.domain.IdentitySession

/**
 * Boundary for GoreeCloud Identity.
 *
 * The development adapter below exists only to exercise entitlement behavior while the
 * production GoreeCloud Identity application-facing runtime remains unaccepted.
 * Audience and release-channel grants here are fixtures and do not define production policy.
 */
interface IdentityGateway {
    val availableSessions: List<IdentitySession>
    val initialSession: IdentitySession
}

object DevelopmentIdentityGateway : IdentityGateway {
    override val availableSessions = listOf(
        IdentitySession(
            subjectId = "dev:standard",
            displayName = "Standard demo",
            audiences = setOf("audience:standard"),
            isAuthenticated = true,
            allowedReleaseChannels = setOf(DownloadReleaseChannel.STABLE),
        ),
        IdentitySession(
            subjectId = "dev:administrator",
            displayName = "Administrator demo",
            audiences = setOf(
                "audience:standard",
                "audience:administrator",
            ),
            isAuthenticated = true,
            allowedReleaseChannels = setOf(
                DownloadReleaseChannel.STABLE,
                DownloadReleaseChannel.RELEASE_CANDIDATE,
                DownloadReleaseChannel.BETA,
            ),
        ),
        IdentitySession(
            subjectId = "dev:developer",
            displayName = "Developer demo",
            audiences = setOf("audience:developer"),
            isAuthenticated = true,
            allowedReleaseChannels = DownloadReleaseChannel.entries.toSet(),
        ),
        IdentitySession(
            subjectId = "dev:release-tester",
            displayName = "Release tester demo",
            audiences = setOf(
                "audience:standard",
                "audience:developer",
            ),
            isAuthenticated = true,
            allowedReleaseChannels = DownloadReleaseChannel.entries.toSet(),
        ),
        IdentitySession(
            subjectId = "dev:signed-out",
            displayName = "Signed out",
            audiences = emptySet(),
            isAuthenticated = false,
            allowedReleaseChannels = emptySet(),
        ),
    )

    override val initialSession: IdentitySession = availableSessions.first()
}
