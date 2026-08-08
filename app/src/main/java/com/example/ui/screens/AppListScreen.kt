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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    val categories = remember { listOf("All", "Social", "Entertainment", "Gaming", "App", "System") }

    val filteredApps = remember(installedApps, searchQuery, selectedCategory) {
        installedApps.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                    item.appName.contains(searchQuery, ignoreCase = true) ||
                    item.packageName.contains(searchQuery, ignoreCase = true)
            val matchesCat = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
            matchesSearch && matchesCat
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PixelBgCanvas)
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
                        color = PixelDarkBorder,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "DAILY DEEN JOURNEY | SPIRITUAL VITALITY",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = PixelDarkBorder.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp
                    )
                }

                // Calligraphy Header
                Text(
                    text = "بِسْمِ اللَّهِ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PixelDarkBorder,
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
                        color = PixelDarkBorder
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
                    text = "APP BLOCKER & LIMITS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PixelDarkBorder,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${filteredApps.size} apps found",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PixelDarkBorder.copy(alpha = 0.7f)
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
                        color = PixelDarkBorder.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PixelDarkBorder
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.refreshInstalledApps() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = PixelDarkBorder
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PixelDarkBorder,
                    unfocusedBorderColor = PixelDarkBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.5.dp, PixelDarkBorder, RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 4. CATEGORY FILTER CHIPS
            // ==========================================
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PixelDarkBorder else Color.White)
                            .border(width = 2.dp, color = PixelDarkBorder, shape = RoundedCornerShape(12.dp))
                            .clickable { viewModel.setSelectedCategory(category) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else PixelDarkBorder
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 5. APP ITEMS LIST
            // ==========================================
            if (isLoadingApps) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PixelGreenAccent)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Scanning installed applications...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PixelDarkBorder
                        )
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
                        color = PixelDarkBorder.copy(alpha = 0.7f)
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

@Composable
fun PixelAppItemCard(
    app: InstalledAppItem,
    onToggleBlock: (Boolean) -> Unit,
    onUpdateLimit: (Int, Boolean) -> Unit
) {
    var sliderValue by remember(app.dailyLimitMinutes) { mutableFloatStateOf(app.dailyLimitMinutes.toFloat()) }

    PixelCardContainer(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Red Star Polygon Badge Icon with letter/number
                PixelStarBadge(
                    text = app.appName.take(1),
                    modifier = Modifier.size(42.dp),
                    badgeColor = Color(0xFFBA2D0B)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // App Title and Subtitle Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${app.category} • Spent ${app.todayUsageSeconds / 60}m today ",
                            fontSize = 11.sp,
                            color = PixelDarkBorder.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "• +5 pts",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelGreenAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 8-Bit Retro Pixel Switch
                PixelSwitch(
                    checked = app.isBlocked,
                    onCheckedChange = { onToggleBlock(it) },
                    activeColor = if (app.isInstantBlocked) PixelGoldAccent else PixelGreenAccent
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
                                tint = PixelDarkBorder,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Daily Limit: ${sliderValue.toInt()} min",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PixelDarkBorder
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
                                color = if (app.isInstantBlocked) PixelDarkBorder else Color.Gray
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
                            thumbColor = PixelDarkBorder,
                            activeTrackColor = PixelGreenAccent,
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        )
                    )
                }
            }
        }
    }
}
