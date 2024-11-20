package com.example.pennykeeper.ui.home

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pennykeeper.data.model.ExpenseUiModel
import kotlinx.coroutines.launch
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
    val dailyLimit by homeViewModel.dailyBudgetFlow.collectAsState(initial = 0.0)

    // Compute the spent amount for today
    val spentAmount = remember(expenses) {
        val today = Calendar.getInstance()
        expenses.filter { expense ->
            val expenseDate = Calendar.getInstance().apply { time = expense.date }
            expenseDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    expenseDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        }.sumOf { it.amount }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Determine message based on spending ratio
    val message = when {
        dailyLimit == 0.0 -> ""
        spentAmount / dailyLimit > 1.0 -> "🚨 You've exceeded your budget! 🐷💸"
        spentAmount / dailyLimit > 0.8 -> "⚠️ You're at ${"%.1f".format((spentAmount / dailyLimit) * 100)}%! 🐷"
        spentAmount / dailyLimit > 0.5 -> "😊 Keep an eye on spending: ${"%.1f".format((spentAmount / dailyLimit) * 100)}%."
        spentAmount / dailyLimit > 0.3 -> "👍 Doing well! ${"%.1f".format((spentAmount / dailyLimit) * 100)}% used. 🌟"
        spentAmount / dailyLimit > 0.1 -> "✨ Great start! ${"%.1f".format((spentAmount / dailyLimit) * 100)}% spent. 🌈"
        else -> ""
    }

    // Show the appropriate snackbar message
    LaunchedEffect(spentAmount, dailyLimit) {
        if (message.isNotEmpty()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.33f)
                            .fillMaxWidth()
                    ) {
                        HabitTrackerSection(dailyLimit = dailyLimit, spentAmount = spentAmount)
                    }

                    // SnackbarHost with centered text
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = { snackbarData ->
                            Snackbar(
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Text(
                                    text = snackbarData.visuals.message,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

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
                                    .padding(horizontal = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                items(expenses) { expense ->
                                    ExpenseCard(
                                        expense = expense,
                                        onClick = { onNavigateToEdit(expense.id) }
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@SuppressLint("DefaultLocale")
@Composable
private fun HabitTrackerSection(
    dailyLimit: Double,
    spentAmount: Double
) {
    val remainingBudget = dailyLimit - spentAmount
    val progress = if (dailyLimit > 0) {
        (spentAmount / dailyLimit).coerceIn(0.0, 1.0)
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Daily Limit Text
        Text(
            text = "Daily Limit: $${String.format("%.2f", dailyLimit)}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Progress Bar
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        )

        // Spent and Remaining Text
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Spent: $${String.format("%.2f", spentAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Remaining: $${String.format("%.2f", remainingBudget)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun ExpenseCard(
    expense: ExpenseUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Place and Category
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = expense.place,
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = expense.categoryName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (expense.isRecurring) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recurring",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Right side: Amount and Date
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = SimpleDateFormat("MMM d", Locale.getDefault())
                        .format(expense.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }


}