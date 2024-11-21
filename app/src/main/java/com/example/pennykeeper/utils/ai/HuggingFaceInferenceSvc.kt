package com.example.pennykeeper.utils.ai

import com.example.pennykeeper.BuildConfig
import com.example.pennykeeper.data.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale

class HuggingFaceInferenceSvc(private val categoryRepository: CategoryRepository) {
    private val client = OkHttpClient()
    private val apiKey = BuildConfig.HUGGINGFACE_API_KEY
    private val baseUrl = "https://api-inference.huggingface.co/models/gpt2"
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    suspend fun getResponse(question: String, context: String): String = withContext(Dispatchers.IO) {
        var attempts = 0
        val maxAttempts = 3

        while (attempts < maxAttempts) {
            try {
                return@withContext makeRequest(question, context)
            } catch (e: IOException) {
                attempts++
                println("Attempt $attempts failed: ${e.message}")

                if (e.message?.contains("is currently loading") == true) {
                    delay(5000)
                    println("Model loading. Retrying... Attempt $attempts of $maxAttempts")
                } else if (attempts == maxAttempts) {
                    println("Failed after $maxAttempts attempts: ${e.message}")
                    return@withContext generateGenericResponse(context)
                }
            }
        }
        generateGenericResponse(context)
    }

    private suspend fun makeRequest(question: String, context: String): String {
        val prompt = """
            Financial Analysis:
            Data: $context
            Question: $question
            Analysis:
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("inputs", prompt)
            put("parameters", JSONObject().apply {
                put("max_length", 150)
                put("temperature", 0.7)
                put("top_p", 0.9)
                put("return_full_text", false)
            })
        }

        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        println("Raw API Response: $responseBody") // Debug log

        if (!response.isSuccessful || responseBody == null) {
            throw IOException("API Error: ${response.code} - $responseBody")
        }

        return try {
            val text = try {
                val jsonArray = JSONArray(responseBody)
                jsonArray.getJSONObject(0).optString("generated_text", "")
            } catch (e: Exception) {
                try {
                    val jsonObject = JSONObject(responseBody)
                    jsonObject.optString("generated_text", "")
                } catch (e: Exception) {
                    if (responseBody.startsWith("\"") && responseBody.endsWith("\"")) {
                        responseBody.substring(1, responseBody.length - 1)
                    } else {
                        responseBody
                    }
                }
            }

            if (text.isBlank()) {
                throw IOException("Empty response from API")
            }

            val expenses = parseFinancialData(context)
            formatResponse(text, expenses)

        } catch (e: Exception) {
            println("Parse error: ${e.message}")
            throw IOException("Failed to parse response: ${e.message}")
        }
    }

    private suspend fun parseFinancialData(context: String): Map<String, Double> {
        val expenses = mutableMapOf<String, Double>()
        println("Parsing context: $context") // Debug log

        // Get all categories from the database
        val dbCategories = categoryRepository.categories.first().map { it.name.lowercase() }

        // Split into lines and clean up
        val lines = context.lowercase()
            .lines()
            .filter { it.isNotEmpty() }

        lines.forEach { line ->
            try {
                // Look for categories from database
                val category = dbCategories.find { category ->
                    line.contains(category, ignoreCase = true)
                }

                // Find dollar amount using regex
                val dollarAmount = "\\$\\s*([0-9]+(?:\\.[0-9]{2})?)"
                    .toRegex()
                    .find(line)
                    ?.groupValues
                    ?.get(1)
                    ?.toDoubleOrNull()

                if (dollarAmount != null) {
                    val expenseCategory = category ?: run {
                        // If no category matched, get the default category
                        val defaultCategory = categoryRepository.getDefaultCategory()?.name?.lowercase()
                        defaultCategory ?: "other"
                    }

                    println("Found valid expense: $expenseCategory = $dollarAmount") // Debug log

                    // Add to existing amount if category already exists
                    expenses[expenseCategory] = expenses.getOrDefault(expenseCategory, 0.0) + dollarAmount
                }
            } catch (e: Exception) {
                println("Error parsing line '$line': ${e.message}")
            }
        }

        println("Final parsed expenses: $expenses") // Debug log
        return expenses
    }

    private fun formatResponse(aiResponse: String, expenses: Map<String, Double>): String {
        val total = expenses.values.sum()

        return buildString {
            appendLine("💡 AI Analysis")
            appendLine("────────────────")
            appendLine(aiResponse.trim())
            appendLine("\nActual Statistics:")
            appendLine("• Total spending: ${currencyFormatter.format(total)}")
            appendLine("• Number of transactions: ${expenses.size}")
            if (expenses.isNotEmpty()) {
                val avgSpending = total / expenses.size
                appendLine("• Average transaction: ${currencyFormatter.format(avgSpending)}")
                val highestEntry = expenses.maxByOrNull { it.value }
                highestEntry?.let {
                    appendLine("• Largest expense: ${currencyFormatter.format(it.value)} (${it.key})")
                }

                // Add category breakdown
                appendLine("\nCategory Breakdown:")
                expenses.forEach { (category, amount) ->
                    val percentage = (amount / total * 100).toInt()
                    appendLine("• ${category.capitalize()}: ${currencyFormatter.format(amount)} ($percentage%)")
                }
            }
        }
    }

    private suspend fun generateGenericResponse(context: String): String {
        val expenses = parseFinancialData(context)
        val total = expenses.values.sum()

        return buildString {
            appendLine("📊 Basic Analysis")
            appendLine("────────────────")
            appendLine("• Total spending: ${currencyFormatter.format(total)}")
            appendLine("• Number of transactions: ${expenses.size}")
            if (expenses.isNotEmpty()) {
                val avgSpending = total / expenses.size
                appendLine("• Average transaction: ${currencyFormatter.format(avgSpending)}")
                val highestEntry = expenses.maxByOrNull { it.value }
                highestEntry?.let {
                    appendLine("• Largest expense: ${currencyFormatter.format(it.value)} (${it.key})")
                }

                // Add category breakdown
                appendLine("\nCategory Breakdown:")
                expenses.forEach { (category, amount) ->
                    val percentage = (amount / total * 100).toInt()
                    appendLine("• ${category.capitalize()}: ${currencyFormatter.format(amount)} ($percentage%)")
                }
            }
        }
    }

    private fun String.capitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}