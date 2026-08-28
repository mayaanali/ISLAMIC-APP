package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InstalledAppItem
import com.example.ui.components.PixelAnimatedErrorScreen
import com.example.ui.components.PixelAnimatedLoadingScreen
import com.example.ui.components.PixelAvatarIcon
import com.example.ui.components.PixelBgCanvas
import com.example.ui.components.PixelCardContainer
import com.example.ui.components.PixelCoinIcon
import com.example.ui.components.PixelDarkBorder
import com.example.ui.components.PixelGoldAccent
import com.example.ui.components.PixelGreenAccent
import com.example.ui.components.PixelLiveIrlTrackerCard
import com.example.ui.components.PixelStarBadge
import com.example.ui.components.PixelSwitch
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SlateBlue
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AppListScreen(viewModel: MainViewModel) {
    val installedApps by viewModel.installedApps.collectAsState()
    val isLoadingApps by viewModel.isLoadingApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val hasError by viewModel.hasError.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    if (hasError) {
        PixelAnimatedErrorScreen(
            errorMessage = errorMessage ?: "App Shield Telemetry Error",
            onRetry = { viewModel.retryEngine() }
        )
        return
    }

    if (isLoadingApps) {
        PixelAnimatedLoadingScreen(
            statusText = "SCANNING INSTALLED APPS TELEMETRY..."
        )
        return
    }

    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val textColor = if (isDarkMode) Color(0xFFF8FAFC) else SlateBlue
    val subtextColor = if (isDarkMode) Color(0xFF94A3B8) else SlateBlue.copy(alpha = 0.7f)
    val cardBg = if (isDarkMode) Color(0xFF131B2E) else Color.White
    val borderCol = if (isDarkMode) Color(0xFF2A3854) else SlateBlue

    val categoryList = remember(installedApps) {
        val totalCount = installedApps.size
        val blockedCount = installedApps.count { it.isBlocked }
        val socialCount = installedApps.count { it.category.equals("Social", ignoreCase = true) }
        val entertainmentCount = installedApps.count { it.category.equals("Entertainment", ignoreCase = true) }
        val gamingCount = installedApps.count { it.category.equals("Gaming", ignoreCase = true) }
        listOf(
            "All" to "All ($totalCount)",
            "Blocked" to "Blocked ($blockedCount)",
            "Social" to "Social ($socialCount)",
            "Entertainment" to "Entertainment ($entertainmentCount)",
            "Gaming" to "Gaming ($gamingCount)"
        )
    }

    val filteredApps = remember(installedApps, searchQuery, selectedCategory) {
        installedApps.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                    item.appName.contains(searchQuery, ignoreCase = true) ||
                    item.packageName.contains(searchQuery, ignoreCase = true)
            val matchesCat = when (selectedCategory) {
                "All" -> true
                "Blocked" -> item.isBlocked
                else -> item.category.equals(selectedCategory, ignoreCase = true)
            }
            matchesSearch && matchesCat
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color(0xFF080C14) else PixelBgCanvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // ==========================================
            // 1. TOP HEADER ROW (Matching Guided Path Header)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title and Subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Guided Path",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "DAILY DEEN JOURNEY | SPIRITUAL VITALITY",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = subtextColor,
                        letterSpacing = 0.8.sp
                    )
                }

                // Calligraphy Header
                Text(
                    text = "بِسْمِ اللَّهِ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) PixelGoldAccent else SlateBlue,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // Coin Counter & Pixel Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PixelCoinIcon(modifier = Modifier.size(20.dp))
                    Text(
                        text = String.format("%,d", coins),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    PixelAvatarIcon(modifier = Modifier.size(30.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 2. MAIN SECTION TITLE & COUNT
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "APPS YOU WANT TO BLOCK",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Select apps to restrict during prayer times & late-night focus (${filteredApps.size} found)",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = subtextColor,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 3. PIXELATED SEARCH BAR
            // ==========================================
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "Search apps or package names...",
                        fontSize = 13.sp,
                        color = subtextColor.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = textColor
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = textColor
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.refreshInstalledApps() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = textColor
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedBorderColor = if (isDarkMode) PixelGoldAccent else SlateBlue,
                    unfocusedBorderColor = borderCol
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, borderCol, RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 4. CATEGORY FILTER CHIPS
            // ==========================================
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categoryList) { (catKey, catLabel) ->
                    val isSelected = selectedCategory == catKey
                    val chipBg = if (isSelected) (if (isDarkMode) Color(0xFF10B981) else SlateBlue) else cardBg
                    val chipText = if (isSelected) (if (isDarkMode) Color(0xFF080C14) else Color.White) else textColor
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(chipBg)
                            .border(width = 1.5.dp, color = if (isSelected) chipBg else borderCol, shape = RoundedCornerShape(12.dp))
                            .clickable { viewModel.setSelectedCategory(catKey) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = catLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = chipText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Test Late-Night Friction Engine Simulation Banner
            var showFrictionSimulation by remember { mutableStateOf(false) }
            val streakDays by viewModel.streakDays.collectAsState()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkMode) Color(0xFF162032) else Color(0xFFEFF6FF))
                    .border(1.dp, if (isDarkMode) Color(0xFF2563EB).copy(alpha = 0.5f) else Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                    .clickable { showFrictionSimulation = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🛡️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Test Late-Night Friction Engine",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = "Preview 60s hard pause, typing reset & +50 XP hatch",
                            fontSize = 10.sp,
                            color = subtextColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDarkMode) Color(0xFF2563EB) else SlateBlue)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "TEST",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            if (showFrictionSimulation) {
                androidx.compose.ui.window.Dialog(
                    onDismissRequest = { showFrictionSimulation = false },
                    properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    com.example.ui.components.LateNightFrictionScreen(
                        appName = "Social Media (Demo)",
                        reason = "Late-Night Shield Active",
                        streakDays = streakDays,
                        onboardingGoal = viewModel.getOnboardingGoalSummary(),
                        onGoHome = { showFrictionSimulation = false },
                        onEscapeHatchReward = {
                            viewModel.awardEscapeHatchPoints()
                            showFrictionSimulation = false
                        },
                        onUnlockConfirmed = {
                            showFrictionSimulation = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // 5. APP ITEMS LIST
            // ==========================================
            if (isLoadingApps) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(6) {
                        com.example.ui.components.PixelShimmerAppCard()
                    }
                }
            } else if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matching applications found.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = subtextColor
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        PixelAppItemCard(
                            app = app,
                            onToggleBlock = { enable ->
                                viewModel.toggleAppBlock(app, enable)
                            },
                            onUpdateLimit = { limitMin, isInstant ->
                                viewModel.updateAppLimit(app.packageName, app.appName, limitMin, isInstant)
                            }
                        )
                    }

                    // Spacer at end of list for bottom pill nav bar
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

private val globalIconCache = java.util.concurrent.ConcurrentHashMap<String, androidx.compose.ui.graphics.ImageBitmap>()

@Composable
fun AppIconImage(
    packageName: String,
    appName: String,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var cachedImage by remember(packageName) { mutableStateOf(globalIconCache[packageName]) }

    if (cachedImage == null) {
        androidx.compose.runtime.LaunchedEffect(packageName) {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val icon = context.packageManager.getApplicationIcon(packageName)
                    val w = icon.intrinsicWidth.coerceAtLeast(48)
                    val h = icon.intrinsicHeight.coerceAtLeast(48)
                    val bmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(bmp)
                    icon.setBounds(0, 0, canvas.width, canvas.height)
                    icon.draw(canvas)
                    val imageBmp = bmp.asImageBitmap()
                    globalIconCache[packageName] = imageBmp
                    cachedImage = imageBmp
                } catch (e: Exception) {
                    // Fallback
                }
            }
        }
    }

    val img = cachedImage
    if (img != null) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF8FAFC))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                bitmap = img,
                contentDescription = appName,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        }
    } else {
        PixelStarBadge(
            text = appName.take(1).uppercase(),
            modifier = modifier,
            badgeColor = Color(0xFFBA2D0B)
        )
    }
}

@Composable
fun PixelAppItemCard(
    app: InstalledAppItem,
    onToggleBlock: (Boolean) -> Unit,
    onUpdateLimit: (Int, Boolean) -> Unit
) {
    var sliderValue by remember(app.dailyLimitMinutes) { mutableFloatStateOf(app.dailyLimitMinutes.toFloat()) }
    val isDark = LocalIsDarkTheme.current
    val itemTextColor = if (isDark) Color(0xFFF8FAFC) else SlateBlue
    val itemSubtextColor = if (isDark) Color(0xFF94A3B8) else SlateBlue.copy(alpha = 0.7f)

    PixelCardContainer(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Real Application Logo Icon
                AppIconImage(
                    packageName = app.packageName,
                    appName = app.appName,
                    modifier = Modifier.size(42.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // App Title and Subtitle Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = itemTextColor
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${app.category} • Spent ${app.todayUsageSeconds / 60}m today ",
                            fontSize = 11.sp,
                            color = itemSubtextColor
                        )
                        Text(
                            text = "• +5 pts",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color(0xFF10B981) else PixelGreenAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 8-Bit Retro Pixel Switch
                PixelSwitch(
                    checked = app.isBlocked,
                    onCheckedChange = { onToggleBlock(it) },
                    activeColor = if (app.isInstantBlocked) PixelGoldAccent else (if (isDark) Color(0xFF10B981) else PixelGreenAccent)
                )
            }

            // Expanded Limit Settings (When Block is active)
            AnimatedVisibility(visible = app.isBlocked) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = itemTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Daily Limit: ${sliderValue.toInt()} min",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = itemTextColor
                            )
                        }

                        // Instant Block Toggle Switch
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = if (app.isInstantBlocked) PixelGoldAccent else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Instant Block",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (app.isInstantBlocked) itemTextColor else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            PixelSwitch(
                                checked = app.isInstantBlocked,
                                onCheckedChange = { isInstant ->
                                    onUpdateLimit(sliderValue.toInt(), isInstant)
                                },
                                activeColor = PixelGoldAccent,
                                modifier = Modifier.size(height = 22.dp, width = 42.dp)
                            )
                        }
                    }

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        onValueChangeFinished = {
                            onUpdateLimit(sliderValue.toInt(), app.isInstantBlocked)
                        },
                        valueRange = 0f..180f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = if (isDark) Color(0xFF10B981) else SlateBlue,
                            activeTrackColor = if (isDark) Color(0xFF10B981) else PixelGreenAccent,
                            inactiveTrackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                        )
                    )
                }
            }
        }
    }
}
