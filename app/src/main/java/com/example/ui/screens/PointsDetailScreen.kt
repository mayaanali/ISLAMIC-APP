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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyQuest
import com.example.ui.components.PixelAccountProfileDialog
import com.example.ui.components.PixelAvatarIcon
import com.example.ui.components.PixelBgCanvas
import com.example.ui.components.PixelCardContainer
import com.example.ui.components.PixelCoinIcon
import com.example.ui.components.PixelCrescentStarIcon
import com.example.ui.components.PixelDarkBorder
import com.example.ui.components.PixelGoldAccent
import com.example.ui.components.PixelGreenAccent
import com.example.ui.components.PixelQuestActionDialog
import com.example.ui.components.PixelZakatDonationWizard
import com.example.ui.theme.SlateBlue
import com.example.ui.viewmodel.MainViewModel

@Composable
fun PointsDetailScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val coins by viewModel.coins.collectAsState()
    val prayersList by viewModel.prayersList.collectAsState()
    val dailyQuests by viewModel.dailyQuests.collectAsState()
    val totalZakatDonated by viewModel.totalZakatDonated.collectAsState()
    val purityIndex by viewModel.purityIndex.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val resistedCount by viewModel.resistedCount.collectAsState()

    var selectedQuestForAction by remember { mutableStateOf<DailyQuest?>(null) }
    var showZakatWizard by remember { mutableStateOf(false) }
    var showAccountProfile by remember { mutableStateOf(false) }

    val completedPrayerPoints = prayersList.filter { it.isCompleted }.sumOf { it.totalPoints }
    val completedQuestPoints = dailyQuests.filter { it.isCompleted }.sumOf { it.points }
    val pointsEarnedToday = completedPrayerPoints + completedQuestPoints

    val unclaimedQuests = dailyQuests.filter { !it.isCompleted }
    val pendingQuestsCount = unclaimedQuests.size
    val unclaimedQuestPoints = unclaimedQuests.sumOf { it.points }

    // Dialogs
    if (selectedQuestForAction != null) {
        PixelQuestActionDialog(
            quest = selectedQuestForAction!!,
            onDismiss = { selectedQuestForAction = null },
            onAcceptQuest = { /* Marked as accepted in dialog */ },
            onExchangeQuest = { viewModel.randomizeDailyQuests() },
            onVerifyAndClaimPoints = { questId ->
                viewModel.claimQuestPoints(questId)
            }
        )
    }

    if (showZakatWizard) {
        PixelZakatDonationWizard(
            onDismiss = { showZakatWizard = false },
            onConfirmDonation = { amount ->
                viewModel.logZakatDonation(amount)
            }
        )
    }

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

    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val textColor = if (isDarkMode) Color(0xFFF8FAFC) else SlateBlue
    val subtextColor = if (isDarkMode) Color(0xFF94A3B8) else SlateBlue.copy(alpha = 0.75f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color(0xFF080C14) else PixelBgCanvas)
    ) {
        // Geometric Islamic Tile Lattice Background Effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val step = 80f
            val dotColor = if (isDarkMode) Color(0xFF101726) else Color(0xFFDCDFE3)
            val lineCol = if (isDarkMode) Color(0xFF0D1420) else Color(0xFFE2E5E9)

            for (x in 0..(w / step).toInt()) {
                for (y in 0..(h / step).toInt()) {
                    val px = x * step
                    val py = y * step
                    drawCircle(
                        color = dotColor,
                        radius = 20f,
                        center = Offset(px, py),
                        style = Stroke(width = 1.5f)
                    )
                    drawLine(
                        color = lineCol,
                        start = Offset(px - 10f, py),
                        end = Offset(px + 10f, py),
                        strokeWidth = 1f
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // 1. TOP HEADER WITH BACK NAVIGATION
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, PixelDarkBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PixelDarkBorder
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Points Breakdown",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelDarkBorder,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "HOW POINTS WERE EARNED TODAY",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = PixelDarkBorder.copy(alpha = 0.7f),
                            letterSpacing = 0.8.sp
                        )
                    }
                }

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

            // ==========================================
            // 2. UNCLAIMED POINTS ALERT LINE (If user forgot to claim)
            // ==========================================
            PixelCardContainer(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (unclaimedQuestPoints > 0) Color(0xFFFFFBEB) else Color(0xFFF0FDF4),
                borderColor = if (unclaimedQuestPoints > 0) PixelGoldAccent else PixelGreenAccent
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (unclaimedQuestPoints > 0) PixelGoldAccent else PixelGreenAccent,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                if (unclaimedQuestPoints > 0) {
                                    Text(
                                        text = "⚡ $pendingQuestsCount Quests Available to Complete ($unclaimedQuestPoints PTS)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFB45309)
                                    )
                                    Text(
                                        text = "Tap below or complete quests to claim your points for today.",
                                        fontSize = 11.sp,
                                        color = PixelDarkBorder.copy(alpha = 0.75f)
                                    )
                                } else {
                                    Text(
                                        text = "✓ All Available Quest Points Claimed Today!",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PixelGreenAccent
                                    )
                                    Text(
                                        text = "Awesome dedication! Keep up your spiritual habits.",
                                        fontSize = 11.sp,
                                        color = PixelDarkBorder.copy(alpha = 0.75f)
                                    )
                                }
                            }
                        }
                    }

                    if (unclaimedQuestPoints > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.claimAllPendingQuests() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PixelGoldAccent,
                                contentColor = PixelDarkBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "⚡ CLAIM ALL $unclaimedQuestPoints POINTS NOW",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 3. POINTS SUMMARY BREAKDOWN CARD
            // ==========================================
            PixelCardContainer(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "TODAY'S POINTS EARNED LOG",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PixelDarkBorder.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Earned Today",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PixelDarkBorder
                        )
                        Text(
                            text = "+$pointsEarnedToday pts",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelGreenAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Breakdown items
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Five Daily Prayers", fontSize = 12.sp, color = PixelDarkBorder)
                        Text("+$completedPrayerPoints pts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PixelGreenAccent)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Daily Deen Quests", fontSize = 12.sp, color = PixelDarkBorder)
                        Text("+$completedQuestPoints pts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PixelGreenAccent)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Zakat & Charity Logged", fontSize = 12.sp, color = PixelDarkBorder)
                        Text("$${String.format("%.2f", totalZakatDonated)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PixelGoldAccent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 4. DAILY QUESTS SYSTEM & CLAIMING
            // ==========================================
            PixelCardContainer(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DAILY DEEN QUESTS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PixelDarkBorder.copy(alpha = 0.7f),
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Complete & Earn Points",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PixelDarkBorder
                            )
                        }

                        // Shuffle Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { viewModel.randomizeDailyQuests() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Shuffle",
                                tint = PixelDarkBorder,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Shuffle",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PixelDarkBorder
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    dailyQuests.forEach { quest ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (quest.isCompleted) PixelGreenAccent.copy(alpha = 0.12f)
                                    else Color(0xFFF8FAFC)
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (quest.isCompleted) PixelGreenAccent else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (quest.isCompleted) {
                                        viewModel.toggleQuestCompletion(quest.id)
                                    } else {
                                        selectedQuestForAction = quest
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (quest.isCompleted) Icons.Default.CheckCircle else Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (quest.isCompleted) PixelGreenAccent else PixelGoldAccent,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = "[${quest.category.label}] ${quest.title}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PixelDarkBorder
                                        )
                                        Text(
                                            text = quest.description,
                                            fontSize = 10.5.sp,
                                            color = PixelDarkBorder.copy(alpha = 0.65f)
                                        )
                                    }
                                }

                                Text(
                                    text = if (quest.isCompleted) "✓ Claimed (+${quest.points})" else "+${quest.points} pts",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (quest.isCompleted) PixelGreenAccent else PixelDarkBorder,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 5. ZAKAT & SADAQAH LOGGING
            // ==========================================
            PixelCardContainer(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Zakat",
                                tint = PixelGoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ZAKAT & SADAQAH LOG",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PixelDarkBorder.copy(alpha = 0.7f),
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "Total Donated: $${String.format("%.2f", totalZakatDonated)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PixelGreenAccent
                                )
                            }
                        }

                        Button(
                            onClick = { showZakatWizard = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PixelGoldAccent,
                                contentColor = PixelDarkBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Log Zakat",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
