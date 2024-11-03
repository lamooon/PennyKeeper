package com.example.pennykeeper.data.model

import java.util.Date

data class Expense(
    val id: Int,
    val amount: Double,
    val place: String,
    val category: ExpenseCategory,
    val date: Date
)

enum class ExpenseCategory {
    GROCERIES,
    SUBSCRIPTIONS,
    TAXES,
    ENTERTAINMENT,
    UTILITIES,
    OTHER
}
