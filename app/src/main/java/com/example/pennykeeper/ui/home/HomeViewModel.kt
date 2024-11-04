package com.example.pennykeeper.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.ExpenseCategory
import com.example.pennykeeper.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class HomeViewModel(private val repository: ExpenseRepository) : ViewModel() {
    val expenses = repository.expenses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addExpense(amount: Double, place: String, category: ExpenseCategory) {
        viewModelScope.launch {
            val newExpense = Expense(
                amount = amount,
                place = place,
                category = category,
                date = Date()
            )
            repository.addExpense(newExpense)
        }
    }
}