package com.example.data

data class PrayerItem(
    val id: String,
    val name: String,
    val sequence: Int,
    val timing: String,
    val basePoints: Int,
    val isCompleted: Boolean = false,
    val isStruggling: Boolean = false,
    val struggleBonus: Int = 10
) {
    val totalPoints: Int
        get() = if (isStruggling) basePoints + struggleBonus else basePoints
}

enum class QuestCategory(val label: String) {
    CHARITY_ZAKAT("Zakat & Sadaqah"),
    QURAN_DHIKR("Quran & Adhkar"),
    DEEN_LIFESTYLE("Deen & Good Deeds")
}

data class DailyQuest(
    val id: String,
    val title: String,
    val category: QuestCategory,
    val points: Int,
    val description: String,
    val isCompleted: Boolean = false
)
