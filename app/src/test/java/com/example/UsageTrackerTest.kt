package com.example

import com.example.utils.UsageTracker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UsageTrackerTest {

    @Test
    fun testFormatDurationSeconds() {
        val formattedZero = UsageTracker.formatDuration(0L)
        assertEquals("0m", formattedZero)

        val formattedMinutes = UsageTracker.formatDuration(180L) // 3 minutes
        assertEquals("3m", formattedMinutes)

        val formattedHoursAndMins = UsageTracker.formatDuration(3660L) // 1 hr 1 min
        assertEquals("1h 1m", formattedHoursAndMins)
    }

    @Test
    fun testScreenTimePercentageCalculation() {
        val totalAllowedSeconds = 7200L // 2 hours
        val currentUsageSeconds = 3600L // 1 hour

        val percentage = (currentUsageSeconds.toFloat() / totalAllowedSeconds.toFloat()) * 100f
        assertEquals(50.0f, percentage, 0.01f)
    }

    @Test
    fun testFocusSessionDurationCalculations() {
        val sessionMinutes = 25
        val totalSeconds = sessionMinutes * 60
        assertEquals(1500, totalSeconds)

        val elapsed = 300 // 5 minutes elapsed
        val remaining = totalSeconds - elapsed
        val progress = elapsed.toFloat() / totalSeconds.toFloat()

        assertEquals(1200, remaining)
        assertEquals(0.20f, progress, 0.001f)
    }
}
