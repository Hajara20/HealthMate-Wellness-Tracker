package com.example.healthmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.healthmate.ui.theme.HealthMateTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: HealthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedTab by remember { mutableIntStateOf(0) }
                    val records by viewModel.allRecords.collectAsState()

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                                    label = { Text("Dashboard") },
                                    selected = selectedTab == 0,
                                    onClick = { selectedTab = 0 }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Record") },
                                    label = { Text("Add Entry") },
                                    selected = selectedTab == 1,
                                    onClick = { selectedTab = 1 }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "History") },
                                    label = { Text("History") },
                                    selected = selectedTab == 2,
                                    onClick = { selectedTab = 2 }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (selectedTab) {
                                0 -> DashboardScreen(records)
                                1 -> AddRecordScreen(viewModel, onRecordAdded = { selectedTab = 2 })
                                2 -> HistoryScreen(records, viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(records: List<com.example.healthmate.data.HealthRecord>) {
    // Get today's date string format matching dummy/input format YYYY-MM-DD
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val todayRecord = records.find { it.date == currentDate } ?: com.example.healthmate.data.HealthRecord(date = currentDate, steps = 0, calories = 0, water = 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "HealthMate Dashboard", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Today – $currentDate", style = MaterialTheme.typography.titleMedium, color = Color.Gray)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Steps
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👣 Steps:", modifier = Modifier.width(140.dp), style = MaterialTheme.typography.bodyLarge)
                    Text(text = "${todayRecord.steps}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
                // Calories (Red theme as requested)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔥 Calories:", modifier = Modifier.width(140.dp), style = MaterialTheme.typography.bodyLarge)
                    Text(text = "${todayRecord.calories} kcal", style = MaterialTheme.typography.titleMedium, color = Color(0xFFD32F2F))
                }
                // Water (Blue theme as requested)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💧 Water Intake:", modifier = Modifier.width(140.dp), style = MaterialTheme.typography.bodyLarge)
                    Text(text = "${todayRecord.water} ml", style = MaterialTheme.typography.titleMedium, color = Color(0xFF1976D2))
                }
            }
        }
    }
}

@Composable
fun AddRecordScreen(viewModel: HealthViewModel, onRecordAdded: () -> Unit) {
    var dateInput by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var stepsInput by remember { mutableStateOf("") }
    var caloriesInput by remember { mutableStateOf("") }
    var waterInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Add Daily Health Entry", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = dateInput,
            onValueChange = { dateInput = it },
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = stepsInput,
            onValueChange = { stepsInput = it },
            label = { Text("Steps Walked") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = caloriesInput,
            onValueChange = { caloriesInput = it },
            label = { Text("Calories Burned (kcal)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = waterInput,
            onValueChange = { waterInput = it },
            label = { Text("Water Intake (ml)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val steps = stepsInput.toIntOrNull()
                val calories = caloriesInput.toIntOrNull()
                val water = waterInput.toIntOrNull()

                if (dateInput.isBlank()) {
                    errorMessage = "Date cannot be blank."
                } else if (steps == null || calories == null || water == null) {
                    errorMessage = "Please enter valid numeric values for steps, calories, and water."
                } else {
                    viewModel.addRecord(dateInput, steps, calories, water)
                    onRecordAdded()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Entry")
        }
    }
}

@Composable
fun HistoryScreen(records: List<com.example.healthmate.data.HealthRecord>, viewModel: HealthViewModel) {
    var searchQuery by remember { mutableStateOf("") }

    // Filter records by date search query
    val filteredRecords = records.filter { it.date.contains(searchQuery, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Health Records History", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search/Filter by Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredRecords) { record ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "📅 ${record.date}", style = MaterialTheme.typography.titleSmall)
                            Text(text = "👣 Steps: ${record.steps}")
                            Text(text = "🔥 Calories: ${record.calories} kcal", color = Color(0xFFD32F2F))
                            Text(text = "💧 Water: ${record.water} ml", color = Color(0xFF1976D2))
                        }
                        IconButton(onClick = { viewModel.deleteRecord(record) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Record", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}