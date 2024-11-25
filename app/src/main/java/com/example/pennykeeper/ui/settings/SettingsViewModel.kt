package com.example.pennykeeper.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.model.ExpenseUiModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class SettingsViewModel(
) : ViewModel() {

    /*
        Manages chat messages between users and AI
     */
    data class ChatMessage(
        val content: String,
        val isUser: Boolean
    )

    /*
        Manages reactive updates using StateFlow
     */
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    fun analyzeAllData() {
        /*
        TODO: You will implement this for the tutorial
         */
    }

    fun sendMessage(message: String) {
        /*
        TODO: You will implement this for the tutorial
         */

    }

    /*
        parses all the Room DB for AI to understand the spending patterns
     */
    private fun buildExpenseContext(expenses: List<ExpenseUiModel>): String {
        if (expenses.isEmpty()) return ""

        val total = expenses.sumOf { it.amount }
        return buildString {
            appendLine("Here are your recent expenses:")
            expenses.forEach { expense ->
                appendLine("- $${expense.amount} spent at ${expense.place} (${expense.categoryName}) on ${expense.date}")
            }
            appendLine("\nTotal spending: $${String.format("%.2f", total)}")
        }
    }

    private fun generateBasicAnalysis(expenses: List<ExpenseUiModel>): String {
        val total = expenses.sumOf { it.amount }
        val categorizedExpenses = expenses.groupBy { it.categoryName }
        val highestCategory = categorizedExpenses
            .maxByOrNull { (_, expenses) -> expenses.sumOf { it.amount } }

        return buildString {
            appendLine("📊 Basic Expense Analysis")
            appendLine("Total spending: $${String.format("%.2f", total)}")
            appendLine("Number of transactions: ${expenses.size}")

            if (highestCategory != null) {
                val categoryTotal = highestCategory.value.sumOf { it.amount }
                val percentage = (categoryTotal / total * 100).toInt()
                appendLine("\nHighest spending category: ${highestCategory.key}")
                appendLine("Amount: $${String.format("%.2f", categoryTotal)} ($percentage%)")
                appendLine("\nSuggestion: Consider setting a budget limit for ${highestCategory.key} to reduce expenses.")
            }
        }
    }
}