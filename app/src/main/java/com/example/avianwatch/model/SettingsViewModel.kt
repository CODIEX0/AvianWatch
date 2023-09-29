package com.example.avianwatch.model
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {
    // LiveData to hold the text for txtMaxRadius
    private val _maxRadiusText = MutableLiveData<String>()
    val maxRadiusText: LiveData<String>
        get() = _maxRadiusText

    // LiveData for maxRadiusSeekBar progress
    private val _maxRadiusProgress = MutableLiveData<Int>()
    val maxRadiusProgress: LiveData<Int>
        get() = _maxRadiusProgress

    // Constructor or initialization code for ViewModel

    // Function to update maxRadiusText based on maxRadiusSeekBar progress
    fun updateMaxRadiusText(progress: Int) {
        val text = progress.toString() // Customize the text format as needed
        _maxRadiusText.value = text
    }

    // Function to update maxRadiusSeekBar progress
    fun setMaxRadiusProgress(progress: Int) {
        _maxRadiusProgress.value = progress
    }
}