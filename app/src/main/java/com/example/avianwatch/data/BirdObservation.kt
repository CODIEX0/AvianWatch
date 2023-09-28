package com.example.avianwatch.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class BirdObservation(
    val userID: String? = null,
    val oid: String? = null,
    val birdSpecies: String,
    val additionalNotes: String,
    val birdImage: String?,
    val dateTime: String?,
    val hotspot: Hotspot
)