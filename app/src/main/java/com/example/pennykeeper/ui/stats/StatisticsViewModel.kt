package com.example.pennykeeper.ui.stats

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.ExpenseCategory
import com.example.pennykeeper.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

class StatisticsViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _selectedPeriod = MutableStateFlow(TimePeriod.MONTH)
    val selectedPeriod = _selectedPeriod.asStateFlow()

    private val calendar = Calendar.getInstance()

    data class CategoryExpense(
        val category: ExpenseCategory,
        val amount: Double,
        val color: Color,
        val percentage: Float
    )

    enum class TimePeriod {
        WEEK,
        MONTH,
        YEAR
    }

    private val categoryColors = mapOf(
        ExpenseCategory.GROCERIES to Color(0xFF4CAF50),      // Green
        ExpenseCategory.SUBSCRIPTIONS to Color(0xFF2196F3),  // Blue
        ExpenseCategory.TAXES to Color(0xFFF44336),          // Red
        ExpenseCategory.ENTERTAINMENT to Color(0xFFFF9800),  // Orange
        ExpenseCategory.UTILITIES to Color(0xFF9C27B0),      // Purple
        ExpenseCategory.OTHER to Color(0xFF607D8B)           // Blue Grey
    )

    private val _categoryExpenses = MutableStateFlow<List<CategoryExpense>>(emptyList())
    val categoryExpenses = _categoryExpenses.asStateFlow()

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount = _totalAmount.asStateFlow()

    init {
        viewModelScope.launch {
            repository.expenses.collect { expenses ->
                updateStatistics(expenses)
            }
        }
    }

    fun setPeriod(period: TimePeriod) {
        _selectedPeriod.value = period
        viewModelScope.launch {
            repository.expenses.firstOrNull()?.let { expenses ->
                updateStatistics(expenses)
            }
        }
    }

    private fun updateStatistics(expenses: List<Expense>) {
        val filteredExpenses = filterExpensesByPeriod(expenses)
        val total = filteredExpenses.sumOf { it.amount }
        _totalAmount.value = total

        val categoryAmounts = filteredExpenses
            .groupBy { it.category }
            .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }

        _categoryExpenses.value = categoryAmounts.map { (category, amount) ->
            CategoryExpense(
                category = category,
                amount = amount,
                color = categoryColors[category] ?: Color.Gray,
                percentage = if (total > 0) (amount / total).toFloat() else 0f
            )
        }.sortedByDescending { it.amount }
    }

    private fun filterExpensesByPeriod(expenses: List<Expense>): List<Expense> {
        val currentDate = Calendar.getInstance()
        val startDate = Calendar.getInstance()

        when (_selectedPeriod.value) {
            TimePeriod.WEEK -> {
                startDate.add(Calendar.DAY_OF_YEAR, -7)
            }
            TimePeriod.MONTH -> {
                startDate.set(Calendar.DAY_OF_MONTH, 1)
            }
            TimePeriod.YEAR -> {
                startDate.set(Calendar.DAY_OF_YEAR, 1)
            }
        }

        return expenses.filter { expense ->
            val expenseDate = Calendar.getInstance().apply { time = expense.date }
            expenseDate.after(startDate) && expenseDate.before(currentDate)
        }
    }

    fun getCategoryName(category: ExpenseCategory): String {
        return when (category) {
            ExpenseCategory.GROCERIES -> "Groceries"
            ExpenseCategory.SUBSCRIPTIONS -> "Subscriptions"
            ExpenseCategory.TAXES -> "Taxes"
            ExpenseCategory.ENTERTAINMENT -> "Entertainment"
            ExpenseCategory.UTILITIES -> "Utilities"
            ExpenseCategory.OTHER -> "Other"
        }
    }
}