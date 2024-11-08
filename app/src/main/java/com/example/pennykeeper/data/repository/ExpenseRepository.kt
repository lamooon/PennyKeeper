package com.example.pennykeeper.data.repository


import com.example.pennykeeper.data.dao.ExpenseDao
import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.RecurringPeriod
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val expenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val recurringExpenses: Flow<List<Expense>> = expenseDao.getRecurringExpenses()

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

    fun getDueRecurringExpenses(date: Date): Flow<List<Expense>> {
        return expenseDao.getDueRecurringExpenses(date)
    }

    suspend fun updateRecurringExpenseNextDueDate(expense: Expense) {
        val nextDueDate = calculateNextDueDate(expense.date, expense.recurringPeriod!!)
        expenseDao.updateExpense(expense.copy(nextDueDate = nextDueDate))
    }

    private fun calculateNextDueDate(currentDate: Date, period: RecurringPeriod): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate

        when (period) {
            RecurringPeriod.DAILY -> calendar.add(Calendar.DAY_OF_MONTH, 1)
            RecurringPeriod.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            RecurringPeriod.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            RecurringPeriod.YEARLY -> calendar.add(Calendar.YEAR, 1)
        }

        return calendar.time
    }
}