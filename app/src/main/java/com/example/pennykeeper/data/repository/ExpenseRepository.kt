package com.example.pennykeeper.data.repository

import com.example.pennykeeper.data.dao.ExpenseDao
import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.RecurringPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val expenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val recurringExpenses: Flow<List<Expense>> = expenseDao.getRecurringExpenses()

    fun getExpensesByPeriod(period: TimePeriod, date: Date = Date()): Flow<List<Expense>> {
        return expenses.map { expenseList ->
            val calendar = Calendar.getInstance()
            calendar.time = date

            val filteredExpenses = when (period) {
                TimePeriod.WEEK -> {
                    // Get start and end of week
                    val startCalendar = calendar.clone() as Calendar
                    startCalendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                    val endCalendar = startCalendar.clone() as Calendar
                    endCalendar.add(Calendar.DAY_OF_WEEK, 6)

                    expenseList.filter {
                        val expenseDate = Calendar.getInstance().apply { time = it.date }
                        expenseDate.after(startCalendar) && expenseDate.before(endCalendar)
                    }
                }
                TimePeriod.MONTH -> {
                    // Get expenses for current month
                    val currentMonth = calendar.get(Calendar.MONTH)
                    val currentYear = calendar.get(Calendar.YEAR)

                    expenseList.filter {
                        val expenseDate = Calendar.getInstance().apply { time = it.date }
                        expenseDate.get(Calendar.MONTH) == currentMonth &&
                                expenseDate.get(Calendar.YEAR) == currentYear
                    }
                }
                TimePeriod.YEAR -> {
                    // Get expenses for current year
                    val currentYear = calendar.get(Calendar.YEAR)

                    expenseList.filter {
                        val expenseDate = Calendar.getInstance().apply { time = it.date }
                        expenseDate.get(Calendar.YEAR) == currentYear
                    }
                }
            }
            filteredExpenses
        }
    }

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

    enum class TimePeriod {
        WEEK, MONTH, YEAR
    }
}