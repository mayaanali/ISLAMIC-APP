package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlabasterSand
import com.example.ui.theme.DesertGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SlateBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Top Header matching User Reference Image 2:
 * - "Guided Path" + "DAILY DEEN JOURNEY | SPIRITUAL VITALITY"
 * - Neumorphic Coin Pill Badge ("🟡 1,240")
 * - Neumorphic Profile Circle ("JD")
 */
@Composable
fun GuidedPathHeader(
    coins: Int,
    initials: String = "JD",
    onCoinClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val titleColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF2C3E5A)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    val pillBg = if (isDark) Color(0xFF131B2E) else Color.White
    val pillBorder = if (isDark) Color(0xFF2A3854) else Color(0xFFE2E8F0)
    val avatarBg = if (isDark) Color(0xFF1E293B) else Color(0xFF1C2738)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Title & Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Guided Path",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = titleColor,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "DAILY DEEN JOURNEY | SPIRITUAL VITALITY",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = subtitleColor,
                letterSpacing = 0.8.sp
            )
        }

        // Right: Coins Pill + User Avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Coin Counter Pill with Soft Drop Shadow
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color(0xFF94A3B8).copy(alpha = 0.25f),
                        ambientColor = if (isDark) Color.Black.copy(alpha = 0.3f) else Color(0xFFCBD5E1).copy(alpha = 0.15f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(pillBg)
                    .border(1.2.dp, pillBorder, RoundedCornerShape(20.dp))
                    .clickable { onCoinClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PixelCoinIcon(modifier = Modifier.size(22.dp))
                    Text(
                        text = String.format("%,d", coins),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = titleColor
                    )
                }
            }

            // User Initials Avatar ("JD")
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        spotColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color(0xFF64748B).copy(alpha = 0.3f)
                    )
                    .clip(CircleShape)
                    .background(avatarBg)
                    .border(1.5.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0), CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Purity Index Section matching User Reference Image 2:
 * - Intricate Arabesque Floral Arch radiating in background
 * - Multi-layered Neumorphic Plate
 * - 3D Crimson Heart with Islamic Geometric Star Engraving & Red Noor Glow
 * - "92%" + "PURITY INDEX"
 */
@Composable
fun GuidedPathPurityDial(
    purityPercentage: Int,
    onTapHeart: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val infiniteTransition = rememberInfiniteTransition(label = "QalbPulse")

    // Rhythmic organic heartbeat scale
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartScale"
    )

    // Soft aura glow breathing
    val auraGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraGlow"
    )

    var tapRippleRadius by remember { mutableFloatStateOf(0f) }
    var tapRippleAlpha by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    val textColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    val watermarkColor = if (isDark) Color(0xFF1E2B45).copy(alpha = 0.45f) else Color(0xFFCBD5E1).copy(alpha = 0.55f)
    val discOuterBg = if (isDark) Color(0xFF101726) else Color(0xFFF8FAFC)
    val discInnerBg = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp),
        contentAlignment = Alignment.Center
    ) {
        // LAYER 1: Radiating Islamic Arabesque Watermark Arch
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cX = w / 2f
            val cY = h * 0.48f

            drawArabesqueWatermarkArch(
                centerX = cX,
                centerY = cY,
                radius = w * 0.44f,
                color = watermarkColor
            )
        }

        // LAYER 2: Multi-Layered Neumorphic Circular Dial Plate
        Box(
            modifier = Modifier
                .size(240.dp)
                .shadow(
                    elevation = if (isDark) 10.dp else 8.dp,
                    shape = CircleShape,
                    spotColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0xFF94A3B8).copy(alpha = 0.35f),
                    ambientColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color(0xFFCBD5E1).copy(alpha = 0.25f)
                )
                .clip(CircleShape)
                .background(discOuterBg)
                .border(1.5.dp, if (isDark) Color(0xFF2A3854) else Color(0xFFE2E8F0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Inner Inset Bevel Dish
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(discInnerBg)
                    .border(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9), CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            onTapHeart()
                            scope.launch {
                                tapRippleRadius = 0f
                                tapRippleAlpha = 0.8f
                                while (tapRippleRadius < 260f) {
                                    tapRippleRadius += 20f
                                    tapRippleAlpha = (1f - tapRippleRadius / 260f).coerceAtLeast(0f)
                                    delay(16)
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Heart & Noor Canvas
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val w = size.width
                    val h = size.height
                    val cX = w / 2f
                    val cY = h * 0.38f // Place heart in upper portion of inner dish

                    // Tap ripple wave
                    if (tapRippleAlpha > 0f) {
                        drawCircle(
                            color = Color(0xFFFF334B).copy(alpha = tapRippleAlpha),
                            radius = tapRippleRadius,
                            center = Offset(cX, cY),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }

                    // Soft ambient radiant red glow behind heart
                    val glowRadius = w * 0.32f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF2A4B).copy(alpha = 0.45f * auraGlow),
                                Color(0xFFFF5252).copy(alpha = 0.20f * auraGlow),
                                Color.Transparent
                            ),
                            center = Offset(cX, cY),
                            radius = glowRadius
                        ),
                        radius = glowRadius,
                        center = Offset(cX, cY)
                    )

                    // 3D Sculpted Heart with Islamic Geometric Star Engraving
                    scale(scale = heartScale, pivot = Offset(cX, cY)) {
                        drawEngraved3DHeart(
                            centerX = cX,
                            centerY = cY,
                            width = w * 0.34f,
                            height = w * 0.32f
                        )
                    }
                }

                // Typography underneath heart
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$purityPercentage%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "PURITY INDEX",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = subtitleColor,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }
    }
}

/**
 * Draws the ornate Islamic Arabesque watermark arch radiating behind the circular plate
 */
private fun DrawScope.drawArabesqueWatermarkArch(
    centerX: Float,
    centerY: Float,
    radius: Float,
    color: Color
) {
    val strokeWidth = 1.2f.dp.toPx()

    // Outer radiating concentric ornamental circles
    drawCircle(
        color = color.copy(alpha = color.alpha * 0.8f),
        radius = radius * 1.08f,
        center = Offset(centerX, centerY),
        style = Stroke(width = strokeWidth)
    )

    drawCircle(
        color = color.copy(alpha = color.alpha * 0.6f),
        radius = radius * 1.22f,
        center = Offset(centerX, centerY),
        style = Stroke(width = strokeWidth)
    )

    // Arabesque Arch Crest Petals (16 radiating Islamic petal arches)
    val numPetals = 16
    for (i in 0 until numPetals) {
        val angle = (i * (360f / numPetals) - 90f) * (PI / 180f)
        val rBase = radius * 1.08f
        val rTip = radius * 1.28f

        val p1x = centerX + rBase * cos(angle - 0.12).toFloat()
        val p1y = centerY + rBase * sin(angle - 0.12).toFloat()
        val p2x = centerX + rTip * cos(angle).toFloat()
        val p2y = centerY + rTip * sin(angle).toFloat()
        val p3x = centerX + rBase * cos(angle + 0.12).toFloat()
        val p3y = centerY + rBase * sin(angle + 0.12).toFloat()

        val petalPath = Path().apply {
            moveTo(p1x, p1y)
            cubicTo(
                p1x + (p2x - p1x) * 0.4f, p1y + (p2y - p1y) * 0.4f,
                p2x - 10f, p2y - 10f,
                p2x, p2y
            )
            cubicTo(
                p2x + 10f, p2y - 10f,
                p3x + (p2x - p3x) * 0.4f, p3y + (p2y - p3y) * 0.4f,
                p3x, p3y
            )
        }
        drawPath(petalPath, color = color, style = Stroke(width = strokeWidth))

        // Small decorative diamond / dot at the tip of each arch
        drawCircle(
            color = color,
            radius = 2.5f.dp.toPx(),
            center = Offset(p2x, p2y)
        )
    }

    // Secondary inner filigree lace ring
    for (i in 0 until numPetals * 2) {
        val angle = (i * (360f / (numPetals * 2))) * (PI / 180f)
        val innerR = radius * 1.15f
        val dotX = centerX + innerR * cos(angle).toFloat()
        val dotY = centerY + innerR * sin(angle).toFloat()
        drawCircle(color = color.copy(alpha = color.alpha * 0.7f), radius = 1.2f.dp.toPx(), center = Offset(dotX, dotY))
    }
}

/**
 * Draws the 3D Textured Crimson Heart with Islamic Geometric Star Engraving matching Image 2
 */
private fun DrawScope.drawEngraved3DHeart(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float
) {
    val heartPath = Path().apply {
        val topY = centerY - height * 0.48f
        val bottomY = centerY + height * 0.52f
        val leftX = centerX - width * 0.5f
        val rightX = centerX + width * 0.5f

        moveTo(centerX, topY + height * 0.28f)
        // Left lobe
        cubicTo(
            leftX, topY - height * 0.12f,
            leftX - width * 0.05f, topY + height * 0.52f,
            centerX, bottomY
        )
        // Right lobe
        cubicTo(
            rightX + width * 0.05f, topY + height * 0.52f,
            rightX, topY - height * 0.12f,
            centerX, topY + height * 0.28f
        )
        close()
    }

    // 1. Base Volumetric 3D Crimson Red Shading
    drawPath(
        path = heartPath,
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF5C75), // Bright highlight
                Color(0xFFDC2626), // Rich crimson core
                Color(0xFF991B1B), // Deep ruby shade
                Color(0xFF7F1D1D)  // Shadow contour
            ),
            center = Offset(centerX - width * 0.12f, centerY - height * 0.15f),
            radius = width * 0.65f
        )
    )

    // 2. Clip inside heart to draw the Islamic Geometric Star (Girih) Engraving
    clipPath(heartPath) {
        val starCenter = Offset(centerX, centerY - height * 0.02f)
        val starRadius = width * 0.35f
        val engravingColor = Color(0xFF6B0E0E).copy(alpha = 0.55f)
        val highlightColor = Color(0xFFFF8599).copy(alpha = 0.45f)
        val engraveStroke = 1.4f.dp.toPx()

        // 8-Pointed Star Lattice Engraving (Girih Mandala)
        val points = 8
        for (pass in 0..1) {
            val offsetAngle = if (pass == 0) 0f else (180f / points)
            val starPath = Path().apply {
                for (i in 0 until points) {
                    val outerAngle = (i * (360f / points) + offsetAngle) * (PI / 180f)
                    val innerAngle = ((i + 0.5f) * (360f / points) + offsetAngle) * (PI / 180f)

                    val ox = starCenter.x + (starRadius * cos(outerAngle)).toFloat()
                    val oy = starCenter.y + (starRadius * sin(outerAngle)).toFloat()
                    val ix = starCenter.x + ((starRadius * 0.45f) * cos(innerAngle)).toFloat()
                    val iy = starCenter.y + ((starRadius * 0.45f) * sin(innerAngle)).toFloat()

                    if (i == 0) moveTo(ox, oy) else lineTo(ox, oy)
                    lineTo(ix, iy)
                }
                close()
            }
            // Shadow line (carved into heart)
            drawPath(starPath, color = engravingColor, style = Stroke(width = engraveStroke))
            // Subtle highlight line offset
            drawPath(starPath, color = highlightColor, style = Stroke(width = engraveStroke * 0.5f))
        }

        // Concentric geometric tessellation lines radiating across the lobes
        for (r in listOf(0.2f, 0.55f, 0.85f)) {
            drawCircle(
                color = engravingColor,
                radius = starRadius * r,
                center = starCenter,
                style = Stroke(width = engraveStroke * 0.7f)
            )
        }
    }

    // 3. Specular Clay Glaze Rim Highlight
    drawPath(
        path = heartPath,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.45f),
                Color.Transparent,
                Color(0xFF450A0A).copy(alpha = 0.4f)
            ),
            start = Offset(centerX - width * 0.4f, centerY - height * 0.4f),
            end = Offset(centerX + width * 0.4f, centerY + height * 0.4f)
        ),
        style = Stroke(width = 2.dp.toPx())
    )
}
