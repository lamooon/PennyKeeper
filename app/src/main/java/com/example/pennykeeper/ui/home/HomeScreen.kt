package com.example.pennykeeper.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.ExpenseUiModel
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Top 1/3: Habit Tracker
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
                if (expenses.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Press + button to add expenses.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(expenses) { expense ->
                            ExpenseCard(
                                expense = expense,
                                onClick = { onNavigateToEdit(expense.id) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

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
    expense: ExpenseUiModel,
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = expense.categoryName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (expense.isRecurring) {
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = expense.recurringPeriod?.name ?: "",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Recurring",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(expense.date),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}