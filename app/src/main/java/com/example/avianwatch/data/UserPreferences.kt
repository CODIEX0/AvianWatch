package com.example.avianwatch.data

import com.google.maps.model.TravelMode

data class UserPreferences(
    var userUID: String? = null,
    var unitSystem: String = "Metric",
    var maxRadius: Int = 50
)