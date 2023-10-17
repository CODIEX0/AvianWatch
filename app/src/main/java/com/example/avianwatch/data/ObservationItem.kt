package com.example.avianwatch.data

import java.util.Date

data class ObservationItem(
    var uid: String? = null,
    val birdName: String = "",
    val date: String? = null,
    val location: String = "",
    val notes: String = "",
    val image: String? = null,
    val hotspot: Hotspot? = null
)