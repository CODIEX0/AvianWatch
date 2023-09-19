package com.example.avianwatch.data

import java.util.Date

data class ObservationItem(
    var uid: String?,
    val birdName: String,
    val date: String?,
    val location: String,
    val notes: String,
    val image: String?
)