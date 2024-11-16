package com.example.pennykeeper.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

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

    // Function to save the budget
    fun saveBudget(budget: Double) {
        viewModelScope.launch {
            settingsRepository.saveBudget(budget)
            _isBudgetSaved.value = true
            kotlinx.coroutines.delay(2000)
            _isBudgetSaved.value = false
        }
    }
}
