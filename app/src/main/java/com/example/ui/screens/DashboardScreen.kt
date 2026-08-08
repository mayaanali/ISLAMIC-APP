package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyQuest
import com.example.data.PrayerItem
import com.example.data.QuestCategory
import com.example.ui.components.DoraemonEasterEgg
import com.example.ui.components.PixelAccountProfileDialog
import com.example.ui.components.PixelAnimatedErrorScreen
import com.example.ui.components.PixelAnimatedLoadingScreen
import com.example.ui.components.PixelAvatarIcon
import com.example.ui.components.PixelBgCanvas
import com.example.ui.components.PixelCardContainer
import com.example.ui.components.PixelCoinIcon
import com.example.ui.components.PixelCrescentStarIcon
import com.example.ui.components.PixelDarkBorder
import com.example.ui.components.PixelGoldAccent
import com.example.ui.components.PixelGreenAccent
import com.example.ui.components.PixelLiveIrlTipsModal
import com.example.ui.components.PixelLiveIrlTrackerCard
import com.example.ui.components.PixelPurityGauge
import com.example.ui.components.PixelQuestActionDialog
import com.example.ui.components.PixelRedHeart
import com.example.ui.components.PixelSetLimitDialog
import com.example.ui.components.PixelXpLevelBar
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToApps: () -> Unit,
    onNavigateToFocus: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToAICoach: () -> Unit,
    onNavigateToQuests: () -> Unit = {}
) {
    val coins by viewModel.coins.collectAsState()
    val purityIndex by viewModel.purityIndex.collectAsState()
    val prayersList by viewModel.prayersList.collectAsState()
    val dailyQuests by viewModel.dailyQuests.collectAsState()
    val remainingScreenTimeMinutes by viewModel.remainingScreenTimeMinutes.collectAsState()
    val socialMediaLimitMinutes by viewModel.socialMediaLimitMinutes.collectAsState()
    val generalAppsLimitMinutes by viewModel.generalAppsLimitMinutes.collectAsState()
    val liveTotalSeconds by viewModel.liveScreenTimeSeconds.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val totalZakatDonated by viewModel.totalZakatDonated.collectAsState()
    val resistedCount by viewModel.resistedCount.collectAsState()
    val isLoadingApps by viewModel.isLoadingApps.collectAsState()
    val hasError by viewModel.hasError.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var pullOffsetPx by remember { mutableFloatStateOf(0f) }
    var isPrayersExpanded by remember { mutableStateOf(false) }
    var showSetLimitDialog by remember { mutableStateOf(false) }
    var showIrlTipsModal by remember { mutableStateOf(false) }
    var showAccountProfile by remember { mutableStateOf(false) }
    var selectedQuestForAction by remember { mutableStateOf<DailyQuest?>(null) }

    // Account Profile Dialog
    if (showAccountProfile) {
        PixelAccountProfileDialog(
            totalCoins = coins,
            streakDays = streakDays,
            purityIndex = purityIndex,
            zakatDonated = totalZakatDonated,
            resistedCount = resistedCount,
            onDismiss = { showAccountProfile = false }
        )
    }

    // Quest Action Dialog
    if (selectedQuestForAction != null) {
        PixelQuestActionDialog(
            quest = selectedQuestForAction!!,
            onDismiss = { selectedQuestForAction = null },
            onAcceptQuest = { /* Marked as accepted */ },
            onExchangeQuest = { viewModel.randomizeDailyQuests() },
            onVerifyAndClaimPoints = { questId ->
                viewModel.claimQuestPoints(questId)
            }
        )
    }

    // Handle Error State
    if (hasError) {
        PixelAnimatedErrorScreen(
            errorMessage = errorMessage ?: "Deen Shield Telemetry Sync Error",
            onRetry = { viewModel.retryEngine() }
        )
        return
    }

    // Handle Loading State
    if (isLoadingApps) {
        PixelAnimatedLoadingScreen(
            statusText = "SYNCING REAL-TIME DEEN TELEMETRY..."
        )
        return
    }

    // Calculate prayer points earned today & quest points
    val completedPrayerPoints = prayersList.filter { it.isCompleted }.sumOf { it.totalPoints }
    val completedQuestPoints = dailyQuests.filter { it.isCompleted }.sumOf { it.points }
    val pointsEarnedToday = completedPrayerPoints + completedQuestPoints
    val unclaimedQuestPoints = dailyQuests.filter { !it.isCompleted }.sumOf { it.points }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PixelBgCanvas)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        if (dragAmount > 0 || pullOffsetPx > 0) {
                            pullOffsetPx = (pullOffsetPx + dragAmount * 0.5f).coerceIn(0f, 250f)
                        }
                    },
                    onDragEnd = { pullOffsetPx = 0f }
                )
            }
    ) {
        // Geometric Arabesque Tile Background Effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val step = 80f

            for (x in 0..(w / step).toInt()) {
                for (y in 0..(h / step).toInt()) {
                    val px = x * step
                    val py = y * step
                    drawCircle(
                        color = Color(0xFFDCDFE3),
                        radius = 20f,
                        center = Offset(px, py),
                        style = Stroke(width = 1.5f)
                    )
                    drawLine(
                        color = Color(0xFFE2E5E9),
                        start = Offset(px - 10f, py),
                        end = Offset(px + 10f, py),
                        strokeWidth = 1f
                    )
                }
            }
        }

        // Over-scroll Easter Egg
        DoraemonEasterEgg(pullOffsetPx = pullOffsetPx)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // 1. TOP HEADER SECTION
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
                        text = String.format("%,d pts", coins),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(modifier = Modifier.clickable { showAccountProfile = true }) {
                        PixelAvatarIcon(modifier = Modifier.size(30.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Gamified 2D XP & Level Progress Bar
            PixelXpLevelBar(coins = coins)

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 2. PURITY INDEX CIRCULAR GAUGE (With Islamic Pattern & Solid 8-Bit Pixel Heart)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                PixelPurityGauge(purityPercentage = purityIndex)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time IRL Screen Time Tracker Card
            PixelLiveIrlTrackerCard(
                liveTotalSeconds = liveTotalSeconds,
                onClick = { showIrlTipsModal = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 3. ACTIVE SHIELD CARD (Matches screenshot)
            // ==========================================
            PixelCardContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToApps() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PixelGreenAccent.copy(alpha = 0.15f))
                                .border(1.5.dp, PixelGreenAccent, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            PixelCrescentStarIcon(modifier = Modifier.size(20.dp), color = PixelGreenAccent)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Active Shield",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PixelDarkBorder
                            )
                            Text(
                                text = "Instagram blocked • Focus active",
                                fontSize = 11.sp,
                                color = PixelDarkBorder.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(PixelGreenAccent, shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Safe",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 4. POINTS EARNED TODAY & PRAYER TRACKER BOX
            // ==========================================
            PixelCardContainer(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToQuests() }
                        ) {
                            Text(
                                text = "PRAYER TRACKER & POINTS EARNED TODAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PixelDarkBorder.copy(alpha = 0.7f),
                                letterSpacing = 0.8.sp
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$pointsEarnedToday",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PixelGreenAccent
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "pts Today",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PixelDarkBorder,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }

                        // Dropdown Toggle Button for Prayers
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                .clickable { isPrayersExpanded = !isPrayersExpanded }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Prayers",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PixelDarkBorder
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = if (isPrayersExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Toggle Prayers Dropdown",
                                tint = PixelDarkBorder,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = {
                            if (pointsEarnedToday > 0) (pointsEarnedToday / 100f).coerceIn(0f, 1f) else 0.1f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PixelGreenAccent,
                        trackColor = Color(0xFFE2E8F0)
                    )

                    // 5 Prayer Sequence List - Only visible when drop down menu is clicked
                    AnimatedVisibility(visible = isPrayersExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            prayersList.forEach { prayer ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.togglePrayerCompletion(prayer.id) }
                                        .padding(vertical = 6.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (prayer.isCompleted) PixelGreenAccent else Color(0xFFCBD5E1),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = prayer.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (prayer.isCompleted) PixelDarkBorder else PixelDarkBorder.copy(alpha = 0.6f)
                                        )
                                    }

                                    Text(
                                        text = "+${prayer.totalPoints} pts",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (prayer.isCompleted) PixelGreenAccent else PixelDarkBorder.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // UNCLAIMED POINTS INDICATOR LINE
                    if (unclaimedQuestPoints > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp))
                                .border(1.dp, PixelGoldAccent, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.claimAllPendingQuests()
                                    onNavigateToQuests()
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "⚡ You have $unclaimedQuestPoints unclaimed quest points!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB45309)
                                )
                                Text(
                                    text = "Tap to claim →",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PixelDarkBorder
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Tap box to view full Points Breakdown & Quests →",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = PixelGreenAccent,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 5. SCREEN TIME LIMIT CARD
            // ==========================================
            PixelCardContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSetLimitDialog = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SCREEN TIME LIMIT (TAP TO SET)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PixelDarkBorder.copy(alpha = 0.7f),
                            letterSpacing = 0.8.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HourglassBottom,
                                contentDescription = "Hourglass",
                                tint = PixelGoldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${remainingScreenTimeMinutes}m Left",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PixelDarkBorder
                            )
                        }
                        Text(
                            text = "Social Media: ${socialMediaLimitMinutes}m | Apps: ${generalAppsLimitMinutes}m",
                            fontSize = 9.5.sp,
                            color = PixelDarkBorder.copy(alpha = 0.65f)
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (remainingScreenTimeMinutes / 60f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .width(100.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PixelGoldAccent,
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom nav bar
        }

        // ==========================================
        // 6. MODAL DIALOGS
        // ==========================================
        if (showSetLimitDialog) {
            PixelSetLimitDialog(
                initialSocialMediaMinutes = socialMediaLimitMinutes,
                initialGeneralAppsMinutes = generalAppsLimitMinutes,
                onDismiss = { showSetLimitDialog = false },
                onSaveLimits = { socialMins, generalMins ->
                    viewModel.setDailyLimits(socialMins, generalMins)
                }
            )
        }

        if (showIrlTipsModal) {
            PixelLiveIrlTipsModal(
                liveTotalSeconds = liveTotalSeconds,
                onDismiss = { showIrlTipsModal = false }
            )
        }
    }
}
