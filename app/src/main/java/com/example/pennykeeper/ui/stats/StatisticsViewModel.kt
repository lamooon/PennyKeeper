package com.example.pennykeeper.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.model.ExpenseCategory
import com.example.pennykeeper.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.ZoneId

class StatisticsViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _selectedTimeRange = MutableStateFlow(TimeRange.MONTH)
    val selectedTimeRange = _selectedTimeRange.asStateFlow()

    val expensesByCategory = combine(
        repository.expenses,
        selectedTimeRange
    ) { expenses, timeRange ->
        val filteredExpenses = expenses.filter { expense ->
            val expenseDate = expense.date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val startDate = when (timeRange) {
                TimeRange.WEEK -> LocalDate.now().minusWeeks(1)
                TimeRange.MONTH -> LocalDate.now().minusMonths(1)
                TimeRange.YEAR -> LocalDate.now().minusYears(1)
                TimeRange.ALL -> LocalDate.MIN
            }
            expenseDate.isAfter(startDate) || expenseDate.isEqual(startDate)
        }

        filteredExpenses
            .groupBy { it.category }
            .mapValues { (_, expenses) ->
                expenses.sumOf { it.amount }
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val totalExpenses = expensesByCategory.map { it.values.sum() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    fun setTimeRange(timeRange: TimeRange) {
        _selectedTimeRange.value = timeRange
    }
}

enum class TimeRange {
    WEEK, MONTH, YEAR, ALL
}