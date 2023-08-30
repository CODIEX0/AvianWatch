package com.example.avianwatch.data

import java.util.UUID

data class HotspotData(
    val hotspotId: UUID,
    val hotspotName: String,
    val hotspotLatitude: Double,
    val hotspotLongitude: Double
)