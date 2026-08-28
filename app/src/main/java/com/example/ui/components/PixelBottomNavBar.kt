package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.Screen
import com.example.ui.theme.AlabasterSand
import com.example.ui.theme.DesertGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeumorphicBox
import com.example.ui.theme.SlateBlue
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 5-Tab Neumorphic Navigation Bar matching User Reference Image 1:
 * 1. Mosque with Heart (Dashboard / Guided Path)
 * 2. Prayer Rug / Sajjadah (Focus & Salah)
 * 3. Hand with Coin / Sadaqah (Quests & Points)
 * 4. Crown (AI Coach & Spiritual Mastery)
 * 5. App Bag / Shield (App List)
 */
@Composable
fun PixelBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current

    val navBarBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF6F8FB)
    val navBorder = if (isDark) Color(0xFF2A3854) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Soft Neumorphic Navigation Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isDark) 8.dp else 6.dp,
                    shape = RoundedCornerShape(22.dp),
                    spotColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0xFF94A3B8).copy(alpha = 0.35f),
                    ambientColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color(0xFFCBD5E1).copy(alpha = 0.25f)
                )
                .clip(RoundedCornerShape(22.dp))
                .background(navBarBg)
                .border(1.2.dp, navBorder, RoundedCornerShape(22.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Mosque with Heart (Guided Path)
                val isTab1 = currentRoute == Screen.Dashboard.route
                NavIconItem(
                    isSelected = isTab1,
                    onClick = { onNavigate(Screen.Dashboard.route) }
                ) { tint ->
                    MosqueHeartTabIcon(modifier = Modifier.size(28.dp), tint = tint)
                }

                // Tab 2: Prayer Rug / Musalla (Focus / Salah)
                val isTab2 = currentRoute == Screen.Focus.route
                NavIconItem(
                    isSelected = isTab2,
                    onClick = { onNavigate(Screen.Focus.route) }
                ) { tint ->
                    PrayerRugTabIcon(modifier = Modifier.size(28.dp), tint = tint)
                }

                // Tab 3: Hand with Coin / Sadaqah (Quests & Zakat)
                val isTab3 = currentRoute == Screen.Quests.route
                NavIconItem(
                    isSelected = isTab3,
                    onClick = { onNavigate(Screen.Quests.route) }
                ) { tint ->
                    HandCoinTabIcon(modifier = Modifier.size(28.dp), tint = tint)
                }

                // Tab 4: Crown / Trophy (Leaderboard & Rankings)
                val isTab4 = currentRoute == Screen.Leaderboard.route
                NavIconItem(
                    isSelected = isTab4,
                    onClick = { onNavigate(Screen.Leaderboard.route) }
                ) { tint ->
                    CrownTabIcon(modifier = Modifier.size(28.dp), tint = tint)
                }

                // Tab 5: Bag / App Shield
                val isTab5 = currentRoute == Screen.Apps.route
                NavIconItem(
                    isSelected = isTab5,
                    onClick = { onNavigate(Screen.Apps.route) }
                ) { tint ->
                    BagTabIcon(modifier = Modifier.size(28.dp), tint = tint)
                }
            }
        }
    }
}

@Composable
private fun NavIconItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable (tint: Color) -> Unit
) {
    val isDark = LocalIsDarkTheme.current

    val activeTint = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B)
    val inactiveTint = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
    val tint by animateColorAsState(targetValue = if (isSelected) activeTint else inactiveTint, label = "TabTint")
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1.0f, animationSpec = spring(), label = "TabScale")

    val selectedBg = if (isDark) Color(0xFF1E2B45) else Color(0xFFFFFFFF)
    val selectedBorder = if (isDark) Color(0xFF334A70) else Color(0xFFE2E8F0)

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(46.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (isSelected) {
                    Modifier
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color(0xFF64748B).copy(alpha = 0.2f)
                        )
                        .background(selectedBg)
                        .border(1.2.dp, selectedBorder, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        icon(tint)
    }
}

/**
 * 1. Mosque with Heart Icon (matching Image 1)
 */
@Composable
fun MosqueHeartTabIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E293B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8f.dp.toPx()

        // Dome Center
        val domePath = Path().apply {
            moveTo(w * 0.30f, h * 0.65f)
            lineTo(w * 0.30f, h * 0.46f)
            cubicTo(w * 0.30f, h * 0.28f, w * 0.48f, h * 0.24f, w * 0.50f, h * 0.16f)
            cubicTo(w * 0.52f, h * 0.24f, w * 0.70f, h * 0.28f, w * 0.70f, h * 0.46f)
            lineTo(w * 0.70f, h * 0.65f)
        }
        drawPath(domePath, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Crescent on Dome Top
        val crescentPath = Path().apply {
            moveTo(w * 0.50f, h * 0.08f)
            cubicTo(w * 0.45f, h * 0.10f, w * 0.45f, h * 0.15f, w * 0.50f, h * 0.16f)
            cubicTo(w * 0.47f, h * 0.14f, w * 0.47f, h * 0.10f, w * 0.50f, h * 0.08f)
        }
        drawPath(crescentPath, color = tint, style = Stroke(width = stroke * 0.8f, cap = StrokeCap.Round))

        // Left Minaret
        drawLine(tint, Offset(w * 0.18f, h * 0.82f), Offset(w * 0.18f, h * 0.30f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.12f, h * 0.82f), Offset(w * 0.12f, h * 0.30f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.09f, h * 0.30f), Offset(w * 0.21f, h * 0.30f), strokeWidth = stroke, cap = StrokeCap.Round)
        // Left Minaret Top Cone
        val leftCone = Path().apply {
            moveTo(w * 0.12f, h * 0.30f)
            lineTo(w * 0.15f, h * 0.18f)
            lineTo(w * 0.18f, h * 0.30f)
        }
        drawPath(leftCone, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Right Minaret
        drawLine(tint, Offset(w * 0.82f, h * 0.82f), Offset(w * 0.82f, h * 0.30f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.88f, h * 0.82f), Offset(w * 0.88f, h * 0.30f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.79f, h * 0.30f), Offset(w * 0.91f, h * 0.30f), strokeWidth = stroke, cap = StrokeCap.Round)
        // Right Minaret Top Cone
        val rightCone = Path().apply {
            moveTo(w * 0.82f, h * 0.30f)
            lineTo(w * 0.85f, h * 0.18f)
            lineTo(w * 0.88f, h * 0.30f)
        }
        drawPath(rightCone, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Base Line
        drawLine(tint, Offset(w * 0.10f, h * 0.82f), Offset(w * 0.90f, h * 0.82f), strokeWidth = stroke, cap = StrokeCap.Round)

        // Heart at Bottom Center-Right (prominent in image 1)
        val heartPath = Path().apply {
            val hCx = w * 0.65f
            val hCy = h * 0.70f
            val hw = w * 0.28f
            val hh = h * 0.26f

            moveTo(hCx, hCy + hh * 0.5f)
            // Left lobe
            cubicTo(
                hCx - hw * 0.6f, hCy + hh * 0.15f,
                hCx - hw * 0.6f, hCy - hh * 0.45f,
                hCx, hCy - hh * 0.15f
            )
            // Right lobe
            cubicTo(
                hCx + hw * 0.6f, hCy - hh * 0.45f,
                hCx + hw * 0.6f, hCy + hh * 0.15f,
                hCx, hCy + hh * 0.5f
            )
            close()
        }
        // Fill behind heart to clear background lines
        drawPath(heartPath, color = Color.White)
        drawPath(heartPath, color = tint, style = Stroke(width = stroke * 1.1f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * 2. Prayer Rug / Sajjadah Icon (matching Image 1)
 */
@Composable
fun PrayerRugTabIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E293B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8f.dp.toPx()

        val rx = w * 0.26f
        val ry = h * 0.16f
        val rw = w * 0.48f
        val rh = h * 0.68f

        // Outer Rug Rectangle
        drawRoundRect(
            color = tint,
            topLeft = Offset(rx, ry),
            size = Size(rw, rh),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = stroke)
        )

        // Top Fringes (4 small vertical dashes)
        for (i in 0..4) {
            val fx = rx + rw * (i / 4f)
            drawLine(tint, Offset(fx, ry - h * 0.07f), Offset(fx, ry), strokeWidth = stroke * 0.9f, cap = StrokeCap.Round)
        }

        // Bottom Fringes
        for (i in 0..4) {
            val fx = rx + rw * (i / 4f)
            drawLine(tint, Offset(fx, ry + rh), Offset(fx, ry + rh + h * 0.07f), strokeWidth = stroke * 0.9f, cap = StrokeCap.Round)
        }

        // Inner Arch / Mihrab
        val archPath = Path().apply {
            val ix = rx + rw * 0.2f
            val iy = ry + rh * 0.85f
            val iw = rw * 0.6f
            val ih = rh * 0.65f

            moveTo(ix, iy)
            lineTo(ix, iy - ih * 0.5f)
            cubicTo(
                ix, iy - ih,
                ix + iw, iy - ih,
                ix + iw, iy - ih * 0.5f
            )
            lineTo(ix + iw, iy)
        }
        drawPath(archPath, color = tint, style = Stroke(width = stroke * 0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * 3. Hand with Coin / Sadaqah Icon (matching Image 1)
 */
@Composable
fun HandCoinTabIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E293B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8f.dp.toPx()

        // Floating Coin above hand
        val coinRadius = w * 0.16f
        val coinCenter = Offset(w * 0.54f, h * 0.28f)
        drawCircle(
            color = tint,
            radius = coinRadius,
            center = coinCenter,
            style = Stroke(width = stroke)
        )
        // Currency symbol '$' or star inside coin
        drawLine(tint, Offset(coinCenter.x, coinCenter.y - coinRadius * 0.55f), Offset(coinCenter.x, coinCenter.y + coinRadius * 0.55f), strokeWidth = stroke * 0.8f, cap = StrokeCap.Round)
        val sPath = Path().apply {
            moveTo(coinCenter.x + coinRadius * 0.3f, coinCenter.y - coinRadius * 0.25f)
            cubicTo(coinCenter.x, coinCenter.y - coinRadius * 0.45f, coinCenter.x - coinRadius * 0.3f, coinCenter.y - coinRadius * 0.1f, coinCenter.x, coinCenter.y)
            cubicTo(coinCenter.x + coinRadius * 0.3f, coinCenter.y + coinRadius * 0.1f, coinCenter.x, coinCenter.y + coinRadius * 0.45f, coinCenter.x - coinRadius * 0.3f, coinCenter.y + coinRadius * 0.25f)
        }
        drawPath(sPath, color = tint, style = Stroke(width = stroke * 0.8f, cap = StrokeCap.Round))

        // Hand holding coin
        val handPath = Path().apply {
            // Sleeve/wrist on left
            moveTo(w * 0.12f, h * 0.60f)
            lineTo(w * 0.28f, h * 0.60f)
            // Thumb curving up toward coin
            cubicTo(w * 0.34f, h * 0.56f, w * 0.38f, h * 0.46f, w * 0.42f, h * 0.48f)
            cubicTo(w * 0.44f, h * 0.50f, w * 0.40f, h * 0.58f, w * 0.46f, h * 0.60f)
            // Outstretched palm and fingers
            lineTo(w * 0.74f, h * 0.60f)
            cubicTo(w * 0.84f, h * 0.60f, w * 0.88f, h * 0.66f, w * 0.84f, h * 0.72f)
            cubicTo(w * 0.80f, h * 0.76f, w * 0.72f, h * 0.76f, w * 0.62f, h * 0.78f)
            lineTo(w * 0.28f, h * 0.78f)
            lineTo(w * 0.12f, h * 0.78f)
            close()
        }
        drawPath(handPath, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Sleeve cuff line
        drawLine(tint, Offset(w * 0.22f, h * 0.56f), Offset(w * 0.22f, h * 0.82f), strokeWidth = stroke * 0.9f, cap = StrokeCap.Round)
    }
}

/**
 * 4. Crown Icon (matching Image 1)
 */
@Composable
fun CrownTabIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E293B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8f.dp.toPx()

        val baseY = h * 0.74f
        val topY = h * 0.34f
        val midY = h * 0.46f

        // Base arched bar
        val basePath = Path().apply {
            moveTo(w * 0.18f, baseY)
            cubicTo(w * 0.4f, baseY + h * 0.04f, w * 0.6f, baseY + h * 0.04f, w * 0.82f, baseY)
            lineTo(w * 0.82f, baseY - h * 0.06f)
            cubicTo(w * 0.6f, baseY - h * 0.02f, w * 0.4f, baseY - h * 0.02f, w * 0.18f, baseY - h * 0.06f)
            close()
        }
        drawPath(basePath, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Crown peaks
        val crownPeaks = Path().apply {
            moveTo(w * 0.18f, baseY - h * 0.06f)
            // Left peak
            lineTo(w * 0.24f, topY + h * 0.04f)
            // Left valley
            lineTo(w * 0.38f, midY)
            // Center peak (highest)
            lineTo(w * 0.50f, topY)
            // Right valley
            lineTo(w * 0.62f, midY)
            // Right peak
            lineTo(w * 0.76f, topY + h * 0.04f)
            lineTo(w * 0.82f, baseY - h * 0.06f)
        }
        drawPath(crownPeaks, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Pearls on 3 Peaks
        val pearlR = w * 0.045f
        drawCircle(tint, pearlR, Offset(w * 0.24f, topY + h * 0.04f))
        drawCircle(tint, pearlR, Offset(w * 0.50f, topY))
        drawCircle(tint, pearlR, Offset(w * 0.76f, topY + h * 0.04f))
    }
}

/**
 * 5. Bag Icon (matching Image 1)
 */
@Composable
fun BagTabIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E293B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8f.dp.toPx()

        // Bag body
        val bagPath = Path().apply {
            moveTo(w * 0.26f, h * 0.38f)
            lineTo(w * 0.20f, h * 0.78f)
            cubicTo(w * 0.20f, h * 0.86f, w * 0.30f, h * 0.86f, w * 0.36f, h * 0.86f)
            lineTo(w * 0.64f, h * 0.86f)
            cubicTo(w * 0.70f, h * 0.86f, w * 0.80f, h * 0.86f, w * 0.80f, h * 0.78f)
            lineTo(w * 0.74f, h * 0.38f)
            close()
        }
        drawPath(bagPath, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Curved Loop Handle at top
        val handlePath = Path().apply {
            moveTo(w * 0.36f, h * 0.38f)
            cubicTo(w * 0.36f, h * 0.16f, w * 0.64f, h * 0.16f, w * 0.64f, h * 0.38f)
        }
        drawPath(handlePath, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round))

        // Decorative smile curve on front of bag
        val smilePath = Path().apply {
            moveTo(w * 0.40f, h * 0.52f)
            cubicTo(w * 0.44f, h * 0.64f, w * 0.56f, h * 0.64f, w * 0.60f, h * 0.52f)
        }
        drawPath(smilePath, color = tint, style = Stroke(width = stroke * 0.85f, cap = StrokeCap.Round))
    }
}
