package com.cycletracker.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface TemperatureDao {
    @Query("SELECT * FROM temperature_entries ORDER BY date ASC, hour ASC, minute ASC")
    suspend fun getAllEntries(): List<TemperatureEntry>

    @Query("SELECT * FROM temperature_entries WHERE date >= :startDate ORDER BY date ASC, hour ASC, minute ASC")
    suspend fun getEntriesSince(startDate: Long): List<TemperatureEntry>

    @Insert
    suspend fun insertEntry(entry: TemperatureEntry)

    @Delete
    suspend fun deleteEntry(entry: TemperatureEntry)

    @Query("DELETE FROM temperature_entries")
    suspend fun deleteAll()
}
