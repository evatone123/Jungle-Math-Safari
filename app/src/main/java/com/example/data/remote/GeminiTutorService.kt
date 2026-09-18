package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiTutorService {

    private const val TAG = "GeminiTutorService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getTutorGuidance(
        question: Question,
        hintStep: Int,
        childName: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If no API key configured or placeholder, return progressive safe hint
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getDeterministicHint(question, hintStep)
        }

        val prompt = buildTutorPrompt(question, hintStep, childName)

        try {
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                val systemInstruction = JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().put("text",
                            "You are Safari Owl, a friendly cartoon animal tutor in 'Jungle Math Safari' for children ages 4-10. " +
                            "Use warm, cheerful, simple words (max 2 short sentences). " +
                            "Encourage the child! " +
                            "Never reveal the final answer directly if hint step is 1 or 2; instead guide them to count or think step by step. " +
                            "Keep it safe, clean, and playful with animal sounds or jungle metaphors."
                        ))
                    }
                    put("parts", parts)
                }
                put("systemInstruction", systemInstruction)

                val generationConfig = JSONObject().apply {
                    put("temperature", 0.5)
                    put("topP", 0.9)
                }
                put("generationConfig", generationConfig)
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseString = response.body?.string() ?: ""
                val root = JSONObject(responseString)
                val candidates = root.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")?.trim()

                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            } else {
                Log.w(TAG, "Gemini API call failed with code: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying Gemini API", e)
        }

        // Fallback to deterministic tutor hint
        return@withContext getDeterministicHint(question, hintStep)
    }

    private fun buildTutorPrompt(question: Question, hintStep: Int, childName: String): String {
        return """
            The child's name is '$childName'.
            The math problem is: "${question.promptText}"
            Math formula: "${question.formulaText}"
            Correct Answer: ${question.correctAnswer}
            Hint Level requested: $hintStep of 3.
            If Hint Level 1: Give a friendly, gentle conceptual hint.
            If Hint Level 2: Give an encouraging step-by-step strategy.
            If Hint Level 3: Give the final explanation and celebrate their effort.
        """.trimIndent()
    }

    fun getDeterministicHint(question: Question, hintStep: Int): String {
        val index = (hintStep - 1).coerceIn(0, question.progressiveHints.lastIndex)
        return question.progressiveHints[index]
    }
}
