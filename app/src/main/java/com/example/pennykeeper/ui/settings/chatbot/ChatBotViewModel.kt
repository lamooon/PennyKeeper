package com.example.pennykeeper.ui.settings.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.repository.CategoryRepository
import com.example.pennykeeper.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ChatBotViewModel(
    private val aiService: AIService,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository

) : ViewModel() {

    data class ChatMessage(
        val content: String,
        val isUser: Boolean
    )

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()


    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            _chatHistory.value += ChatMessage(userMessage, isUser = true)
            _isAnalyzing.value = true

            try {
                val expenses = expenseRepository.getAllExpenses()

                val categoryTotals = expenses.groupBy { it.categoryName }
                    .mapValues { (_, expenses) ->
                        expenses.sumOf { it.amount }
                    }
                    .toList()
                    .sortedByDescending { it.second }

                val highestCategory = categoryTotals.firstOrNull()

                val fullMessage = """
                [Financial Data]
                Spending Summary (Highest to Lowest):
                ${categoryTotals.joinToString("\n") { (category, total) ->
                    "- $category: $$total"
                }}
                
                Highest spending category: ${highestCategory?.first} with $${highestCategory?.second}
                
                [User Question]
                $userMessage
            """.trimIndent()

                val response = aiService.makeRequest(fullMessage, systemPrompt = true)
                _chatHistory.value += ChatMessage(response, isUser = false)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unknown error occurred"
                _chatHistory.value += ChatMessage(
                    "Sorry, I encountered an error: $errorMessage",
                    isUser = false
                )
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    private fun generateGenericResponse(message: String): String {
        // Extract highest category and amount from the message
        val regex = "Highest spending category: (.*?) with \\$(\\d+(?:\\.\\d+)?)".toRegex()
        val matchResult = regex.find(message)

        return if (matchResult != null) {
            val (category, amount) = matchResult.destructured
            """
            I'm currently offline, but I can see that your highest spending category is $category with $$amount.
            
            To help you manage your finances better:
            1. Monitor this category closely
            2. Look for ways to reduce spending if needed
            3. Set a budget if you haven't already
            
            Please try again when online for more detailed analysis.
            """.trimIndent()
        } else {
            """
            I'm currently offline. When back online, I can help you with:
            • Analyzing your spending patterns
            • Providing budgeting advice
            • Identifying areas for savings
            • Answering specific financial questions
            
            Please check your internet connection and try again.
            """.trimIndent()
        }
    }
}