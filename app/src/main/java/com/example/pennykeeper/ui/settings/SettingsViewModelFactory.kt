// SettingsViewModelFactory.kt

package com.example.pennykeeper.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pennykeeper.data.database.DatabaseProvider
import com.example.pennykeeper.data.repository.SettingsRepository

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(context)
            val settingsDao = database.settingsDao()
            val repository = SettingsRepository(settingsDao)
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}