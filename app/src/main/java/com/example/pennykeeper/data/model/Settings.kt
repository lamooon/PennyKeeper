package com.example.pennykeeper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings_table")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dailyBudget: Double
)
