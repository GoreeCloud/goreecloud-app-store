package com.goreecloud.appstore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.goreecloud.appstore.data.CatalogJsonLoader
import com.goreecloud.appstore.domain.IdentitySession
import com.goreecloud.appstore.domain.StoreItem
import com.goreecloud.appstore.domain.StoreItemType
import com.goreecloud.appstore.domain.EntitlementEngine
import com.goreecloud.appstore.identity.DevelopmentIdentityGateway
import com.goreecloud.appstore.platform.PlatformIntegrationRegistry
import com.goreecloud.appstore.platform.UnavailablePackageDeliveryGateway

enum class StoreTab(val title: String, val glyph: String) {
    DISCOVER("Discover", "◆"),
    APPS("Apps", "▦"),
    SERVICES("Services", "◈"),
    UPDATES("Updates", "↻"),
    LIBRARY("Library", "▤"),
}

@Composable
fun GoreeCloudAppStore() {
    val context = LocalContext.current
    val allItems = remember { CatalogJsonLoader.load(context) }
    val identityGateway = remember { DevelopmentIdentityGateway }
    var session by remember { mutableStateOf(identityGateway.initialSession) }
    var selectedTab by remember { mutableStateOf(StoreTab.DISCOVER) }
    var query by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf<StoreItem?>(null) }

    val entitled = remember(session, allItems) {
        EntitlementEngine.visibleItems(session, allItems)
    }
    val visible = remember(entitled, selectedTab, query) {
        val tabItems = when (selectedTab) {
            StoreTab.DISCOVER -> entitled
            StoreTab.APPS -> entitled.filter { it.type == StoreItemType.APPLICATION }
            StoreTab.SERVICES -> entitled.filter { it.type == StoreItemType.SERVICE }
            StoreTab.UPDATES, StoreTab.LIBRARY -> emptyList()
        }
        if (query.isBlank()) tabItems else tabItems.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.summary.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
        }
    }

    GlazeTheme {
        Scaffold(
            bottomBar = {
                StoreNavigationCapsule(
                    selected = selectedTab,
                    onSelected = { selectedTab = it },
                )
            },
        ) { scaffoldPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .statusBarsPadding(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    StoreHeader(
                        session = session,
                        sessions = identityGateway.availableSessions,
                        onSessionSelected = { session = it },
                    )
                }
                item { DevelopmentBoundaryBanner() }
                item { StoreHero(visibleCount = entitled.size) }
                item {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = GlazeCapsuleShape,
                        label = { Text("Search what is available to you") },
                    )
                }
                item {
                    Text(
                        text = selectedTab.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                if (selectedTab == StoreTab.UPDATES) {
                    item {
                        EmptyState(
                            title = "Updates are not connected yet",
                            body = "The update feed will become available after signed catalog and package-delivery integration is accepted.",
                        )
                    }
                } else if (selectedTab == StoreTab.LIBRARY) {
                    item {
                        EmptyState(
                            title = "Library history is not connected yet",
                            body = "Installed-app history and recoverable library state will be added through the Everkeep-backed library boundary.",
                        )
                    }
                } else if (visible.isEmpty()) {
                    item {
                        EmptyState(
                            title = if (session.isAuthenticated) "Nothing available here" else "Sign in to see your catalog",
                            body = if (session.isAuthenticated) {
                                "This development identity has no matching catalog entries for the current view."
                            } else {
                                "Production sign-in will be provided by GoreeCloud Identity."
                            },
                        )
                    }
                } else {
                    items(visible, key = { it.id }) { item ->
                        StoreItemCard(item = item, onClick = { selectedItem = item })
                    }
                }

                item {
                    PlatformBoundaryCard()
                }
            }
        }

        selectedItem?.let { item ->
            StoreItemDialog(item = item, onDismiss = { selectedItem = null })
        }
    }
}

@Composable
private fun StoreHeader(
    session: IdentitySession,
    sessions: List<IdentitySession>,
    onSessionSelected: (IdentitySession) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text("GoreeCloud", style = MaterialTheme.typography.labelLarge)
            Text(
                "App Store",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Box {
            TextButton(
                modifier = Modifier.height(48.dp),
                onClick = { expanded = true },
            ) {
                Text("${session.displayName}  ▾")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                sessions.forEach { candidate ->
                    DropdownMenuItem(
                        text = { Text(candidate.displayName) },
                        onClick = {
                            onSessionSelected(candidate)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun DevelopmentBoundaryBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = GlazeCardShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("Development identity adapter", fontWeight = FontWeight.SemiBold)
            Text(
                "The account switcher uses non-production fixtures to exercise multi-user entitlements. It does not claim live GoreeCloud Identity integration.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun StoreHero(visibleCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = GlazeCardShape,
        tonalElevation = 6.dp,
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Your GoreeCloud, in one place.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Discover applications and services authorized for the active GoreeCloud identity.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                "$visibleCount development catalog entries available",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun StoreItemCard(item: StoreItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = GlazeCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = GlazeCardShape,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        item.name.take(1),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.name, fontWeight = FontWeight.SemiBold)
                Text(
                    item.summary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "${item.type.label()} · ${item.category} · ${item.releaseChannel.name.lowercase()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text("›", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun EmptyState(title: String, body: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = GlazeCardShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun PlatformBoundaryCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = GlazeCardShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Platform integration checkpoint", fontWeight = FontWeight.SemiBold)
            PlatformIntegrationRegistry.current.forEach { integration ->
                Column {
                    Text(integration.system, style = MaterialTheme.typography.labelLarge)
                    Text(integration.detail, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun StoreNavigationCapsule(selected: StoreTab, onSelected: (StoreTab) -> Unit) {
    Surface(tonalElevation = 8.dp) {
        NavigationBar(
            modifier = Modifier.navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            StoreTab.entries.forEach { tab ->
                NavigationBarItem(
                    selected = selected == tab,
                    onClick = { onSelected(tab) },
                    icon = { Text(tab.glyph) },
                    label = { Text(tab.title) },
                )
            }
        }
    }
}

@Composable
private fun StoreItemDialog(item: StoreItem, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(item.name) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(item.summary)
                Text("Type: ${item.type.label()}")
                Text("Category: ${item.category}")
                item.version?.let { Text("Version: $it") }
                Text("Channel: ${item.releaseChannel.name.lowercase()}")
                Text("This entry is visible because the active development session satisfies its catalog audience rule.")
                Spacer(Modifier.height(4.dp))
                Text(
                    if (item.type == StoreItemType.APPLICATION) {
                        "Package installation is intentionally unavailable until artifact provenance and Wardveil verification are connected."
                    } else {
                        "Service launch is intentionally unavailable until production Identity authorization and endpoint policy are connected."
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            Button(
                enabled = UnavailablePackageDeliveryGateway.isAvailable,
                onClick = {},
            ) {
                Text(if (item.type == StoreItemType.APPLICATION) "Install" else "Open")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}

private fun StoreItemType.label(): String = when (this) {
    StoreItemType.APPLICATION -> "Application"
    StoreItemType.SERVICE -> "Service"
}
