package com.example.avianwatch.api

import com.example.avianwatch.data.Hotspot
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface eBirdApiService {

    @GET("ws1.1/ref/hotspot/region")
    fun getHotspotsByQuery(
        @Query("query") query: String,
        @Query("api_key") apiKey: String
    ): Call<List<Hotspot>>

    @GET("ws1.1/ref/hotspot/geo")
    fun getNearbyHotspots(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("api_key") apiKey: String
    ): Call<List<Hotspot>>
}