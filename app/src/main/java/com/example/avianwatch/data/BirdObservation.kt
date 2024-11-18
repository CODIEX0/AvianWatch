package com.example.avianwatch.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class BirdObservation(
    val userID: String,
    val oid: String,
    val birdSpecies: String,
    val additionalNotes: String,
    val birdImage: String?,
    val dateTime: String?,
    val location: String
){
    constructor() : this("", "", "", "", null, null, "")
}