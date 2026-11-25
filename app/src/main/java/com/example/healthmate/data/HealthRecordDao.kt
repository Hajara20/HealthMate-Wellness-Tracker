package com.example.healthmate.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {
    @Query("SELECT * FROM health_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<HealthRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HealthRecord)

    @Delete
    suspend fun deleteRecord(record: HealthRecord)

    @Query("SELECT * FROM health_records WHERE date = :searchDate LIMIT 1")
    suspend fun getRecordByDate(searchDate: String): HealthRecord?

    @Update
    suspend fun updateRecord(record: HealthRecord): Int
}