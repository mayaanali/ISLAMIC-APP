package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppBlockerDao {

    @Query("SELECT * FROM blocked_apps ORDER BY appName ASC")
    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>>

    @Query("SELECT * FROM blocked_apps ORDER BY appName ASC")
    suspend fun getBlockedAppsListSync(): List<BlockedAppEntity>

    @Query("SELECT * FROM blocked_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getAppByPackage(packageName: String): BlockedAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateApp(app: BlockedAppEntity)

    @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
    suspend fun deleteApp(packageName: String)

    @Query("UPDATE blocked_apps SET todayUsageSeconds = :usageSeconds, isLimitExceeded = :isExceeded WHERE packageName = :packageName")
    suspend fun updateUsage(packageName: String, usageSeconds: Long, isExceeded: Boolean)

    // Focus Sessions
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateFocusSession(session: FocusSessionEntity)

    // Usage Logs
    @Query("SELECT * FROM usage_logs WHERE dateString = :dateString ORDER BY usageSeconds DESC")
    fun getUsageLogsForDate(dateString: String): Flow<List<UsageLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageLog(log: UsageLogEntity)
}
