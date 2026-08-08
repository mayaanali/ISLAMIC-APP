package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.DailyQuest
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Theme Colors for Guided Path Retro Aesthetic
val PixelBgCanvas = Color(0xFFF1F3F5)
val PixelCardBg = Color(0xFFFFFFFF)
val PixelDarkBorder = Color(0xFF2B2E3A)
val PixelGreenAccent = Color(0xFF2EC4B6)
val PixelGoldAccent = Color(0xFFFFB703)
val PixelRedHeart = Color(0xFFE63946)

/**
 * Pixelated Card Container with Islamic Architectural Notched Border & Corner Flourishes
 */
@Composable
fun PixelCardContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = PixelCardBg,
    borderColor: Color = PixelDarkBorder,
    borderWidth: Dp = 2.5.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .border(width = borderWidth, color = borderColor, shape = RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        // Subtle Islamic corner flourishes drawn on top-left and top-right inner corners
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val fSize = 16f

            // Top Left Corner Arabesque Notch
            val topLeftPath = Path().apply {
                moveTo(8f, 24f)
                lineTo(24f, 8f)
                lineTo(32f, 8f)
                lineTo(8f, 32f)
                close()
            }
            drawPath(topLeftPath, color = borderColor.copy(alpha = 0.25f))

            // Top Right Corner Arabesque Notch
            val topRightPath = Path().apply {
                moveTo(w - 8f, 24f)
                lineTo(w - 24f, 8f)
                lineTo(w - 32f, 8f)
                lineTo(w - 8f, 32f)
                close()
            }
            drawPath(topRightPath, color = borderColor.copy(alpha = 0.25f))

            // Bottom Left Corner
            val bottomLeftPath = Path().apply {
                moveTo(8f, h - 24f)
                lineTo(24f, h - 8f)
                lineTo(32f, h - 8f)
                lineTo(8f, h - 32f)
                close()
            }
            drawPath(bottomLeftPath, color = borderColor.copy(alpha = 0.25f))

            // Bottom Right Corner
            val bottomRightPath = Path().apply {
                moveTo(w - 8f, h - 24f)
                lineTo(w - 24f, h - 8f)
                lineTo(w - 32f, h - 8f)
                lineTo(w - 8f, h - 32f)
                close()
            }
            drawPath(bottomRightPath, color = borderColor.copy(alpha = 0.25f))
        }

        content()
    }
}

/**
 * Pixelated Heart Drawing (Solid 8-bit Red Heart with White Specular Highlight)
 */
@Composable
fun PixelHeartIcon(
    modifier: Modifier = Modifier.size(64.dp),
    scaleFactor: Float = 1f
) {
    Canvas(modifier = modifier) {
        scale(scaleFactor) {
            val pixelGrid = listOf(
                "0011100011100",
                "0111110111110",
                "1111111111111",
                "1111111111221", // '2' is white specular highlight
                "1111111111221",
                "1111111111111",
                "0111111111110",
                "0011111111100",
                "0001111111000",
                "0000111110000",
                "0000011100000",
                "0000001000000"
            )
            val rows = pixelGrid.size
            val cols = pixelGrid[0].length
            val pixelSize = size.width / cols

            val offsetX = (size.width - cols * pixelSize) / 2f
            val offsetY = (size.height - rows * pixelSize) / 2f

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val char = pixelGrid[r][c]
                    if (char != '0') {
                        val color = if (char == '2') Color.White else PixelRedHeart
                        // Fill pixel
                        drawRect(
                            color = color,
                            topLeft = Offset(offsetX + c * pixelSize, offsetY + r * pixelSize),
                            size = Size(pixelSize + 0.5f, pixelSize + 0.5f)
                        )
                        // Black border for edge pixels
                        val isEdge = r == 0 || r == rows - 1 || c == 0 || c == cols - 1 ||
                                (r > 0 && pixelGrid[r - 1][c] == '0') ||
                                (r < rows - 1 && pixelGrid[r + 1][c] == '0') ||
                                (c > 0 && pixelGrid[r][c - 1] == '0') ||
                                (c < cols - 1 && pixelGrid[r][c + 1] == '0')

                        if (isEdge) {
                            drawRect(
                                color = PixelDarkBorder,
                                topLeft = Offset(offsetX + c * pixelSize, offsetY + r * pixelSize),
                                size = Size(pixelSize, pixelSize),
                                style = Stroke(width = 1.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Pixel Gold Coin Icon
 */
@Composable
fun PixelCoinIcon(modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val cX = size.width / 2f
        val cY = size.height / 2f
        val r = size.width / 2.2f

        // Outer Dark Border
        drawCircle(color = PixelDarkBorder, radius = r, center = Offset(cX, cY))
        // Gold Fill
        drawCircle(color = PixelGoldAccent, radius = r - 2f, center = Offset(cX, cY))
        // Inner Highlight Ring
        drawCircle(color = Color(0xFFFFF3B0), radius = r * 0.65f, center = Offset(cX, cY))
        drawCircle(color = PixelGoldAccent, radius = r * 0.5f, center = Offset(cX, cY))
    }
}

/**
 * Pixelated Character Avatar Icon
 */
@Composable
fun PixelAvatarIcon(modifier: Modifier = Modifier.size(32.dp)) {
    Canvas(modifier = modifier) {
        val p = size.width / 10f

        // Background dark frame
        drawRoundRect(
            color = PixelDarkBorder,
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )

        // Face skin pixel block
        drawRect(
            color = Color(0xFFFFD1B3),
            topLeft = Offset(p * 2f, p * 3f),
            size = Size(p * 6f, p * 5f)
        )

        // White Taqiyah / Cap
        drawRect(
            color = Color.White,
            topLeft = Offset(p * 2f, p * 1.5f),
            size = Size(p * 6f, p * 2f)
        )

        // Dark beard
        drawRect(
            color = Color(0xFF212529),
            topLeft = Offset(p * 2f, p * 6f),
            size = Size(p * 6f, p * 2.5f)
        )

        // Eyes
        drawRect(
            color = Color.Black,
            topLeft = Offset(p * 3f, p * 4f),
            size = Size(p * 1f, p * 1f)
        )
        drawRect(
            color = Color.Black,
            topLeft = Offset(p * 6f, p * 4f),
            size = Size(p * 1f, p * 1f)
        )
    }
}

/**
 * Pixelated Crescent Moon & Star Icon
 */
@Composable
fun PixelCrescentStarIcon(
    modifier: Modifier = Modifier.size(24.dp),
    color: Color = Color(0xFF2EC4B6)
) {
    Canvas(modifier = modifier) {
        val cX = size.width / 2f
        val cY = size.height / 2f
        val r = size.width / 2.2f

        // Moon Outer Circle
        drawCircle(color = color, radius = r * 0.8f, center = Offset(cX - r * 0.1f, cY))
        // Moon Inner Cutout
        drawCircle(color = Color.White, radius = r * 0.65f, center = Offset(cX + r * 0.2f, cY - r * 0.1f))

        // Star
        drawCircle(color = color, radius = r * 0.25f, center = Offset(cX + r * 0.45f, cY - r * 0.35f))
    }
}

/**
 * Pixelated Circular Gauge (Purity Index Ring)
 */
@Composable
fun PixelPurityGauge(
    purityPercentage: Int,
    modifier: Modifier = Modifier.size(230.dp)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = purityPercentage / 100f,
        label = "PurityProgress"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val cX = width / 2f
            val cY = height / 2f
            val radius = width * 0.42f

            val totalSegments = 28
            val filledSegments = (animatedProgress * totalSegments).toInt()

            // Outer Pixel Border Ring
            drawCircle(
                color = PixelDarkBorder,
                radius = radius + 12f,
                center = Offset(cX, cY),
                style = Stroke(width = 4f)
            )

            // Inner Segment Ring
            for (i in 0 until totalSegments) {
                val angleDeg = (i * (360f / totalSegments)) - 90f
                val angleRad = angleDeg * (PI / 180f)

                val isFilled = i < filledSegments
                val segmentColor = if (isFilled) PixelDarkBorder else Color(0xFFD1D5DB)

                val segWidth = 14f
                val segHeight = 12f

                val x = cX + radius * cos(angleRad).toFloat()
                val y = cY + radius * sin(angleRad).toFloat()

                rotate(degrees = angleDeg + 90f, pivot = Offset(x, y)) {
                    drawRect(
                        color = segmentColor,
                        topLeft = Offset(x - segWidth / 2f, y - segHeight / 2f),
                        size = Size(segWidth, segHeight)
                    )
                    drawRect(
                        color = PixelDarkBorder,
                        topLeft = Offset(x - segWidth / 2f, y - segHeight / 2f),
                        size = Size(segWidth, segHeight),
                        style = Stroke(width = 1f)
                    )
                }
            }

            // Inner Boundary Circle Background Fill with Islamic Mosaic Pattern
            val innerRadius = radius - 16f
            drawCircle(
                color = Color(0xFFF8FAFC),
                radius = innerRadius,
                center = Offset(cX, cY)
            )

            // Draw Islamic Geometric Tile Lattice inside Gauge
            for (angle in 0 until 360 step 45) {
                val rad = angle * (PI / 180f)
                val x1 = cX + innerRadius * cos(rad).toFloat()
                val y1 = cY + innerRadius * sin(rad).toFloat()
                drawLine(
                    color = Color(0xFFCBD5E1),
                    start = Offset(cX, cY),
                    end = Offset(x1, y1),
                    strokeWidth = 1.2f
                )

                // Concentric inner geometric rings
                drawCircle(
                    color = Color(0xFFCBD5E1),
                    radius = innerRadius * 0.5f,
                    center = Offset(cX, cY),
                    style = Stroke(width = 1f)
                )
                drawCircle(
                    color = Color(0xFFCBD5E1),
                    radius = innerRadius * 0.8f,
                    center = Offset(cX, cY),
                    style = Stroke(width = 1f)
                )
            }

            // Inner Boundary Circle Border
            drawCircle(
                color = PixelDarkBorder,
                radius = innerRadius,
                center = Offset(cX, cY),
                style = Stroke(width = 3f)
            )
        }

        // Center Content inside Gauge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Crescent Moon Icon at top of inner circle
            PixelCrescentStarIcon(
                modifier = Modifier.size(22.dp),
                color = PixelGoldAccent
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Large 8-Bit Pixel Heart
            PixelHeartIcon(modifier = Modifier.size(56.dp))

            Spacer(modifier = Modifier.height(2.dp))

            // Percentage Text
            Text(
                text = "$purityPercentage%",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PixelDarkBorder,
                letterSpacing = (-0.5).sp
            )

            // Subtitle
            Text(
                text = "PURITY INDEX",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PixelDarkBorder.copy(alpha = 0.8f),
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * Pixelated Mosque / Minaret Icon for Bottom Navigation
 */
@Composable
fun PixelMosqueIcon(modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val cX = size.width / 2f
        val cY = size.height / 2f
        val w = size.width

        // Main Dome Path
        val path = Path().apply {
            moveTo(cX, cY - w * 0.4f)
            cubicTo(cX - w * 0.35f, cY - w * 0.2f, cX - w * 0.35f, cY + w * 0.2f, cX - w * 0.35f, cY + w * 0.35f)
            lineTo(cX + w * 0.35f, cY + w * 0.35f)
            cubicTo(cX + w * 0.35f, cY + w * 0.2f, cX + w * 0.35f, cY - w * 0.2f, cX, cY - w * 0.4f)
            close()
        }
        drawPath(path, color = PixelDarkBorder)

        // Minaret Pillars
        drawRect(PixelDarkBorder, Offset(cX - w * 0.45f, cY - w * 0.1f), Size(w * 0.1f, w * 0.45f))
        drawRect(PixelDarkBorder, Offset(cX + w * 0.35f, cY - w * 0.1f), Size(w * 0.1f, w * 0.45f))
        // Doorway
        drawRect(Color.White, Offset(cX - w * 0.12f, cY + w * 0.05f), Size(w * 0.24f, w * 0.3f))
    }
}

/**
 * Pixelated Kaaba / Box Icon for Bottom Navigation
 */
@Composable
fun PixelKaabaIcon(modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Dark cube
        drawRect(color = PixelDarkBorder, topLeft = Offset(w * 0.15f, h * 0.2f), size = Size(w * 0.7f, h * 0.65f))
        // Gold Kiswah Band
        drawRect(color = PixelGoldAccent, topLeft = Offset(w * 0.15f, h * 0.35f), size = Size(w * 0.7f, h * 0.12f))
        // Gold Door
        drawRect(color = PixelGoldAccent, topLeft = Offset(w * 0.55f, h * 0.55f), size = Size(w * 0.2f, h * 0.3f))
    }
}

/**
 * Pixelated Gear Icon for Bottom Navigation
 */
@Composable
fun PixelGearIcon(modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val cX = size.width / 2f
        val cY = size.height / 2f
        val r = size.width / 2.5f

        drawCircle(color = PixelDarkBorder, radius = r, center = Offset(cX, cY))
        drawCircle(color = Color.White, radius = r * 0.45f, center = Offset(cX, cY))

        // Teeth
        for (i in 0 until 6) {
            val angle = i * 60f * (PI / 180f)
            val tx = cX + (r * 1.1f) * cos(angle).toFloat()
            val ty = cY + (r * 1.1f) * sin(angle).toFloat()
            drawCircle(color = PixelDarkBorder, radius = r * 0.25f, center = Offset(tx, ty))
        }
    }
}

/**
 * Pixelated Stopwatch Icon (8-bit Timer Icon with Red Wedge)
 */
@Composable
fun PixelStopwatchIcon(modifier: Modifier = Modifier.size(36.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cX = w / 2f
        val cY = h / 2f + h * 0.05f
        val r = w * 0.38f

        // Top button & ring
        drawRect(color = PixelDarkBorder, topLeft = Offset(cX - w * 0.08f, 0f), size = Size(w * 0.16f, h * 0.12f))
        drawRect(color = Color(0xFFCBD5E1), topLeft = Offset(cX - w * 0.05f, 2f), size = Size(w * 0.1f, h * 0.08f))

        // Outer Casing Circle
        drawCircle(color = PixelDarkBorder, radius = r, center = Offset(cX, cY))
        drawCircle(color = Color.White, radius = r * 0.78f, center = Offset(cX, cY))

        // Red timer sector/slice (representing focused time segment)
        drawArc(
            color = Color(0xFFBA2D0B),
            startAngle = 270f,
            sweepAngle = 100f,
            useCenter = true,
            topLeft = Offset(cX - r * 0.78f, cY - r * 0.78f),
            size = Size(r * 1.56f, r * 1.56f)
        )

        // Center needle & pivot
        drawLine(
            color = PixelDarkBorder,
            start = Offset(cX, cY),
            end = Offset(cX + r * 0.5f, cY - r * 0.3f),
            strokeWidth = 3f
        )
        drawCircle(color = PixelDarkBorder, radius = 3f, center = Offset(cX, cY))

        // Casing border
        drawCircle(color = PixelDarkBorder, radius = r, center = Offset(cX, cY), style = Stroke(width = 2.5f))
    }
}

/**
 * Pixelated Timer Circular Gauge Container with Mosaic Outer Ring
 */
@Composable
fun PixelTimerGauge(
    progress: Float,
    formattedTime: String,
    statusText: String,
    modifier: Modifier = Modifier.size(240.dp)
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cX = w / 2f
            val cY = h / 2f
            val radius = w / 2f - 8f

            // Outer Mosaic Ring (Black / Dark Pixel Outer Border)
            drawCircle(
                color = PixelDarkBorder,
                radius = radius,
                center = Offset(cX, cY),
                style = Stroke(width = 16f)
            )

            // Inner Mosaic Tile Blocks (Islamic Pixel Gauge Ring)
            val totalBlocks = 24
            val filledBlocks = (progress * totalBlocks).toInt()

            for (i in 0 until totalBlocks) {
                val startAngle = i * (360f / totalBlocks) - 90f
                val color = if (i < filledBlocks) Color(0xFF64748B) else Color(0xFFE2E8F0)

                drawArc(
                    color = color,
                    startAngle = startAngle + 2f,
                    sweepAngle = (360f / totalBlocks) - 4f,
                    useCenter = false,
                    topLeft = Offset(cX - radius + 3f, cY - radius + 3f),
                    size = Size((radius - 3f) * 2f, (radius - 3f) * 2f),
                    style = Stroke(width = 10f)
                )
            }

            // Inner Circle Fill
            val innerRadius = radius - 16f
            drawCircle(
                color = Color(0xFFF8FAFC),
                radius = innerRadius,
                center = Offset(cX, cY)
            )

            // Inner Boundary Circle Border
            drawCircle(
                color = PixelDarkBorder,
                radius = innerRadius,
                center = Offset(cX, cY),
                style = Stroke(width = 3f)
            )

            // Islamic Geometric Tile Lattice inside Gauge Background
            for (angle in 0 until 360 step 45) {
                val rad = angle * (PI / 180f)
                val x1 = cX + innerRadius * cos(rad).toFloat()
                val y1 = cY + innerRadius * sin(rad).toFloat()
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(cX, cY),
                    end = Offset(x1, y1),
                    strokeWidth = 1f
                )
            }
        }

        // Center Content inside Gauge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PixelStopwatchIcon(modifier = Modifier.size(36.dp))

            Spacer(modifier = Modifier.height(6.dp))

            // Timer Text
            Text(
                text = formattedTime,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PixelDarkBorder,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Status Subtitle
            Text(
                text = statusText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PixelDarkBorder.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
fun PixelStarBadge(
    text: String,
    modifier: Modifier = Modifier.size(44.dp),
    badgeColor: Color = Color(0xFFBA2D0B)
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(w * 0.25f, 0f)
                lineTo(w * 0.75f, 0f)
                lineTo(w, h * 0.25f)
                lineTo(w, h * 0.75f)
                lineTo(w * 0.75f, h)
                lineTo(w * 0.25f, h)
                lineTo(0f, h * 0.75f)
                lineTo(0f, h * 0.25f)
                close()
            }

            drawPath(path = path, color = badgeColor)
            drawPath(path = path, color = PixelDarkBorder, style = Stroke(width = 4f))
        }

        Text(
            text = text.take(1).uppercase(),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}

/**
 * 8-Bit Retro Pixel Toggle Switch (Matches screenshot)
 */
@Composable
fun PixelSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = PixelGreenAccent
) {
    val trackBgColor = if (checked) activeColor else Color(0xFFCBD5E1)

    Box(
        modifier = modifier
            .width(52.dp)
            .height(28.dp)
            .background(trackBgColor, shape = RoundedCornerShape(14.dp))
            .border(width = 2.5.dp, color = PixelDarkBorder, shape = RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 3.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        // Thumb (White Circle with Pixel Dark Border)
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color.White, shape = CircleShape)
                .border(width = 2.dp, color = PixelDarkBorder, shape = CircleShape)
        )
    }
}

/**
 * Green Segmented Gauge Ring Container for Permission Icons
 */
@Composable
fun PixelGreenRingGaugeIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier.size(64.dp),
    ringColor: Color = PixelGreenAccent,
    isGranted: Boolean = false
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cX = w / 2f
            val cY = h / 2f
            val r = w / 2f - 4f

            // Outer dark pixel border
            drawCircle(color = PixelDarkBorder, radius = r, center = Offset(cX, cY), style = Stroke(width = 4f))

            // Segmented ring segments
            val segments = 12
            val activeColor = if (isGranted) ringColor else ringColor
            for (i in 0 until segments) {
                val startAngle = i * (360f / segments) - 90f
                drawArc(
                    color = activeColor,
                    startAngle = startAngle + 2f,
                    sweepAngle = (360f / segments) - 4f,
                    useCenter = false,
                    topLeft = Offset(cX - r + 4f, cY - r + 4f),
                    size = Size((r - 4f) * 2f, (r - 4f) * 2f),
                    style = Stroke(width = 8f)
                )
            }

            // Inner circle fill
            drawCircle(color = Color.White, radius = r - 10f, center = Offset(cX, cY))
            drawCircle(color = PixelDarkBorder, radius = r - 10f, center = Offset(cX, cY), style = Stroke(width = 2.5f))
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isGranted) PixelGreenAccent else Color(0xFFBA2D0B),
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Pixelated Permission Card matching the design screenshot
 */
@Composable
fun PixelIslamicPermissionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isGranted: Boolean,
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE2E8F0))
            .border(2.dp, PixelDarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Gauge Icon
            PixelGreenRingGaugeIcon(
                icon = icon,
                modifier = Modifier.size(64.dp),
                isGranted = isGranted
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Right content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Missing / Granted badge
                    Box(
                        modifier = Modifier
                            .background(
                                if (isGranted) PixelGreenAccent else Color(0xFFBA2D0B),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, PixelDarkBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isGranted) "✓ Granted" else "🚨 Missing",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 10.5.sp,
                    color = PixelDarkBorder.copy(alpha = 0.8f),
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Grant Permission Banner Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isGranted) PixelGreenAccent.copy(alpha = 0.2f) else PixelGreenAccent
                        )
                        .border(1.5.dp, PixelDarkBorder, RoundedCornerShape(8.dp))
                        .clickable { onGrantClick() }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PixelCrescentStarIcon(
                            modifier = Modifier.size(12.dp),
                            color = if (isGranted) PixelGreenAccent else PixelGoldAccent
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isGranted) "✓ Granted (Tap to Manage/Revoke)" else "Grant Permission",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isGranted) PixelDarkBorder else Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        PixelCrescentStarIcon(
                            modifier = Modifier.size(12.dp),
                            color = if (isGranted) PixelGreenAccent else PixelGoldAccent
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2D Gamified XP & Level Progress Bar
 */
@Composable
fun PixelXpLevelBar(
    coins: Int,
    modifier: Modifier = Modifier
) {
    val level = (coins / 100) + 1
    val xpInLevel = coins % 100
    val xpRequired = 100
    val progress = xpInLevel.toFloat() / xpRequired.toFloat()

    val rankTitle = when (level) {
        1 -> "Novice Seeker"
        2 -> "Focused Apprentice"
        3 -> "Qalb Guardian"
        4 -> "Deen Knight"
        else -> "Master Focus Warrior"
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "XpProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(2.dp, PixelGoldAccent, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Level Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PixelGoldAccent)
                    .border(1.5.dp, Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "L$level",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PixelDarkBorder
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rankTitle.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelGoldAccent,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "$xpInLevel / $xpRequired XP",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // XP Meter Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFF334155))
                        .border(1.dp, PixelDarkBorder, RoundedCornerShape(5.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(10.dp)
                            .background(PixelGreenAccent)
                    )
                }
            }
        }
    }
}

/**
 * 2D Pixelated Animated Loading Screen / Overlay
 */
@Composable
fun PixelAnimatedLoadingScreen(
    statusText: String = "SYNCING DEEN SHIELD ENGINE...",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LoadingAnimation")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "MoonRotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PixelBgCanvas)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Rotating & Bouncing 2D Crescent Hourglass Ring
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A))
                    .border(3.dp, PixelGoldAccent, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    rotate(rotationAngle) {
                        scale(pulseScale) {
                            val w = size.width
                            val h = size.height

                            drawCircle(
                                color = PixelGreenAccent,
                                radius = w * 0.4f,
                                style = Stroke(width = 6f)
                            )
                            drawCircle(
                                color = PixelGoldAccent,
                                radius = w * 0.2f,
                                center = Offset(w / 2f + 10f, h / 2f - 10f)
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.HourglassEmpty,
                    contentDescription = "Loading",
                    tint = PixelGoldAccent,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = statusText,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PixelDarkBorder,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 8-bit Dot indicators
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { index ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "DotAlpha$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(PixelGreenAccent.copy(alpha = dotAlpha), shape = RoundedCornerShape(2.dp))
                            .border(1.dp, PixelDarkBorder, shape = RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

/**
 * 2D Pixelated Animated Error Screen / Overlay
 */
@Composable
fun PixelAnimatedErrorScreen(
    errorMessage: String = "Deen Shield Telemetry Interrupted: Real-time sensor sync failed.",
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PixelBgCanvas)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFFF1F2))
                .border(3.dp, Color(0xFFBA2D0B), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Glitching Red Broken Shield
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFBA2D0B))
                        .border(2.5.dp, PixelDarkBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🚨 ENGINE SHIELD ERROR",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFBA2D0B),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = errorMessage,
                    fontSize = 11.5.sp,
                    color = PixelDarkBorder,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFBA2D0B),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .border(2.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RETRY ENGINE SHIELD",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2D Pixelated Real-Time IRL Screen Time Tracker Card Component
 */
@Composable
fun PixelLiveIrlTrackerCard(
    liveTotalSeconds: Long,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "IrlDotPulse")

    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LiveDotAlpha"
    )

    val hours = liveTotalSeconds / 3600
    val minutes = (liveTotalSeconds % 3600) / 60
    val seconds = liveTotalSeconds % 60

    val formattedLiveTime = String.format("%02dh %02dm %02ds", hours, minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A))
            .border(2.dp, PixelGoldAccent, RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Flashing Red Live LED
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                Color(0xFFEF4444).copy(alpha = dotAlpha),
                                shape = CircleShape
                            )
                            .border(1.dp, Color.White, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE IRL SCREEN TIME",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFEF4444),
                        letterSpacing = 0.8.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PixelGreenAccent.copy(alpha = 0.2f))
                        .border(1.dp, PixelGreenAccent, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (onClick != null) "TAP FOR TIPS 💡" else "REAL-TIME ACTIVE",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = PixelGreenAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = formattedLiveTime,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Ticking live from Android UsageStatsManager • Tap for breakdown",
                        fontSize = 9.5.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = PixelGoldAccent,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * 2D Pixel Modal Dialog for Setting Screen Time Limits
 */
@Composable
fun PixelSetLimitDialog(
    initialSocialMediaMinutes: Int,
    initialGeneralAppsMinutes: Int,
    onDismiss: () -> Unit,
    onSaveLimits: (socialMediaMins: Int, generalMins: Int) -> Unit
) {
    var socialMins by remember { mutableIntStateOf(initialSocialMediaMinutes) }
    var generalMins by remember { mutableIntStateOf(initialGeneralAppsMinutes) }

    val presetDurations = listOf(15, 30, 45, 60, 90, 120)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(3.dp, PixelGoldAccent, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PixelCrescentStarIcon(modifier = Modifier.size(20.dp), color = PixelGoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SET SCREEN TIME LIMITS",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelGoldAccent
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Configure maximum daily screen time allowances for social media & entertainment apps.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Social Media Limit Picker
                Text(
                    text = "📱 Social Media Daily Limit: $socialMins min",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presetDurations.take(4).forEach { min ->
                        val selected = socialMins == min
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) PixelGreenAccent else Color(0xFF334155))
                                .border(1.5.dp, PixelDarkBorder, RoundedCornerShape(8.dp))
                                .clickable { socialMins = min }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${min}m",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) PixelDarkBorder else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // General Apps Limit Picker
                Text(
                    text = "🎮 Other Apps & Gaming Limit: $generalMins min",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presetDurations.takeLast(4).forEach { min ->
                        val selected = generalMins == min
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) PixelGreenAccent else Color(0xFF334155))
                                .border(1.5.dp, PixelDarkBorder, RoundedCornerShape(8.dp))
                                .clickable { generalMins = min }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${min}m",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) PixelDarkBorder else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onSaveLimits(socialMins, generalMins)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PixelGoldAccent,
                        contentColor = PixelDarkBorder
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .border(2.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = "SAVE & APPLY DAILY LIMITS",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

/**
 * 2D Pixel Modal Dialog showing Real-time IRL Screen Time Tips & Breakdown
 */
@Composable
fun PixelLiveIrlTipsModal(
    liveTotalSeconds: Long,
    onDismiss: () -> Unit
) {
    val totalMinutes = liveTotalSeconds / 60

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(3.dp, PixelGreenAccent, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = PixelGoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "IRL SCREEN TIME TIPS",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelGoldAccent
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.5.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Today's Total Screen Time",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${totalMinutes / 60}h ${totalMinutes % 60}m",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PixelGreenAccent
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = PixelGreenAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actionable Tips List
                Text(
                    text = "💡 DIGITAL WELLNESS RECOMMENDATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TipItem(
                        icon = "📱",
                        title = "Reduce Short Video & Social Feeds",
                        description = "Limit TikTok, Instagram Reels, and YouTube Shorts to max 20 mins/day to reclaim cognitive focus."
                    )
                    TipItem(
                        icon = "🌙",
                        title = "Set Night Screen Curfew",
                        description = "Stop screen usage 30 minutes before Isha or sleep to protect your natural melatonin cycle."
                    )
                    TipItem(
                        icon = "📖",
                        title = "Replace Doomscrolling with Dhikr",
                        description = "Replace idle phone pickups with 5 minutes of Morning/Evening Adhkar or Surah Al-Mulk."
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PixelGreenAccent,
                        contentColor = PixelDarkBorder
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .border(2.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = "GOT IT, I'LL STAY FOCUSED!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun TipItem(
    icon: String,
    title: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF334155))
            .border(1.dp, PixelDarkBorder, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PixelGoldAccent
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

/**
 * 2D Pixel Modal Dialog for Quest Action (Accept, Exchange, or Live Photo Proof Capture)
 */
@Composable
fun PixelQuestActionDialog(
    quest: DailyQuest,
    onDismiss: () -> Unit,
    onAcceptQuest: (String) -> Unit,
    onExchangeQuest: (String) -> Unit,
    onVerifyAndClaimPoints: (String) -> Unit
) {
    var isAccepted by remember { mutableStateOf(false) }
    var hasTakenLivePhoto by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(3.dp, PixelGoldAccent, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = PixelGoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "QUEST MANAGEMENT",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelGoldAccent
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quest Title Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.5.dp, PixelDarkBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "[${quest.category.label}]",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PixelGreenAccent
                            )
                            Text(
                                text = "+${quest.points} PTS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PixelGoldAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = quest.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = quest.description,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isAccepted) {
                    // Option 1 & 2: Accept or Exchange
                    Text(
                        text = "Accept this quest to start or exchange it for a new daily task.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                onExchangeQuest(quest.id)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF334155),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .border(1.5.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "EXCHANGE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                isAccepted = true
                                onAcceptQuest(quest.id)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PixelGreenAccent,
                                contentColor = PixelDarkBorder
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .border(2.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ACCEPT QUEST",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                } else {
                    // Accepted Mode: Must Take Live Camera Photo Proof
                    Text(
                        text = "📷 LIVE PHOTO VERIFICATION REQUIRED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelGoldAccent
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Old gallery uploads are restricted. You must snap a live photo of your quest accomplishment.",
                        fontSize = 10.5.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Simulated Live Viewfinder Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (hasTakenLivePhoto) Color(0xFF065F46) else Color(0xFF020617))
                            .border(2.dp, if (hasTakenLivePhoto) PixelGreenAccent else Color(0xFF475569), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasTakenLivePhoto) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PixelGreenAccent,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "✓ Live Proof Captured & Timestamped!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = PixelGoldAccent,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ready to Snap Live Proof",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isVerifying) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                color = PixelGreenAccent,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Reviewing Live Photo...",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = PixelGreenAccent
                            )
                        }
                    } else if (!hasTakenLivePhoto) {
                        Button(
                            onClick = { hasTakenLivePhoto = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PixelGoldAccent,
                                contentColor = PixelDarkBorder
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .border(2.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SNAP LIVE PHOTO NOW",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                isVerifying = true
                                onVerifyAndClaimPoints(quest.id)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PixelGreenAccent,
                                contentColor = PixelDarkBorder
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .border(2.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                        ) {
                            Text(
                                text = "SUBMIT & CLAIM +${quest.points} POINTS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2D Pixel Modal Dialog for Zakat & Sadaqah Donation Wizard with Credit Card Payment
 */
@Composable
fun PixelZakatDonationWizard(
    onDismiss: () -> Unit,
    onConfirmDonation: (amount: Double) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // Step 1: NGO, Step 2: Amount, Step 3: Card Details, Step 4: Success
    var selectedNgo by remember { mutableStateOf("Local Mosque & Islamic Center") }
    var customAmountText by remember { mutableStateOf("10") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvc by remember { mutableStateOf("") }

    val ngos = listOf(
        "Local Mosque & Islamic Center",
        "Islamic Relief Worldwide",
        "Pure Water Well Foundation",
        "Orphan Care & Quran Education Fund"
    )

    val donationAmount = customAmountText.toDoubleOrNull() ?: 10.0
    val pointsAwarded = donationAmount.toInt().coerceIn(1, 100)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(3.dp, PixelGoldAccent, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = PixelGoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ZAKAT & SADAQAH LOG",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelGoldAccent
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Step Bar
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (step > index) PixelGreenAccent else Color(0xFF334155))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (step) {
                    1 -> {
                        // Step 1: Select NGO or Cause
                        Text(
                            text = "STEP 1: SELECT CHARITY / NGO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelGreenAccent,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ngos.forEach { ngo ->
                                val isSelected = selectedNgo == ngo
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A))
                                        .border(
                                            width = 1.5.dp,
                                            color = if (isSelected) PixelGoldAccent else Color(0xFF334155),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedNgo = ngo }
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalance,
                                            contentDescription = null,
                                            tint = if (isSelected) PixelGoldAccent else Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = ngo,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color.LightGray
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { step = 2 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PixelGoldAccent,
                                contentColor = PixelDarkBorder
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .border(2.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                        ) {
                            Text(
                                text = "NEXT: ENTER DONATION AMOUNT →",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    2 -> {
                        // Step 2: Amount Input
                        Text(
                            text = "STEP 2: ENTER DONATION AMOUNT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelGreenAccent,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = customAmountText,
                            onValueChange = { customAmountText = it },
                            label = { Text("Donation Amount ($)", color = Color.White) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PixelGoldAccent,
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Presets
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("10", "25", "50", "100").forEach { amt ->
                                val selected = customAmountText == amt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) PixelGreenAccent else Color(0xFF334155))
                                        .clickable { customAmountText = amt }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$$amt",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) PixelDarkBorder else Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Points Calculation Note
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "⭐ Points Earned: +$pointsAwarded PTS ($1 donated = 1 pt, maximum cap is 100 points).",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = PixelGoldAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { step = 3 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PixelGoldAccent,
                                contentColor = PixelDarkBorder
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .border(2.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                        ) {
                            Text(
                                text = "NEXT: PAYMENT DETAILS →",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    3 -> {
                        // Step 3: Card Details
                        Text(
                            text = "STEP 3: DEBIT / CREDIT CARD DETAILS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelGreenAccent,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = { Text("Card Number", color = Color.White) },
                            placeholder = { Text("4000 1234 5678 9010", color = Color.Gray) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PixelGoldAccent,
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cardExpiry,
                                onValueChange = { cardExpiry = it },
                                label = { Text("MM/YY", color = Color.White) },
                                placeholder = { Text("12/28", color = Color.Gray) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = PixelGoldAccent,
                                    unfocusedBorderColor = Color(0xFF475569)
                                )
                            )

                            OutlinedTextField(
                                value = cardCvc,
                                onValueChange = { cardCvc = it },
                                label = { Text("CVC", color = Color.White) },
                                placeholder = { Text("123", color = Color.Gray) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = PixelGoldAccent,
                                    unfocusedBorderColor = Color(0xFF475569)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onConfirmDonation(donationAmount)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PixelGreenAccent,
                                contentColor = PixelDarkBorder
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .border(2.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CONFIRM DONATION OF $${String.format("%.2f", donationAmount)}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2D Pixel Account Profile & Rank Dialog
 */
@Composable
fun PixelAccountProfileDialog(
    totalCoins: Int,
    streakDays: Int,
    purityIndex: Int,
    zakatDonated: Double,
    resistedCount: Int,
    onDismiss: () -> Unit
) {
    // Rank System Logic
    val rankTitle = when {
        totalCoins >= 5000 -> "Master Mujahid"
        totalCoins >= 2000 -> "Diamond Paladin"
        totalCoins >= 1000 -> "Platinum Legend"
        totalCoins >= 500 -> "Gold Champion"
        totalCoins >= 200 -> "Silver Sentinel"
        else -> "Bronze Guardian"
    }

    val rankIcon = when {
        totalCoins >= 5000 -> "👑"
        totalCoins >= 2000 -> "💠"
        totalCoins >= 1000 -> "💎"
        totalCoins >= 500 -> "🥇"
        totalCoins >= 200 -> "🥈"
        else -> "🥉"
    }

    val rankColor = when {
        totalCoins >= 5000 -> Color(0xFFEF4444)
        totalCoins >= 2000 -> Color(0xFF38BDF8)
        totalCoins >= 1000 -> Color(0xFFE2E8F0)
        totalCoins >= 500 -> PixelGoldAccent
        totalCoins >= 200 -> Color(0xFF94A3B8)
        else -> Color(0xFFD97706)
    }

    val nextThreshold = when {
        totalCoins >= 5000 -> 10000
        totalCoins >= 2000 -> 5000
        totalCoins >= 1000 -> 2000
        totalCoins >= 500 -> 1000
        totalCoins >= 200 -> 500
        else -> 200
    }

    val progress = (totalCoins.toFloat() / nextThreshold.toFloat()).coerceIn(0f, 1f)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(3.dp, PixelGoldAccent, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACCOUNT & RANK PROFILE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelGoldAccent
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Avatar Box
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .border(3.dp, rankColor, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    PixelAvatarIcon(modifier = Modifier.size(54.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rank Badge & Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = rankIcon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = rankTitle.uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = rankColor
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // XP Bar to Next Rank
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "XP Progress to Next Rank",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "$totalCoins / $nextThreshold PTS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PixelGoldAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = rankColor,
                        trackColor = Color(0xFF334155)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Grid
                Text(
                    text = "📊 SPIRITUAL & HABIT STATS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatTile(
                            title = "Streak Days",
                            value = "$streakDays 🔥",
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "Purity Index",
                            value = "$purityIndex%",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatTile(
                            title = "Distractions Resisted",
                            value = "$resistedCount 🛡️",
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "Total Zakat Donated",
                            value = "$${String.format("%.2f", zakatDonated)}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PixelGoldAccent,
                        contentColor = PixelDarkBorder
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .border(2.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = "CLOSE PROFILE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, PixelDarkBorder, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 9.5.sp,
                color = Color.White.copy(alpha = 0.65f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PixelGreenAccent
            )
        }
    }
}



