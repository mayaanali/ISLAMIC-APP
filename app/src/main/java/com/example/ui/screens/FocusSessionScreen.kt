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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PixelAvatarIcon
import com.example.ui.components.PixelBgCanvas
import com.example.ui.components.PixelCardContainer
import com.example.ui.components.PixelCoinIcon
import com.example.ui.components.PixelDarkBorder
import com.example.ui.components.PixelGoldAccent
import com.example.ui.components.PixelTimerGauge
import com.example.ui.viewmodel.MainViewModel

@Composable
fun FocusSessionScreen(viewModel: MainViewModel) {
    val focusTimer by viewModel.focusTimer.collectAsState()
    val coins by viewModel.coins.collectAsState()

    var selectedDuration by remember { mutableIntStateOf(25) }
    val durations = remember { listOf(15, 25, 45, 60, 90) }

    val minutesRemaining = if (focusTimer.isActive) focusTimer.secondsRemaining / 60 else selectedDuration.toLong()
    val secondsRemaining = if (focusTimer.isActive) focusTimer.secondsRemaining % 60 else 0L
    val formattedTime = String.format("%02d:%02d", minutesRemaining, secondsRemaining)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PixelBgCanvas)
    ) {
        // Islamic Background Tile Watermark Effect
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
            // 1. TOP HEADER WITH COINS & AVATAR
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Focus Mode Timer",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Lock out distractions during high-priority focus sessions",
                        fontSize = 10.5.sp,
                        color = PixelDarkBorder.copy(alpha = 0.75f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PixelCoinIcon(modifier = Modifier.size(22.dp))
                    Text(
                        text = String.format("%,d", coins),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder
                    )
                    PixelAvatarIcon(modifier = Modifier.size(34.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 2. CENTER PIXEL TIMER CIRCULAR GAUGE
            // ==========================================
            PixelTimerGauge(
                progress = if (focusTimer.isActive) focusTimer.progress else 1f,
                formattedTime = formattedTime,
                statusText = if (focusTimer.isActive) "Focusing..." else "Ready to Focus",
                modifier = Modifier.size(240.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ==========================================
            // 3. SELECT FOCUS DURATION CHIPS
            // ==========================================
            AnimatedVisibility(visible = !focusTimer.isActive) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Select Focus Duration",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(durations) { min ->
                            val isSelected = selectedDuration == min
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) Color(0xFFBA2D0B) else Color.White
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = PixelDarkBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedDuration = min }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "$min min",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) Color.White else PixelDarkBorder
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // 4. ACTION BUTTON (DEEP RED PIXEL BUTTON)
            // ==========================================
            if (focusTimer.isActive) {
                Button(
                    onClick = { viewModel.stopFocusTimer() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2D0B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(width = 2.dp, color = PixelDarkBorder, shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "End",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "End Focus Session",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = { viewModel.startFocusTimer("Deep Work Sprint", selectedDuration) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2D0B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(width = 2.dp, color = PixelDarkBorder, shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start $selectedDuration Min Focus Session",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // 5. FOCUS SHIELD GUARANTEE CARD
            // ==========================================
            PixelCardContainer(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PixelGoldAccent.copy(alpha = 0.2f))
                                .border(1.5.dp, PixelGoldAccent, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock",
                                tint = PixelDarkBorder,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Focus Shield Guarantee",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelDarkBorder
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "While focus mode is active, opening any instant blocked app will automatically present the FocusGuard overlay shield.",
                        fontSize = 11.sp,
                        color = PixelDarkBorder.copy(alpha = 0.8f),
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

