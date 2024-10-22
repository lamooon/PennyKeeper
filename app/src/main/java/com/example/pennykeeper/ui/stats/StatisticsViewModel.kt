package com.example.pennykeeper.ui.stats


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.model.ExpenseCategory
import com.example.pennykeeper.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatisticsViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _expensesByCategory = MutableStateFlow<Map<ExpenseCategory, Double>>(emptyMap())
    val expensesByCategory = _expensesByCategory.asStateFlow()

    init {
        viewModelScope.launch {
            repository.expenses.collect {
                _expensesByCategory.value = repository.getExpensesByCategory()
            }
        }
    }
}