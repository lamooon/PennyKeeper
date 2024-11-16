package com.example.pennykeeper.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.*

fun formatCurrency(amount: Double): String {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return currencyFormatter.format(amount)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val budgetState by settingsViewModel.budget.collectAsState()
    var newBudgetInput by remember { mutableStateOf(budgetState.toString()) }
    val isBudgetSaved by settingsViewModel.isBudgetSaved.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Budget setting section
                Text(
                    text = "Current Budget: ${formatCurrency(budgetState)}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = newBudgetInput,
                    onValueChange = { newBudgetInput = it },
                    label = { Text("Set New Budget") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = newBudgetInput.isNotEmpty() && newBudgetInput.toDoubleOrNull() == null
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val budget = newBudgetInput.toDoubleOrNull()
                            if (budget != null) {
                                settingsViewModel.saveBudget(budget)
                            }
                        },
                        enabled = newBudgetInput.toDoubleOrNull() != null
                    ) {
                        Text("Save Budget")
                    }
                }

                if (isBudgetSaved) {
                    Text(
                        text = "Budget saved successfully!",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
