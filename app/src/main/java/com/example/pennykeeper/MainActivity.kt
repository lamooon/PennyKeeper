package com.example.pennykeeper

import PennyKeeper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pennykeeper.data.database.ExpenseDatabase
import com.example.pennykeeper.data.repository.ExpenseRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = ExpenseDatabase.getDatabase(applicationContext)
        val expenseRepository = ExpenseRepository(database.expenseDao())

        setContent {
            PennyKeeper(expenseRepository)
        }
    }
}