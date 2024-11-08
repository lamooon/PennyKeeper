package com.example.pennykeeper.ui.stats

import androidx.lifecycle.ViewModel
import com.example.pennykeeper.data.repository.ExpenseRepository

class StatisticsViewModel(private val repository: ExpenseRepository) : ViewModel() {
    val expenses = repository.expenses

    // Add statistics-related logic here
}