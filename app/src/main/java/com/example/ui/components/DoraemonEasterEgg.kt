package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun DoraemonEasterEgg(
    pullOffsetPx: Float,
    thresholdPx: Float = 140f
) {
    val isRevealed = pullOffsetPx > thresholdPx

    val animatedOffset by animateFloatAsState(
        targetValue = if (isRevealed) 0f else -180f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "DoraemonSpring"
    )

    if (pullOffsetPx > 20f) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, animatedOffset.roundToInt()) },
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E202C),
                shadowElevation = 8.dp,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Doraemon Custom Canvas Drawing
                    Canvas(modifier = Modifier.size(54.dp)) {
                        val cX = size.width / 2f
                        val cY = size.height / 2f
                        val r = size.width / 2.2f

                        // 1. Blue Head
                        drawCircle(
                            color = Color(0xFF00A0E9), // Doraemon Sky Blue
                            radius = r,
                            center = Offset(cX, cY)
                        )

                        // 2. White Face Oval
                        drawOval(
                            color = Color.White,
                            topLeft = Offset(cX - r * 0.78f, cY - r * 0.5f),
                            size = Size(r * 1.56f, r * 1.45f)
                        )

                        // 3. Eyes
                        val eyeR = r * 0.22f
                        drawOval(
                            color = Color.White,
                            topLeft = Offset(cX - eyeR * 1.8f, cY - r * 0.85f),
                            size = Size(eyeR * 1.7f, eyeR * 2.2f)
                        )
                        drawOval(
                            color = Color.Black,
                            topLeft = Offset(cX - eyeR * 1.8f, cY - r * 0.85f),
                            size = Size(eyeR * 1.7f, eyeR * 2.2f),
                            style = Stroke(width = 2f)
                        )
                        // Left pupil
                        drawCircle(
                            color = Color.Black,
                            radius = eyeR * 0.35f,
                            center = Offset(cX - eyeR * 0.9f, cY - r * 0.45f)
                        )

                        drawOval(
                            color = Color.White,
                            topLeft = Offset(cX + eyeR * 0.1f, cY - r * 0.85f),
                            size = Size(eyeR * 1.7f, eyeR * 2.2f)
                        )
                        drawOval(
                            color = Color.Black,
                            topLeft = Offset(cX + eyeR * 0.1f, cY - r * 0.85f),
                            size = Size(eyeR * 1.7f, eyeR * 2.2f),
                            style = Stroke(width = 2f)
                        )
                        // Right pupil
                        drawCircle(
                            color = Color.Black,
                            radius = eyeR * 0.35f,
                            center = Offset(cX + eyeR * 0.9f, cY - r * 0.45f)
                        )

                        // 4. Red Nose
                        drawCircle(
                            color = Color(0xFFE60012),
                            radius = r * 0.2f,
                            center = Offset(cX, cY - r * 0.22f)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = r * 0.06f,
                            center = Offset(cX - r * 0.05f, cY - r * 0.26f)
                        )

                        // Nose Line down to mouth
                        drawLine(
                            color = Color.Black,
                            start = Offset(cX, cY - r * 0.02f),
                            end = Offset(cX, cY + r * 0.4f),
                            strokeWidth = 3f
                        )

                        // Whiskers (3 on left, 3 on right)
                        drawLine(Color.Black, Offset(cX - r * 0.25f, cY - r * 0.05f), Offset(cX - r * 0.75f, cY - r * 0.18f), 2.5f)
                        drawLine(Color.Black, Offset(cX - r * 0.25f, cY + r * 0.1f), Offset(cX - r * 0.8f, cY + r * 0.1f), 2.5f)
                        drawLine(Color.Black, Offset(cX - r * 0.25f, cY + r * 0.25f), Offset(cX - r * 0.75f, cY + r * 0.38f), 2.5f)

                        drawLine(Color.Black, Offset(cX + r * 0.25f, cY - r * 0.05f), Offset(cX + r * 0.75f, cY - r * 0.18f), 2.5f)
                        drawLine(Color.Black, Offset(cX + r * 0.25f, cY + r * 0.1f), Offset(cX + r * 0.8f, cY + r * 0.1f), 2.5f)
                        drawLine(Color.Black, Offset(cX + r * 0.25f, cY + r * 0.25f), Offset(cX + r * 0.75f, cY + r * 0.38f), 2.5f)

                        // Big Smile Arc
                        drawArc(
                            color = Color.Black,
                            startAngle = 10f,
                            sweepAngle = 160f,
                            useCenter = false,
                            topLeft = Offset(cX - r * 0.6f, cY - r * 0.1f),
                            size = Size(r * 1.2f, r * 0.65f),
                            style = Stroke(width = 3.5f)
                        )

                        // Red Collar & Golden Bell
                        drawRect(
                            color = Color(0xFFE60012),
                            topLeft = Offset(cX - r * 0.6f, cY + r * 0.78f),
                            size = Size(r * 1.2f, r * 0.18f)
                        )
                        drawCircle(
                            color = Color(0xFFFFD700),
                            radius = r * 0.15f,
                            center = Offset(cX, cY + r * 0.9f)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Doraemon says:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF00A0E9),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " salute! Stay focused & protect your Qalb! ✨",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
