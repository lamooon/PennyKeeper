package com.example.pennykeeper.ui.settings.chatbot

import com.example.pennykeeper.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AIService {
    private val client = OkHttpClient()
    private val apiKey = BuildConfig.OPENROUTER_API_KEY
    private val baseUrl = "https://openrouter.ai/api/v1/chat/completions"

    suspend fun makeRequest(message: String): String {
        val jsonBody = JSONObject().apply {
            put("model", "meta-llama/llama-3.2-1b-instruct:free")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", message)
                })
            })
        }

        val request = Request.Builder()
            .url(baseUrl)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .header("Authorization", "Bearer $apiKey")
            .header("HTTP-Referer", "https://pennykeeper.app")
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("API call failed with code: ${response.code}")
                    }

                    val responseBody = response.body?.string()
                        ?: throw IOException("Empty response body")

                    JSONObject(responseBody)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                }
            } catch (e: Exception) {
                throw IOException("Request failed: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun getResponse(request: Request): String {
        return suspendCoroutine { continuation ->
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        continuation.resumeWithException(
                            RuntimeException("API call failed: ${response.code}")
                        )
                        return@use
                    }

                    val responseBody = response.body?.string()
                    if (responseBody == null) {
                        continuation.resumeWithException(
                            RuntimeException("Empty response body")
                        )
                        return@use
                    }

                    try {
                        val content = JSONObject(responseBody)
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                        continuation.resume(content)
                    } catch (e: Exception) {
                        continuation.resumeWithException(
                            RuntimeException("Failed to parse response: ${e.message}")
                        )
                    }
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }
}