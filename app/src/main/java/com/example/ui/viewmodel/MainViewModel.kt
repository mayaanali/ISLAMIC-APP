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
import com.example.data.LeaderboardRegion
import com.example.data.LeaderboardUser
import com.example.data.PrayerItem
import com.example.data.QuestCategory
import com.example.data.SpiritualLeague
import com.example.data.UserArchetype
import com.example.service.AppBlockerForegroundService
import com.example.utils.PermissionManager
import com.example.utils.UsageTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import com.example.data.PointLedgerEntry
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PermissionStatus(
    val hasUsageStats: Boolean = false,
    val hasOverlay: Boolean = false,
    val hasAccessibility: Boolean = false,
    val isIgnoringBattery: Boolean = false,
    val hasLocation: Boolean = false,
    val hasNotification: Boolean = false
) {
    val coreGrantedCount: Int get() = listOf(hasUsageStats, hasOverlay, hasAccessibility, isIgnoringBattery, hasLocation, hasNotification).count { it }
    val totalCount: Int get() = 6
    val allGranted: Boolean get() = coreGrantedCount == totalCount
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

    private val prefs = application.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    // Dark Mode State with SharedPreferences Persistence (Item 1)
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("pref_dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Onboarding & Diagnostic State
    private val _isOnboardingCompleted = MutableStateFlow(prefs.getBoolean("pref_onboarding_completed", true))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val savedArchetypeStr = prefs.getString("pref_user_archetype", UserArchetype.SHIELD_SEEKER.name) ?: UserArchetype.SHIELD_SEEKER.name
    private val _userArchetype = MutableStateFlow(
        try {
            UserArchetype.valueOf(savedArchetypeStr)
        } catch (e: Exception) {
            UserArchetype.SHIELD_SEEKER
        }
    )
    val userArchetype: StateFlow<UserArchetype> = _userArchetype.asStateFlow()

    // Map of Question ID (1..6) -> Pair(OptionKey ("A".."E"), CustomText)
    private val _diagnosticAnswers = MutableStateFlow<Map<Int, Pair<String, String>>>(emptyMap())
    val diagnosticAnswers: StateFlow<Map<Int, Pair<String, String>>> = _diagnosticAnswers.asStateFlow()

    private val _nightShieldActive = MutableStateFlow(prefs.getBoolean("pref_night_shield", true))
    val nightShieldActive: StateFlow<Boolean> = _nightShieldActive.asStateFlow()

    fun toggleDarkMode() {
        val newMode = !_isDarkMode.value
        _isDarkMode.value = newMode
        prefs.edit().putBoolean("pref_dark_mode", newMode).apply()
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("pref_dark_mode", enabled).apply()
    }

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

    // Location & Mosque Management State
    val locationState = com.example.utils.LocationAndMosqueManager.locationState
    val calculatedPrayers = com.example.utils.LocationAndMosqueManager.calculatedPrayers
    val nearbyMosques = com.example.utils.LocationAndMosqueManager.nearbyMosques

    private var irlTickerJob: Job? = null

    init {
        checkPermissions()
        refreshInstalledApps()
        calculateTotalScreenTime()
        randomizeDailyQuests()
        recalculatePurityIndex()
        startIRLTracker()
        startLeaderboardLiveSimulation()
    }

    fun updateLocationState(newState: com.example.utils.UserLocationState) {
        com.example.utils.LocationAndMosqueManager.updateLocation(newState)
    }

    fun setCalculationMethod(method: com.example.utils.CalculationMethod) {
        com.example.utils.LocationAndMosqueManager.setCalculationMethod(method)
    }

    fun toggleMosqueBookmark(mosqueId: String) {
        com.example.utils.LocationAndMosqueManager.toggleBookmarkMosque(mosqueId)
    }

    fun startRealGpsTracking(context: Context) {
        com.example.utils.LocationAndMosqueManager.startDeviceLocationUpdates(context)
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
     * Tapping the 3D Qalb heart triggers a dhikr / spiritual pulse (+1 pt).
     */
    fun recordHeartTap() {
        _coins.value += 1
        recalculatePurityIndex()
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
            isIgnoringBattery = PermissionManager.isIgnoringBatteryOptimizations(context),
            hasLocation = PermissionManager.hasLocationPermission(context),
            hasNotification = PermissionManager.hasNotificationPermission(context)
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

    /**
     * Record an answer for a diagnostic onboarding question
     */
    fun saveDiagnosticAnswer(questionId: Int, optionKey: String, customText: String = "") {
        val current = _diagnosticAnswers.value.toMutableMap()
        current[questionId] = Pair(optionKey, customText)
        _diagnosticAnswers.value = current
    }

    /**
     * Completes the diagnostic questionnaire, calculates the tailored user archetype,
     * configures the app's rules and gamification, and saves state to SharedPreferences.
     */
    fun completeDiagnosticAndApplyIntervention(): UserArchetype {
        val answers = _diagnosticAnswers.value

        val q1 = answers[1]?.first ?: "B"
        val q2 = answers[2]?.first ?: "B"
        val q3 = answers[3]?.first ?: "A"
        val q4 = answers[4]?.first ?: "A"
        val q5 = answers[5]?.first ?: "B"
        val q6 = answers[6]?.first ?: "B"

        // Determine Archetype based on diagnostic triggers
        val calculatedArchetype = when {
            // Shield Seeker: Flags hidden sins, late night vulnerability, or desire struggles
            q3 == "C" || q4 == "A" || q5 == "B" || q6 == "B" -> UserArchetype.SHIELD_SEEKER

            // Anchorless: Guilt, disconnected, rarely praying, missed prayers
            q1 == "C" || q1 == "D" || q2 == "C" || q2 == "D" || q4 == "D" -> UserArchetype.ANCHORLESS

            // Distracted: Wasting time, environment, inconsistency, screen time struggles
            q3 == "A" || q5 == "A" || q5 == "D" || q1 == "B" -> UserArchetype.DISTRACTED

            // Ihsan Striver: Seeking spiritual excellence, deep knowledge, charity
            else -> UserArchetype.IHSAN_STRIVER
        }

        _userArchetype.value = calculatedArchetype
        _isOnboardingCompleted.value = true

        // Apply tailored interventions to app rules
        when (calculatedArchetype) {
            UserArchetype.SHIELD_SEEKER -> {
                _nightShieldActive.value = true
                _socialMediaLimitMinutes.value = 30
                _generalAppsLimitMinutes.value = 45
            }
            UserArchetype.ANCHORLESS -> {
                _nightShieldActive.value = true
                // Boost Fajr struggle points for motivation
                val prayers = _prayersList.value.toMutableList()
                val fajrIdx = prayers.indexOfFirst { it.id == "fajr" }
                if (fajrIdx != -1) {
                    prayers[fajrIdx] = prayers[fajrIdx].copy(isStruggling = true, basePoints = 20)
                    _prayersList.value = prayers
                }
            }
            UserArchetype.DISTRACTED -> {
                _nightShieldActive.value = false
                _socialMediaLimitMinutes.value = 45
                _generalAppsLimitMinutes.value = 60
            }
            UserArchetype.IHSAN_STRIVER -> {
                _nightShieldActive.value = false
                _socialMediaLimitMinutes.value = 60
            }
        }

        prefs.edit()
            .putBoolean("pref_onboarding_completed", true)
            .putString("pref_user_archetype", calculatedArchetype.name)
            .putBoolean("pref_night_shield", _nightShieldActive.value)
            .apply()

        return calculatedArchetype
    }

    /**
     * Allows retaking the diagnostic questionnaire at any time
     */
    fun restartOnboarding() {
        _diagnosticAnswers.value = emptyMap()
        _isOnboardingCompleted.value = false
        prefs.edit().putBoolean("pref_onboarding_completed", false).apply()
    }

    fun toggleNightShield(enabled: Boolean) {
        _nightShieldActive.value = enabled
        prefs.edit().putBoolean("pref_night_shield", enabled).apply()
    }

    // =========================================================
    // PROFILE & JOURNEY PERSONA
    // =========================================================
    private val _userName = MutableStateFlow(prefs.getString("pref_user_name", "Jibril Dawood") ?: "Jibril Dawood")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userHandle = MutableStateFlow(prefs.getString("pref_user_handle", "@jibril_d") ?: "@jibril_d")
    val userHandle: StateFlow<String> = _userHandle.asStateFlow()

    private val _userBio = MutableStateFlow(
        prefs.getString("pref_user_bio", "Seeking Ihsan, guarding my gaze, and dedicating my focus for the sake of Allah.")
            ?: "Seeking Ihsan, guarding my gaze, and dedicating my focus for the sake of Allah."
    )
    val userBio: StateFlow<String> = _userBio.asStateFlow()

    private val _userAvatarStyle = MutableStateFlow(prefs.getString("pref_user_avatar", "avatar_1") ?: "avatar_1")
    val userAvatarStyle: StateFlow<String> = _userAvatarStyle.asStateFlow()

    private val _journeyPersonaMode = MutableStateFlow(
        prefs.getString("pref_journey_persona", "Muslim (Strengthening Deen)") ?: "Muslim (Strengthening Deen)"
    )
    val journeyPersonaMode: StateFlow<String> = _journeyPersonaMode.asStateFlow()

    fun updateUserProfile(name: String, handle: String, bio: String, avatar: String) {
        _userName.value = name.trim().ifEmpty { "Jibril Dawood" }
        _userHandle.value = handle.trim().ifEmpty { "@jibril_d" }
        _userBio.value = bio.trim()
        _userAvatarStyle.value = avatar

        prefs.edit()
            .putString("pref_user_name", _userName.value)
            .putString("pref_user_handle", _userHandle.value)
            .putString("pref_user_bio", _userBio.value)
            .putString("pref_user_avatar", _userAvatarStyle.value)
            .apply()
    }

    fun setJourneyPersonaMode(mode: String) {
        _journeyPersonaMode.value = mode
        prefs.edit().putString("pref_journey_persona", mode).apply()
    }

    // =========================================================
    // SPIRITUAL & PRAYER CALCULATION
    // =========================================================
    private val _calculationMethodName = MutableStateFlow(
        prefs.getString("pref_calc_method_name", "Muslim World League (MWL)") ?: "Muslim World League (MWL)"
    )
    val calculationMethodName: StateFlow<String> = _calculationMethodName.asStateFlow()

    private val _asrJuristicSchool = MutableStateFlow(
        prefs.getString("pref_asr_school", "Standard (Shafi'i, Maliki, Hanbali)") ?: "Standard (Shafi'i, Maliki, Hanbali)"
    )
    val asrJuristicSchool: StateFlow<String> = _asrJuristicSchool.asStateFlow()

    private val _prayerNotificationSound = MutableStateFlow(
        prefs.getString("pref_prayer_sound", "Full Makkah Adhan") ?: "Full Makkah Adhan"
    )
    val prayerNotificationSound: StateFlow<String> = _prayerNotificationSound.asStateFlow()

    private val _prePrayerReminderMinutes = MutableStateFlow(prefs.getInt("pref_pre_reminder_mins", 15))
    val prePrayerReminderMinutes: StateFlow<Int> = _prePrayerReminderMinutes.asStateFlow()

    private val _isQiblaCalibrated = MutableStateFlow(prefs.getBoolean("pref_qibla_calibrated", true))
    val isQiblaCalibrated: StateFlow<Boolean> = _isQiblaCalibrated.asStateFlow()

    fun setCalculationMethodByName(name: String) {
        _calculationMethodName.value = name
        prefs.edit().putString("pref_calc_method_name", name).apply()

        // Sync with LocationAndMosqueManager
        val method = when {
            name.contains("ISNA", ignoreCase = true) -> com.example.utils.CalculationMethod.ISNA
            name.contains("MWL", ignoreCase = true) || name.contains("Muslim World", ignoreCase = true) -> com.example.utils.CalculationMethod.MWL
            name.contains("Umm", ignoreCase = true) -> com.example.utils.CalculationMethod.UMM_AL_QURA
            name.contains("Egypt", ignoreCase = true) -> com.example.utils.CalculationMethod.EGYPTIAN
            else -> com.example.utils.CalculationMethod.KARACHI
        }
        com.example.utils.LocationAndMosqueManager.setCalculationMethod(method)
    }

    fun setAsrJuristicSchool(school: String) {
        _asrJuristicSchool.value = school
        prefs.edit().putString("pref_asr_school", school).apply()
    }

    fun setPrayerNotificationSound(sound: String) {
        _prayerNotificationSound.value = sound
        prefs.edit().putString("pref_prayer_sound", sound).apply()
    }

    fun setPrePrayerReminderMinutes(minutes: Int) {
        _prePrayerReminderMinutes.value = minutes
        prefs.edit().putInt("pref_pre_reminder_mins", minutes).apply()
    }

    fun calibrateQibla() {
        _isQiblaCalibrated.value = true
        prefs.edit().putBoolean("pref_qibla_calibrated", true).apply()
    }

    // =========================================================
    // SHIELD & FOCUS (APP BLOCKER & SIN PROTECTION)
    // =========================================================
    private val _frictionCooldownRule = MutableStateFlow(
        prefs.getString("pref_friction_rule", "5-Minute Waiting Timer") ?: "5-Minute Waiting Timer"
    )
    val frictionCooldownRule: StateFlow<String> = _frictionCooldownRule.asStateFlow()

    private val _discreteSitrMode = MutableStateFlow(prefs.getBoolean("pref_discrete_sitr_mode", false))
    val discreteSitrMode: StateFlow<Boolean> = _discreteSitrMode.asStateFlow()

    fun setFrictionCooldownRule(rule: String) {
        _frictionCooldownRule.value = rule
        prefs.edit().putString("pref_friction_rule", rule).apply()
    }

    fun toggleDiscreteSitrMode(enabled: Boolean) {
        _discreteSitrMode.value = enabled
        prefs.edit().putBoolean("pref_discrete_sitr_mode", enabled).apply()
    }

    // =========================================================
    // GAMIFICATION & LEADERBOARD
    // =========================================================
    private val _leaderboardAnonymousMode = MutableStateFlow(prefs.getBoolean("pref_leaderboard_anon", false))
    val leaderboardAnonymousMode: StateFlow<Boolean> = _leaderboardAnonymousMode.asStateFlow()

    private val _leaderboardAlias = MutableStateFlow(
        prefs.getString("pref_leaderboard_alias", "Mujahid #4812") ?: "Mujahid #4812"
    )
    val leaderboardAlias: StateFlow<String> = _leaderboardAlias.asStateFlow()

    private val _streakFreezeCount = MutableStateFlow(prefs.getInt("pref_streak_freeze_count", 2))
    val streakFreezeCount: StateFlow<Int> = _streakFreezeCount.asStateFlow()

    private val _streakFreezeActive = MutableStateFlow(prefs.getBoolean("pref_streak_freeze_active", false))
    val streakFreezeActive: StateFlow<Boolean> = _streakFreezeActive.asStateFlow()

    private val _pointLedger = MutableStateFlow<List<PointLedgerEntry>>(
        listOf(
            PointLedgerEntry("1", "Completed Fajr on time", 20, true, "Today 05:22 AM", "Salah", "🕌"),
            PointLedgerEntry("2", "Resisted Night Sin Urge (20-min Shield)", 35, true, "Yesterday 11:45 PM", "Shield", "🛡️"),
            PointLedgerEntry("3", "Donated Zakat in Marketplace", 100, true, "2 days ago", "Charity", "🪙"),
            PointLedgerEntry("4", "Redeemed 'Late Night Shield Booster'", 50, false, "3 days ago", "Marketplace", "✨"),
            PointLedgerEntry("5", "Read Surah Al-Kahf Friday Quest", 30, true, "Friday", "Quran", "📖"),
            PointLedgerEntry("6", "Completed Dhuhr at Mosque", 15, true, "Today 01:15 PM", "Salah", "🕌")
        )
    )
    val pointLedger: StateFlow<List<PointLedgerEntry>> = _pointLedger.asStateFlow()

    // =========================================================
    // REAL-TIME LEADERBOARD ENGINE
    // =========================================================
    private val _selectedLeaderboardRegion = MutableStateFlow(LeaderboardRegion.GLOBAL)
    val selectedLeaderboardRegion: StateFlow<LeaderboardRegion> = _selectedLeaderboardRegion.asStateFlow()

    private val _selectedLeaderboardTimeframe = MutableStateFlow("Live Realtime")
    val selectedLeaderboardTimeframe: StateFlow<String> = _selectedLeaderboardTimeframe.asStateFlow()

    private val baseCompetitors = listOf(
        LeaderboardUser(1, "Tariq Al-Mansoor", "@tariq_m", false, 2840, 98, 42, LeaderboardRegion.MENA, SpiritualLeague.DIAMOND_TAQWA, "", 0, true, "Completed Tahajjud & Fajr"),
        LeaderboardUser(2, "Maryam Khan", "@maryam_k", false, 2690, 96, 38, LeaderboardRegion.SOUTH_ASIA, SpiritualLeague.DIAMOND_TAQWA, "", 1, true, "Logged 20m Quran recitation"),
        LeaderboardUser(3, "Zayd Bilal", "@zayd_b", false, 2410, 94, 30, LeaderboardRegion.NORTH_AMERICA, SpiritualLeague.DIAMOND_TAQWA, "", -1, true, "Donated Sadaqah ($50)"),
        LeaderboardUser(4, "Fatima Zahra", "@fatima_z", false, 2180, 92, 27, LeaderboardRegion.MENA, SpiritualLeague.DIAMOND_TAQWA, "", 2, true, "Guarded gaze & night shield"),
        LeaderboardUser(5, "Hamza Thorne", "@hamza_uk", false, 1950, 89, 21, LeaderboardRegion.EUROPE, SpiritualLeague.EMERALD_MURAQABAH, "", 0, true, "Prayed Dhuhr at Central Mosque"),
        LeaderboardUser(6, "Amina Yusuf", "@amina_y", false, 1820, 87, 19, LeaderboardRegion.AFRICA, SpiritualLeague.EMERALD_MURAQABAH, "", 3, true, "Completed morning Adhkar"),
        LeaderboardUser(7, "Ibrahim Malik", "@ibrahim_m", false, 1640, 85, 17, LeaderboardRegion.NORTH_AMERICA, SpiritualLeague.EMERALD_MURAQABAH, "", -1, true, "Completed 45m deep focus"),
        LeaderboardUser(8, "Nurul Huda", "@nurul_h", false, 1490, 84, 15, LeaderboardRegion.SOUTHEAST_ASIA, SpiritualLeague.EMERALD_MURAQABAH, "", 1, true, "Shielded against late-night triggers"),
        LeaderboardUser(9, "Khalid Omar", "@khalid_o", false, 1310, 81, 14, LeaderboardRegion.CENTRAL_ASIA, SpiritualLeague.EMERALD_MURAQABAH, "", 0, true, "Logged Asr on time"),
        LeaderboardUser(10, "Khadija Ali", "@khadija_a", false, 1150, 78, 12, LeaderboardRegion.EUROPE, SpiritualLeague.RUBY_SABR, "", 2, true, "Read Surah Al-Mulk")
    )

    private val _competitorsList = MutableStateFlow<List<LeaderboardUser>>(baseCompetitors)

    val currentLeaderboard: StateFlow<List<LeaderboardUser>> = kotlinx.coroutines.flow.combine(
        _competitorsList,
        _selectedLeaderboardRegion,
        _coins,
        _purityIndex,
        _streakDays,
        _userName,
        _userHandle,
        _leaderboardAlias,
        _leaderboardAnonymousMode
    ) { params ->
        val competitors = params[0] as List<LeaderboardUser>
        val region = params[1] as LeaderboardRegion
        val userCoins = params[2] as Int
        val userPurity = params[3] as Int
        val userStreak = params[4] as Int
        val uName = params[5] as String
        val uHandle = params[6] as String
        val uAlias = params[7] as String
        val isAnon = params[8] as Boolean

        val currentUserLeague = when {
            userCoins >= 2000 && userPurity >= 90 -> SpiritualLeague.DIAMOND_TAQWA
            userCoins >= 1200 && userPurity >= 80 -> SpiritualLeague.EMERALD_MURAQABAH
            userCoins >= 600 && userPurity >= 60 -> SpiritualLeague.RUBY_SABR
            else -> SpiritualLeague.SAPPHIRE_IKHLAS
        }

        val currentUserEntry = LeaderboardUser(
            rank = 0, // Computed dynamically
            name = if (isAnon) uAlias else uName,
            alias = if (isAnon) "Anonymous Believer" else uHandle,
            isCurrentUser = true,
            points = userCoins,
            shieldPurityPercent = userPurity,
            streakDays = userStreak,
            region = LeaderboardRegion.GLOBAL,
            league = currentUserLeague,
            rankDelta = 2,
            isOnline = true,
            recentAction = "Guarding purity & tracking Salah"
        )

        val filteredCompetitors = if (region == LeaderboardRegion.GLOBAL) {
            competitors
        } else {
            competitors.filter { it.region == region }
        }

        val combined = (filteredCompetitors + currentUserEntry)
            .sortedWith(
                compareByDescending<LeaderboardUser> { it.points }
                    .thenByDescending { it.shieldPurityPercent }
                    .thenByDescending { it.streakDays }
            )

        combined.mapIndexed { index, user ->
            user.copy(rank = index + 1)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private var leaderboardSimJob: Job? = null

    fun startLeaderboardLiveSimulation() {
        leaderboardSimJob?.cancel()
        leaderboardSimJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(8000L) // Real-time pulse every 8 seconds
                val current = _competitorsList.value.toMutableList()
                val randomIndex = (0 until current.size).random()
                val target = current[randomIndex]
                val pointInc = listOf(5, 10, 15, 20).random()
                val updated = target.copy(
                    points = target.points + pointInc,
                    recentAction = listOf(
                        "Resisted night trigger (+${pointInc} XP)",
                        "Logged Salah on time (+${pointInc} XP)",
                        "Completed Quran reflection",
                        "Recited Morning Adhkar",
                        "Maintained 100% Shield Purity"
                    ).random()
                )
                current[randomIndex] = updated
                _competitorsList.value = current
            }
        }
    }

    fun setLeaderboardRegion(region: LeaderboardRegion) {
        _selectedLeaderboardRegion.value = region
    }

    fun setLeaderboardTimeframe(timeframe: String) {
        _selectedLeaderboardTimeframe.value = timeframe
    }

    // =========================================================
    // LATE-NIGHT FRICTION ENGINE & SITR PRIVACY HELPERS
    // =========================================================
    private val _isSensitiveLogsUnlocked = MutableStateFlow(false)
    val isSensitiveLogsUnlocked: StateFlow<Boolean> = _isSensitiveLogsUnlocked.asStateFlow()

    private val _isBiometricLockEnabled = MutableStateFlow(prefs.getBoolean("pref_biometric_logs", true))
    val isBiometricLockEnabled: StateFlow<Boolean> = _isBiometricLockEnabled.asStateFlow()

    fun toggleBiometricLock(enabled: Boolean) {
        _isBiometricLockEnabled.value = enabled
        prefs.edit().putBoolean("pref_biometric_logs", enabled).apply()
    }

    fun unlockSensitiveLogsWithPasscode() {
        _isSensitiveLogsUnlocked.value = true
    }

    fun lockSensitiveLogs() {
        _isSensitiveLogsUnlocked.value = false
    }

    /**
     * Gamified Escape Hatch:
     * User clicks "Cancel Unlock & Earn +50 Points"
     * 1. Awards +50 Points immediately.
     * 2. Logs positive entry in point ledger.
     * 3. Increases shield purity index.
     * 4. Keeps streak safe.
     */
    fun awardEscapeHatchPoints() {
        _coins.value += 50
        _resistedCountToday.value += 1
        _purityIndex.value = (_purityIndex.value + 4).coerceAtMost(100)

        recordLedgerTransaction(
            title = "Resilience Victory: Refused Late-Night Unlock",
            points = 50,
            isCredit = true,
            category = "Shield Resilience",
            iconEmoji = "🛡️"
        )
        recalculatePurityIndex()
    }

    /**
     * Retrieves the user's primary goal formulated during onboarding for The Mirror step
     */
    fun getOnboardingGoalSummary(): String {
        val q6Answer = _diagnosticAnswers.value[6]?.first
        val customText = _diagnosticAnswers.value[6]?.second ?: ""
        if (customText.isNotBlank()) return customText

        return when (q6Answer) {
            "A" -> "unbreakable consistency in five daily prayers and experiencing true Khushu"
            "B" -> "breaking free from destructive digital triggers and attaining pure peace of heart"
            "C" -> "gaining a deeper understanding of Islam and connecting faith with daily peace"
            "D" -> "building a lifestyle of constant good deeds, charity, and barakah"
            else -> "break free from late-night habits and strengthen your connection with Allah"
        }
    }

    fun toggleLeaderboardAnonymous(enabled: Boolean) {
        _leaderboardAnonymousMode.value = enabled
        prefs.edit().putBoolean("pref_leaderboard_anon", enabled).apply()
    }

    fun setLeaderboardAlias(alias: String) {
        _leaderboardAlias.value = alias.trim().ifEmpty { "Mujahid #4812" }
        prefs.edit().putString("pref_leaderboard_alias", _leaderboardAlias.value).apply()
    }

    fun useStreakFreeze(): Boolean {
        if (_streakFreezeCount.value > 0 && !_streakFreezeActive.value) {
            _streakFreezeCount.value -= 1
            _streakFreezeActive.value = true
            prefs.edit()
                .putInt("pref_streak_freeze_count", _streakFreezeCount.value)
                .putBoolean("pref_streak_freeze_active", true)
                .apply()
            return true
        }
        return false
    }

    fun buyStreakFreeze(costCoins: Int = 150): Boolean {
        if (_coins.value >= costCoins) {
            _coins.value -= costCoins
            _streakFreezeCount.value += 1
            prefs.edit().putInt("pref_streak_freeze_count", _streakFreezeCount.value).apply()
            recordLedgerTransaction(
                title = "Purchased Streak Freeze Item",
                points = costCoins,
                isCredit = false,
                category = "Marketplace",
                iconEmoji = "🧊"
            )
            return true
        }
        return false
    }

    fun recordLedgerTransaction(
        title: String,
        points: Int,
        isCredit: Boolean,
        category: String,
        iconEmoji: String = "⭐"
    ) {
        val entry = PointLedgerEntry(
            id = System.currentTimeMillis().toString(),
            title = title,
            points = points,
            isCredit = isCredit,
            timestamp = "Just now",
            category = category,
            iconEmoji = iconEmoji
        )
        _pointLedger.value = listOf(entry) + _pointLedger.value
    }

    // =========================================================
    // APP PREFERENCES & THEME ACCENTS
    // =========================================================
    private val _themeAccent = MutableStateFlow(
        prefs.getString("pref_theme_accent", "Emerald Green") ?: "Emerald Green"
    )
    val themeAccent: StateFlow<String> = _themeAccent.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(
        prefs.getString("pref_language", "English") ?: "English"
    )
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _defaultReciter = MutableStateFlow(
        prefs.getString("pref_reciter", "Mishary Rashid Alafasy") ?: "Mishary Rashid Alafasy"
    )
    val defaultReciter: StateFlow<String> = _defaultReciter.asStateFlow()

    private val _hapticFeedbackEnabled = MutableStateFlow(prefs.getBoolean("pref_haptic", true))
    val hapticFeedbackEnabled: StateFlow<Boolean> = _hapticFeedbackEnabled.asStateFlow()

    private val _soundEffectsEnabled = MutableStateFlow(prefs.getBoolean("pref_sound_effects", true))
    val soundEffectsEnabled: StateFlow<Boolean> = _soundEffectsEnabled.asStateFlow()

    fun setThemeAccent(accent: String) {
        _themeAccent.value = accent
        prefs.edit().putString("pref_theme_accent", accent).apply()
    }

    fun setSelectedLanguage(lang: String) {
        _selectedLanguage.value = lang
        prefs.edit().putString("pref_language", lang).apply()
    }

    fun setDefaultReciter(reciter: String) {
        _defaultReciter.value = reciter
        prefs.edit().putString("pref_reciter", reciter).apply()
    }

    fun toggleHapticFeedback(enabled: Boolean) {
        _hapticFeedbackEnabled.value = enabled
        prefs.edit().putBoolean("pref_haptic", enabled).apply()
    }

    fun toggleSoundEffects(enabled: Boolean) {
        _soundEffectsEnabled.value = enabled
        prefs.edit().putBoolean("pref_sound_effects", enabled).apply()
    }

    fun clearPersonalReflectionLogs() {
        // Clears reflection logs and resets diagnostic answers
        _diagnosticAnswers.value = emptyMap()
    }

    fun exportUserDataSummary(): String {
        return """
            --- NOUR DEEN DATA EXPORT ---
            User: ${_userName.value} (${_userHandle.value})
            Persona: ${_journeyPersonaMode.value}
            Spiritual Archetype: ${_userArchetype.value.title}
            Streak: ${_streakDays.value} Days (Purity: ${purityIndex.value}%)
            Coins / XP: ${_coins.value}
            Night Shield Active: ${_nightShieldActive.value}
            Discrete Sitr Mode: ${_discreteSitrMode.value}
            Calculation Method: ${_calculationMethodName.value}
            Asr School: ${_asrJuristicSchool.value}
        """.trimIndent()
    }

    fun resetAccountProfile() {
        updateUserProfile("Jibril Dawood", "@jibril_d", "Seeking Ihsan and shielding my gaze for Allah.", "avatar_1")
        _coins.value = 1240
        _streakDays.value = 14
        _streakFreezeCount.value = 2
        _streakFreezeActive.value = false
        _nightShieldActive.value = true
        _discreteSitrMode.value = false
    }
}
