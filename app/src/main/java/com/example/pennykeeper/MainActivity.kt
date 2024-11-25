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
import com.example.pennykeeper.data.repository.CategoryRepository
import com.example.pennykeeper.data.repository.ExpenseRepository


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ExpenseDatabase.getDatabase(applicationContext)

        val expenseRepository = ExpenseRepository(
            expenseDao = database.expenseDao(),
            categoryDao = database.categoryDao()
        )
        val categoryRepository = CategoryRepository(database.categoryDao())

        setContent {

            MaterialTheme(

            ) {
                PennyKeeper(
                    expenseRepository = expenseRepository,
                    categoryRepository = categoryRepository
                )
            }
        }
    }
}