package com.example.pennykeeper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val place: String,
    val category: ExpenseCategory,
    val date: Date,
    val isRecurring: Boolean = false,
    val recurringPeriod: RecurringPeriod? = null,
    val nextDueDate: Date? = null
)

enum class RecurringPeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

enum class ExpenseCategory {
    GROCERIES,
    SUBSCRIPTIONS,
    TAXES,
    ENTERTAINMENT,
    UTILITIES,
    OTHER
}