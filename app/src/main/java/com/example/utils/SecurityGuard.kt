package com.example.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import java.io.File
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

data class SuspiciousServiceInfo(
    val id: String,
    val packageName: String,
    val canPerformGestures: Boolean,
    val isSystemOrAllowed: Boolean
)

object SecurityGuard {

    private const val TAG = "SecurityGuard"
    private const val KEY_ALIAS = "FocusGuardMasterKey_v2"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128

    // ==========================================
    // 1. HARDWARE-BACKED ANDROID KEYSTORE CIPHER
    // ==========================================
    fun getOrCreateMasterKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            if (entry != null) {
                return entry.secretKey
            }
            val key = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
            if (key != null) {
                return key
            }
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    /**
     * Encrypt sensitive payload using KeyStore AES-256 GCM.
     */
    fun encryptData(plainText: ByteArray): Pair<ByteArray, ByteArray> {
        val secretKey = getOrCreateMasterKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        // AndroidKeyStore requires cipher to generate its own IV when encrypting
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv ?: ByteArray(GCM_IV_LENGTH).also { SecureRandom().nextBytes(it) }
        val cipherText = cipher.doFinal(plainText)
        return Pair(iv, cipherText)
    }

    /**
     * Decrypt sensitive payload using KeyStore AES-256 GCM.
     */
    fun decryptData(iv: ByteArray, cipherText: ByteArray): ByteArray {
        val secretKey = getOrCreateMasterKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(cipherText)
    }

    /**
     * Retrieve or generate a 256-bit randomized SQLCipher database passphrase,
     * encrypted at rest using the hardware-backed Android KeyStore master key.
     */
    fun getOrGenerateDatabasePassphrase(context: Context): ByteArray {
        val prefs = context.getSharedPreferences("secure_db_vault", Context.MODE_PRIVATE)
        val ivBase64 = prefs.getString("db_iv", null)
        val encKeyBase64 = prefs.getString("db_encrypted_key", null)

        if (ivBase64 != null && encKeyBase64 != null) {
            try {
                val iv = android.util.Base64.decode(ivBase64, android.util.Base64.NO_WRAP)
                val encKey = android.util.Base64.decode(encKeyBase64, android.util.Base64.NO_WRAP)
                return decryptData(iv, encKey)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decrypt database passphrase with KeyStore, generating new key", e)
            }
        }

        // Generate 32-byte (256-bit) randomized cryptographic key
        val newKey = ByteArray(32)
        SecureRandom().nextBytes(newKey)

        try {
            val (iv, encrypted) = encryptData(newKey)
            prefs.edit()
                .putString("db_iv", android.util.Base64.encodeToString(iv, android.util.Base64.NO_WRAP))
                .putString("db_encrypted_key", android.util.Base64.encodeToString(encrypted, android.util.Base64.NO_WRAP))
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save encrypted database passphrase", e)
        }

        return newKey
    }

    // ==========================================
    // 2. ROOT & TAMPER DETECTION
    // ==========================================
    fun isDeviceRooted(): Boolean {
        // 1. Check build tags
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        // 2. Check known su binary paths
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su",
            "/system/xbin/daemonsu"
        )
        for (path in paths) {
            try {
                if (File(path).exists()) return true
            } catch (e: Exception) {
                // Ignore security exceptions
            }
        }

        // 3. Check which su command
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val result = process.inputStream.bufferedReader().readLine()
            process.destroy()
            result != null
        } catch (t: Throwable) {
            false
        }
    }

    // ==========================================
    // 3. EMULATOR DETECTION
    // ==========================================
    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.BOARD == "QC_Reference_Phone"
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST.startsWith("Build")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT)
    }

    // ==========================================
    // 4. ACCESSIBILITY SERVICE HIJACK INSPECTION
    // ==========================================
    fun scanSuspiciousAccessibilityServices(context: Context): List<SuspiciousServiceInfo> {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            ?: return emptyList()

        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        ) ?: emptyList()

        val myPackage = context.packageName
        val trustedPackages = setOf(
            myPackage,
            "com.google.android.marvin.talkback",
            "com.samsung.accessibility",
            "com.android.talkback"
        )

        val results = mutableListOf<SuspiciousServiceInfo>()
        for (service in enabledServices) {
            val serviceInfo = service.resolveInfo?.serviceInfo ?: continue
            val pkg = serviceInfo.packageName ?: ""
            val capabilities = service.capabilities
            val canPerformGestures = (capabilities and AccessibilityServiceInfo.CAPABILITY_CAN_PERFORM_GESTURES) != 0

            val isTrusted = trustedPackages.contains(pkg) || pkg.startsWith("com.android.") || pkg.startsWith("com.google.android.")
            if (!isTrusted || canPerformGestures) {
                results.add(
                    SuspiciousServiceInfo(
                        id = service.id ?: pkg,
                        packageName = pkg,
                        canPerformGestures = canPerformGestures,
                        isSystemOrAllowed = isTrusted
                    )
                )
            }
        }
        return results
    }

    // ==========================================
    // 5. FLAG_SECURE UI ENFORCEMENT
    // ==========================================
    fun enforceFlagSecure(activity: Activity) {
        try {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply FLAG_SECURE", e)
        }
    }
}
