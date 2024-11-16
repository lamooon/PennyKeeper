package com.example.pennykeeper.data.repository

import com.example.pennykeeper.data.dao.SettingsDao
import com.example.pennykeeper.data.model.Budget
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val settingsDao: SettingsDao) {

    // Get current budget from the database
    fun getCurrentBudget(): Flow<Budget> {
        return settingsDao.getCurrentBudget()
    }

    // Save new budget to the database
    suspend fun saveBudget(budget: Double) {
        settingsDao.insert(Budget(dailyBudget = budget))
    }
}
