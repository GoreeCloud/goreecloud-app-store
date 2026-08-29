package com.goreecloud.appstore.identity

import com.goreecloud.appstore.domain.IdentitySession

/**
 * Boundary for GoreeCloud Identity.
 *
 * The development adapter below exists only to exercise entitlement behavior while the
 * production GoreeCloud Identity application-facing runtime remains unaccepted.
 * Audience names here are fixtures and do not define production GoreeCloud Identity policy.
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
        ),
        IdentitySession(
            subjectId = "dev:administrator",
            displayName = "Administrator demo",
            audiences = setOf(
                "audience:standard",
                "audience:administrator",
            ),
            isAuthenticated = true,
        ),
        IdentitySession(
            subjectId = "dev:developer",
            displayName = "Developer demo",
            audiences = setOf("audience:developer"),
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
