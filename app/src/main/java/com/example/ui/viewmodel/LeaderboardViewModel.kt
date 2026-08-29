package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.AppContainer
import com.example.data.DefaultAppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SquadLeaderboardEntry(
    val squadName: String,
    val membersCount: Int,
    val combinedPurity: Int,
    val totalSquadXp: Int,
    val activeMultiplier: Float,
    val isUserSquad: Boolean = false
)

data class LeaderboardUser(
    val rank: Int,
    val displayName: String,
    val xp: Int,
    val streakDays: Int,
    val purityPercentage: Int,
    val region: String,
    val isCurrentUser: Boolean = false,
    val badge: String = "🛡️ Pure Guard"
)

class LeaderboardViewModel(
    application: Application,
    private val container: AppContainer = DefaultAppContainer(application)
) : AndroidViewModel(application) {

    private val _selectedRegion = MutableStateFlow("Global")
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow("Weekly Tournament")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe.asStateFlow()

    private val _isSitrModeAnonymous = MutableStateFlow(false)
    val isSitrModeAnonymous: StateFlow<Boolean> = _isSitrModeAnonymous.asStateFlow()

    private val _squads = MutableStateFlow(
        listOf(
            SquadLeaderboardEntry("Fajr Vanguard", 4, 98, 4820, 1.30f, isUserSquad = true),
            SquadLeaderboardEntry("Istiqamah Circle", 5, 96, 4210, 1.25f),
            SquadLeaderboardEntry("Qiyam Striders", 3, 94, 3890, 1.20f),
            SquadLeaderboardEntry("Sabr & Focus", 4, 91, 3420, 1.15f)
        )
    )
    val squads: StateFlow<List<SquadLeaderboardEntry>> = _squads.asStateFlow()

    private val _users = MutableStateFlow(
        listOf(
            LeaderboardUser(1, "Zayd Al-Faruq", 3450, 42, 99, "MENA", badge = "👑 Luminary Vanguard"),
            LeaderboardUser(2, "Hamza M.", 3120, 35, 98, "North America", badge = "⚡ Pure Focus"),
            LeaderboardUser(3, "Bilal K.", 2980, 28, 97, "Europe", badge = "🌙 Qiyam Sentinel"),
            LeaderboardUser(4, "Anonymous Seeker #849", 2640, 21, 95, "South Asia", isCurrentUser = true, badge = "🛡️ Pure Guard"),
            LeaderboardUser(5, "Tariq S.", 2410, 19, 94, "North America", badge = "⚔️ Istiqamah Shield"),
            LeaderboardUser(6, "Yusuf R.", 2180, 14, 92, "Southeast Asia", badge = "🌱 Steadfast Believer")
        )
    )
    val users: StateFlow<List<LeaderboardUser>> = _users.asStateFlow()

    fun selectRegion(region: String) {
        _selectedRegion.value = region
    }

    fun selectTimeframe(timeframe: String) {
        _selectedTimeframe.value = timeframe
    }

    fun toggleSitrMode(enabled: Boolean) {
        _isSitrModeAnonymous.value = enabled
    }
}
