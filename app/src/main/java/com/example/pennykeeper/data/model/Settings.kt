package com.example.pennykeeper.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings (
    @PrimaryKey val id: Int = 1, // Singleton pattern
    val budget: Double
)
