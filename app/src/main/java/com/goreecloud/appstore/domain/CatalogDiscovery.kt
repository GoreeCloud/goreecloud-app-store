package com.goreecloud.appstore.domain

object CatalogDiscovery {
    fun categories(
        items: List<StoreItem>,
        type: StoreItemType? = null,
    ): List<String> = items
        .asSequence()
        .filter { type == null || it.type == type }
        .map { it.category.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
        .sortedBy { it.lowercase() }
        .toList()

    fun filter(
        items: List<StoreItem>,
        type: StoreItemType? = null,
        query: String = "",
        category: String? = null,
    ): List<StoreItem> {
        val normalizedQuery = query.trim()
        return items.filter { item ->
            val matchesType = type == null || item.type == type
            val matchesCategory = category == null || item.category == category
            val matchesQuery = normalizedQuery.isEmpty() ||
                item.name.contains(normalizedQuery, ignoreCase = true) ||
                item.summary.contains(normalizedQuery, ignoreCase = true) ||
                item.category.contains(normalizedQuery, ignoreCase = true)
            matchesType && matchesCategory && matchesQuery
        }
    }
}
