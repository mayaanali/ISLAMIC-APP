package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppBlockerAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var lastBlockedPackage = ""
    private var lastBlockTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val packageName = event.packageName?.toString() ?: return

        // Skip checking ourselves
        if (packageName == applicationContext.packageName ||
            packageName.contains("launcher") ||
            packageName.contains("systemui")
        ) {
            return
        }

        // Throttle rapid duplicate triggers
        val now = System.currentTimeMillis()
        if (packageName == lastBlockedPackage && (now - lastBlockTime) < 1500L) {
            return
        }

        serviceScope.launch {
            checkAndBlockPackage(packageName)
        }
    }

    private suspend fun checkAndBlockPackage(packageName: String) {
        val database = AppDatabase.getDatabase(applicationContext)
        val app = database.dao().getAppByPackage(packageName) ?: return

        val isBlocked = app.isEnabled && (app.isInstantBlocked || app.isLimitExceeded)

        if (isBlocked) {
            lastBlockedPackage = packageName
            lastBlockTime = System.currentTimeMillis()

            val reason = if (app.isInstantBlocked) "Instant Block Active" else "Daily Time Limit Reached (${app.dailyLimitMinutes} min)"

            withContext(Dispatchers.Main) {
                val intent = Intent(applicationContext, BlockOverlayActivity::class.java).apply {
                    putExtra(BlockOverlayActivity.EXTRA_APP_NAME, app.appName)
                    putExtra(BlockOverlayActivity.EXTRA_PACKAGE_NAME, packageName)
                    putExtra(BlockOverlayActivity.EXTRA_REASON, reason)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
            }
        }
    }

    override fun onInterrupt() {
        // Service interrupted
    }
}
