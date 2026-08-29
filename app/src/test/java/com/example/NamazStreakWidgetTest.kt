package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.widget.NamazStreakWidget
import com.example.widget.NamazStreakWidgetReceiver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NamazStreakWidgetTest {

    @Test
    fun testNamazStreakWidgetReceiverInitialization() {
        val receiver = NamazStreakWidgetReceiver()
        assertNotNull(receiver)
        assertNotNull(receiver.glanceAppWidget)
        assertTrue(receiver.glanceAppWidget is NamazStreakWidget)
    }

    @Test
    fun testNamazStreakPreferencesPersistence() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("namaz_streak_count", 5)
            .putLong("last_namaz_logged_timestamp", System.currentTimeMillis())
            .putBoolean("widget_preview_extinction_mode", true)
            .commit()

        assertEquals(5, prefs.getInt("namaz_streak_count", 1))
        assertTrue(prefs.getBoolean("widget_preview_extinction_mode", false))
    }
}
