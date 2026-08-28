package com.example.data

/**
 * Data structures for Profile, Ledger, and Settings Folders
 */

data class PointLedgerEntry(
    val id: String,
    val title: String,
    val points: Int,
    val isCredit: Boolean, // true = earned (+), false = spent (-)
    val timestamp: String,
    val category: String,
    val iconEmoji: String = "⭐"
)

enum class SettingsFolder(
    val title: String,
    val subtitle: String,
    val iconName: String,
    val badge: String? = null
) {
    PERMISSIONS(
        title = "Permissions",
        subtitle = "Usage, Overlay, Accessibility, Battery, Location & Notifications",
        iconName = "🛡️",
        badge = "ENGINE"
    ),
    THEME(
        title = "Theme & Appearance",
        subtitle = "Dark Mode, Light Canvas, Minimalist Accents & Contrast",
        iconName = "🎨",
        badge = null
    ),
    SPIRITUAL_CALCULATION(
        title = "Spiritual & Prayer Calculation",
        subtitle = "Calculation Method, Asr School, Adhan Audio & Qibla Compass",
        iconName = "🕌",
        badge = "DEEN"
    ),
    SHIELD_AND_FOCUS(
        title = "Shield & Focus",
        subtitle = "App Blocklist, Cooldown Friction & Discrete Sitr Mode",
        iconName = "🔒",
        badge = "BLOCKER"
    ),
    GAMIFICATION_LEADERBOARD(
        title = "Gamification & Leaderboard",
        subtitle = "Anonymous Mode, Point Ledger History & Streak Freeze",
        iconName = "🏆",
        badge = "XP"
    ),
    ACCOUNT_JOURNEY_PERSONA(
        title = "Account & Journey Persona",
        subtitle = "Muslim vs. Seeker Mode, Retake Diagnostic & Data Privacy",
        iconName = "👤",
        badge = "PERSONA"
    ),
    APP_PREFERENCES(
        title = "App Preferences",
        subtitle = "Language, Quran Reciter, Haptic Feedback & Audio Effects",
        iconName = "⚙️",
        badge = null
    )
}
