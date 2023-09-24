package com.example.avianwatch.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class BirdObservation(
    val userID: String? = null,
    val birdSpecies: String,
    val birdImageData: String?,
    val dateTime: String?,
    val hotspot: Hotspot,
    val additionalNotes: String
)