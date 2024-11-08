package com.example.pennykeeper.ui.expense

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.ExpenseCategory
import com.example.pennykeeper.data.model.RecurringPeriod
import com.example.pennykeeper.data.repository.ExpenseRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class EditExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {
    var expense by mutableStateOf<Expense?>(null)
        private set

    var amount by mutableStateOf("")
        private set
    var place by mutableStateOf("")
        private set
    var category by mutableStateOf(ExpenseCategory.OTHER)
        private set
    var date by mutableStateOf(Date())
        private set
    var isRecurring by mutableStateOf(false)
        private set
    var recurringPeriod by mutableStateOf<RecurringPeriod?>(null)
        private set

    fun loadExpense(id: Int) {
        if (id != -1) {
            viewModelScope.launch {
                expense = repository.getExpenseById(id)
                expense?.let { exp ->
                    amount = exp.amount.toString()
                    place = exp.place
                    category = exp.category
                    date = exp.date
                    isRecurring = exp.isRecurring
                    recurringPeriod = exp.recurringPeriod
                }
            }
        }
    }

    fun updateAmount(newAmount: String) {
        amount = newAmount
    }

    fun updatePlace(newPlace: String) {
        place = newPlace
    }

    fun updateCategory(newCategory: ExpenseCategory) {
        category = newCategory
    }

    fun updateDate(newDate: Date) {
        date = newDate
    }

    fun updateRecurring(recurring: Boolean) {
        isRecurring = recurring
        if (!recurring) {
            recurringPeriod = null
        }
    }

    fun updateRecurringPeriod(period: RecurringPeriod) {
        recurringPeriod = period
    }

    fun saveExpense(onComplete: () -> Unit) {
        viewModelScope.launch {
            val amountDouble = amount.toDoubleOrNull() ?: return@launch
            val newExpense = expense?.copy(
                amount = amountDouble,
                place = place,
                category = category,
                date = date,
                isRecurring = isRecurring,
                recurringPeriod = recurringPeriod,
                nextDueDate = if (isRecurring) calculateNextDueDate(date, recurringPeriod!!) else null
            ) ?: Expense(
                amount = amountDouble,
                place = place,
                category = category,
                date = date,
                isRecurring = isRecurring,
                recurringPeriod = recurringPeriod,
                nextDueDate = if (isRecurring) calculateNextDueDate(date, recurringPeriod!!) else null
            )

            if (expense == null) {
                repository.addExpense(newExpense)
            } else {
                repository.updateExpense(newExpense)
            }
            onComplete()
        }
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

    fun deleteExpense(onComplete: () -> Unit) {
        viewModelScope.launch {
            expense?.let {
                repository.deleteExpense(it)
                onComplete()
            }
        }
    }
}