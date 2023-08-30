package com.example.avianwatch.data

import java.util.Date
import java.util.UUID

data class UserBirdObservation(
    val observationId: UUID,
    val birdSpecies: String,
    val observationDateTime: Date,
    val locationLatitude: Double,
    val locationLongitude: Double,
    val additionalNotes: String
)