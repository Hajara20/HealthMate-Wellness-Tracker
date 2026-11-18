package com.example.healthmate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_records")
data class HealthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String,      // Format: "YYYY-MM-DD"
    val steps: Int,
    val calories: Int,
    val water: Int
)