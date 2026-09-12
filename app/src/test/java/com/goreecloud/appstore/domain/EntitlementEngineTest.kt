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
    fun releaseChannelClaimIsRequiredInAdditionToAudienceEntitlement() {
        val item = fixtureItem(ReleaseChannel.DEVELOPMENT)
        val administrator = IdentitySession(
            "admin",
            "Administrator",
            setOf("audience:standard", "audience:administrator", "channel:stable"),
            true,
        )
        val developer = IdentitySession(
            "developer",
            "Developer",
            setOf("audience:standard", "channel:stable", "channel:development"),
            true,
        )

        assertFalse(EntitlementEngine.canView(administrator, item))
        assertTrue(EntitlementEngine.canView(developer, item))
        assertEquals(listOf(item), EntitlementEngine.visibleItems(developer, listOf(item)))
        assertTrue(EntitlementEngine.visibleItems(administrator, listOf(item)).isEmpty())
    }

    private fun fixtureItem(channel: ReleaseChannel) = StoreItem(
        id = "goreecloud.fixture",
        name = "Fixture",
        summary = "Fixture",
        type = StoreItemType.APPLICATION,
        category = "Testing",
        version = "development",
        releaseChannel = channel,
        packageName = null,
        serviceUrl = null,
        accessRule = AccessRule(anyAudience = setOf("audience:standard")),
    )
}
