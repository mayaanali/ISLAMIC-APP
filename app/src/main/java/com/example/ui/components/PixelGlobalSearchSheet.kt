package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyQuest
import com.example.data.InstalledAppItem
import com.example.data.PrayerItem
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SlateBlue
import com.example.ui.viewmodel.MainViewModel

enum class SearchFilterType(val label: String, val icon: String) {
    ALL("All", "✨"),
    APPS("Apps", "📱"),
    QUESTS("Quests", "⚔️"),
    PRAYERS("Prayers", "🕌"),
    ACTIONS("Actions", "⚡")
}

sealed class SearchResultItem {
    data class AppResult(val app: InstalledAppItem) : SearchResultItem()
    data class QuestResult(val quest: DailyQuest) : SearchResultItem()
    data class PrayerResult(val prayer: PrayerItem) : SearchResultItem()
    data class ActionItem(
        val title: String,
        val subtitle: String,
        val icon: ImageVector,
        val actionType: String
    ) : SearchResultItem()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixelGlobalSearchSheet(
    viewModel: MainViewModel,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onNavigateToApps: () -> Unit,
    onNavigateToFocus: () -> Unit,
    onOpenLimitDialog: (InstalledAppItem) -> Unit,
    onOpenQuestDialog: (DailyQuest) -> Unit,
    onOpenProfileDialog: () -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = LocalIsDarkTheme.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val installedApps by viewModel.installedApps.collectAsState()
    val dailyQuests by viewModel.dailyQuests.collectAsState()
    val prayers by viewModel.prayersList.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(SearchFilterType.ALL) }
    val recentSearches = remember { mutableStateListOf("Instagram", "Fajr", "Limit", "Quran", "YouTube") }

    val bgSheetColor = if (isDark) Color(0xFF080C14) else Color(0xFFF4F6F9)
    val cardColor = if (isDark) Color(0xFF131B2E) else Color.White
    val borderColor = if (isDark) Color(0xFF2A3854) else Color(0xFFCBD5E1)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else SlateBlue
    val textSecondary = if (isDark) Color(0xFF94A3B8) else SlateBlue.copy(alpha = 0.7f)
    val accentEmerald = Color(0xFF10B981)
    val accentGold = Color(0xFFF59E0B)

    // Filter results dynamically
    val searchResults: List<SearchResultItem> = remember(searchQuery, selectedFilter, installedApps, dailyQuests, prayers) {
        val q = searchQuery.trim().lowercase()
        val list = mutableListOf<SearchResultItem>()

        // 1. Built-in quick actions
        val quickActions = listOf(
            SearchResultItem.ActionItem("Toggle Dark / Night Mode", if (isDarkMode) "Switch to Alabaster Sand Day Mode" else "Switch to Obsidian Midnight Dark Mode", if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, "TOGGLE_DARK"),
            SearchResultItem.ActionItem("Open Qibla Direction Compass", "Real-time compass & Makkah angle", Icons.Default.Explore, "OPEN_QIBLA"),
            SearchResultItem.ActionItem("Find Nearby Masjids (GPS)", "Masjid locator with distance calculation", Icons.Default.Mosque, "OPEN_MASJIDS"),
            SearchResultItem.ActionItem("View User Profile & Rank Stats", "Current rank, spiritual streak & achievements", Icons.Default.Person, "OPEN_PROFILE"),
            SearchResultItem.ActionItem("App Blocker & Focus Restrictions", "Configure blacklist & time limits", Icons.Default.Shield, "NAV_APPS")
        )

        // Filter Actions
        if (selectedFilter == SearchFilterType.ALL || selectedFilter == SearchFilterType.ACTIONS) {
            if (q.isEmpty()) {
                list.addAll(quickActions)
            } else {
                list.addAll(quickActions.filter {
                    it.title.lowercase().contains(q) || it.subtitle.lowercase().contains(q)
                })
            }
        }

        // Filter Apps
        if (selectedFilter == SearchFilterType.ALL || selectedFilter == SearchFilterType.APPS) {
            val matchedApps = if (q.isEmpty()) {
                installedApps.take(6)
            } else {
                installedApps.filter {
                    it.appName.lowercase().contains(q) ||
                    it.packageName.lowercase().contains(q) ||
                    it.category.lowercase().contains(q)
                }
            }
            list.addAll(matchedApps.map { SearchResultItem.AppResult(it) })
        }

        // Filter Quests
        if (selectedFilter == SearchFilterType.ALL || selectedFilter == SearchFilterType.QUESTS) {
            val matchedQuests = if (q.isEmpty()) {
                dailyQuests.take(4)
            } else {
                dailyQuests.filter {
                    it.title.lowercase().contains(q) ||
                    it.description.lowercase().contains(q) ||
                    it.category.label.lowercase().contains(q)
                }
            }
            list.addAll(matchedQuests.map { SearchResultItem.QuestResult(it) })
        }

        // Filter Prayers
        if (selectedFilter == SearchFilterType.ALL || selectedFilter == SearchFilterType.PRAYERS) {
            val matchedPrayers = if (q.isEmpty()) {
                prayers
            } else {
                prayers.filter {
                    it.name.lowercase().contains(q) ||
                    it.timing.lowercase().contains(q) ||
                    "prayer salah namaz".contains(q)
                }
            }
            list.addAll(matchedPrayers.map { SearchResultItem.PrayerResult(it) })
        }

        list
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = bgSheetColor,
        tonalElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "IN-APP SEARCH & DISCOVERY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentGold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Explore The Guided Path",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = textPrimary,
                        letterSpacing = (-0.5).sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Search",
                        tint = textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Text Field with Pixel Neumorphic Styling
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search apps, quests, salah, actions...",
                        fontSize = 13.5.sp,
                        color = textSecondary.copy(alpha = 0.7f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (isDark) accentEmerald else SlateBlue,
                        modifier = Modifier.size(22.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor,
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    focusedBorderColor = accentEmerald,
                    unfocusedBorderColor = borderColor
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, if (isDark) borderColor else Color.Transparent, RoundedCornerShape(18.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Filter Chips (All, Apps, Quests, Prayers, Actions)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SearchFilterType.values()) { filter ->
                    val isSelected = selectedFilter == filter
                    val chipBg = if (isSelected) (if (isDark) accentEmerald else SlateBlue) else cardColor
                    val chipBorder = if (isSelected) (if (isDark) accentEmerald else SlateBlue) else borderColor
                    val chipTextColor = if (isSelected) (if (isDark) Color(0xFF080C14) else Color.White) else textPrimary

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(chipBg)
                            .border(1.dp, chipBorder, RoundedCornerShape(14.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = filter.icon, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = filter.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = chipTextColor
                            )
                        }
                    }
                }
            }

            // Recent Searches Pill Row (when query is empty)
            if (searchQuery.isEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Recent Searches",
                        tint = textSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RECENT SEARCHES",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(recentSearches) { term ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDark) Color(0xFF1A2338) else Color(0xFFE2E8F0))
                                .clickable { searchQuery = term }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = term,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = textPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Result Count Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SEARCH RESULTS (${searchResults.size})",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textSecondary,
                    letterSpacing = 0.8.sp
                )
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "Query: \"$searchQuery\"",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = accentGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Results List
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔍", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matching items found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching for an app name, salah, or quest keyword",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(searchResults) { item ->
                        when (item) {
                            is SearchResultItem.ActionItem -> {
                                SearchActionRow(
                                    action = item,
                                    isDark = isDark,
                                    cardColor = cardColor,
                                    borderColor = borderColor,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentEmerald = accentEmerald,
                                    onClick = {
                                        when (item.actionType) {
                                            "TOGGLE_DARK" -> viewModel.toggleDarkMode()
                                            "OPEN_QIBLA", "OPEN_MASJIDS" -> {
                                                onDismiss()
                                                onNavigateToFocus()
                                            }
                                            "OPEN_PROFILE" -> {
                                                onDismiss()
                                                onOpenProfileDialog()
                                            }
                                            "NAV_APPS" -> {
                                                onDismiss()
                                                onNavigateToApps()
                                            }
                                        }
                                    }
                                )
                            }
                            is SearchResultItem.AppResult -> {
                                SearchAppRow(
                                    app = item.app,
                                    viewModel = viewModel,
                                    isDark = isDark,
                                    cardColor = cardColor,
                                    borderColor = borderColor,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentEmerald = accentEmerald,
                                    onOpenLimit = {
                                        onDismiss()
                                        onOpenLimitDialog(item.app)
                                    }
                                )
                            }
                            is SearchResultItem.QuestResult -> {
                                SearchQuestRow(
                                    quest = item.quest,
                                    viewModel = viewModel,
                                    isDark = isDark,
                                    cardColor = cardColor,
                                    borderColor = borderColor,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentGold = accentGold,
                                    onClick = {
                                        onDismiss()
                                        onOpenQuestDialog(item.quest)
                                    }
                                )
                            }
                            is SearchResultItem.PrayerResult -> {
                                SearchPrayerRow(
                                    prayer = item.prayer,
                                    viewModel = viewModel,
                                    isDark = isDark,
                                    cardColor = cardColor,
                                    borderColor = borderColor,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentEmerald = accentEmerald
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchActionRow(
    action: SearchResultItem.ActionItem,
    isDark: Boolean,
    cardColor: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentEmerald: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .border(1.2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.title,
                        tint = if (isDark) accentEmerald else SlateBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = action.title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = action.subtitle,
                        fontSize = 11.sp,
                        color = textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Execute ➔",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) accentEmerald else SlateBlue
                )
            }
        }
    }
}

@Composable
private fun SearchAppRow(
    app: InstalledAppItem,
    viewModel: MainViewModel,
    isDark: Boolean,
    cardColor: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentEmerald: Color,
    onOpenLimit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .border(1.2.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // App Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.appName.take(1).uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = textPrimary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = app.appName,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (app.isBlocked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "BLOCKED",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFEF4444)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${app.category} • Limit: ${if (app.dailyLimitMinutes > 0) "${app.dailyLimitMinutes}m" else "None"}",
                        fontSize = 11.sp,
                        color = textSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quick Toggle Block
                IconButton(
                    onClick = { viewModel.toggleAppBlock(app, !app.isBlocked) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (app.isBlocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Toggle Block",
                        tint = if (app.isBlocked) Color(0xFFEF4444) else textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Set Limit
                IconButton(
                    onClick = onOpenLimit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Set Limit",
                        tint = if (isDark) accentEmerald else SlateBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchQuestRow(
    quest: DailyQuest,
    viewModel: MainViewModel,
    isDark: Boolean,
    cardColor: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentGold: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .border(1.2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (quest.isCompleted) Color(0xFF10B981).copy(alpha = 0.2f) else accentGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (quest.isCompleted) Icons.Default.CheckCircle else Icons.Default.Stars,
                        contentDescription = "Quest",
                        tint = if (quest.isCompleted) Color(0xFF10B981) else accentGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = quest.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = quest.description,
                        fontSize = 10.5.sp,
                        color = textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (quest.isCompleted) Color(0xFF10B981).copy(alpha = 0.2f) else accentGold.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+${quest.points} pts",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (quest.isCompleted) Color(0xFF10B981) else accentGold
                )
            }
        }
    }
}

@Composable
private fun SearchPrayerRow(
    prayer: PrayerItem,
    viewModel: MainViewModel,
    isDark: Boolean,
    cardColor: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentEmerald: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .border(1.2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { viewModel.togglePrayerCompletion(prayer.id) }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (prayer.isCompleted) Icons.Default.CheckCircle else Icons.Default.Mosque,
                    contentDescription = prayer.name,
                    tint = if (prayer.isCompleted) accentEmerald else textSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = prayer.name,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = "Time: ${prayer.timing} • Reward: +${prayer.totalPoints} pts",
                        fontSize = 11.sp,
                        color = textSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (prayer.isCompleted) accentEmerald else (if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (prayer.isCompleted) "Completed ✓" else "Mark Done",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (prayer.isCompleted) (if (isDark) Color(0xFF080C14) else Color.White) else textPrimary
                )
            }
        }
    }
}
