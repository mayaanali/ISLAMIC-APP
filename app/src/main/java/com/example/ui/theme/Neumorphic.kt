package com.example.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Clay Neumorphic Palette Colors
val ClayBgDark = Color(0xFF181A28)
val ClaySurfaceDark = Color(0xFF181A28)
val ClayLightShadowDark = Color(0xFF26293D)
val ClayDarkShadowDark = Color(0xFF0C0D15)

val ClayBgLight = Color(0xFFE8ECEF)
val ClaySurfaceLight = Color(0xFFE8ECEF)
val ClayLightShadowLight = Color(0xFFFFFFFF)
val ClayDarkShadowLight = Color(0xFFB8C2CC)

@Composable
fun getClayColors(isDark: Boolean = true): Triple<Color, Color, Color> {
    return if (isDark) {
        Triple(ClaySurfaceDark, ClayLightShadowDark, ClayDarkShadowDark)
    } else {
        Triple(ClaySurfaceLight, ClayLightShadowLight, ClayDarkShadowLight)
    }
}

/**
 * Extruded Neumorphic Container (Pops out of the screen)
 */
@Composable
fun NeumorphicBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    isDark: Boolean = true,
    elevation: Dp = 6.dp,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    val (surfaceColor, lightShadow, darkShadow) = getClayColors(isDark)

    Box(
        modifier = modifier
            .drawBehind {
                val shadowRadius = elevation.toPx()
                val offsetPx = (elevation / 2).toPx()

                drawIntoCanvas { canvas ->
                    // Top-Left Light Shadow
                    val lightPaint = Paint().apply {
                        color = lightShadow
                        style = PaintingStyle.Fill
                        asFrameworkPaint().apply {
                            this.color = lightShadow.toArgb()
                            this.maskFilter = android.graphics.BlurMaskFilter(
                                shadowRadius,
                                android.graphics.BlurMaskFilter.Blur.NORMAL
                            )
                        }
                    }
                    canvas.drawRect(
                        left = -offsetPx,
                        top = -offsetPx,
                        right = size.width - offsetPx,
                        bottom = size.height - offsetPx,
                        paint = lightPaint
                    )

                    // Bottom-Right Dark Shadow
                    val darkPaint = Paint().apply {
                        color = darkShadow
                        style = PaintingStyle.Fill
                        asFrameworkPaint().apply {
                            this.color = darkShadow.toArgb()
                            this.maskFilter = android.graphics.BlurMaskFilter(
                                shadowRadius,
                                android.graphics.BlurMaskFilter.Blur.NORMAL
                            )
                        }
                    }
                    canvas.drawRect(
                        left = offsetPx,
                        top = offsetPx,
                        right = size.width + offsetPx,
                        bottom = size.height + offsetPx,
                        paint = darkPaint
                    )
                }
            }
            .clip(shape)
            .background(surfaceColor),
        contentAlignment = contentAlignment,
        content = content
    )
}

/**
 * Inset / Recessed Neumorphic Container (Pressed into the screen like an engraved number or heavy switch)
 */
@Composable
fun NeumorphicInsetBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    isDark: Boolean = true,
    glowColor: Color? = null,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    val (surfaceColor, lightShadow, darkShadow) = getClayColors(isDark)

    Box(
        modifier = modifier
            .clip(shape)
            .background(surfaceColor)
            .drawBehind {
                val offsetPx = 6.dp.toPx()
                val blurPx = 10.dp.toPx()

                drawIntoCanvas { canvas ->
                    // Top-Left Inset Dark Shadow (Creates carved depth)
                    val darkPaint = Paint().apply {
                        color = darkShadow
                        asFrameworkPaint().apply {
                            this.color = darkShadow.toArgb()
                            this.maskFilter = android.graphics.BlurMaskFilter(
                                blurPx,
                                android.graphics.BlurMaskFilter.Blur.NORMAL
                            )
                        }
                    }
                    canvas.drawRect(
                        left = 0f,
                        top = 0f,
                        right = size.width,
                        bottom = offsetPx * 2,
                        paint = darkPaint
                    )

                    // Optional Inner Glow Accent (e.g. Y2K Neon Green / Cyan)
                    glowColor?.let { color ->
                        val glowPaint = Paint().apply {
                            asFrameworkPaint().apply {
                                this.color = color.copy(alpha = 0.35f).toArgb()
                                this.maskFilter = android.graphics.BlurMaskFilter(
                                    blurPx * 1.5f,
                                    android.graphics.BlurMaskFilter.Blur.NORMAL
                                )
                            }
                        }
                        canvas.drawCircle(
                            center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2),
                            radius = size.width / 2.2f,
                            paint = glowPaint
                        )
                    }
                }
            },
        contentAlignment = contentAlignment,
        content = content
    )
}

/**
 * Interactive Neumorphic Button with Tactile Physical Press Animation
 */
@Composable
fun NeumorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    isDark: Boolean = true,
    isPressed: Boolean = false,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    var pressedState by remember { mutableStateOf(false) }
    val effectivePressed = isPressed || pressedState

    val elevation by animateFloatAsState(
        targetValue = if (effectivePressed) 1f else 8f,
        animationSpec = tween(durationMillis = 120),
        label = "ButtonElevation"
    )

    if (effectivePressed) {
        NeumorphicInsetBox(
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            pressedState = true
                            tryAwaitRelease()
                            pressedState = false
                            onClick()
                        }
                    )
                },
            shape = shape,
            isDark = isDark,
            contentAlignment = contentAlignment,
            content = content
        )
    } else {
        NeumorphicBox(
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            pressedState = true
                            tryAwaitRelease()
                            pressedState = false
                            onClick()
                        }
                    )
                },
            shape = shape,
            isDark = isDark,
            elevation = elevation.dp,
            contentAlignment = contentAlignment,
            content = content
        )
    }
}
