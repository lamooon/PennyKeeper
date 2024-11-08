package com.example.pennykeeper.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pennykeeper.data.model.Expense
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToAdd: () -> Unit,
) {
    val expenses by homeViewModel.expenses.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top 1/3: Habit Tracker (kept blank for now)
            Box(
                modifier = Modifier
                    .weight(0.33f)
                    .fillMaxWidth()
            ) {
                HabitTrackerSection()
            }

            // Bottom 2/3: Financial Manager (Expense List)
            Box(
                modifier = Modifier
                    .weight(0.67f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(expenses) { expense ->
                        ExpenseCard(
                            expense = expense,
                            onClick = { onNavigateToEdit(expense.id) }
                        )
                    }
                }
            }
        }

        // FAB to navigate to AddScreen
        FloatingActionButton(
            onClick = onNavigateToAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Expense")
        }
    }
}

@Composable
private fun HabitTrackerSection() {
    // Placeholder for Habit Tracker
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Habit Tracker implementation will go here
    }
}

@Composable
private fun ExpenseCard(
    expense: Expense,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = expense.place,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = expense.category.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(expense.date),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}