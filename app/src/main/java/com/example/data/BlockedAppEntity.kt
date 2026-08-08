package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_apps")
data class BlockedAppEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val dailyLimitMinutes: Int = 30, // Default 30 min limit
    val isInstantBlocked: Boolean = false, // Instant block toggle
    val category: String = "Other",
    val todayUsageSeconds: Long = 0L,
    val isLimitExceeded: Boolean = false,
    val isEnabled: Boolean = true,
    val updatedTimestamp: Long = System.currentTimeMillis()
)
