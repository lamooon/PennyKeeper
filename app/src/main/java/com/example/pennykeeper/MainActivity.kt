package com.example.pennykeeper

import PennyKeeper
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.collectAsState
import com.example.pennykeeper.data.database.ExpenseDatabase
import com.example.pennykeeper.data.database.SettingsDatabase
import com.example.pennykeeper.data.repository.CategoryRepository
import com.example.pennykeeper.data.repository.ExpenseRepository
import com.example.pennykeeper.data.repository.SettingsRepository
import com.example.pennykeeper.data.repository.ThemeRepository


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ExpenseDatabase.getDatabase(applicationContext)
        val settingDatabase = SettingsDatabase.getDatabase(applicationContext)

        val expenseRepository = ExpenseRepository(
            expenseDao = database.expenseDao(),
            categoryDao = database.categoryDao()
        )
        val settingsRepository = SettingsRepository(settingDatabase.settingsDao())
        val categoryRepository = CategoryRepository(database.categoryDao())
        val themeRepository = ThemeRepository(applicationContext)

        setContent {
            val isDarkMode = themeRepository.isDarkMode.collectAsState().value

            MaterialTheme(
                colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()
            ) {
                PennyKeeper(
                    expenseRepository = expenseRepository,
                    settingsRepository = settingsRepository,
                    categoryRepository = categoryRepository,
                    themeRepository = themeRepository
                )
            }
        }
    }
}