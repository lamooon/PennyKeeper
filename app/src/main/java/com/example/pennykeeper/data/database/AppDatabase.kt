
// AppDatabase.kt

package com.example.pennykeeper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pennykeeper.data.dao.SettingsDao
import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.Settings

@Database(entities = [Settings::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
}