package com.example.pennykeeper.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.pennykeeper.ui.theme.getThemePreference
import com.example.pennykeeper.ui.theme.saveThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.pennykeeper.ui.theme.PREFS_NAME

class ThemeRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Initialize _isDarkMode with the current theme preference from shared preferences
    private val _isDarkMode = MutableStateFlow(getThemePreference(context))  // Get theme preference using context
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    // Toggle the dark mode setting and save it to SharedPreferences
    fun toggleDarkMode() {
        val newMode = !_isDarkMode.value
        _isDarkMode.value = newMode
        saveThemePreference(context, newMode)  // Pass context, not sharedPreferences
    }
}
