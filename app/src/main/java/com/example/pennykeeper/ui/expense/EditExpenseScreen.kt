package com.example.pennykeeper.ui.expense

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pennykeeper.data.model.ExpenseCategory
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(
    viewModel: EditExpenseViewModel,
    expenseId: Int,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(expenseId) {
        viewModel.loadExpense(expenseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (expenseId == -1) "Add Expense" else "Edit Expense") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (expenseId != -1) {
                        IconButton(onClick = {
                            viewModel.deleteExpense(onNavigateBack)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.amount,
                onValueChange = { viewModel.updateAmount(it) },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.place,
                onValueChange = { viewModel.updatePlace(it) },
                label = { Text("Place") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { },
            ) {
                OutlinedTextField(
                    value = viewModel.category.name,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = false,
                    onDismissRequest = { }
                ) {
                    ExpenseCategory.values().forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = { viewModel.updateCategory(category) }
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.saveExpense(onNavigateBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (expenseId == -1) "Add" else "Save")
            }
        }
    }
}