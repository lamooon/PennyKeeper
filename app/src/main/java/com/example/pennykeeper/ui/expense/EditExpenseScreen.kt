package com.example.pennykeeper.ui.expense

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pennykeeper.data.model.ExpenseCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(
    viewModel: EditExpenseViewModel,
    expenseId: Int,
    onNavigateBack: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val expense by viewModel.expense.collectAsState()

    LaunchedEffect(expenseId) {
        viewModel.loadExpense(expenseId)
    }

    expense?.let { currentExpense ->
        var amount by remember { mutableStateOf(currentExpense.amount.toString()) }
        var place by remember { mutableStateOf(currentExpense.place) }
        var category by remember { mutableStateOf(currentExpense.category) }
        var showCategoryDropdown by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            TextField(
                value = place,
                onValueChange = { place = it },
                label = { Text("Place") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Box(modifier = Modifier.padding(vertical = 8.dp)) {
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val amountDouble = amount.toDoubleOrNull() ?: 0.0
                        if (amountDouble > 0 && place.isNotBlank()) {
                            viewModel.updateExpense(amountDouble, place, category)
                            onNavigateBack()
                        }
                    }
                ) {
                    Text("Update")
                }

                Button(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            }
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Expense") },
                text = { Text("Are you sure you want to delete this expense?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteExpense()
                            showDeleteConfirmation = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}