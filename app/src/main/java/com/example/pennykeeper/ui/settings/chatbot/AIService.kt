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

class AIService {
    private val client = OkHttpClient()
    private val apiKey = BuildConfig.OPENROUTER_API_KEY
    private val baseUrl = "https://openrouter.ai/api/v1/chat/completions"

    suspend fun makeRequest(message: String, systemPrompt: Boolean = false): String {
        return try {
            val jsonBody = JSONObject().apply {
                put("model", "meta-llama/llama-3.2-1b-instruct:free")
                put("messages", JSONArray().apply {
                    if (systemPrompt) {
                        put(JSONObject().apply {
                            put("role", "system")
                            put("content", """
                                You are a helpful financial assistant. Your role is to:
                                1. Analyze financial data and provide insights
                                2. Answer questions about budgeting and spending
                                3. Give practical financial advice based on the user's spending patterns
                                4. Be concise and direct in your responses
                                5. Use numbers and percentages when relevant
                                Keep responses focused on financial matters and the user's data.
                            """.trimIndent())
                        })
                    }
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

            withContext(Dispatchers.IO) {
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
            }
        } catch (e: Exception) {
            generateGenericResponse(message)
        }
    }

    private fun generateGenericResponse(message: String): String {
        // Extract highest category and amount from the message
        val regex = "Highest spending category: (.*?) with \\$(\\d+(?:\\.\\d+)?)".toRegex()
        val spendingInfo = regex.find(message)?.let { match ->
            val (category, amount) = match.destructured
            "\nYour highest spending category is $category with $$amount."
        } ?: ""

        return """
I'm currently offline.$spendingInfo

I can help you better when online with:
• Detailed spending analysis
• Personalized budgeting advice
• Savings recommendations
• Specific financial questions

Please check your internet connection and try again.
""".trimIndent()
    }
}