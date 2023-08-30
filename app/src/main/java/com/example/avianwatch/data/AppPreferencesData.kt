package com.example.avianwatch.data

import java.util.UUID

data class AppPreferencesData(
    val userId: UUID,
    val preferredUnitSystem: UnitSystem,
    val maximumDistanceForTravel: Double
)