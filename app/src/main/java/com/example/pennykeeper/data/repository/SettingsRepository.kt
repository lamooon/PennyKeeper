// SettingsRepository.kt

package com.example.pennykeeper.data.repository

import com.example.pennykeeper.data.dao.SettingsDao
import com.example.pennykeeper.data.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val settingsDao: SettingsDao) {

    // Expose the budget as a Flow
    val budgetFlow: Flow<Double> = settingsDao.getSettings()
        .map { settings ->
            settings?.budget ?: 0.0
        }

    // Suspend function to save the budget
    suspend fun saveBudget(budget: Double) {
        val settings = Settings(budget = budget)
        settingsDao.insertSettings(settings)
    }
}