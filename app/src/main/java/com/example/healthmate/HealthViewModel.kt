package com.example.healthmate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.data.HealthDatabase
import com.example.healthmate.data.HealthRecord
import com.example.healthmate.data.HealthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HealthRepository
    val allRecords: StateFlow<List<HealthRecord>>

    init {
        val healthDao = HealthDatabase.getDatabase(application).healthRecordDao()
        repository = HealthRepository(healthDao)

        allRecords = repository.allRecords.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addRecord(date: String, steps: Int, calories: Int, water: Int) {
        viewModelScope.launch {
            val newRecord = HealthRecord(
                date = date,
                steps = steps,
                calories = calories,
                water = water
            )
            repository.insertRecord(newRecord)
        }
    }

    fun deleteRecord(record: HealthRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }
}