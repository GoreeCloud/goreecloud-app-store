package com.goreecloud.appstore.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogDiscoveryTest {
    private val items = listOf(
        item("browser", "GoreeCloud Browser", "Web browser", StoreItemType.APPLICATION, "Internet"),
        item("notes", "GoreeCloud Notes", "Private notes", StoreItemType.APPLICATION, "Productivity"),
        item("identity", "Identity Center", "Identity service", StoreItemType.SERVICE, "Platform"),
        item("mesh", "Mesh Center", "Coordination service", StoreItemType.SERVICE, "Platform"),
    )

    @Test
    fun categoriesAreScopedToTheRequestedType() {
        assertEquals(
            listOf("Internet", "Productivity"),
            CatalogDiscovery.categories(items, StoreItemType.APPLICATION),
        )
        assertEquals(
            listOf("Platform"),
            CatalogDiscovery.categories(items, StoreItemType.SERVICE),
        )
    }

    @Test
    fun queryMatchesNameSummaryAndCategoryCaseInsensitively() {
        assertEquals(listOf("browser"), CatalogDiscovery.filter(items, query = "BROWSER").map { it.id })
        assertEquals(listOf("notes"), CatalogDiscovery.filter(items, query = "private").map { it.id })
        assertEquals(listOf("identity", "mesh"), CatalogDiscovery.filter(items, query = "platform").map { it.id })
    }

    @Test
    fun typeAndCategoryFiltersComposeWithoutWideningResults() {
        assertEquals(
            listOf("identity", "mesh"),
            CatalogDiscovery.filter(
                items,
                type = StoreItemType.SERVICE,
                category = "Platform",
            ).map { it.id },
        )
        assertEquals(
            emptyList<String>(),
            CatalogDiscovery.filter(
                items,
                type = StoreItemType.APPLICATION,
                category = "Platform",
            ).map { it.id },
        )
    }

    private fun item(
        id: String,
        name: String,
        summary: String,
        type: StoreItemType,
        category: String,
    ) = StoreItem(
        id = id,
        name = name,
        summary = summary,
        type = type,
        category = category,
        version = null,
        releaseChannel = ReleaseChannel.DEVELOPMENT,
        packageName = null,
        serviceUrl = null,
        accessRule = AccessRule(),
    )
}
