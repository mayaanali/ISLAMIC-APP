package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesertGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SlateBlue

/**
 * High-performance 60fps Shimmer Sweep modifier respecting Dark and Light modes.
 */
fun Modifier.pixelShimmer(
    shape: Shape = RoundedCornerShape(8.dp)
): Modifier = composed {
    val isDark = LocalIsDarkTheme.current
    val transition = rememberInfiniteTransition(label = "PixelShimmer")

    val translateAnim by transition.animateFloat(
        initialValue = -300f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )

    val shimmerColors = if (isDark) {
        listOf(
            Color(0xFF131B2E),
            Color(0xFF1E2B45),
            Color(0xFF2A3A5C),
            Color(0xFF1E2B45),
            Color(0xFF131B2E)
        )
    } else {
        listOf(
            Color(0xFFE2E8F0),
            Color(0xFFF1F5F9),
            Color(0xFFFFFFFF),
            Color(0xFFF1F5F9),
            Color(0xFFE2E8F0)
        )
    }

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim + 300f, translateAnim + 300f)
    )

    this
        .clip(shape)
        .background(brush)
}

/**
 * Pixelated Shimmer Skeleton for App List Items
 */
@Composable
fun PixelShimmerAppCard(
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val cardBg = if (isDark) Color(0xFF131B2E) else Color.White
    val cardBorder = if (isDark) Color(0xFF2A3854) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.2.dp, cardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon Placeholder
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .pixelShimmer(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Text Info Columns
            Column(modifier = Modifier.weight(1f)) {
                // Title line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .pixelShimmer(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle / category line
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(12.dp)
                            .pixelShimmer(RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(75.dp)
                            .height(12.dp)
                            .pixelShimmer(RoundedCornerShape(4.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Switch / Toggle placeholder
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(26.dp)
                    .pixelShimmer(RoundedCornerShape(13.dp))
            )
        }
    }
}

/**
 * Pixelated Shimmer Skeleton for Quests
 */
@Composable
fun PixelShimmerQuestCard(
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val cardBg = if (isDark) Color(0xFF131B2E) else Color.White
    val cardBorder = if (isDark) Color(0xFF2A3854) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.2.dp, cardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox placeholder
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .pixelShimmer(RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(14.dp)
                        .pixelShimmer(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(11.dp)
                        .pixelShimmer(RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Points badge placeholder
            Box(
                modifier = Modifier
                    .width(52.dp)
                    .height(24.dp)
                    .pixelShimmer(RoundedCornerShape(12.dp))
            )
        }
    }
}

/**
 * Pixelated Shimmer Skeleton for Prayer Row
 */
@Composable
fun PixelShimmerPrayerCard(
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val cardBg = if (isDark) Color(0xFF131B2E) else Color.White
    val cardBorder = if (isDark) Color(0xFF2A3854) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.2.dp, cardBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .pixelShimmer(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(15.dp)
                        .pixelShimmer(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(11.dp)
                        .pixelShimmer(RoundedCornerShape(4.dp))
                )
            }

            Box(
                modifier = Modifier
                    .width(65.dp)
                    .height(28.dp)
                    .pixelShimmer(RoundedCornerShape(8.dp))
            )
        }
    }
}
