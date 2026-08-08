package com.example.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.Calendar

object UsageTracker {

    data class AppUsageInfo(
        val packageName: String,
        val totalTimeInForegroundMs: Long
    )

    /**
     * Get usage stats for today for all apps installed on the device.
     */
    fun getTodayUsageStats(context: Context): Map<String, Long> {
        if (!PermissionManager.hasUsageStatsPermission(context)) {
            return emptyMap()
        }

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return emptyMap()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val statsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: return emptyMap()

        val usageMap = mutableMapOf<String, Long>()
        for (stat in statsList) {
            val existing = usageMap[stat.packageName] ?: 0L
            if (stat.totalTimeInForeground > existing) {
                usageMap[stat.packageName] = stat.totalTimeInForeground
            }
        }
        return usageMap
    }

    /**
     * Get foreground time in seconds for a specific package today.
     */
    fun getAppForegroundSecondsToday(context: Context, packageName: String): Long {
        val stats = getTodayUsageStats(context)
        val ms = stats[packageName] ?: 0L
        return ms / 1000L
    }
}
