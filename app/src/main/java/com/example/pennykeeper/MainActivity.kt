package com.example.pennykeeper

import PennyKeeper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pennykeeper.data.database.ExpenseDatabase
import com.example.pennykeeper.data.database.SettingsDatabase
import com.example.pennykeeper.data.repository.CategoryRepository
import com.example.pennykeeper.data.repository.ExpenseRepository
import com.example.pennykeeper.data.repository.SettingsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = ExpenseDatabase.getDatabase(applicationContext)
        val setting_database = SettingsDatabase.getDatabase(applicationContext)

        val expenseRepository = ExpenseRepository(
            expenseDao = database.expenseDao(),
            categoryDao = database.categoryDao()
        )
        val settingsRepository = SettingsRepository(setting_database.settingsDao())
        val categoryRepository = CategoryRepository(database.categoryDao())

        setContent {
            PennyKeeper(
                expenseRepository = expenseRepository,
                settingsRepository = settingsRepository,
                categoryRepository = categoryRepository
            )
        }
    }
}