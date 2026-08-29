package com.example.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.R
import java.util.Calendar

class NamazStreakWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Read actual streak and status from SharedPreferences
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val streakCount = prefs.getInt("namaz_streak_count", 1).coerceAtLeast(1)
        val lastLoggedTime = prefs.getLong("last_namaz_logged_timestamp", 0L)
        val forceExtinctionMode = prefs.getBoolean("widget_preview_extinction_mode", false)

        val isExpiringSoon = forceExtinctionMode || checkIsExpiringSoon(lastLoggedTime)

        provideContent {
            ClaymorphicWidgetContent(
                streakCount = streakCount,
                isExpiringSoon = isExpiringSoon
            )
        }
    }

    @Composable
    private fun ClaymorphicWidgetContent(
        streakCount: Int,
        isExpiringSoon: Boolean
    ) {
        // Claymorphic Color Palette: Soft milk-white base and warm sand/ash tones
        val backgroundColor = if (isExpiringSoon) Color(0xFFF2EFE9) else Color(0xFFFBF9F5)
        val cardInnerColor = if (isExpiringSoon) Color(0xFFE5DFD7) else Color(0xFFF4F0E8)
        val textColorPrimary = if (isExpiringSoon) Color(0xFF5A524A) else Color(0xFF2C2521)
        val textColorSecondary = if (isExpiringSoon) Color(0xFF8A8076) else Color(0xFFA89F91)
        val statusBadgeColor = if (isExpiringSoon) Color(0xFF9E4838) else Color(0xFFD97736)

        val imageRes = if (isExpiringSoon) {
            R.drawable.namaz_streak_ash_1787942637665
        } else {
            R.drawable.namaz_streak_vibrant_1787942622883
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(backgroundColor))
                .cornerRadius(28.dp)
                .clickable(actionStartActivity<MainActivity>())
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pillowy Clay Container
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(cardInnerColor))
                    .cornerRadius(22.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    // 3D Clay Mat & Flame Asset (Active flame vs Ash-grey dying ember)
                    Image(
                        provider = ImageProvider(imageRes),
                        contentDescription = if (isExpiringSoon) "Extinguishing Namaz Streak" else "Active Namaz Streak",
                        modifier = GlanceModifier
                            .size(76.dp)
                            .cornerRadius(16.dp)
                    )

                    Spacer(modifier = GlanceModifier.height(4.dp))

                    // Debossed Clay Counter & Label
                    Row(
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Text(
                            text = "$streakCount",
                            style = TextStyle(
                                color = ColorProvider(textColorPrimary),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        Text(
                            text = "STREAK",
                            style = TextStyle(
                                color = ColorProvider(textColorSecondary),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(2.dp))

                    // Dynamic State Subtitle
                    Text(
                        text = if (isExpiringSoon) "⚠️ Fading • Pray Soon" else "✨ Burning Pure",
                        style = TextStyle(
                            color = ColorProvider(statusBadgeColor),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }

    private fun checkIsExpiringSoon(lastLoggedTime: Long): Boolean {
        if (lastLoggedTime == 0L) {
            // If nothing logged today and it's late afternoon/evening (after 18:00), it is expiring soon
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            return hour >= 18
        }
        val elapsedMs = System.currentTimeMillis() - lastLoggedTime
        // Expiring soon if more than 20 hours have passed since last logged prayer
        return elapsedMs > 20 * 60 * 60 * 1000L
    }
}
