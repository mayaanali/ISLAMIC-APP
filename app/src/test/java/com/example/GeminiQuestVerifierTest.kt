package com.example

import android.graphics.Bitmap
import com.example.utils.GeminiQuestVerifier
import com.example.utils.QuestVerificationResult
import org.json.JSONObject
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
class GeminiQuestVerifierTest {

    @Test
    fun testQuestVerificationResultCreation() {
        val successResult = QuestVerificationResult(
            isVerified = true,
            confidenceScore = 0.95f,
            reason = "Quran page verified successfully.",
            isAiPowered = true
        )

        assertTrue(successResult.isVerified)
        assertEquals(0.95f, successResult.confidenceScore, 0.01f)
        assertEquals("Quran page verified successfully.", successResult.reason)
        assertTrue(successResult.isAiPowered)
    }

    @Test
    fun testJsonParsingForVerificationResponse() {
        val sampleAiResponseJson = """
            {
                "isVerified": true,
                "confidenceScore": 0.92,
                "reason": "Prayer rug and tasbih clearly visible in frame."
            }
        """.trimIndent()

        val jsonObject = JSONObject(sampleAiResponseJson)
        val isVerified = jsonObject.getBoolean("isVerified")
        val confidence = jsonObject.getDouble("confidenceScore").toFloat()
        val reason = jsonObject.getString("reason")

        assertTrue(isVerified)
        assertEquals(0.92f, confidence, 0.01f)
        assertEquals("Prayer rug and tasbih clearly visible in frame.", reason)
    }

    @Test
    fun testRejectionJsonParsing() {
        val sampleAiRejectJson = """
            {
                "isVerified": false,
                "confidenceScore": 0.88,
                "reason": "Photo contains unrelated screen shot of text messages."
            }
        """.trimIndent()

        val jsonObject = JSONObject(sampleAiRejectJson)
        val isVerified = jsonObject.getBoolean("isVerified")
        val reason = jsonObject.getString("reason")

        assertFalse(isVerified)
        assertTrue(reason.contains("unrelated"))
    }
}
