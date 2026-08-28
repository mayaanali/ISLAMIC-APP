package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LeaderboardRegion
import com.example.data.LeaderboardUser
import com.example.data.SpiritualLeague
import com.example.ui.components.PixelAvatarIcon
import com.example.ui.components.PixelCardContainer
import com.example.ui.components.PixelCoinIcon
import com.example.ui.components.PixelStarBadge
import com.example.ui.theme.DesertGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SlateBlue
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LeaderboardScreen(viewModel: MainViewModel) {
    val leaderboardUsers by viewModel.currentLeaderboard.collectAsState()
    val selectedRegion by viewModel.selectedLeaderboardRegion.collectAsState()
    val selectedTimeframe by viewModel.selectedLeaderboardTimeframe.collectAsState()
    val isAnonMode by viewModel.leaderboardAnonymousMode.collectAsState()
    val alias by viewModel.leaderboardAlias.collectAsState()
    val userCoins by viewModel.coins.collectAsState()
    val userPurity by viewModel.purityIndex.collectAsState()
    val userStreak by viewModel.streakDays.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val textColor = if (isDarkMode) Color(0xFFF8FAFC) else SlateBlue
    val subtextColor = if (isDarkMode) Color(0xFF94A3B8) else SlateBlue.copy(alpha = 0.7f)
    val cardBg = if (isDarkMode) Color(0xFF131B2E) else Color.White
    val borderCol = if (isDarkMode) Color(0xFF2A3854) else Color(0xFFE2E8F0)

    val currentUser = leaderboardUsers.firstOrNull { it.isCurrentUser }

    var showLeagueInfoModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 76.dp)
            .testTag("leaderboard_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))

                // Top Realtime Header Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GLOBAL LEADERBOARD",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Live Pulse Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIVE REALTIME",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Rank calculated in real-time by Deen XP & Shield Purity %",
                            style = MaterialTheme.typography.bodySmall,
                            color = subtextColor
                        )
                    }

                    IconButton(
                        onClick = { showLeagueInfoModal = !showLeagueInfoModal },
                        modifier = Modifier.testTag("league_info_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Spiritual Leagues",
                            tint = DesertGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Timeframe Segmented Switch (Live / Weekly / All-Time)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBg)
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Live Realtime", "Weekly Tournament", "All-Time Legends").forEach { tf ->
                        val isSelected = selectedTimeframe == tf
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldGreen else Color.Transparent)
                                .clickable { viewModel.setLeaderboardTimeframe(tf) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tf,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else subtextColor
                            )
                        }
                    }
                }
            }

            // Region Selector Horizontal Chips
            item {
                Column {
                    Text(
                        text = "REGION FILTER",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = subtextColor,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(LeaderboardRegion.values()) { region ->
                            val isSelected = selectedRegion == region
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) (if (isDarkMode) Color(0xFF1E2B45) else Color(0xFFE2E8F0)) else cardBg)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) EmeraldGreen else borderCol,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.setLeaderboardRegion(region) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = region.flagEmoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = region.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) textColor else subtextColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Top 3 Podium Visual Display
            if (leaderboardUsers.size >= 3) {
                item {
                    PodiumDisplay(
                        topUsers = leaderboardUsers.take(3),
                        isDarkMode = isDarkMode
                    )
                }
            }

            // Anonymous Sitr Mode Banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF1F5F9))
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "🛡️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sitr Mode (Anonymous on Board)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                text = if (isAnonMode) "Showing as $alias" else "Showing real name in rankings",
                                fontSize = 11.sp,
                                color = subtextColor
                            )
                        }
                    }

                    Switch(
                        checked = isAnonMode,
                        onCheckedChange = { viewModel.toggleLeaderboardAnonymous(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmeraldGreen
                        ),
                        modifier = Modifier.testTag("leaderboard_anon_switch")
                    )
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LEADERBOARD RANKINGS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = subtextColor,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${leaderboardUsers.size} Believers",
                        fontSize = 11.sp,
                        color = subtextColor
                    )
                }
            }

            // Leaderboard List Rows
            itemsIndexed(leaderboardUsers) { index, user ->
                LeaderboardRowItem(
                    user = user,
                    isDarkMode = isDarkMode
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Pinned User Ranking Floating Bar at Bottom
        if (currentUser != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                UserStickyRankCard(
                    user = currentUser,
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

/**
 * Top 3 Podium
 */
@Composable
private fun PodiumDisplay(
    topUsers: List<LeaderboardUser>,
    isDarkMode: Boolean
) {
    val u1 = topUsers.getOrNull(0)
    val u2 = topUsers.getOrNull(1)
    val u3 = topUsers.getOrNull(2)

    val cardBg = if (isDarkMode) Color(0xFF131B2E) else Color.White
    val borderCol = if (isDarkMode) Color(0xFF2A3854) else Color(0xFFE2E8F0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, borderCol, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // #2 Silver
        if (u2 != null) {
            PodiumPillar(
                user = u2,
                rank = 2,
                medalEmoji = "🥈",
                pillarHeight = 90.dp,
                pillarColor = Color(0xFF94A3B8),
                isDarkMode = isDarkMode
            )
        }

        // #1 Gold
        if (u1 != null) {
            PodiumPillar(
                user = u1,
                rank = 1,
                medalEmoji = "👑",
                pillarHeight = 118.dp,
                pillarColor = DesertGold,
                isDarkMode = isDarkMode
            )
        }

        // #3 Bronze
        if (u3 != null) {
            PodiumPillar(
                user = u3,
                rank = 3,
                medalEmoji = "🥉",
                pillarHeight = 75.dp,
                pillarColor = Color(0xFFCD7F32),
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
private fun PodiumPillar(
    user: LeaderboardUser,
    rank: Int,
    medalEmoji: String,
    pillarHeight: androidx.compose.ui.unit.Dp,
    pillarColor: Color,
    isDarkMode: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(95.dp)
    ) {
        Text(text = medalEmoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(pillarColor.copy(alpha = 0.2f))
                .border(2.dp, pillarColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.take(1).uppercase(),
                fontWeight = FontWeight.Black,
                color = if (isDarkMode) Color.White else SlateBlue,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = user.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isDarkMode) Color.White else SlateBlue,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${user.points} XP",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldGreen
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pillar block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pillarHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(pillarColor.copy(alpha = 0.25f))
                .border(1.dp, pillarColor.copy(alpha = 0.5f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "#$rank",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = pillarColor
                )
                Text(
                    text = "${user.shieldPurityPercent}% Shield",
                    fontSize = 9.sp,
                    color = if (isDarkMode) Color(0xFFCBD5E1) else SlateBlue
                )
            }
        }
    }
}

/**
 * Leaderboard Item Row
 */
@Composable
private fun LeaderboardRowItem(
    user: LeaderboardUser,
    isDarkMode: Boolean
) {
    val cardBg = when {
        user.isCurrentUser -> if (isDarkMode) Color(0xFF1E2D4A) else Color(0xFFECFDF5)
        else -> if (isDarkMode) Color(0xFF131B2E) else Color.White
    }
    val borderCol = when {
        user.isCurrentUser -> EmeraldGreen
        user.rank == 1 -> DesertGold
        else -> if (isDarkMode) Color(0xFF2A3854) else Color(0xFFE2E8F0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(if (user.isCurrentUser) 1.5.dp else 1.dp, borderCol, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Rank & Delta
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(44.dp)
        ) {
            Text(
                text = "#${user.rank}",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = when (user.rank) {
                    1 -> DesertGold
                    2 -> Color(0xFF94A3B8)
                    3 -> Color(0xFFCD7F32)
                    else -> if (isDarkMode) Color(0xFF94A3B8) else SlateBlue
                }
            )
            if (user.rankDelta > 0) {
                Text(
                    text = " ▲",
                    fontSize = 9.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Avatar & Details
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isDarkMode) Color.White else SlateBlue
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (user.isCurrentUser) "${user.name} (You)" else user.name,
                        fontSize = 13.sp,
                        fontWeight = if (user.isCurrentUser) FontWeight.Black else FontWeight.Bold,
                        color = if (isDarkMode) Color.White else SlateBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = user.region.flagEmoji, fontSize = 11.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${user.league.iconEmoji} ${user.league.title}",
                        fontSize = 10.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                    Text(text = " • ", fontSize = 10.sp, color = Color(0xFF64748B))
                    Text(
                        text = "🔥 ${user.streakDays}d",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF59E0B)
                    )
                }
            }
        }

        // Points & Shield Percentage Badge
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.width(90.dp)
        ) {
            Text(
                text = "${user.points} XP",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = EmeraldGreen
            )

            // Shield Purity Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "🛡️ ${user.shieldPurityPercent}%",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }
        }
    }
}

/**
 * Sticky Pinned User Rank Card
 */
@Composable
private fun UserStickyRankCard(
    user: LeaderboardUser,
    isDarkMode: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDarkMode) Color(0xFF101B2E) else Color(0xFF0F172A))
            .border(1.5.dp, EmeraldGreen, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rank Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${user.rank}",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Your Live Rank: #${user.rank}",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Top ${if (user.rank <= 3) "Podium!" else "${(user.rank * 10).coerceAtMost(100)}% Worldwide"}",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${user.points} Deen XP",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldGreen
                    )
                    Text(
                        text = "🛡️ ${user.shieldPurityPercent}% Shield Purity",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF34D399)
                    )
                }
            }
        }
    }
}
