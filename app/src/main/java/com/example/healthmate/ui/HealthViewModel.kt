
package com.example.healthmate.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthmate.data.HealthDatabase
import com.example.healthmate.data.HealthRecord
import com.example.healthmate.data.HealthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// MVVM ViewModel exposing observable record state and running CRUD operations on coroutines
class HealthViewModel(private val repository: HealthRepository) : ViewModel() {

    // Converts repository Flow into a lifecycle-aware StateFlow for Compose UI observation
    val records: StateFlow<List<HealthRecord>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Alias preserving compatibility with callers referencing allRecords
    val allRecords: StateFlow<List<HealthRecord>> = records

    // Convenience overload constructing a HealthRecord from raw field values
    fun addRecord(
        date: String,
        steps: Int,
        calories: Int,
        water: Int,
        onResult: (Boolean) -> Unit = {}
    ) {
        addRecord(
            HealthRecord(
                date = date,
                steps = steps,
                calories = calories,
                water = water
            ),
            onResult = onResult
        )
    }

    // Inserts a new HealthRecord only if no record exists for the date
    fun addRecord(
        record: HealthRecord,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val existingRecord = repository.getRecordByDate(record.date)

            if (existingRecord != null) {
                onResult(false)
            } else {
                repository.insertRecord(record)
                onResult(true)
            }
        }
    }

    // Alias for addRecord to support callers using insertRecord
    fun insertRecord(
        date: String,
        steps: Int,
        calories: Int,
        water: Int,
        onResult: (Boolean) -> Unit = {}
    ) {
        addRecord(date, steps, calories, water, onResult)
    }

    // Alias for addRecord to support callers using insertRecord
    fun insertRecord(
        record: HealthRecord,
        onResult: (Boolean) -> Unit = {}
    ) {
        addRecord(record, onResult)
    }

    // Adds water to an existing daily record, or creates one if none exists
    fun addWaterIntake(date: String, amount: Int) {
        viewModelScope.launch {
            val existingRecord = repository.getRecordByDate(date)

            if (existingRecord != null) {
                repository.updateRecord(
                    existingRecord.copy(
                        water = existingRecord.water + amount
                    )
                )
            } else {
                repository.insertRecord(
                    HealthRecord(
                        date = date,
                        steps = 0,
                        calories = 0,
                        water = amount
                    )
                )
            }
        }
    }

    // Updates an existing HealthRecord asynchronously via HealthRepository
    fun updateRecord(record: HealthRecord) {
        viewModelScope.launch {
            repository.updateRecord(record)
        }
    }

    // Deletes a HealthRecord asynchronously via HealthRepository after UI confirmation
    fun deleteRecord(record: HealthRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }

    // Builds HealthViewModel wired to the Room database and HealthRepository
    class Factory(private val application: Application) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
                val database = HealthDatabase.getDatabase(application)
                val repository = HealthRepository(database.healthRecordDao())
                return HealthViewModel(repository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
