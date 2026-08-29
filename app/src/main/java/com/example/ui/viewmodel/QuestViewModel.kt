package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppContainer
import com.example.data.DefaultAppContainer
import com.example.utils.GeminiQuestVerifier
import com.example.utils.QuestVerificationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class BossBattle(
    val id: String,
    val name: String,
    val subtitle: String,
    val totalHp: Int,
    val currentHp: Int,
    val rewardBadge: String,
    val rewardXp: Int,
    val isDefeated: Boolean = false,
    val weakness: String = "Sustained Deep Focus"
)

data class MascotCompanion(
    val name: String = "Aether Guardian",
    val title: String = "Spiritual Shield Falcon",
    val stage: String = "Luminary Tier II",
    val mood: String = "Vigilant & Peaceful 🌟",
    val level: Int = 4,
    val purityPercentage: Int = 94,
    val equippedAccessory: String = "Retro Pixel Halo",
    val unlockedAccessories: List<String> = listOf("Pixel Halo", "Y2K Goggles", "Emerald Cape", "Golden Tasbih")
)

data class DailyQuestItem(
    val id: String,
    val title: String,
    val description: String,
    val pointsReward: Int,
    val isCompleted: Boolean = false,
    val isPendingVerification: Boolean = false,
    val verificationNotes: String = ""
)

class QuestViewModel(
    application: Application,
    private val container: AppContainer = DefaultAppContainer(application)
) : AndroidViewModel(application) {

    private val _bossBattles = MutableStateFlow(
        listOf(
            BossBattle(
                id = "boss_1",
                name = "THE SCROLL TYRANT",
                subtitle = "Weekly Milestone: 10 Hours Focused",
                totalHp = 600, // 600 minutes (10 hours)
                currentHp = 180,
                rewardBadge = "🛡️ Crown of Istiqamah",
                rewardXp = 500,
                isDefeated = false
            ),
            BossBattle(
                id = "boss_2",
                name = "NIGHT PHANTOM",
                subtitle = "Late-Night Defense: 5 Nights Pristine",
                totalHp = 5,
                currentHp = 1,
                rewardBadge = "🌙 Celestial Shield",
                rewardXp = 350,
                isDefeated = false
            )
        )
    )
    val bossBattles: StateFlow<List<BossBattle>> = _bossBattles.asStateFlow()

    private val _mascot = MutableStateFlow(MascotCompanion())
    val mascot: StateFlow<MascotCompanion> = _mascot.asStateFlow()

    private val _dailyQuests = MutableStateFlow(
        listOf(
            DailyQuestItem("q1", "Fajr Sanctuary", "Pray Fajr on time and do 5 min morning adhkar", 50, isCompleted = true),
            DailyQuestItem("q2", "Deep Quran Recitation", "Read 1 Juz or 10 verses with translation", 40, isCompleted = false),
            DailyQuestItem("q3", "Digital Fasting Window", "Keep phone locked for 45 continuous minutes", 60, isCompleted = false),
            DailyQuestItem("q4", "Sadaqah / Community Good", "Give charity or assist family today", 35, isCompleted = false)
        )
    )
    val dailyQuests: StateFlow<List<DailyQuestItem>> = _dailyQuests.asStateFlow()

    fun verifyQuestWithPhoto(
        questId: String,
        bitmap: Bitmap,
        onResult: (QuestVerificationResult) -> Unit
    ) {
        val quest = _dailyQuests.value.find { it.id == questId } ?: return
        _dailyQuests.value = _dailyQuests.value.map {
            if (it.id == questId) it.copy(isPendingVerification = true) else it
        }

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                GeminiQuestVerifier.verifyQuestPhoto(
                    bitmap = bitmap,
                    questTitle = quest.title,
                    questDescription = quest.description
                )
            }

            _dailyQuests.value = _dailyQuests.value.map {
                if (it.id == questId) {
                    it.copy(
                        isCompleted = result.isVerified,
                        isPendingVerification = false,
                        verificationNotes = result.reason
                    )
                } else it
            }

            if (result.isVerified) {
                // Damage boss battle!
                damageActiveBoss(quest.pointsReward)
            }

            onResult(result)
        }
    }

    fun completeQuestManual(questId: String) {
        _dailyQuests.value = _dailyQuests.value.map {
            if (it.id == questId) it.copy(isCompleted = true) else it
        }
        damageActiveBoss(40)
    }

    private fun damageActiveBoss(damageAmount: Int) {
        _bossBattles.value = _bossBattles.value.map { boss ->
            if (!boss.isDefeated) {
                val newHp = (boss.currentHp - damageAmount).coerceAtLeast(0)
                boss.copy(
                    currentHp = newHp,
                    isDefeated = newHp == 0
                )
            } else boss
        }
    }

    fun equipMascotAccessory(accessory: String) {
        _mascot.value = _mascot.value.copy(equippedAccessory = accessory)
    }
}
