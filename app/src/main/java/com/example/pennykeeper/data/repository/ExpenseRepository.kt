package com.example.pennykeeper.data.repository


import com.example.pennykeeper.data.dao.ExpenseDao
import com.example.pennykeeper.data.model.Expense
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val expenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun getExpenseById(id: Int): Expense? {
        return expenseDao.getExpenseById(id)
    }
}