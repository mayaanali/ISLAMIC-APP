package com.example.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.BuildConfig
import com.example.utils.UsageTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class InstalledAppItem(
    val packageName: String,
    val appName: String,
    val category: String,
    val isSystemApp: Boolean,
    val todayUsageSeconds: Long = 0L,
    val isBlocked: Boolean = false,
    val isInstantBlocked: Boolean = false,
    val dailyLimitMinutes: Int = 30
)

class AppRepository(
    private val context: Context,
    private val database: AppDatabase
) {
    private val dao = database.dao()
    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    val blockedAppsFlow: Flow<List<BlockedAppEntity>> = dao.getAllBlockedApps()
    val focusSessionsFlow: Flow<List<FocusSessionEntity>> = dao.getAllFocusSessions()

    /**
     * Get installed apps on device with current usage and blocked status.
     * Excludes essential OS infrastructure packages to prevent UI clutter and lag.
     */
    suspend fun getInstalledApps(): List<InstalledAppItem> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val usageMap = UsageTracker.getTodayUsageStats(context)
        val blockedList = dao.getBlockedAppsListSync()
        val blockedMap = blockedList.associateBy { it.packageName }

        val installedPackages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val result = mutableListOf<InstalledAppItem>()

        // Known essential OS packages to exclude from the blocker list
        val osExcludedPrefixes = listOf(
            "com.android.providers",
            "com.android.systemui",
            "com.android.phone",
            "com.android.server",
            "com.android.stk",
            "com.android.bluetooth",
            "com.android.inputmethod",
            "com.android.keyguard",
            "com.qualcomm",
            "com.sec.android.provider",
            "com.samsung.android.provider",
            "com.miui.guardprovider",
            "android"
        )

        for (appInfo in installedPackages) {
            val pkg = appInfo.packageName

            // Skip current app, android framework, and low-level system background daemons
            if (pkg == context.packageName || pkg == "android") continue

            val isExcludedOS = osExcludedPrefixes.any { pkg.startsWith(it) }
            val hasLaunchIntent = pm.getLaunchIntentForPackage(pkg) != null
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            // Exclude system packages that do NOT have a launch intent and are OS utilities
            if (isSystem && (!hasLaunchIntent || isExcludedOS)) {
                continue
            }

            val appName = try {
                appInfo.loadLabel(pm).toString()
            } catch (e: Exception) {
                pkg
            }

            // Categorization
            val category = when {
                pkg.contains("youtube") || pkg.contains("netflix") || pkg.contains("tiktok") || pkg.contains("spotify") -> "Entertainment"
                pkg.contains("instagram") || pkg.contains("facebook") || pkg.contains("twitter") || pkg.contains("snapchat") || pkg.contains("reddit") || pkg.contains("whatsapp") -> "Social"
                pkg.contains("game") || pkg.contains("pubg") || pkg.contains("clash") -> "Gaming"
                pkg.contains("chrome") || pkg.contains("browser") -> "Web"
                isSystem -> "System"
                else -> "App"
            }

            val blockedEntity = blockedMap[pkg]
            val usageSec = (usageMap[pkg] ?: 0L) / 1000L

            result.add(
                InstalledAppItem(
                    packageName = pkg,
                    appName = appName,
                    category = category,
                    isSystemApp = isSystem,
                    todayUsageSeconds = usageSec,
                    isBlocked = blockedEntity?.isEnabled ?: false,
                    isInstantBlocked = blockedEntity?.isInstantBlocked ?: false,
                    dailyLimitMinutes = blockedEntity?.dailyLimitMinutes ?: 30
                )
            )
        }

        result.sortedWith(
            compareByDescending<InstalledAppItem> { it.isBlocked }
                .thenByDescending { it.todayUsageSeconds }
                .thenBy { it.appName }
        )
    }

    suspend fun toggleAppBlockStatus(item: InstalledAppItem, enableBlock: Boolean) = withContext(Dispatchers.IO) {
        if (enableBlock) {
            val entity = BlockedAppEntity(
                packageName = item.packageName,
                appName = item.appName,
                dailyLimitMinutes = item.dailyLimitMinutes,
                isInstantBlocked = item.isInstantBlocked,
                category = item.category,
                todayUsageSeconds = item.todayUsageSeconds,
                isEnabled = true
            )
            dao.insertOrUpdateApp(entity)
        } else {
            dao.deleteApp(item.packageName)
        }
    }

    suspend fun updateAppLimit(packageName: String, appName: String, limitMinutes: Int, isInstantBlocked: Boolean) = withContext(Dispatchers.IO) {
        val existing = dao.getAppByPackage(packageName)
        val entity = BlockedAppEntity(
            packageName = packageName,
            appName = appName,
            dailyLimitMinutes = limitMinutes,
            isInstantBlocked = isInstantBlocked,
            category = existing?.category ?: "App",
            todayUsageSeconds = existing?.todayUsageSeconds ?: 0L,
            isEnabled = true
        )
        dao.insertOrUpdateApp(entity)
    }

    suspend fun startFocusSession(title: String, durationMinutes: Int, isStrictLock: Boolean): Long = withContext(Dispatchers.IO) {
        val session = FocusSessionEntity(
            title = title,
            durationMinutes = durationMinutes,
            isStrictLock = isStrictLock
        )
        dao.insertFocusSession(session)
    }

    /**
     * Call Gemini API to generate AI Screen Time Coach analysis & tips.
     */
    suspend fun getAICoachInsights(totalScreenTimeMinutes: Long, topAppsInfo: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "💡 **FocusGuard AI Tip**: You've used $totalScreenTimeMinutes minutes of screen time today. Setting a 30-minute daily cap on social media apps can boost your daily productivity by up to 25%!"
        }

        try {
            val promptText = """
                You are FocusGuard AI, an empathetic and highly motivating digital wellness coach.
                Analyze this user's screen time today:
                - Total Screen Time: $totalScreenTimeMinutes minutes
                - Top Used Apps: $topAppsInfo
                
                Provide a concise, encouraging 3-bullet insight & action plan:
                1. Positive reflection on their current focus state.
                2. One specific actionable strategy to cut distraction today.
                3. A powerful single-line motivational quote.
                Format with markdown and emoji headers.
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", promptText)
                            })
                        })
                    })
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseStr = response.body?.string() ?: ""
                val rootJson = JSONObject(responseStr)
                val text = rootJson
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                return@withContext text
            } else {
                return@withContext "🌱 **Daily Focus Reflection**: You're making progress tracking your time! Focus on finishing key tasks before opening entertainment apps."
            }
        } catch (e: Exception) {
            return@withContext "⚡ **Smart Focus Strategy**: Small intentional breaks away from screens recharge your mental stamina better than endless scrolling."
        }
    }
}
