package com.example.pennykeeper.utils.ai

import com.example.pennykeeper.data.model.ExpenseUiModel
import com.example.pennykeeper.ui.settings.formatCurrency

class FinancialAnalyzer(private val expenses: List<ExpenseUiModel>) {
    fun getExpenseContext(): String {
        return buildString {
            append("Financial Data:\n")
            val categoryTotals = expenses.groupBy { it.categoryName }
                .mapValues { it.value.sumOf { exp -> exp.amount } }

            append("Total spending: ${formatCurrency(expenses.sumOf { it.amount })}\n")
            append("Categories:\n")
            categoryTotals.forEach { (category, amount) ->
                append("- $category: ${formatCurrency(amount)}\n")
            }
        }
    }

    fun analyzeExpenses(question: String): String {
        val categoryTotals = expenses.groupBy { it.categoryName }
            .mapValues { it.value.sumOf { exp -> exp.amount } }

        return when {
            categoryTotals.isEmpty() -> "No expense data available."
            else -> {
                val total = expenses.sumOf { it.amount }
                val highest = categoryTotals.maxByOrNull { it.value }
                val lowest = categoryTotals.minByOrNull { it.value }

                "Based on your expense data:\n" +
                        "Total: ${formatCurrency(total)}\n" +
                        "Highest: ${highest?.key} (${formatCurrency(highest?.value ?: 0.0)})\n" +
                        "Lowest: ${lowest?.key} (${formatCurrency(lowest?.value ?: 0.0)})"
            }
        }
    }
}
