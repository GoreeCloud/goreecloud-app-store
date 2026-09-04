@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.goreecloud.appstore.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.goreecloud.appstore.R
import com.goreecloud.appstore.data.CatalogJsonLoader
import com.goreecloud.appstore.domain.CatalogDiscovery
import com.goreecloud.appstore.domain.EntitlementEngine
import com.goreecloud.appstore.domain.IdentitySession
import com.goreecloud.appstore.domain.ReleaseChannel
import com.goreecloud.appstore.domain.StoreItem
import com.goreecloud.appstore.domain.StoreItemType
import com.goreecloud.appstore.identity.DevelopmentIdentityGateway
import com.goreecloud.appstore.platform.IntegrationState
import com.goreecloud.appstore.platform.PlatformIntegrationRegistry
import com.goreecloud.appstore.platform.UnavailablePackageDeliveryGateway

private enum class ExperienceTab(val title: String, val icon: ImageVector) {
    DISCOVER("Discover", Icons.Rounded.Home),
    APPS("Apps", Icons.Rounded.Apps),
    SERVICES("Services", Icons.Rounded.Cloud),
    UPDATES("Updates", Icons.Rounded.Update),
    LIBRARY("Library", Icons.Rounded.LibraryBooks),
}

@Composable
fun GoreeCloudAppStoreExperience() {
    val context = LocalContext.current
    val allItems = remember { CatalogJsonLoader.load(context) }
    val identityGateway = remember { DevelopmentIdentityGateway }
    val listState = rememberLazyListState()

    var session by remember { mutableStateOf(identityGateway.initialSession) }
    var selectedTab by remember { mutableStateOf(ExperienceTab.DISCOVER) }
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedItem by remember { mutableStateOf<StoreItem?>(null) }
    var showPlatformStatus by remember { mutableStateOf(false) }

    val entitled = remember(session, allItems) {
        EntitlementEngine.visibleItems(session, allItems)
    }
    val browsable = selectedTab in setOf(
        ExperienceTab.DISCOVER,
        ExperienceTab.APPS,
        ExperienceTab.SERVICES,
    )
    val browsingType = when (selectedTab) {
        ExperienceTab.APPS -> StoreItemType.APPLICATION
        ExperienceTab.SERVICES -> StoreItemType.SERVICE
        else -> null
    }
    val categories = remember(entitled, selectedTab) {
        if (browsable) CatalogDiscovery.categories(entitled, browsingType) else emptyList()
    }
    val visible = remember(entitled, selectedTab, query, selectedCategory) {
        if (browsable) {
            CatalogDiscovery.filter(
                items = entitled,
                type = browsingType,
                query = query,
                category = selectedCategory,
            )
        } else {
            emptyList()
        }
    }

    LaunchedEffect(selectedTab, session.subjectId) {
        query = ""
        selectedCategory = null
        listState.scrollToItem(0)
    }

    GlazeTheme {
        Scaffold(
            topBar = {
                ExperienceTopBar(
                    session = session,
                    sessions = identityGateway.availableSessions,
                    onSessionSelected = { session = it },
                    onShowPlatformStatus = { showPlatformStatus = true },
                )
            },
            bottomBar = { ExperienceNavigation(selectedTab) { selectedTab = it } },
            containerColor = MaterialTheme.colorScheme.background,
        ) { scaffoldPadding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                when (selectedTab) {
                    ExperienceTab.DISCOVER -> {
                        item { DevelopmentNotice { showPlatformStatus = true } }
                        item { ExperienceHero(entitled.size) }
                        item { ExperienceSearch(query, "Search apps and services") { query = it } }
                        if (categories.size > 1) {
                            item {
                                ExperienceCategoryFilters(
                                    categories = categories,
                                    selectedCategory = selectedCategory,
                                    onCategorySelected = { selectedCategory = it },
                                )
                            }
                        }
                        item { SectionHeading("Available to you", countLabel(visible.size)) }
                    }
                    ExperienceTab.APPS -> {
                        item { TabIntro("Apps", "Applications available to this development identity.") }
                        item { ExperienceSearch(query, "Search apps") { query = it } }
                        if (categories.size > 1) {
                            item {
                                ExperienceCategoryFilters(
                                    categories = categories,
                                    selectedCategory = selectedCategory,
                                    onCategorySelected = { selectedCategory = it },
                                )
                            }
                        }
                        item { SectionHeading("Applications", countLabel(visible.size)) }
                    }
                    ExperienceTab.SERVICES -> {
                        item { TabIntro("Services", "Services available to this development identity.") }
                        item { ExperienceSearch(query, "Search services") { query = it } }
                        if (categories.size > 1) {
                            item {
                                ExperienceCategoryFilters(
                                    categories = categories,
                                    selectedCategory = selectedCategory,
                                    onCategorySelected = { selectedCategory = it },
                                )
                            }
                        }
                        item { SectionHeading("Services", countLabel(visible.size)) }
                    }
                    ExperienceTab.UPDATES -> {
                        item { TabIntro("Updates", "Approved updates will appear here after production release delivery is connected.") }
                        item {
                            UnavailableState(
                                Icons.Rounded.Update,
                                "Updates are unavailable in this development build",
                                "Authenticated release metadata, package verification, and delivery remain pending.",
                            ) { showPlatformStatus = true }
                        }
                    }
                    ExperienceTab.LIBRARY -> {
                        item { TabIntro("Library", "Installed and previously available GoreeCloud applications will be managed here.") }
                        item {
                            UnavailableState(
                                Icons.Rounded.LibraryBooks,
                                "Library history is unavailable in this development build",
                                "Per-identity install history and Everkeep-backed recovery remain pending.",
                            ) { showPlatformStatus = true }
                        }
                    }
                }

                if (browsable) {
                    if (visible.isEmpty()) {
                        item {
                            EmptyState(
                                authenticated = session.isAuthenticated,
                                hasActiveFilter = query.isNotBlank() || selectedCategory != null,
                            )
                        }
                    } else {
                        items(visible, key = { it.id }) { item ->
                            ExperienceItemCard(item) { selectedItem = item }
                        }
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }

        selectedItem?.let { item -> ExperienceItemSheet(item) { selectedItem = null } }
        if (showPlatformStatus) PlatformStatusSheet { showPlatformStatus = false }
    }
}

@Composable
private fun ExperienceTopBar(
    session: IdentitySession,
    sessions: List<IdentitySession>,
    onSessionSelected: (IdentitySession) -> Unit,
    onShowPlatformStatus: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(tonalElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("GoreeCloud", style = MaterialTheme.typography.labelLarge, maxLines = 1)
                    Surface(shape = GlazeCapsuleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                        Text(
                            "Development",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            maxLines = 1,
                        )
                    }
                }
                Text("App Store", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, maxLines = 1)
            }

            Box(modifier = Modifier.widthIn(max = 160.dp)) {
                TextButton(
                    modifier = Modifier.heightIn(min = 48.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp),
                    onClick = { expanded = true },
                ) {
                    Icon(Icons.Rounded.AccountCircle, contentDescription = null)
                    Spacer(Modifier.size(4.dp))
                    Text(
                        sessionLabel(session),
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(Icons.Rounded.ExpandMore, contentDescription = null)
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
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Development status") },
                        leadingIcon = { Icon(Icons.Rounded.Info, contentDescription = null) },
                        onClick = {
                            expanded = false
                            onShowPlatformStatus()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun DevelopmentNotice(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = GlazeSmallCardShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Development catalog",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    "Entitlements are simulated and installation remains disabled.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription = "View development status")
        }
    }
}

@Composable
private fun ExperienceHero(visibleCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = GlazeCardShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Image(
                painter = painterResource(R.drawable.goreecloud_app_store_icon),
                contentDescription = null,
                modifier = Modifier.size(52.dp).clip(GlazeArtworkShape),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Your GoreeCloud, in one place.",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    "Browse applications and services authorized for this development identity.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Surface(shape = GlazeCapsuleShape, color = MaterialTheme.colorScheme.surface) {
                    Text(
                        availableLabel(visibleCount),
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun TabIntro(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ExperienceSearch(query: String, placeholder: String, onQueryChanged: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        singleLine = true,
        shape = GlazeCapsuleShape,
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        placeholder = { Text(placeholder, maxLines = 1, overflow = TextOverflow.Ellipsis) },
    )
}

@Composable
private fun ExperienceCategoryFilters(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Categories",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("All") },
                    modifier = Modifier.heightIn(min = 48.dp),
                )
            }
            items(categories, key = { it }) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategorySelected(if (selectedCategory == category) null else category)
                    },
                    label = { Text(category, maxLines = 1) },
                    modifier = Modifier.heightIn(min = 48.dp),
                )
            }
        }
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ExperienceItemCard(item: StoreItem, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = GlazeCardShape,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ExperienceArtwork(item, 64.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(item.summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    "${typeLabel(item.type)} · ${item.category}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription = "View ${item.name}")
        }
    }
}

@Composable
private fun EmptyState(authenticated: Boolean, hasActiveFilter: Boolean) {
    val title = when {
        hasActiveFilter -> "No matching results"
        authenticated -> "Nothing is available here"
        else -> "Sign in to see your catalog"
    }
    val body = when {
        hasActiveFilter -> "Change the search term or category filter within the catalog available to this identity."
        authenticated -> "This development identity has no entries in this section."
        else -> "Production sign-in will be provided by GoreeCloud Identity."
    }
    Surface(modifier = Modifier.fillMaxWidth(), shape = GlazeCardShape, color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun UnavailableState(icon: ImageVector, title: String, body: String, onDetails: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 30.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Surface(modifier = Modifier.size(72.dp), shape = GlazeSmallCardShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(34.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 420.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 460.dp))
        TextButton(onClick = onDetails) { Text("Development status") }
    }
}

@Composable
private fun ExperienceItemSheet(item: StoreItem, onDismiss: () -> Unit) {
    val deliveryAvailable = remember { UnavailablePackageDeliveryGateway.isAvailable }
    val actionLabel = if (item.type == StoreItemType.APPLICATION) "Install" else "Open"
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ExperienceArtwork(item, 80.dp)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(item.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("${typeLabel(item.type)} · ${item.category}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ChannelPill(item.releaseChannel)
                }
            }
            Text(item.summary, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
                enabled = item.type == StoreItemType.APPLICATION && deliveryAvailable,
            ) { Text(actionLabel) }
            Surface(modifier = Modifier.fillMaxWidth(), shape = GlazeSmallCardShape, color = MaterialTheme.colorScheme.surfaceVariant) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailField("Type", typeLabel(item.type))
                    DetailField("Category", item.category)
                    DetailField("Channel", channelLabel(item.releaseChannel))
                    DetailField("Version", item.version ?: "Not provided")
                    DetailField("Access", "Available to the active development identity")
                }
            }
            Text(
                if (item.type == StoreItemType.APPLICATION) {
                    "Installation remains unavailable until authenticated release metadata, immutable artifact provenance, Wardveil verification, and Android package delivery are connected and accepted."
                } else {
                    "Service opening remains unavailable until production Identity authorization and approved endpoint policy are connected and accepted."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("Close") }
        }
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PlatformStatusSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Development status", modifier = Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                TextButton(onClick = onDismiss) { Text("Close") }
            }
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    "These diagnostics describe implementation boundaries; they are not production trust or acceptance badges.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                PlatformIntegrationRegistry.current.forEachIndexed { index, status ->
                    if (index > 0) HorizontalDivider()
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(status.system, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
                            StatusPill(status.state)
                        }
                        Text(status.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Surface(modifier = Modifier.fillMaxWidth(), shape = GlazeSmallCardShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(
                        "Production acceptance remains false for this App Store build.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(state: IntegrationState) {
    Surface(shape = GlazeCapsuleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
        Text(
            when (state) {
                IntegrationState.TARGETED -> "Targeted"
                IntegrationState.SOURCE_BOUNDARY -> "Boundary ready"
                IntegrationState.NOT_CONNECTED -> "Not connected"
            },
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun ChannelPill(channel: ReleaseChannel) {
    Surface(shape = GlazeCapsuleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
        Text(
            channelLabel(channel),
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun ExperienceArtwork(item: StoreItem, size: Dp) {
    val resource = when (item.id) {
        "goreecloud.browser" -> R.drawable.goreecloud_browser_icon
        "goreecloud.messenger" -> R.drawable.goreecloud_messenger_icon
        "goreecloud.location" -> R.drawable.goreecloud_location_icon
        "goreecloud.manager" -> R.drawable.goreecloud_manager_icon
        "goreecloud.contacts" -> R.drawable.goreecloud_contacts_icon
        "goreecloud.tasks" -> R.drawable.goreecloud_tasks_icon
        "goreecloud.notes" -> R.drawable.goreecloud_notes_icon
        "goreecloud.memos" -> R.drawable.goreecloud_memos_icon
        "goreecloud.launcher" -> R.drawable.goreecloud_launcher_icon
        "goreecloud.keyboard" -> R.drawable.goreecloud_keyboard_icon
        "goreecloud.identity-center" -> R.drawable.goreecloud_identity_center_icon
        "goreecloud.mesh-center" -> R.drawable.goreecloud_mesh_center_icon
        else -> null
    }
    if (resource != null) {
        Image(
            painter = painterResource(resource),
            contentDescription = null,
            modifier = Modifier.size(size).clip(GlazeArtworkShape),
        )
    } else {
        Surface(modifier = Modifier.size(size), shape = GlazeArtworkShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    if (item.type == StoreItemType.SERVICE) Icons.Rounded.Cloud else Icons.Rounded.Apps,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(size * 0.48f),
                )
            }
        }
    }
}

@Composable
private fun ExperienceNavigation(selected: ExperienceTab, onSelected: (ExperienceTab) -> Unit) {
    NavigationBar(modifier = Modifier.navigationBarsPadding(), containerColor = MaterialTheme.colorScheme.surfaceVariant, tonalElevation = 2.dp) {
        ExperienceTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selected == tab,
                onClick = { onSelected(tab) },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(tab.title, maxLines = 1, overflow = TextOverflow.Clip) },
            )
        }
    }
}

private fun sessionLabel(session: IdentitySession): String = when (session.subjectId) {
    "dev:standard" -> "Standard"
    "dev:administrator" -> "Administrator"
    "dev:developer" -> "Developer"
    "dev:signed-out" -> "Signed out"
    else -> session.displayName
}

private fun availableLabel(count: Int) = if (count == 1) "1 available" else "$count available"
private fun countLabel(count: Int) = if (count == 1) "1 item in this development catalog" else "$count items in this development catalog"
private fun typeLabel(type: StoreItemType) = if (type == StoreItemType.APPLICATION) "Application" else "Service"
private fun channelLabel(channel: ReleaseChannel): String = when (channel) {
    ReleaseChannel.STABLE -> "Stable"
    ReleaseChannel.BETA -> "Beta"
    ReleaseChannel.DEVELOPMENT -> "Development"
}
