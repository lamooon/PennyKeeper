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
import com.example.pennykeeper.data.model.ExpenseCategory

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onNavigateToEdit: (Int) -> Unit
) {
    val expenses by homeViewModel.expenses.collectAsState()
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Expenses",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(expenses) { expense ->
                    ExpenseItem(
                        expense = expense,
                        onClick = { onNavigateToEdit(expense.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddExpenseDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Expense")
        }
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onAddExpense = { amount, place, category ->
                homeViewModel.addExpense(amount, place, category)
                showAddExpenseDialog = false
            }
        )
    }
}





@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onAddExpense: (Double, String, ExpenseCategory) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ExpenseCategory.OTHER) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = place,
                    onValueChange = { place = it },
                    label = { Text("Place") },
                    modifier = Modifier.fillMaxWidth()
                )
                Box {
                    OutlinedButton(
                        onClick = { showCategoryDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(category.name)
                    }
                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        ExpenseCategory.values().forEach { expenseCategory ->
                            DropdownMenuItem(
                                text = { Text(expenseCategory.name) },
                                onClick = {
                                    category = expenseCategory
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    if (amountDouble > 0 && place.isNotBlank()) {
                        onAddExpense(amountDouble, place, category)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ExpenseItem(expense: Expense, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = expense.place, style = MaterialTheme.typography.titleMedium)
            Text(text = "Amount: $${expense.amount}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Category: ${expense.category}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}