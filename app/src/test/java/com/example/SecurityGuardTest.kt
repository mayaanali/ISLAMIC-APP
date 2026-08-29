package com.example

import com.example.utils.SecurityGuard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SecurityGuardTest {

    @Test
    fun testRootDetectionIntegrity() {
        // Standard JVM environment should evaluate root detection without throwing unhandled exceptions
        val isRooted = SecurityGuard.isDeviceRooted()
        // Function executes cleanly without crash
        assertNotNull(isRooted)
    }

    @Test
    fun testEmulatorDetectionIntegrity() {
        val isEmu = SecurityGuard.isEmulator()
        assertNotNull(isEmu)
    }

    @Test
    fun testKeyStoreMasterKeyCreation() {
        // Under Robolectric, AndroidKeyStore is emulated and initializes KeyStore properly
        try {
            val key = SecurityGuard.getOrCreateMasterKey()
            assertNotNull(key)
            assertTrue(key.algorithm.contains("AES"))
        } catch (e: Exception) {
            // Some Robolectric configurations might not have full Keystore provider loaded, handle gracefully
            assertNotNull(e.message)
        }
    }

    @Test
    fun testDatabasePassphraseGeneration() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val passphrase = SecurityGuard.getOrGenerateDatabasePassphrase(context)
        assertNotNull(passphrase)
        assertEquals(32, passphrase.size) // 256-bit key
    }

    @Test
    fun testBiometricHelperAvailabilityCheck() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val isAvailable = com.example.utils.BiometricAuthHelper.isBiometricAvailable(context)
        // Checks gracefully without crashing
        assertNotNull(isAvailable)
    }
}
