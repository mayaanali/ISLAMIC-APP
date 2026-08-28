package com.example.data

/**
 * Leaderboard Region Categories
 */
enum class LeaderboardRegion(val displayName: String, val flagEmoji: String) {
    GLOBAL("Global / Worldwide", "🌍"),
    NORTH_AMERICA("North America", "🌎"),
    MENA("Middle East & North Africa", "🕌"),
    EUROPE("Europe", "🏰"),
    SOUTH_ASIA("South Asia", "🪷"),
    SOUTHEAST_ASIA("Southeast Asia", "🏝️"),
    CENTRAL_ASIA("Central Asia", "🏔️"),
    AFRICA("Sub-Saharan Africa", "🌅")
}

/**
 * Spiritual League Tiers
 */
enum class SpiritualLeague(
    val title: String,
    val iconEmoji: String,
    val minPoints: Int,
    val minPurity: Int,
    val description: String
) {
    DIAMOND_TAQWA("Diamond Taqwa", "💎", 2000, 95, "Elite tier of consistent worship and flawless digital guard"),
    EMERALD_MURAQABAH("Emerald Muraqabah", "💚", 1200, 85, "High awareness of Allah in solitude and public"),
    RUBY_SABR("Ruby Sabr", "🛡️", 600, 70, "Patience during temptations and solid daily prayer baseline"),
    SAPPHIRE_IKHLAS("Sapphire Ikhlas", "🔷", 0, 0, "Sincere beginning on the path of self-purification")
}

/**
 * Individual Leaderboard Entry
 */
data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val alias: String,
    val isCurrentUser: Boolean = false,
    val points: Int,
    val shieldPurityPercent: Int,
    val streakDays: Int,
    val region: LeaderboardRegion,
    val league: SpiritualLeague,
    val avatarUrl: String = "",
    val rankDelta: Int = 0, // e.g. +2 ranks up, 0 unchanged, -1 down
    val isOnline: Boolean = true,
    val recentAction: String = "Just logged Fajr"
) {
    /**
     * Compound Score used for tie-breaking: Points weighted by Shield Purity
     */
    val compoundScore: Int
        get() = (points * (shieldPurityPercent.toDouble() / 100.0)).toInt()
}
