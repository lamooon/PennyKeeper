package com.example.pennykeeper.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.ExpenseCategory
import com.example.pennykeeper.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _expense = MutableStateFlow<Expense?>(null)
    val expense: StateFlow<Expense?> = _expense

    fun loadExpense(id: Int) {
        viewModelScope.launch {
            repository.getExpenseById(id)?.let { expense ->
                _expense.value = expense
            }
        }
    }

    fun updateExpense(amount: Double, place: String, category: ExpenseCategory) {
        viewModelScope.launch {
            _expense.value?.let { currentExpense ->
                val updatedExpense = currentExpense.copy(
                    amount = amount,
                    place = place,
                    category = category
                )
                repository.updateExpense(updatedExpense)
            }
        }
    }

    fun deleteExpense() {
        viewModelScope.launch {
            _expense.value?.let { expense ->
                repository.deleteExpense(expense)
            }
        }
    }
}