package com.goreecloud.appstore.identity

import com.goreecloud.appstore.domain.IdentitySession

/**
 * Boundary for GoreeCloud Identity.
 *
 * The development adapter below exists only to exercise entitlement behavior while the
 * production GoreeCloud Identity application-facing runtime remains unaccepted.
 */
interface IdentityGateway {
    val availableSessions: List<IdentitySession>
    val initialSession: IdentitySession
}

object DevelopmentIdentityGateway : IdentityGateway {
    override val availableSessions = listOf(
        IdentitySession(
            subjectId = "dev:family",
            displayName = "Family demo",
            audiences = setOf("audience:family", "channel:stable"),
            isAuthenticated = true,
        ),
        IdentitySession(
            subjectId = "dev:administrator",
            displayName = "Administrator demo",
            audiences = setOf(
                "audience:family",
                "audience:administrator",
                "channel:stable",
                "channel:internal",
            ),
            isAuthenticated = true,
        ),
        IdentitySession(
            subjectId = "dev:developer",
            displayName = "Developer demo",
            audiences = setOf(
                "audience:developer",
                "channel:development",
            ),
            isAuthenticated = true,
        ),
        IdentitySession(
            subjectId = "dev:signed-out",
            displayName = "Signed out",
            audiences = emptySet(),
            isAuthenticated = false,
        ),
    )

    override val initialSession: IdentitySession = availableSessions.first()
}
