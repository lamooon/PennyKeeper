// SettingsDao.kt

package com.example.pennykeeper.data.dao

import androidx.room.*
import com.example.pennykeeper.data.model.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: Settings)

    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<Settings?>
}