package com.example.pennykeeper.ui.navigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.pennykeeper.ui.home.HomeScreen
import com.example.pennykeeper.ui.home.HomeViewModel
import com.example.pennykeeper.ui.stats.StatisticsScreen
import com.example.pennykeeper.ui.stats.StatisticsViewModel
import com.example.pennykeeper.ui.settings.SettingsScreen
import com.example.pennykeeper.data.repository.ExpenseRepository
import com.example.pennykeeper.ui.expense.EditExpenseScreen
import com.example.pennykeeper.ui.expense.EditExpenseViewModel

@Composable
fun Navigation(expenseRepository: ExpenseRepository) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(navController, startDestination = "home", Modifier.padding(innerPadding)) {
            composable("home") {
                val homeViewModel: HomeViewModel = viewModel { HomeViewModel(expenseRepository) }
                HomeScreen(
                    homeViewModel = homeViewModel,
                    onNavigateToEdit = { expenseId ->
                        navController.navigate("edit/$expenseId")
                    }
                )
            }
            composable(
                route = "edit/{expenseId}",
                arguments = listOf(navArgument("expenseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getInt("expenseId") ?: return@composable
                val editViewModel: EditExpenseViewModel = viewModel {
                    EditExpenseViewModel(expenseRepository)
                }
                EditExpenseScreen(
                    viewModel = editViewModel,
                    expenseId = expenseId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("statistics") {
                val statisticsViewModel: StatisticsViewModel = viewModel {
                    StatisticsViewModel(expenseRepository)
                }
                StatisticsScreen(statisticsViewModel)
            }
            composable("settings") {
                SettingsScreen()
            }
        }
    }
}


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