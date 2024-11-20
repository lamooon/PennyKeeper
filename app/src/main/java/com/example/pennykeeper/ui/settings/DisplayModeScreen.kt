package com.example.pennykeeper.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pennykeeper.ui.theme.saveThemePreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayModeScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    // Observe the current dark mode state from the ViewModel
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    // Set MaterialTheme based on the dark mode state
    MaterialTheme(
        colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Display Mode") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Select Display Mode", style = MaterialTheme.typography.titleLarge)

                // Light Mode Option
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !isDarkMode,
                        onClick = {
                            settingsViewModel.toggleTheme(false) // Switch to Light Mode
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Light Mode")
                }

                // Dark Mode Option
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isDarkMode,
                        onClick = {
                            settingsViewModel.toggleTheme(true) // Switch to Dark Mode
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dark Mode")
                }
            }
        }
    }
}
