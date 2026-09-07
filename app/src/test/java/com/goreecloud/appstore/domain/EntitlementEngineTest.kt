package com.goreecloud.appstore.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EntitlementEngineTest {
    private val releaseTestItem = StoreItem(
        id = "test.application",
        name = "Test Application",
        summary = "Synthetic unit-test item",
        type = StoreItemType.APPLICATION,
        category = "Test",
        version = null,
        releaseChannel = ReleaseChannel.DEVELOPMENT,
        packageName = null,
        serviceUrl = null,
        accessRule = AccessRule(requireSignedIn = true),
    )

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

    @Test
    fun visibleReleasesAreFilteredByExplicitChannelGrant() {
        val session = IdentitySession(
            subjectId = "standard",
            displayName = "Standard",
            audiences = emptySet(),
            isAuthenticated = true,
            allowedReleaseChannels = setOf(DownloadReleaseChannel.STABLE),
        )
        val releases = DownloadReleaseChannel.entries.map { channel ->
            StoreRelease(
                releaseId = "test-${channel.name.lowercase()}",
                itemId = releaseTestItem.id,
                version = "unit-test-${channel.name.lowercase()}",
                channel = channel,
                publicationState = ReleasePublicationState.PUBLISHED,
            )
        }

        assertEquals(
            listOf(DownloadReleaseChannel.STABLE),
            EntitlementEngine.visibleReleases(session, releaseTestItem, releases).map(StoreRelease::channel),
        )
    }

    @Test
    fun blockedWithdrawnAndCrossItemReleasesStayHidden() {
        val session = IdentitySession(
            subjectId = "developer",
            displayName = "Developer",
            audiences = emptySet(),
            isAuthenticated = true,
            allowedReleaseChannels = DownloadReleaseChannel.entries.toSet(),
        )
        val releases = listOf(
            StoreRelease(
                releaseId = "published",
                itemId = releaseTestItem.id,
                version = "unit-test-published",
                channel = DownloadReleaseChannel.DEBUG,
                publicationState = ReleasePublicationState.PUBLISHED,
            ),
            StoreRelease(
                releaseId = "blocked",
                itemId = releaseTestItem.id,
                version = "unit-test-blocked",
                channel = DownloadReleaseChannel.DEBUG,
                publicationState = ReleasePublicationState.BLOCKED,
            ),
            StoreRelease(
                releaseId = "withdrawn",
                itemId = releaseTestItem.id,
                version = "unit-test-withdrawn",
                channel = DownloadReleaseChannel.DEBUG,
                publicationState = ReleasePublicationState.WITHDRAWN,
            ),
            StoreRelease(
                releaseId = "other-item",
                itemId = "test.other",
                version = "unit-test-other",
                channel = DownloadReleaseChannel.DEBUG,
                publicationState = ReleasePublicationState.PUBLISHED,
            ),
        )

        assertEquals(
            listOf("published"),
            EntitlementEngine.visibleReleases(session, releaseTestItem, releases).map(StoreRelease::releaseId),
        )
    }
}
