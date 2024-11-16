package com.example.pennykeeper.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pennykeeper.data.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert
    suspend fun insert(budget: Budget)

    @Query("SELECT * FROM settings_table ORDER BY id DESC LIMIT 1")
    fun getCurrentBudget(): Flow<Budget>
}
