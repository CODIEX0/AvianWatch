package com.example.avianwatch.api

import com.example.avianwatch.data.Hotspot
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface eBirdApiService {

    @POST("data/hotspot")
    fun createHotspot(
        @Body hotspot: Hotspot,
        @Query("key") apiKey: String
    ): Call<Hotspot> // created hotspot

    @GET("data/obs/geo/recent")
    fun getNearbyHotspots(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("key") apiKey: String
    ): Call<List<Hotspot>> // return nearby hotspots
}