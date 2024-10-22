package com.example.pennykeeper


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pennykeeper.ui.navigation.Navigation
import com.example.pennykeeper.ui.theme.PennykeeperTheme
import com.example.pennykeeper.data.repository.ExpenseRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val expenseRepository = ExpenseRepository()

        setContent {
            PennykeeperTheme() {
                Navigation(expenseRepository)
            }
        }
    }
}