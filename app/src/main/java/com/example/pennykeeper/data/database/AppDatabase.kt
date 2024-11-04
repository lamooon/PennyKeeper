// AppDatabase.kt

package com.example.pennykeeper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pennykeeper.data.converters.DateConverter
import com.example.pennykeeper.data.converters.ExpenseCategoryConverter
import com.example.pennykeeper.data.dao.SettingsDao
import com.example.pennykeeper.data.model.Expense
import com.example.pennykeeper.data.model.Settings

@Database(entities = [Expense::class, Settings::class], version = 1)
@TypeConverters(DateConverter::class, ExpenseCategoryConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
}

