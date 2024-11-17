package com.example.pennykeeper.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.repository.ExpenseRepository
import com.example.pennykeeper.data.repository.SettingsRepository
import com.example.pennykeeper.utils.ExpensePrediction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val expenseRepository: ExpenseRepository  // Add 'private val' here
) : ViewModel() {

    // Expose the budget as a StateFlow for the UI to observe
    val budget: StateFlow<Double> = settingsRepository.getCurrentBudget()
        .map { it.dailyBudget }  // Extract the daily budget from the Budget model
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    private val _isBudgetSaved = MutableStateFlow(false)
    val isBudgetSaved: StateFlow<Boolean> = _isBudgetSaved.asStateFlow()

    //prediction service
    private val _predictedExpense = MutableStateFlow<Double>(0.0)
    val predictedExpense: StateFlow<Double> = _predictedExpense.asStateFlow()

    init {
        viewModelScope.launch {
            calculatePrediction()
        }
    }

    // Function to save the budget
    fun saveBudget(budget: Double) {
        viewModelScope.launch {
            settingsRepository.saveBudget(budget)
            _isBudgetSaved.value = true
            kotlinx.coroutines.delay(2000)
            _isBudgetSaved.value = false
        }
    }

    private val _monthlyExpenseTrend = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val monthlyExpenseTrend: StateFlow<List<Pair<String, Double>>> = _monthlyExpenseTrend.asStateFlow()

    private suspend fun calculatePrediction() {
        val expenses = expenseRepository.getAllExpenses()

        // Group expenses by month and calculate monthly totals
        val monthlyExpenses = expenses
            .groupBy { expense ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = expense.date.time
                calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH)
            }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedBy { it.first }
            .mapIndexed { index, pair -> index to pair.second }

        // Prepare last 6 months trend data
        val last6Months = monthlyExpenses.takeLast(6)
        _monthlyExpenseTrend.value = last6Months.map { (monthIndex, amount) ->
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, monthIndex - monthlyExpenses.size + 1)
            val monthYear = "${calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())} ${calendar.get(Calendar.YEAR)}"
            monthYear to amount
        }

        val prediction = ExpensePrediction().predictNextMonthExpense(monthlyExpenses)
        _predictedExpense.value = prediction
    }
}