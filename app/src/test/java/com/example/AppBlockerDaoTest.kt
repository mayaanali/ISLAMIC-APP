package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockerDao
import com.example.data.AppDatabase
import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.data.UsageLogEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppBlockerDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppBlockerDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.dao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeAndReadBlockedApp() = runBlocking {
        val app = BlockedAppEntity(
            packageName = "com.social.media",
            appName = "Social App",
            dailyLimitMinutes = 30,
            isInstantBlocked = false,
            category = "Social",
            todayUsageSeconds = 1200L,
            isLimitExceeded = false,
            isEnabled = true
        )

        dao.insertOrUpdateApp(app)
        val loaded = dao.getAppByPackage("com.social.media")

        assertNotNull(loaded)
        assertEquals("Social App", loaded?.appName)
        assertEquals(30, loaded?.dailyLimitMinutes)
        assertEquals(true, loaded?.isEnabled)
    }

    @Test
    fun updateAppUsageAndCheckLimit() = runBlocking {
        val app = BlockedAppEntity(
            packageName = "com.video.stream",
            appName = "Video Stream",
            dailyLimitMinutes = 15,
            isInstantBlocked = false,
            category = "Entertainment",
            todayUsageSeconds = 0L,
            isLimitExceeded = false,
            isEnabled = true
        )

        dao.insertOrUpdateApp(app)
        dao.updateUsage("com.video.stream", 1800L, true) // 30 min > 15 min limit

        val updated = dao.getAppByPackage("com.video.stream")
        assertNotNull(updated)
        assertEquals(1800L, updated?.todayUsageSeconds)
        assertEquals(true, updated?.isLimitExceeded)
    }

    @Test
    fun deleteBlockedApp() = runBlocking {
        val app = BlockedAppEntity(
            packageName = "com.test.app",
            appName = "Test App",
            dailyLimitMinutes = 20,
            isEnabled = true
        )

        dao.insertOrUpdateApp(app)
        assertNotNull(dao.getAppByPackage("com.test.app"))

        dao.deleteApp("com.test.app")
        val deleted = dao.getAppByPackage("com.test.app")
        assertNull(deleted)
    }

    @Test
    fun insertFocusSession() = runBlocking {
        val session = FocusSessionEntity(
            title = "Night Salah & Study",
            durationMinutes = 45,
            isCompleted = true,
            isStrictLock = true
        )

        val id = dao.insertFocusSession(session)
        assertTrue(id > 0)
    }

    @Test
    fun insertUsageLog() = runBlocking {
        val log = UsageLogEntity(
            packageName = "com.social.media",
            appName = "Social App",
            dateString = "2026-08-28",
            usageSeconds = 1450L
        )

        dao.insertUsageLog(log)
    }
}
