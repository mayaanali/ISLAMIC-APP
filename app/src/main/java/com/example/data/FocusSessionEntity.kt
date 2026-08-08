package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val durationMinutes: Int,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = 0L,
    val isCompleted: Boolean = false,
    val isStrictLock: Boolean = false
)
