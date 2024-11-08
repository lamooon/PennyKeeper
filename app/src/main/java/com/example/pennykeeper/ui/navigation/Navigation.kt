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
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pennykeeper.data.repository.ExpenseRepository
import com.example.pennykeeper.ui.home.HomeScreen
import com.example.pennykeeper.ui.home.HomeViewModel
import com.example.pennykeeper.ui.stats.*
import com.example.pennykeeper.ui.settings.*
import com.example.pennykeeper.ui.expense.EditExpenseScreen
import com.example.pennykeeper.ui.expense.EditExpenseViewModel
import com.example.pennykeeper.ui.home.AddScreen
import kotlin.math.exp

@Composable
fun Navigation(expenseRepository: ExpenseRepository) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationDestination.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationDestination.Home.route) {
                val homeViewModel: HomeViewModel = viewModel {
                    HomeViewModel(expenseRepository)
                }
                HomeScreen(
                    homeViewModel = homeViewModel,
                    onNavigateToEdit = { expenseId ->
                        navController.navigate(NavigationDestination.EditExpense.createRoute(expenseId))
                    },
                    onNavigateToAdd = {
                        navController.navigate(NavigationDestination.AddExpense.route)
                    }
                )
            }

            composable(NavigationDestination.AddExpense.route) {
                AddScreen(
                    viewModel = viewModel { HomeViewModel(expenseRepository) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = NavigationDestination.EditExpense.route,
                arguments = listOf(
                    navArgument(NavigationDestination.EditExpense.expenseIdArg) {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getInt(NavigationDestination.EditExpense.expenseIdArg) ?: return@composable
                val editViewModel: EditExpenseViewModel = viewModel {
                    EditExpenseViewModel(expenseRepository)
                }
                EditExpenseScreen(
                    viewModel = editViewModel,
                    expenseId = expenseId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavigationDestination.Statistics.route) {
                val statisticsViewModel: StatisticsViewModel = viewModel {
                    StatisticsViewModel(expenseRepository)
                }
                StatisticsScreen(statisticsViewModel)
            }

            composable(NavigationDestination.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel()
                SettingsScreen(settingsViewModel)
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            route = NavigationDestination.Home.route,
            title = "Home",
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            route = NavigationDestination.Statistics.route,
            title = "Statistics",
            icon = Icons.AutoMirrored.Filled.List
        ),
        BottomNavItem(
            route = NavigationDestination.Settings.route,
            title = "Settings",
            icon = Icons.Default.Settings
        )
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

private data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)