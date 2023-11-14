package com.example.avianwatch.data

import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

data class HotspotWithMarker(
    val hotspot: Hotspot,
    var marker: MarkerOptions
)

