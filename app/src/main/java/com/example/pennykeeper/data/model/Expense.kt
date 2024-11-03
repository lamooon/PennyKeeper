package com.example.pennykeeper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.pennykeeper.data.converters.DateConverter
import com.example.pennykeeper.data.converters.ExpenseCategoryConverter
import java.util.Date

@Entity(tableName = "expense")
@TypeConverters(DateConverter::class, ExpenseCategoryConverter::class)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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
