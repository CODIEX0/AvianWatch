package com.example.avianwatch.data

import com.google.android.gms.maps.model.LatLng

data class Hotspot(
    val observationID: String?,
    val locationName: String,
    val latLng: LatLng
)