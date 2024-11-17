package com.example.pennykeeper.ui.navigation

sealed class NavigationDestination(val route: String) {
    object Home : NavigationDestination("home")
    object Statistics : NavigationDestination("statistics")
    object Settings : NavigationDestination("settings")

    object EditExpense : NavigationDestination("edit/{expenseId}") {
        fun createRoute(expenseId: Int) = "edit/$expenseId"
        const val expenseIdArg = "expenseId"
    }
    object AddExpense : NavigationDestination("add")
}