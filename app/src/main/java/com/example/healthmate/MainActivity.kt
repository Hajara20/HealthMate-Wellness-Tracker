
package com.example.healthmate

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.healthmate.ui.AddRecordScreen
import com.example.healthmate.ui.DashboardScreen
import com.example.healthmate.ui.HealthViewModel
import com.example.healthmate.ui.HistoryScreen
import com.example.healthmate.ui.theme.HealthMateTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Application entry point that initializes HealthViewModel and hosts the Compose UI
class MainActivity : ComponentActivity() {

    private val viewModel: HealthViewModel by viewModels {
        HealthViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HealthMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

                    // Stores whether the first notification permission request has been made
                    val preferences = remember {
                        context.getSharedPreferences(
                            "HealthMatePreferences",
                            Context.MODE_PRIVATE
                        )
                    }

                    // Handles the user's Allow / Don't allow response
                    val notificationPermissionLauncher =
                        rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission()
                        ) { isGranted ->
                            // The user's choice is remembered by Android and our preferences.
                            // No notification is sent automatically here.
                        }

                    // Requests notification permission only on the first app launch
                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED

                            val requestAlreadyMade = preferences.getBoolean(
                                "notification_permission_requested",
                                false
                            )

                            if (hasPermission) {
                                // Permission was already granted
                                preferences.edit()
                                    .putBoolean(
                                        "notification_permission_requested",
                                        true
                                    )
                                    .apply()

                            } else if (!requestAlreadyMade) {
                                // Remember the request before displaying the permission dialog
                                preferences.edit()
                                    .putBoolean(
                                        "notification_permission_requested",
                                        true
                                    )
                                    .apply()

                                notificationPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                        }
                    }

                    // Tracks active destination tab: 0 = Dashboard, 1 = Add Entry, 2 = History
                    var selectedTab by remember { mutableIntStateOf(0) }

                    // Observes reactive health record list from ViewModel StateFlow
                    val records by viewModel.allRecords.collectAsState()

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Home,
                                            contentDescription = "Dashboard"
                                        )
                                    },
                                    label = { Text("Dashboard") },
                                    selected = selectedTab == 0,
                                    onClick = { selectedTab = 0 }
                                )

                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Add Record"
                                        )
                                    },
                                    label = { Text("Add Entry") },
                                    selected = selectedTab == 1,
                                    onClick = { selectedTab = 1 }
                                )

                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.AutoMirrored.Filled.List,
                                            contentDescription = "History"
                                        )
                                    },
                                    label = { Text("History") },
                                    selected = selectedTab == 2,
                                    onClick = { selectedTab = 2 }
                                )
                            }
                        }
                    ) { innerPadding ->

                        // Respects Scaffold insets so content clears the bottom navigation bar
                        Box(
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            when (selectedTab) {

                                // Dashboard Screen
                                0 -> DashboardScreen(
                                    records = records,
                                    onAddClick = {
                                        selectedTab = 1
                                    },
                                    onQuickAddWater = { amountMl ->

                                        val today = SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                        ).format(Date())

                                        // Adds water to today's existing record
                                        // instead of creating a duplicate record
                                        viewModel.addWaterIntake(
                                            date = today,
                                            amount = amountMl
                                        )
                                    }
                                )

                                // Add Record Screen
                                1 -> AddRecordScreen(
                                    viewModel = viewModel,
                                    onRecordAdded = {
                                        selectedTab = 2
                                    }
                                )

                                // History Screen
                                2 -> HistoryScreen(
                                    records = records,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper function to create the notification channel and display a water reminder
fun showWaterReminderNotification(context: Context) {

    val channelId = "water_reminder_channel"

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Water Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Reminds you to stay hydrated throughout the day"
        }

        notificationManager.createNotificationChannel(channel)
    }

    // Check permission before sending a notification on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Time to Hydrate! 💧")
        .setContentText(
            "Don't forget to drink a glass of water and log it in HealthMate."
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(1001, notification)
}
