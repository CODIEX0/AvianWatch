package com.example.avianwatch.data

data class UserPreferences(
    var userUID: String? = null,
    var unitSystem: String = "Metric",
    var maxRadius: Int = 50
)