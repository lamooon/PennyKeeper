package com.example.pennykeeper.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.model.ExpenseUiModel
import com.example.pennykeeper.data.model.RecurringPeriod
import com.example.pennykeeper.data.repository.CategoryRepository
import com.example.pennykeeper.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class HomeViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    val categories = categoryRepository.categories

    val expenses = expenseRepository.expenses
        .map { it.sortedByDescending { expense -> expense.date } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val totalExpenses = expenses.map { expenseList ->
        expenseList.sumOf { it.amount }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0.0
    )

    val monthlyExpenses = expenses.map { expenseList ->
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        expenseList.filter { expense ->
            calendar.time = expense.date
            calendar.get(Calendar.MONTH) == currentMonth &&
                    calendar.get(Calendar.YEAR) == currentYear
        }.sumOf { it.amount }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0.0
    )

    fun addExpense(expenseUiModel: ExpenseUiModel) {
        viewModelScope.launch {
            try {
                expenseRepository.addExpense(expenseUiModel.copy(
                    nextDueDate = calculateNextDueDate(expenseUiModel.date, expenseUiModel.recurringPeriod)
                ))
                _uiState.update { it.copy(isExpenseAdded = true) }
            } catch (e: IllegalArgumentException) {
                // Handle category not found error
                // You might want to add error handling in the UI state
            }
        }
    }

    fun deleteExpense(expenseId: Int) {
        viewModelScope.launch {
            expenseRepository.getExpenseById(expenseId)?.let { expense ->
                expenseRepository.deleteExpense(expense)
            }
        }
    }

    private fun calculateNextDueDate(currentDate: Date, recurringPeriod: RecurringPeriod?): Date? {
        if (recurringPeriod == null) return null

        val calendar = Calendar.getInstance()
        calendar.time = currentDate

        when (recurringPeriod) {
            RecurringPeriod.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            RecurringPeriod.YEARLY -> calendar.add(Calendar.YEAR, 1)
        }

        return calendar.time
    }

    fun resetExpenseAddedState() {
        _uiState.update { it.copy(isExpenseAdded = false) }
    }
}

data class HomeUiState(
    val isExpenseAdded: Boolean = false
)