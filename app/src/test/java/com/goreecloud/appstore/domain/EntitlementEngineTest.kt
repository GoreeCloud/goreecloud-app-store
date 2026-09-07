package com.goreecloud.appstore.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EntitlementEngineTest {
    @Test
    fun signedOutSessionCannotViewSignedInEntry() {
        val session = IdentitySession("anonymous", "Signed out", emptySet(), false)
        val rule = AccessRule(requireSignedIn = true)

        assertFalse(EntitlementEngine.canView(session, rule))
    }

    @Test
    fun matchingAudienceCanViewEntry() {
        val session = IdentitySession(
            "subject",
            "User",
            setOf("audience:standard"),
            true,
        )
        val rule = AccessRule(anyAudience = setOf("audience:standard"))

        assertTrue(EntitlementEngine.canView(session, rule))
    }

    @Test
    fun administratorDoesNotBypassAudienceRulesImplicitly() {
        val session = IdentitySession(
            "admin",
            "Administrator",
            setOf("audience:administrator"),
            true,
        )
        val rule = AccessRule(anyAudience = setOf("audience:developer"))

        assertFalse(EntitlementEngine.canView(session, rule))
    }

    @Test
    fun releaseChannelsRequireExplicitGrant() {
        val session = IdentitySession(
            subjectId = "admin",
            displayName = "Administrator",
            audiences = setOf("audience:administrator"),
            isAuthenticated = true,
            allowedReleaseChannels = setOf(
                DownloadReleaseChannel.STABLE,
                DownloadReleaseChannel.RELEASE_CANDIDATE,
                DownloadReleaseChannel.BETA,
            ),
        )

        assertTrue(EntitlementEngine.canAccessReleaseChannel(session, DownloadReleaseChannel.STABLE))
        assertTrue(EntitlementEngine.canAccessReleaseChannel(session, DownloadReleaseChannel.RELEASE_CANDIDATE))
        assertTrue(EntitlementEngine.canAccessReleaseChannel(session, DownloadReleaseChannel.BETA))
        assertFalse(EntitlementEngine.canAccessReleaseChannel(session, DownloadReleaseChannel.DEBUG))
    }

    @Test
    fun signedOutSessionHasNoReleaseChannels() {
        val session = IdentitySession(
            subjectId = "signed-out",
            displayName = "Signed out",
            audiences = emptySet(),
            isAuthenticated = false,
            allowedReleaseChannels = DownloadReleaseChannel.entries.toSet(),
        )

        assertEquals(emptyList<DownloadReleaseChannel>(), EntitlementEngine.allowedReleaseChannels(session))
    }
}
