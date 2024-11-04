package com.example.pennykeeper.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pennykeeper.data.dao.ExpenseDao
import com.example.pennykeeper.data.model.Converters
import com.example.pennykeeper.data.model.Expense

@Database(entities = [Expense::class], version = 1)
@TypeConverters(Converters::class)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var Instance: ExpenseDatabase? = null

        fun getDatabase(context: Context): ExpenseDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ExpenseDatabase::class.java,
                    "expense_database"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}