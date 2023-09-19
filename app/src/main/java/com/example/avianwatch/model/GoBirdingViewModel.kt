package com.example.avianwatch.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.avianwatch.data.Hotspot
import com.example.avianwatch.data.HotspotWithMarker
import com.google.android.gms.maps.model.Marker

class GoBirdingViewModel : ViewModel() {

    // MutableLiveData to hold a list of hotspots
    private val hotspotsLiveData = MutableLiveData<List<HotspotWithMarker>>()

    fun addHotspot(hotspot: Hotspot, marker: Marker) {
        val currentHotspots = hotspotsLiveData.value?.toMutableList() ?: mutableListOf()
        currentHotspots.add(HotspotWithMarker(hotspot, marker))
        hotspotsLiveData.value = currentHotspots
    }

    // Function to update the hotspots data
    fun updateHotspotsData(hotspots: List<HotspotWithMarker>) {
        hotspotsLiveData.value = hotspots
    }

    // Function to retrieve the hotspots LiveData
    fun getHotspotsLiveData() = hotspotsLiveData
}