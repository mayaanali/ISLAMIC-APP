package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_logs")
data class UsageLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val dateString: String, // YYYY-MM-DD
    val usageSeconds: Long
)
