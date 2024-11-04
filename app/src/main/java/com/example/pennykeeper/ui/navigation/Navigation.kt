package com.example.pennykeeper.ui.navigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.room.Room
import com.example.pennykeeper.data.database.AppDatabase
import com.example.pennykeeper.ui.home.HomeScreen
import com.example.pennykeeper.ui.home.HomeViewModel
import com.example.pennykeeper.ui.stats.StatisticsScreen
import com.example.pennykeeper.ui.stats.StatisticsViewModel
import com.example.pennykeeper.ui.settings.SettingsScreen
import com.example.pennykeeper.data.repository.ExpenseRepository
import com.example.pennykeeper.ui.settings.SettingsViewModel
import com.example.pennykeeper.data.repository.SettingsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(expenseRepository: ExpenseRepository) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Initialize the database and repository
    val appDatabase = remember {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "penny_keeper_database"
        ).build()
    }

    val settingsDao = appDatabase.settingsDao()
    val settingsRepository = SettingsRepository(settingsDao)


    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(navController, startDestination = "home", Modifier.padding(innerPadding)) {
            composable("home") {
                val homeViewModel: HomeViewModel = viewModel { HomeViewModel(expenseRepository) }
                HomeScreen(homeViewModel)
            }
            composable("statistics") {
                val statisticsViewModel: StatisticsViewModel = viewModel { StatisticsViewModel(expenseRepository) }
                StatisticsScreen(statisticsViewModel)
            }
            composable("settings") {
                SettingsScreen()
        }
        }
    }
}

// BottomNavigationBar implementation remains the same

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("home", "Home", Icons.Default.Home),
        BottomNavItem("statistics", "Statistics", Icons.AutoMirrored.Filled.List),
        BottomNavItem("settings", "Settings", Icons.Default.Settings)
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(val route: String, val title: String, val icon: ImageVector)