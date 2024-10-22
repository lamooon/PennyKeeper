package com.example.pennykeeper.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.ExpenseCategory
import com.example.pennykeeper.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class HomeViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses = _expenses.asStateFlow()

    init {
        viewModelScope.launch {
            repository.expenses.collect {
                _expenses.value = it
            }
        }
    }

    fun addExpense(amount: Double, place: String, category: ExpenseCategory) {
        val newExpense = Expense(
            id = _expenses.value.size + 1,
            amount = amount,
            place = place,
            category = category,
            date = Date()
        )
        repository.addExpense(newExpense)
    }
}