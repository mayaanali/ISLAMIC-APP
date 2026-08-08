package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.utils.UsageTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppBlockerForegroundService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("FocusGuard is active • Monitoring screen limits"))
        startMonitoringLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        serviceJob.cancel()
        super.onDestroy()
    }

    private fun startMonitoringLoop() {
        serviceScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            while (isActive) {
                try {
                    val usageMap = UsageTracker.getTodayUsageStats(applicationContext)
                    val blockedApps = db.dao().getBlockedAppsListSync()

                    var exceededCount = 0

                    for (app in blockedApps) {
                        if (!app.isEnabled) continue

                        val usageMs = usageMap[app.packageName] ?: 0L
                        val usageSec = usageMs / 1000L
                        val limitSec = app.dailyLimitMinutes * 60L
                        val isExceeded = usageSec >= limitSec && app.dailyLimitMinutes > 0

                        if (isExceeded) {
                            exceededCount++
                        }

                        // Update DB if usage changed or limit state changed
                        if (app.todayUsageSeconds != usageSec || app.isLimitExceeded != isExceeded) {
                            db.dao().updateUsage(app.packageName, usageSec, isExceeded)
                        }
                    }

                    // Update notification
                    val statusText = if (exceededCount > 0) {
                        "FocusGuard • $exceededCount app(s) reached daily limit"
                    } else {
                        "FocusGuard is active • ${blockedApps.size} apps monitored"
                    }
                    updateNotification(statusText)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                delay(5000L) // Poll every 5 seconds
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "FocusGuard Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Running in foreground to monitor app time limits and shield distraction."
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(contentText: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FocusGuard App Blocker")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(contentText: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification(contentText))
    }

    companion object {
        private const val CHANNEL_ID = "focus_guard_service_channel"
        private const val NOTIFICATION_ID = 1001

        fun startService(context: Context) {
            val intent = Intent(context, AppBlockerForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, AppBlockerForegroundService::class.java)
            context.stopService(intent)
        }
    }
}
