package com.example.ui.components

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.widget.NamazStreakWidgetReceiver

@Composable
fun ClaymorphicStreakCard(
    streakCount: Int = 1,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExtinctionMode by remember { mutableStateOf(false) }
    var currentStreak by remember { mutableIntStateOf(streakCount.coerceAtLeast(1)) }

    // Claymorphism Theme Colors
    val bgGradient = if (isExtinctionMode) {
        Brush.verticalGradient(
            colors = listOf(Color(0xFFF2EFE9), Color(0xFFE2DBD0), Color(0xFFC7BCB0))
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(Color(0xFFFDFBF7), Color(0xFFF6F0E6), Color(0xFFEAE0D1))
        )
    }

    val cardBg = if (isExtinctionMode) Color(0xFFE8E2D9) else Color(0xFFFAF7F0)
    val textPrimary = if (isExtinctionMode) Color(0xFF423B35) else Color(0xFF2C2521)
    val textSecondary = if (isExtinctionMode) Color(0xFF7D7267) else Color(0xFFA09687)
    val accentColor = if (isExtinctionMode) Color(0xFF8F392B) else Color(0xFFD46927)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = Color(0x33000000),
                ambientColor = Color(0x22000000)
            ),
        shape = RoundedCornerShape(32.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(bgGradient)
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0x66FFFFFF), Color(0x22D4C5B9))
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header with Widget Badge & Extinction Simulator Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = Color(0x22000000))
                                .background(Color(0xFFF5EFE4), RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                contentDescription = null,
                                tint = if (isExtinctionMode) Color(0xFF7A6F64) else Color(0xFFD46927),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Namaz Streak Widget",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = "Jetpack Glance • Claymorphic 3D",
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                        }
                    }

                    // Extinction Preview Switch
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExtinctionMode) "Ash state" else "Vibrant",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isExtinctionMode) Color(0xFF9E4838) else Color(0xFF388E3C)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isExtinctionMode,
                            onCheckedChange = {
                                isExtinctionMode = it
                                // Save preview mode into SharedPreferences so widget updates
                                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                prefs.edit().putBoolean("widget_preview_extinction_mode", it).apply()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF9E4838),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFD46927)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Center Claymorphic Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(26.dp),
                            spotColor = if (isExtinctionMode) Color(0x4426211C) else Color(0x337A5C3D)
                        )
                        .background(cardBg, RoundedCornerShape(26.dp))
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFFFFF), Color(0x44FFFFFF), Color(0x11000000))
                            ),
                            shape = RoundedCornerShape(26.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 3D Visual Asset with Smooth State Swap
                        AnimatedContent(
                            targetState = isExtinctionMode,
                            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) },
                            label = "flame_transition"
                        ) { extinguishing ->
                            val imageRes = if (extinguishing) {
                                R.drawable.namaz_streak_ash_1787942637665
                            } else {
                                R.drawable.namaz_streak_vibrant_1787942622883
                            }
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = if (extinguishing) "Extinguishing Flame" else "Active Flame",
                                modifier = Modifier
                                    .size(130.dp)
                                    .shadow(10.dp, RoundedCornerShape(22.dp), spotColor = Color(0x33000000))
                                    .clip(RoundedCornerShape(22.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isExtinctionMode) Color(0x228F392B) else Color(0x1FD46927),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (isExtinctionMode) "🌪️ PRAYER WINDOW EXPIRING • FLAME FADING" else "🔥 ACTIVE STREAK • FLAME VIBRANT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Counter & Debossed "STREAK"
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$currentStreak",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "STREAK",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = textSecondary
                            )
                        }

                        Text(
                            text = if (isExtinctionMode) "Logged 22 hours ago — Log your Namaz to ignite the flame!" else "Consecutive days on prayer schedule. Keep the fire burning!",
                            fontSize = 12.sp,
                            color = textSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Pin Widget to Home Screen & Increment Streak Demo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            requestPinWidget(context)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isExtinctionMode) Color(0xFF63564A) else Color(0xFF2C2521)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add to Home Screen",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            currentStreak += 1
                            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            prefs.edit()
                                .putInt("namaz_streak_count", currentStreak)
                                .putLong("last_namaz_logged_timestamp", System.currentTimeMillis())
                                .putBoolean("widget_preview_extinction_mode", false)
                                .apply()
                            isExtinctionMode = false
                            Toast.makeText(context, "Namaz logged! Streak: $currentStreak 🔥", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(0.9f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFD46927)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Log Prayer (+1)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C2521)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper to pin Glance AppWidget to Android Home Screen on supported launchers.
 */
private fun requestPinWidget(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
        val myProvider = ComponentName(context, NamazStreakWidgetReceiver::class.java)
        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            val successCallback = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, NamazStreakWidgetReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
            Toast.makeText(context, "Widget pin request sent to Home Screen", Toast.LENGTH_SHORT).show()
            return
        }
    }
    Toast.makeText(context, "Long-press your Home Screen and select FocusGuard > Namaz Streak Widget", Toast.LENGTH_LONG).show()
}
