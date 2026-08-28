package com.example.utils

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class QuestVerificationResult(
    val isVerified: Boolean,
    val confidenceScore: Float,
    val reason: String,
    val isAiPowered: Boolean = true
)

object GeminiQuestVerifier {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun verifyQuestPhoto(
        bitmap: Bitmap,
        questTitle: String,
        questDescription: String
    ): QuestVerificationResult = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext performFallbackVerification(bitmap, questTitle, questDescription)
        }

        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

            val promptText = """
                You are an objective Islamic Quest Verification AI.
                The user submitted a live photo as proof for completing this daily quest:
                Quest Title: "$questTitle"
                Quest Description: "$questDescription"

                INSTRUCTIONS:
                1. Inspect the image carefully for clear evidence of the requested task (e.g., physical or digital Quran text, prayer rug, mosque, charity receipt/log, tasbih, adhkar text, water/abution, etc.).
                2. If the photo shows UNRELATED content like a laptop/phone screen with text message or chat, a blank wall, random object, random selfie, or unrelated app screen, REJECT IT IMMEDIATELY (isVerified = false).
                3. Respond ONLY with a valid JSON object matching this schema:
                {
                  "isVerified": true,
                  "confidenceScore": 0.95,
                  "reason": "Brief 1-2 sentence explanation of what you see in the photo and why it passes or fails verification."
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", promptText))
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", "image/jpeg")
                                    put("data", base64Image)
                                })
                            })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestJson.toString().toRequestBody(mediaType)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful && responseString.isNotBlank()) {
                val jsonResp = JSONObject(responseString)
                val candidates = jsonResp.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

                if (rawText.isNotBlank()) {
                    val aiJson = JSONObject(rawText)
                    val isVerified = aiJson.optBoolean("isVerified", false)
                    val confidence = aiJson.optDouble("confidenceScore", 0.85).toFloat()
                    val reason = aiJson.optString("reason", "Photo analyzed by Gemini AI.")

                    return@withContext QuestVerificationResult(
                        isVerified = isVerified,
                        confidenceScore = confidence,
                        reason = reason,
                        isAiPowered = true
                    )
                }
            }

            return@withContext performFallbackVerification(bitmap, questTitle, questDescription)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext performFallbackVerification(bitmap, questTitle, questDescription)
        }
    }

    private fun performFallbackVerification(
        bitmap: Bitmap,
        questTitle: String,
        questDescription: String
    ): QuestVerificationResult {
        val width = bitmap.width
        val height = bitmap.height
        val isSubstantialPhoto = width >= 50 && height >= 50

        return QuestVerificationResult(
            isVerified = isSubstantialPhoto,
            confidenceScore = 0.80f,
            reason = if (isSubstantialPhoto) {
                "Photo meets resolution dimensions for '$questTitle'."
            } else {
                "Captured image resolution is insufficient."
            },
            isAiPowered = false
        )
    }
}
