package com.example.pennykeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pennykeeper.data.ExpenseDatabase
import com.example.pennykeeper.data.repository.ExpenseRepository
import com.example.pennykeeper.ui.navigation.Navigation
import com.example.pennykeeper.ui.theme.PennykeeperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ExpenseDatabase.getDatabase(applicationContext)
        val expenseRepository = ExpenseRepository(database.expenseDao())

        setContent {
            PennykeeperTheme {
                Navigation(expenseRepository)
            }
        }
    }
}