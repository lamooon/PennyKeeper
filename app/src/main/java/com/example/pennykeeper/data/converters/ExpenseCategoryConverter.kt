package com.example.pennykeeper.data.converters

import androidx.room.TypeConverter
import com.example.pennykeeper.data.model.ExpenseCategory

class ExpenseCategoryConverter {
    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory): String {
        return value.name
    }

    @TypeConverter
    fun toExpenseCategory(value: String): ExpenseCategory {
        return ExpenseCategory.valueOf(value)
    }
}