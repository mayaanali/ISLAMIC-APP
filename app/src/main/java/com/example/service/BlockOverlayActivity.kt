package com.example.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.components.LateNightFrictionScreen
import com.example.ui.theme.MyApplicationTheme

class BlockOverlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enforce FLAG_SECURE to prevent screenshot scraping and overlay interception
        com.example.utils.SecurityGuard.enforceFlagSecure(this)

        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "Distraction Blocker"
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        val reason = intent.getStringExtra(EXTRA_REASON) ?: "Night Shield Active"

        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val streakDays = prefs.getInt("pref_streak_days", 14)
        val onboardingGoal = prefs.getString("pref_onboarding_goal", "break free from late-night habits and strengthen your connection with Allah")
            ?: "break free from late-night habits and strengthen your connection with Allah"

        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF07090E)
                ) {
                    LateNightFrictionScreen(
                        appName = appName,
                        reason = reason,
                        streakDays = streakDays,
                        onboardingGoal = onboardingGoal,
                        onGoHome = {
                            goHome()
                        },
                        onEscapeHatchReward = {
                            // Award +50 XP and save to preferences
                            val currentCoins = prefs.getInt("pref_coins", 1240)
                            prefs.edit().putInt("pref_coins", currentCoins + 50).apply()
                        },
                        onUnlockConfirmed = {
                            // Allowed temporary access
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun goHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(homeIntent)
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        goHome()
    }

    companion object {
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_REASON = "extra_reason"
    }
}
