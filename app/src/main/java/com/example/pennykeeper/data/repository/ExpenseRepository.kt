package com.example.pennykeeper.data.repository

import com.example.pennykeeper.data.dao.ExpenseDao
import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val expenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    fun getExpensesByCategory(): Flow<Map<ExpenseCategory, Double>> {
        return expenses.map { expenseList ->
            expenseList.groupBy { it.category }
                .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }
        }
    }
}