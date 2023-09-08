package com.example.avianwatch.data

import java.util.Date

data class ObservationItem(
    val birdName: String,
    val date: Date,
    val location: String,
    val notes: String,
    val image: String?
)