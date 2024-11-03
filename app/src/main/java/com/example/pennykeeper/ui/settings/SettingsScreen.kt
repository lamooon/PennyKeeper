// SettingsScreen.kt

package com.example.pennykeeper.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double): String {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return currencyFormatter.format(amount)
}
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
    )
) {
    val budgetState by settingsViewModel.budget.collectAsState()
    var newBudgetInput by remember { mutableStateOf(budgetState.toString()) }
    val isBudgetSaved by settingsViewModel.isBudgetSaved.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Current Budget: ${formatCurrency(budgetState)}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = newBudgetInput,
            onValueChange = { newBudgetInput = it },
            label = { Text("Set New Budget") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = newBudgetInput.isNotEmpty() && newBudgetInput.toDoubleOrNull() == null
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val budget = newBudgetInput.toDoubleOrNull()
                if (budget != null) {
                    settingsViewModel.saveBudget(budget)
                }
            },
            enabled = newBudgetInput.toDoubleOrNull() != null,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Budget")
        }

        if (isBudgetSaved) {
            Text(
                text = "Budget saved successfully!",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}