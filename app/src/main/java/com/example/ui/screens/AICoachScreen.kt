package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PixelAnimatedLoadingScreen
import com.example.ui.components.PixelBgCanvas
import com.example.ui.components.PixelCardContainer
import com.example.ui.components.PixelCrescentStarIcon
import com.example.ui.components.PixelDarkBorder
import com.example.ui.components.PixelGoldAccent
import com.example.ui.components.PixelGreenAccent
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AICoachScreen(viewModel: MainViewModel) {
    val aiCoachText by viewModel.aiCoachText.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val totalScreenTimeMinutes by viewModel.totalScreenTimeMinutes.collectAsState()

    if (isAiLoading) {
        PixelAnimatedLoadingScreen(
            statusText = "GEMINI AI ANALYSIS IN PROGRESS..."
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PixelBgCanvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GEMINI FOCUS COACH",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder
                    )
                    Text(
                        text = "2D RETRO AI WISDOM & WELLNESS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PixelDarkBorder.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp
                    )
                }

                IconButton(onClick = { viewModel.generateAICoachInsight() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Insight",
                        tint = PixelDarkBorder
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2D Pixel Card Container
            PixelCardContainer(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PixelGreenAccent.copy(alpha = 0.15f))
                                .border(1.5.dp, PixelGreenAccent, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            PixelCrescentStarIcon(
                                modifier = Modifier.size(20.dp),
                                color = PixelGreenAccent
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "FocusGuard Gemini AI",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PixelDarkBorder
                            )
                            Text(
                                text = "Analyzing $totalScreenTimeMinutes mins screen time today",
                                fontSize = 10.sp,
                                color = PixelDarkBorder.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = aiCoachText
                            ?: "Tap 'Generate AI Analysis' below to receive tailored 2D retro guidance on reducing digital friction and staying steadfast on your daily goals.",
                        fontSize = 12.5.sp,
                        color = PixelDarkBorder,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.generateAICoachInsight() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelGoldAccent,
                            contentColor = PixelDarkBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .border(2.dp, PixelDarkBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = PixelDarkBorder
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GENERATE AI ANALYSIS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
