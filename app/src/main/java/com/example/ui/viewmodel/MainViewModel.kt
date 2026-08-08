package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.BlockedAppEntity
import com.example.data.DailyQuest
import com.example.data.FocusSessionEntity
import com.example.data.InstalledAppItem
import com.example.data.PrayerItem
import com.example.data.QuestCategory
import com.example.service.AppBlockerForegroundService
import com.example.utils.PermissionManager
import com.example.utils.UsageTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PermissionStatus(
    val hasUsageStats: Boolean = false,
    val hasOverlay: Boolean = false,
    val hasAccessibility: Boolean = false,
    val isIgnoringBattery: Boolean = false
) {
    val allGranted: Boolean get() = hasUsageStats && hasOverlay && hasAccessibility && isIgnoringBattery
}

data class FocusTimerState(
    val isActive: Boolean = false,
    val title: String = "Focus Mode",
    val durationMinutes: Int = 25,
    val secondsRemaining: Long = 25 * 60L,
    val progress: Float = 1f
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(
        context = application.applicationContext,
        database = AppDatabase.getDatabase(application.applicationContext)
    )

    // Permissions State
    private val _permissions = MutableStateFlow(PermissionStatus())
    val permissions: StateFlow<PermissionStatus> = _permissions.asStateFlow()

    // Blocked Apps from DB
    val blockedApps: StateFlow<List<BlockedAppEntity>> = repository.blockedAppsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Installed Apps List
    private val _installedApps = MutableStateFlow<List<InstalledAppItem>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppItem>> = _installedApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    // Search Query & Category Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Total Screen Time Today
    private val _totalScreenTimeMinutes = MutableStateFlow(0L)
    val totalScreenTimeMinutes: StateFlow<Long> = _totalScreenTimeMinutes.asStateFlow()

    // Focus Timer State
    private val _focusTimer = MutableStateFlow(FocusTimerState())
    val focusTimer: StateFlow<FocusTimerState> = _focusTimer.asStateFlow()
    private var timerJob: Job? = null

    // AI Coach Insight
    private val _aiCoachText = MutableStateFlow<String?>(null)
    val aiCoachText: StateFlow<String?> = _aiCoachText.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Focus Streak & Urge Resistance State
    private val _streakDays = MutableStateFlow(14)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val _resistedCountToday = MutableStateFlow(5)
    val resistedCountToday: StateFlow<Int> = _resistedCountToday.asStateFlow()
    val resistedCount: StateFlow<Int> = _resistedCountToday.asStateFlow()

    private val _isPanicModeActive = MutableStateFlow(false)
    val isPanicModeActive: StateFlow<Boolean> = _isPanicModeActive.asStateFlow()

    // Guided Path / Spiritual Vitality & Quests State
    private val _coins = MutableStateFlow(0) // Points start from 0 for the user
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _purityIndex = MutableStateFlow(0) // Starts from 0, grows with habits
    val purityIndex: StateFlow<Int> = _purityIndex.asStateFlow()

    private val _totalZakatDonated = MutableStateFlow(0.0)
    val totalZakatDonated: StateFlow<Double> = _totalZakatDonated.asStateFlow()

    // 5 Prayers - Sequence, Timing & Base Points based on difficulty
    private val defaultPrayers = listOf(
        PrayerItem("fajr", "Fajr", 1, "05:15 AM • Dawn", basePoints = 15, isStruggling = true),
        PrayerItem("dhuhr", "Dhuhr", 2, "12:30 PM • Midday", basePoints = 5),
        PrayerItem("asr", "Asr", 3, "03:45 PM • Afternoon", basePoints = 10),
        PrayerItem("maghrib", "Maghrib", 4, "06:45 PM • Sunset", basePoints = 5),
        PrayerItem("isha", "Isha", 5, "08:15 PM • Night", basePoints = 10)
    )

    private val _prayersList = MutableStateFlow<List<PrayerItem>>(defaultPrayers)
    val prayersList: StateFlow<List<PrayerItem>> = _prayersList.asStateFlow()

    // Master pool of daily quests for randomization
    private val masterQuestPool = listOf(
        DailyQuest("q_zakat1", "Pay or Log Zakat / Sadaqah", QuestCategory.CHARITY_ZAKAT, 30, "Give charity to help those in need and purify your wealth"),
        DailyQuest("q_zakat2", "Feed a Fasting Person or Needy", QuestCategory.CHARITY_ZAKAT, 25, "Provide food or water to someone in your community"),
        DailyQuest("q_quran1", "Recite Surah Al-Mulk", QuestCategory.QURAN_DHIKR, 20, "Protection before sleeping; recite with reflection"),
        DailyQuest("q_dhikr1", "Send 100 Salawat upon the Prophet", QuestCategory.QURAN_DHIKR, 15, "Allahumma Salli Ala Muhammad x100"),
        DailyQuest("q_quran2", "Read 2 Pages of Quran", QuestCategory.QURAN_DHIKR, 20, "Spend focused minutes reading the Holy Quran"),
        DailyQuest("q_deed1", "Help a Family Member or Neighbor", QuestCategory.DEEN_LIFESTYLE, 15, "Perform an act of kindness with a warm smile"),
        DailyQuest("q_dhikr2", "Morning & Evening Adhkar", QuestCategory.QURAN_DHIKR, 15, "Recite morning or evening supplications for protection"),
        DailyQuest("q_zakat3", "Calculate Annual Zakat Obligation", QuestCategory.CHARITY_ZAKAT, 35, "Check nisab threshold & compute 2.5% wealth zakat")
    )

    private val _dailyQuests = MutableStateFlow<List<DailyQuest>>(emptyList())
    val dailyQuests: StateFlow<List<DailyQuest>> = _dailyQuests.asStateFlow()

    private val _remainingScreenTimeMinutes = MutableStateFlow(45)
    val remainingScreenTimeMinutes: StateFlow<Int> = _remainingScreenTimeMinutes.asStateFlow()

    private val _socialMediaLimitMinutes = MutableStateFlow(30)
    val socialMediaLimitMinutes: StateFlow<Int> = _socialMediaLimitMinutes.asStateFlow()

    private val _generalAppsLimitMinutes = MutableStateFlow(60)
    val generalAppsLimitMinutes: StateFlow<Int> = _generalAppsLimitMinutes.asStateFlow()

    // Real-time IRL Screen Time Tracking State
    private val _liveScreenTimeSeconds = MutableStateFlow(0L)
    val liveScreenTimeSeconds: StateFlow<Long> = _liveScreenTimeSeconds.asStateFlow()

    private val _isIRLTrackingActive = MutableStateFlow(true)
    val isIRLTrackingActive: StateFlow<Boolean> = _isIRLTrackingActive.asStateFlow()

    // Global Error & Retry Engine State
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError.asStateFlow()

    private var irlTickerJob: Job? = null

    init {
        checkPermissions()
        refreshInstalledApps()
        calculateTotalScreenTime()
        randomizeDailyQuests()
        recalculatePurityIndex()
        startIRLTracker()
    }

    /**
     * Set Daily Screen Time Limits for Social Media & General Apps
     */
    fun setDailyLimits(socialMins: Int, generalMins: Int) {
        _socialMediaLimitMinutes.value = socialMins
        _generalAppsLimitMinutes.value = generalMins
        val totalLimit = socialMins + generalMins
        val usedMinutes = (_liveScreenTimeSeconds.value / 60L).toInt()
        val remaining = (totalLimit - usedMinutes).coerceAtLeast(0)
        _remainingScreenTimeMinutes.value = remaining
    }

    /**
     * Start continuous real-time IRL Screen Time polling loop updating every second
     */
    fun startIRLTracker() {
        irlTickerJob?.cancel()
        irlTickerJob = viewModelScope.launch(Dispatchers.IO) {
            var loopCount = 0
            var baseHardwareSecs = 0L

            while (true) {
                if (loopCount % 5 == 0) {
                    val context = getApplication<Application>().applicationContext
                    val usageMap = UsageTracker.getTodayUsageStats(context)
                    val totalMs = usageMap.values.sumOf { it }
                    baseHardwareSecs = totalMs / 1000L
                    _liveScreenTimeSeconds.value = baseHardwareSecs
                } else {
                    _liveScreenTimeSeconds.value += 1L
                }

                val currentSec = _liveScreenTimeSeconds.value
                _totalScreenTimeMinutes.value = currentSec / 60L

                val totalLimit = _socialMediaLimitMinutes.value + _generalAppsLimitMinutes.value
                val usedMin = (currentSec / 60L).toInt()
                _remainingScreenTimeMinutes.value = (totalLimit - usedMin).coerceAtLeast(0)

                loopCount++
                delay(1000L) // Tick every second
            }
        }
    }

    fun clearError() {
        _hasError.value = false
        _errorMessage.value = null
    }

    fun triggerTestError(message: String = "Deen Shield Telemetry Interrupted: Real-time sensor sync failed.") {
        _errorMessage.value = message
        _hasError.value = true
    }

    fun retryEngine() {
        viewModelScope.launch {
            _isLoadingApps.value = true
            clearError()
            delay(1200L) // Simulate engine re-initialization
            refreshInstalledApps()
            calculateTotalScreenTime()
            _isLoadingApps.value = false
        }
    }

    /**
     * Toggle Prayer Completion and award points (including struggle bonus if active)
     */
    fun togglePrayerCompletion(prayerId: String) {
        val currentList = _prayersList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == prayerId }
        if (index != -1) {
            val item = currentList[index]
            val newState = !item.isCompleted
            val pointsDelta = item.totalPoints

            currentList[index] = item.copy(isCompleted = newState)
            _prayersList.value = currentList

            if (newState) {
                _coins.value += pointsDelta
            } else {
                _coins.value = (_coins.value - pointsDelta).coerceAtLeast(0)
            }
            recalculatePurityIndex()
        }
    }

    /**
     * Toggle Struggle Mode for a Prayer (e.g. struggling to perform Fajr).
     * Adds an extra Struggle Bonus (+10 pts) to motivate the user!
     */
    fun togglePrayerStruggle(prayerId: String) {
        val currentList = _prayersList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == prayerId }
        if (index != -1) {
            val item = currentList[index]
            val newStruggle = !item.isStruggling
            currentList[index] = item.copy(isStruggling = newStruggle)
            _prayersList.value = currentList
        }
    }

    /**
     * Toggle Daily Quest Completion
     */
    fun toggleQuestCompletion(questId: String) {
        val currentList = _dailyQuests.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == questId }
        if (index != -1) {
            val quest = currentList[index]
            val newState = !quest.isCompleted
            currentList[index] = quest.copy(isCompleted = newState)
            _dailyQuests.value = currentList

            if (newState) {
                _coins.value += quest.points
            } else {
                _coins.value = (_coins.value - quest.points).coerceAtLeast(0)
            }
            recalculatePurityIndex()
        }
    }

    /**
     * Claim Daily Quest Points directly
     */
    fun claimQuestPoints(questId: String) {
        val currentList = _dailyQuests.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == questId }
        if (index != -1 && !currentList[index].isCompleted) {
            val quest = currentList[index]
            currentList[index] = quest.copy(isCompleted = true)
            _dailyQuests.value = currentList
            _coins.value += quest.points
            recalculatePurityIndex()
        }
    }

    /**
     * Claim all pending daily quest points with one tap
     */
    fun claimAllPendingQuests() {
        val currentQuests = _dailyQuests.value.toMutableList()
        var addedPoints = 0
        for (i in currentQuests.indices) {
            if (!currentQuests[i].isCompleted) {
                currentQuests[i] = currentQuests[i].copy(isCompleted = true)
                addedPoints += currentQuests[i].points
            }
        }
        _dailyQuests.value = currentQuests
        _coins.value += addedPoints
        recalculatePurityIndex()
    }

    /**
     * Randomize tasks daily (select 4 random quests from the master pool, ensuring Zakat/Charity is included)
     */
    fun randomizeDailyQuests() {
        val zakatQuests = masterQuestPool.filter { it.category == QuestCategory.CHARITY_ZAKAT }.shuffled()
        val otherQuests = masterQuestPool.filter { it.category != QuestCategory.CHARITY_ZAKAT }.shuffled()

        val selected = mutableListOf<DailyQuest>()
        if (zakatQuests.isNotEmpty()) {
            selected.add(zakatQuests.first())
        }
        selected.addAll(otherQuests.take(3))

        _dailyQuests.value = selected.shuffled()
    }

    /**
     * Log a Zakat or Sadaqah donation to earn points directly.
     * $1 donated = 1 point, capped at a maximum of 100 points.
     */
    fun logZakatDonation(amount: Double) {
        if (amount <= 0) return
        _totalZakatDonated.value += amount
        // Points calculation: $1 = 1 point, maximum 100 points
        val awardedPoints = amount.toInt().coerceIn(1, 100)
        _coins.value += awardedPoints
        recalculatePurityIndex()
    }

    private fun recalculatePurityIndex() {
        val completedPrayers = _prayersList.value.count { it.isCompleted }
        val completedQuests = _dailyQuests.value.count { it.isCompleted }

        // Formula based on real habits: 60% prayer weight + 40% quest weight
        val prayerRatio = completedPrayers / 5.0
        val questRatio = if (_dailyQuests.value.isNotEmpty()) completedQuests.toDouble() / _dailyQuests.value.size else 0.0

        val newPurity = ((prayerRatio * 60) + (questRatio * 40)).toInt().coerceIn(0, 100)
        _purityIndex.value = newPurity
    }

    fun incrementResistedCount() {
        _resistedCountToday.value += 1
        _coins.value += 15
        _purityIndex.value = (_purityIndex.value + 1).coerceAtMost(100)
        // Every 3 resistance victories increases streak
        if (_resistedCountToday.value % 3 == 0) {
            _streakDays.value += 1
        }
    }

    fun setPanicMode(active: Boolean) {
        _isPanicModeActive.value = active
    }

    init {
        checkPermissions()
        refreshInstalledApps()
        calculateTotalScreenTime()
    }

    fun checkPermissions() {
        val context = getApplication<Application>().applicationContext
        val status = PermissionStatus(
            hasUsageStats = PermissionManager.hasUsageStatsPermission(context),
            hasOverlay = PermissionManager.hasOverlayPermission(context),
            hasAccessibility = PermissionManager.hasAccessibilityPermission(context),
            isIgnoringBattery = PermissionManager.isIgnoringBatteryOptimizations(context)
        )
        _permissions.value = status

        if (status.hasUsageStats) {
            calculateTotalScreenTime()
        }
    }

    fun startForegroundServiceIfPermissionsGranted(context: Context) {
        if (PermissionManager.hasUsageStatsPermission(context)) {
            AppBlockerForegroundService.startService(context)
        }
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            _isLoadingApps.value = true
            try {
                val list = repository.getInstalledApps()
                _installedApps.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingApps.value = false
            }
        }
    }

    fun calculateTotalScreenTime() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val usageMap = UsageTracker.getTodayUsageStats(context)
            val totalSeconds = usageMap.values.sumOf { it / 1000L }
            _totalScreenTimeMinutes.value = totalSeconds / 60L
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleAppBlock(item: InstalledAppItem, enableBlock: Boolean) {
        // Immediate in-memory state update for zero lag
        val currentList = _installedApps.value.toMutableList()
        val index = currentList.indexOfFirst { it.packageName == item.packageName }
        if (index != -1) {
            currentList[index] = currentList[index].copy(isBlocked = enableBlock)
            _installedApps.value = currentList
        }

        viewModelScope.launch {
            repository.toggleAppBlockStatus(item, enableBlock)
            startForegroundServiceIfPermissionsGranted(getApplication())
        }
    }

    fun updateAppLimit(packageName: String, appName: String, limitMinutes: Int, isInstantBlocked: Boolean) {
        // Immediate in-memory state update for zero lag
        val currentList = _installedApps.value.toMutableList()
        val index = currentList.indexOfFirst { it.packageName == packageName }
        if (index != -1) {
            currentList[index] = currentList[index].copy(
                dailyLimitMinutes = limitMinutes,
                isInstantBlocked = isInstantBlocked
            )
            _installedApps.value = currentList
        }

        viewModelScope.launch {
            repository.updateAppLimit(packageName, appName, limitMinutes, isInstantBlocked)
            startForegroundServiceIfPermissionsGranted(getApplication())
        }
    }

    fun startFocusTimer(title: String, durationMinutes: Int) {
        timerJob?.cancel()
        val totalSec = durationMinutes * 60L
        _focusTimer.value = FocusTimerState(
            isActive = true,
            title = title,
            durationMinutes = durationMinutes,
            secondsRemaining = totalSec,
            progress = 1f
        )

        viewModelScope.launch {
            repository.startFocusSession(title, durationMinutes, isStrictLock = true)
        }

        timerJob = viewModelScope.launch {
            var currentSec = totalSec
            while (currentSec > 0) {
                delay(1000L)
                currentSec--
                val prog = currentSec.toFloat() / totalSec.toFloat()
                _focusTimer.value = _focusTimer.value.copy(
                    secondsRemaining = currentSec,
                    progress = prog
                )
            }
            _focusTimer.value = _focusTimer.value.copy(
                isActive = false,
                secondsRemaining = 0,
                progress = 0f
            )
        }
    }

    fun stopFocusTimer() {
        timerJob?.cancel()
        _focusTimer.value = FocusTimerState(isActive = false)
    }

    fun generateAICoachInsight() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val topApps = _installedApps.value.take(3).joinToString { "${it.appName} (${it.todayUsageSeconds / 60}m)" }
            val text = repository.getAICoachInsights(_totalScreenTimeMinutes.value, topApps)
            _aiCoachText.value = text
            _isAiLoading.value = false
        }
    }
}
