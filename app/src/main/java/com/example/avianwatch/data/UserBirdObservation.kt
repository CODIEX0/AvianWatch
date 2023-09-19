package com.example.avianwatch.data

import java.util.Date
import java.util.UUID

data class UserBirdObservation(
    val userUID: String? = null,
    val uuid: UUID,
    val birdSpecies: String,
    val birdImage: String?,
    val observationDateTime: String?,
    val location: String,
    val additionalNotes: String
)