package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppContainer
import com.example.data.DefaultAppContainer
import com.example.data.FocusSessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GhostRunState(
    val hasGhost: Boolean = true,
    val ghostBestMinutes: Int = 45,
    val ghostProgressPercent: Float = 0f,
    val ghostLeadStatus: String = "Pacing with your Personal Best (45m)"
)

class FocusViewModel(
    application: Application,
    private val container: AppContainer = DefaultAppContainer(application)
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    // Focus Session State
    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _focusSecondsRemaining = MutableStateFlow(25 * 60)
    val focusSecondsRemaining: StateFlow<Int> = _focusSecondsRemaining.asStateFlow()

    private val _selectedSessionDurationMinutes = MutableStateFlow(25)
    val selectedSessionDurationMinutes: StateFlow<Int> = _selectedSessionDurationMinutes.asStateFlow()

    private val _sessionTitle = MutableStateFlow("Deep Spiritual Focus")
    val sessionTitle: StateFlow<String> = _sessionTitle.asStateFlow()

    // Competitor 1: Forest Token Staking (Loss Aversion)
    private val _stakedTokens = MutableStateFlow(0)
    val stakedTokens: StateFlow<Int> = _stakedTokens.asStateFlow()

    // Competitor 2: Flora Hardcore Strict Lock
    private val _isHardcoreMode = MutableStateFlow(false)
    val isHardcoreMode: StateFlow<Boolean> = _isHardcoreMode.asStateFlow()

    // Competitor 3: Focusmate Ghost Run
    private val _ghostRun = MutableStateFlow(GhostRunState())
    val ghostRun: StateFlow<GhostRunState> = _ghostRun.asStateFlow()

    // Squad Synergy Multiplier (Habitica Co-op)
    private val _squadMultiplier = MutableStateFlow(1.25f)
    val squadMultiplier: StateFlow<Float> = _squadMultiplier.asStateFlow()

    private var timerJob: Job? = null

    fun setSessionDuration(minutes: Int) {
        if (!_isSessionActive.value) {
            _selectedSessionDurationMinutes.value = minutes
            _focusSecondsRemaining.value = minutes * 60
        }
    }

    fun setStakedTokens(amount: Int) {
        if (!_isSessionActive.value) {
            _stakedTokens.value = amount
        }
    }

    fun toggleHardcoreMode(enabled: Boolean) {
        _isHardcoreMode.value = enabled
    }

    fun startFocusSession(
        title: String = "Deep Spiritual Focus",
        onSessionStart: (staked: Int) -> Unit = {}
    ) {
        if (_isSessionActive.value) return

        _sessionTitle.value = title
        _isSessionActive.value = true
        onSessionStart(_stakedTokens.value)

        timerJob?.cancel()
        timerJob = viewModelScope.launch(Dispatchers.Default) {
            val totalSeconds = _selectedSessionDurationMinutes.value * 60
            while (_focusSecondsRemaining.value > 0 && _isSessionActive.value) {
                delay(1000L)
                _focusSecondsRemaining.value -= 1

                // Update Ghost Run comparison
                val elapsed = totalSeconds - _focusSecondsRemaining.value
                val progress = elapsed.toFloat() / totalSeconds.toFloat()
                val ghostElapsedFraction = (elapsed.toFloat() / (45 * 60f)).coerceIn(0f, 1f)

                _ghostRun.value = _ghostRun.value.copy(
                    ghostProgressPercent = ghostElapsedFraction,
                    ghostLeadStatus = if (progress >= ghostElapsedFraction) "Ahead of Ghost by ${(progress - ghostElapsedFraction) * 100}%" else "Behind Personal Best pace"
                )
            }

            if (_focusSecondsRemaining.value <= 0) {
                completeFocusSession()
            }
        }
    }

    fun stopOrAbandonSession(onTokensBurned: (Int) -> Unit) {
        timerJob?.cancel()
        if (_stakedTokens.value > 0) {
            onTokensBurned(_stakedTokens.value)
        }
        _isSessionActive.value = false
        _focusSecondsRemaining.value = _selectedSessionDurationMinutes.value * 60
        _stakedTokens.value = 0
    }

    private fun completeFocusSession() {
        _isSessionActive.value = false
        val duration = _selectedSessionDurationMinutes.value
        viewModelScope.launch(Dispatchers.IO) {
            container.repository.startFocusSession(
                title = _sessionTitle.value,
                durationMinutes = duration,
                isStrictLock = _isHardcoreMode.value
            )
        }
    }
}
