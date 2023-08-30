package com.example.avianwatch.data

import java.util.UUID

data class StepData(
    val stepId: UUID,
    val stepInstruction: String,
    val stepDistance: Double,
    val stepDuration: Double,
    val stepStartLocationLatitude: Double,
    val stepStartLocationLongitude: Double,
    val stepEndLocationLatitude: Double,
    val stepEndLocationLongitude: Double
)