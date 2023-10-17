package com.example.avianwatch.data

data class Hotspot(
    val id: String,
    val locName: String,
    val comName: String,
    val lat: Double,
    val lng: Double,
){
    constructor() : this("", "", "", 0.0, 0.0)
}