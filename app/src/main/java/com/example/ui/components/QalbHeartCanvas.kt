package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

data class LightParticle(
    var x: Float,
    var y: Float,
    var alpha: Float,
    val speed: Float,
    val size: Float
)

@Composable
fun QalbHeartCanvas(
    streakDays: Int,
    onTapHeart: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Infinite Animation Transitions for Rhythmic Heartbeat & Aura Pulse
    val infiniteTransition = rememberInfiniteTransition(label = "QalbHeart")

    val beatScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartBeat"
    )

    val auraGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraGlow"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatRotation"
    )

    var tapRippleRadius by remember { mutableFloatStateOf(0f) }
    var tapRippleAlpha by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    // Light Particles drifting up
    val particles = remember {
        mutableStateListOf<LightParticle>().apply {
            repeat(14) {
                add(
                    LightParticle(
                        x = (0.2f + Math.random().toFloat() * 0.6f),
                        y = (0.3f + Math.random().toFloat() * 0.5f),
                        alpha = Math.random().toFloat(),
                        speed = 0.002f + Math.random().toFloat() * 0.003f,
                        size = 4f + Math.random().toFloat() * 8f
                    )
                )
            }
        }
    }

    // Dynamic Noor Aura Colors based on streak progress
    val auraColorCore = when {
        streakDays >= 30 -> Color(0xFF00CEC9) // High Focus Emerald Cyan
        streakDays >= 14 -> Color(0xFF6C5CE7) // Deep Spirit Violet Indigo
        else -> Color(0xFFFF6B81)             // Warm Ruby Rose
    }

    val auraColorOuter = when {
        streakDays >= 30 -> Color(0xFF81ECEC).copy(alpha = 0.15f)
        streakDays >= 14 -> Color(0xFFA29BFE).copy(alpha = 0.15f)
        else -> Color(0xFFFFA502).copy(alpha = 0.15f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    onTapHeart()
                    scope.launch {
                        tapRippleRadius = 0f
                        tapRippleAlpha = 0.8f
                        while (tapRippleRadius < 400f) {
                            tapRippleRadius += 25f
                            tapRippleAlpha = (1f - tapRippleRadius / 400f).coerceAtLeast(0f)
                            delay(16)
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            // 1. Draw Ambient "Noor" Glow behind the 3D Qalb onto the neumorphic canvas
            val noorRadius = (width * 0.45f) * (1f + (streakDays.coerceAtMost(30) / 100f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColorCore.copy(alpha = auraGlowAlpha * 0.5f),
                        auraColorOuter,
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = noorRadius
                ),
                radius = noorRadius,
                center = Offset(centerX, centerY)
            )

            // 2. Draw Tap Vibration Wave Ripple
            if (tapRippleAlpha > 0f) {
                drawCircle(
                    color = auraColorCore.copy(alpha = tapRippleAlpha),
                    radius = tapRippleRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 6f)
                )
            }

            // 3. Draw Floating "Noor Particles"
            particles.forEach { p ->
                p.y -= p.speed
                if (p.y < 0.1f) p.y = 0.8f
                p.alpha = sin(p.y * Math.PI).toFloat()

                drawCircle(
                    color = auraColorCore.copy(alpha = p.alpha * 0.7f),
                    radius = p.size,
                    center = Offset(p.x * width, p.y * height)
                )
            }

            // 4. Draw 3D Sculpted Heart (Qalb) with bezier geometry & volumetric shading
            rotate(degrees = rotationAngle, pivot = Offset(centerX, centerY)) {
                scale(scale = beatScale, pivot = Offset(centerX, centerY)) {
                    val heartPath = Path().apply {
                        val heartWidth = width * 0.42f
                        val heartHeight = heartWidth * 1.05f
                        val topY = centerY - heartHeight * 0.45f
                        val bottomY = centerY + heartHeight * 0.48f

                        moveTo(centerX, topY + heartHeight * 0.25f)

                        // Left lobe bezier curve
                        cubicTo(
                            centerX - heartWidth * 0.55f, topY - heartHeight * 0.1f,
                            centerX - heartWidth * 0.62f, topY + heartHeight * 0.52f,
                            centerX, bottomY
                        )

                        // Right lobe bezier curve
                        cubicTo(
                            centerX + heartWidth * 0.62f, topY + heartHeight * 0.52f,
                            centerX + heartWidth * 0.55f, topY - heartHeight * 0.1f,
                            centerX, topY + heartHeight * 0.25f
                        )
                        close()
                    }

                    // Deep Volumetric 3D Gradient Fill
                    drawPath(
                        path = heartPath,
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.9f),
                                auraColorCore,
                                Color(0xFF100E1D)
                            ),
                            center = Offset(centerX - width * 0.08f, centerY - height * 0.08f),
                            radius = width * 0.35f
                        )
                    )

                    // Specular 3D Highlight Ring for Clay Depth
                    drawPath(
                        path = heartPath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            ),
                            start = Offset(centerX - width * 0.2f, centerY - height * 0.2f),
                            end = Offset(centerX + width * 0.2f, centerY + height * 0.2f)
                        ),
                        style = Stroke(width = 8f)
                    )

                    // Glowing Core Pulse Center
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                auraColorCore.copy(alpha = 0.8f),
                                Color.Transparent
                            ),
                            center = Offset(centerX - width * 0.05f, centerY - height * 0.05f),
                            radius = width * 0.12f
                        ),
                        radius = width * 0.12f,
                        center = Offset(centerX - width * 0.05f, centerY - height * 0.05f)
                    )
                }
            }
        }
    }
}
