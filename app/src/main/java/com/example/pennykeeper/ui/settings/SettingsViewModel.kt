// SettingsViewModel.kt

package com.example.pennykeeper.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennykeeper.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    // Expose the budget as a StateFlow for the UI to observe
    val budget: StateFlow<Double> = settingsRepository.budgetFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // MutableStateFlow to track whether the budget was saved successfully
    private val _isBudgetSaved = MutableStateFlow(false)
    val isBudgetSaved: StateFlow<Boolean> = _isBudgetSaved.asStateFlow()

    // Function to save the budget
    fun saveBudget(budget: Double) {
        viewModelScope.launch {
            settingsRepository.saveBudget(budget)
            // Update the isBudgetSaved flag to true
            _isBudgetSaved.value = true
            // Optionally, reset the flag after a delay
            kotlinx.coroutines.delay(2000)
            _isBudgetSaved.value = false
        }
    }
}