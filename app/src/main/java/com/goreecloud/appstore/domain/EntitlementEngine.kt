package com.goreecloud.appstore.domain

object EntitlementEngine {
    fun canView(session: IdentitySession, rule: AccessRule): Boolean {
        if (rule.requireSignedIn && !session.isAuthenticated) return false
        if (rule.anyAudience.isEmpty()) return true
        return session.audiences.any(rule.anyAudience::contains)
    }

    fun visibleItems(session: IdentitySession, items: List<StoreItem>): List<StoreItem> =
        items.filter { canView(session, it.accessRule) }
}
