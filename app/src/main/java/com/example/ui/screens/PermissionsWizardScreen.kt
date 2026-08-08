package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PixelBgCanvas
import com.example.ui.components.PixelCardContainer
import com.example.ui.components.PixelCoinIcon
import com.example.ui.components.PixelCrescentStarIcon
import com.example.ui.components.PixelDarkBorder
import com.example.ui.components.PixelGoldAccent
import com.example.ui.components.PixelGreenAccent
import com.example.ui.components.PixelIslamicPermissionCard
import com.example.ui.components.PixelStarBadge
import com.example.ui.viewmodel.MainViewModel
import com.example.utils.PermissionManager

@Composable
fun PermissionsWizardScreen(viewModel: MainViewModel) {
    val permissions by viewModel.permissions.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val context = LocalContext.current

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
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // 1. TOP HEADER WITH GUIDED PATH & BISMILLAH
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Guided Path",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelDarkBorder,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "﷽",
                            fontSize = 14.sp,
                            color = PixelDarkBorder.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = "DAILY DEEN JOURNEY | SPIRITUAL VITALITY",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = PixelDarkBorder.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PixelCoinIcon(modifier = Modifier.size(20.dp))
                    Text(
                        text = String.format("%,d", coins),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder
                    )
                    PixelStarBadge(
                        text = "⏱",
                        modifier = Modifier.size(32.dp),
                        badgeColor = PixelGreenAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // 2. MAIN SECTION FRAME CONTAINER
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFCBD5E1).copy(alpha = 0.4f))
                    .border(2.dp, PixelDarkBorder, RoundedCornerShape(14.dp))
                    .padding(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header title block with Crescent Moon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PixelCrescentStarIcon(modifier = Modifier.size(16.dp), color = PixelGoldAccent)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Permission Setup & Engine",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelDarkBorder
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        PixelCrescentStarIcon(modifier = Modifier.size(16.dp), color = PixelGoldAccent)
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Required high-level permissions to detect app launches, calculate screen time, and draw the block overlay.",
                        fontSize = 10.sp,
                        color = PixelDarkBorder.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Card 1: Usage Stats
                    PixelIslamicPermissionCard(
                        title = "1. Usage Stats Access",
                        description = "Allows FocusGuard to poll UsageStatsManager and detect foreground app usage time.",
                        icon = Icons.Default.Timer,
                        isGranted = permissions.hasUsageStats,
                        onGrantClick = { PermissionManager.openUsageStatsSettings(context) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Card 2: Display Over Apps
                    PixelIslamicPermissionCard(
                        title = "2. Display Over Other Apps",
                        description = "Allows FocusGuard to draw the WindowManager TYPE_APPLICATION_OVERLAY full screen shield when a limit is exceeded.",
                        icon = Icons.Default.Layers,
                        isGranted = permissions.hasOverlay,
                        onGrantClick = { PermissionManager.openOverlaySettings(context) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Card 3: Accessibility Service
                    PixelIslamicPermissionCard(
                        title = "3. Accessibility Service",
                        description = "Provides millisecond instant app launch detection (onAccessibilityEvent) to instantly target shield overlay.",
                        icon = Icons.Default.AccessibilityNew,
                        isGranted = permissions.hasAccessibility,
                        onGrantClick = { PermissionManager.openAccessibilitySettings(context) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Card 4: Battery Optimization Exemption
                    PixelIslamicPermissionCard(
                        title = "4. Battery Optimization Exemption",
                        description = "Prevents Android OS from force killing FocusGuard's foreground monitoring service in the background.",
                        icon = Icons.Default.BatteryAlert,
                        isGranted = permissions.isIgnoringBattery,
                        onGrantClick = { PermissionManager.openBatteryOptimizationSettings(context) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 3. ANDROID RESTRICTED SETTINGS GUIDE CARD
            // ==========================================
            PixelCardContainer(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PixelGoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Android 13/14/15 Restricted Settings Fix",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelDarkBorder
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "If Accessibility is grayed out on your device:\n" +
                                "1. Tap 'Open App Info' below.\n" +
                                "2. Tap the ⋮ (three dots) in top-right corner.\n" +
                                "3. Select 'Allow restricted settings'.\n" +
                                "4. Return to Accessibility settings to turn on FocusGuard.",
                        fontSize = 10.5.sp,
                        color = PixelDarkBorder.copy(alpha = 0.85f),
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { PermissionManager.openAppDetailsSettings(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelDarkBorder,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                    ) {
                        Text(
                            text = "Open App Info Page",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
