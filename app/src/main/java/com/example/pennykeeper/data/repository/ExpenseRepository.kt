package com.example.pennykeeper.data.repository


import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class ExpenseRepository {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: Flow<List<Expense>> = _expenses.asStateFlow()

    init {
        // Need to load this via room DB
        _expenses.value = listOf(
            Expense(1, 50.0, "Wellcome", ExpenseCategory.GROCERIES, Date()),
            Expense(2, 15.99, "Netflix", ExpenseCategory.SUBSCRIPTIONS, Date()),
            Expense(3, 500.0, "IRS", ExpenseCategory.TAXES, Date()),
            Expense(4, 30.0, "Cinema", ExpenseCategory.ENTERTAINMENT, Date()),
            Expense(5, 100.0, "Electric Company", ExpenseCategory.UTILITIES, Date())
        )
    }

    fun addExpense(expense: Expense) {
        val updatedList = _expenses.value.toMutableList()
        updatedList.add(expense)
        _expenses.value = updatedList
    }

    fun getExpensesByCategory(): Map<ExpenseCategory, Double> {
        return _expenses.value.groupBy { it.category }
            .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }
    }
}