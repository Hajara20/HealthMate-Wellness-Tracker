# HealthMate – Personal Health & Wellness Tracker

## 1. Project Overview

HealthMate is a native Android application developed to help users record, manage and monitor their daily health and wellness activities. The application allows users to track important health information, including daily steps, calories burned and water intake.

The main purpose of HealthMate is to encourage users to maintain healthy habits by providing an organised way to record and review their daily activities.

## 2. Main Features

- **Dashboard:** Displays a summary of the user's daily health activities.
- **Add Health Records:** Allows users to enter daily steps, calories burned and water intake.
- **View Health History:** Displays previously saved health records.
- **Edit Records:** Allows users to update existing health information.
- **Delete Records:** Allows users to remove unwanted records.
- **Search Records:** Helps users find health records by date.
- **Water Quick Add:** Provides a convenient way to add water intake.
- **Input Validation:** Checks user inputs before saving records.
- **Water Reminders:** Provides reminders to encourage regular water intake.

## 3. Technologies Used

| Technology and the Purpose |

| Kotlin -Main programming language |
| Android Studio - Development environment |
| Jetpack Compose - User interface development |
| Material 3- UI components and styling |
| Room Database - Local data storage using SQLite |
| ViewModel- Manages UI-related data |
| StateFlow-Observes and updates application state |
| Kotlin Coroutines-Supports asynchronous operations |
| Git and GitHub - Version control and source code management |

## 4. Application Architecture

HealthMate follows the Model-View-ViewModel (MVVM) architecture.

The application separates the user interface, application logic and data management into different components. The Repository communicates with the Room database, while the ViewModel manages application data and provides it to the user interface.

## 5. Database

HealthMate uses the Room persistence library to store health records locally on the device. Room provides an abstraction layer over SQLite and supports database operations such as inserting, retrieving, updating and deleting records.

## 6. Project Structure

com.example.healthmate
│
├── data
│   ├── HealthDatabase.kt
│   ├── HealthRecord.kt
│   ├── HealthRecordDao.kt
│   └── HealthRepository.kt
│
├── ui
│   ├── AddRecordScreen.kt
│   ├── DashboardScreen.kt
│   ├── EditRecordDialog.kt
│   ├── HealthViewModel.kt
│   ├── HistoryScreen.kt
│   ├── ValidationHelper.kt
│   └── theme
│
└── MainActivity.kt
```                                        
 7. Installation and Setup

1. Clone or download this repository.
2. Open Android Studio.
3. Select **Open** and choose the downloaded HealthMate project folder.
4. Allow Gradle to synchronise the project.
5. Connect an Android device or start an emulator.
6. Run the application using Android Studio.

8. Development Purpose

This application was developed as part of the Mobile Application Development coursework for the BSc (Hons) Computing Science in Software Engineering programme at Kingston University.

Author: Fathima Hajara Fareed
HealthMate – Personal Health & Wellness Tracker
