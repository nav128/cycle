package com.cycletracker.app

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "temperature_entries")
data class TemperatureEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long, // timestamp in milliseconds
    val hour: Int, // hour of day (0-23)
    val minute: Int, // minute of hour (0-59)
    val temperature: Double // body temperature in Celsius
)
