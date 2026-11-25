package com.example.healthmate.data

import kotlinx.coroutines.flow.Flow

class HealthRepository(private val healthRecordDao: HealthRecordDao) {

    // Observe all records as a Flow for reactive UI updates
    val allRecords: Flow<List<HealthRecord>> = healthRecordDao.getAllRecords()

    // Insert a single health record
    suspend fun insertRecord(record: HealthRecord) {
        healthRecordDao.insertRecord(record)
    }

    // Delete a health record
    suspend fun deleteRecord(record: HealthRecord) {
        healthRecordDao.deleteRecord(record)
    }

    // Update an existing health record and return the number of rows updated
    suspend fun updateRecord(record: HealthRecord): Int {
        return healthRecordDao.updateRecord(record)
    }

    // Retrieve a single health record matching a specific date
    suspend fun getRecordByDate(date: String): HealthRecord? {
        return healthRecordDao.getRecordByDate(date)
    }
}