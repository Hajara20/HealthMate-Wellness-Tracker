package com.example.healthmate.ui

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

// Singleton helper object providing reusable input validation for health records
object ValidationHelper {

    // Validates date format, real calendar date, and non-negative numeric fields; returns error string or null
    fun validate(date: String, steps: String, calories: String, water: String): String? {
        val trimmedDate = date.trim()

        // Rejects strings that do not match the strict YYYY-MM-DD pattern
        if (!Regex("""^\d{4}-\d{2}-\d{2}$""").matches(trimmedDate)) {
            return "Date must be in YYYY-MM-DD format."
        }

        // Creates a non-lenient formatter so impossible dates (like 2026-02-30) fail parsing
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
        }
        try {
            sdf.parse(trimmedDate)
        } catch (_: ParseException) {
            return "Please enter a valid calendar date."
        }

        val s = steps.trim().toIntOrNull()
        val c = calories.trim().toIntOrNull()
        val w = water.trim().toIntOrNull()

        if (s == null || s < 0) return "Steps must be 0 or greater."
        if (c == null || c < 0) return "Calories must be 0 or greater."
        if (w == null || w < 0) return "Water intake must be 0 or greater."

        return null
    }
}
