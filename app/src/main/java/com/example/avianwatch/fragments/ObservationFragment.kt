package com.example.avianwatch.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.avianwatch.R
import com.example.avianwatch.data.BirdObservation
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class ObservationFragment : Fragment() {
    private lateinit var capturedImage: ImageView
    private val cameraRequestCode = 101
    private val CAMERA_PERMISSION_CODE = 100
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_observation, container, false)
        capturedImage = view!!.findViewById(R.id.imgObservationImage)
        val captureButton = view!!.findViewById<ImageButton>(R.id.btnCamera)

        captureButton.setOnClickListener {
            checkCameraPermission()
        }

        return view
    }

    fun addBirdObservationOnMap(userObservation: BirdObservation, mMap: GoogleMap) {
        // Create location request
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000 // Update location every 10 seconds


        // Create location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (locationResult.lastLocation != null) {
                    val location: Location = locationResult.lastLocation!!
                    val latLng = LatLng(location.latitude, location.longitude)

                    val birdSpecies = userObservation.birdSpecies // Replace with the actual bird species field in UserBirdObservation

                    // Create a LatLng object for the observation location
                    val observationLocation = LatLng(latLng.latitude, latLng.longitude)

                    // Create a MarkerOptions object for the bird observation
                    val markerOptions = MarkerOptions()
                        .position(observationLocation)
                        .title("Bird Observation")
                        .snippet(birdSpecies)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

                    // Add the marker to the map
                    val marker = mMap.addMarker(markerOptions)

                    // Optionally, you can store the marker or observation data for future reference
                    // For example, you could store it in a ViewModel, database, or a list for later use.
                    // viewModel.addBirdObservationMarker(userObservation, marker)
                }else{

                }
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == cameraRequestCode && resultCode == Activity.RESULT_OK) {
            val image = data?.extras?.get("data") as Bitmap
            capturedImage.setImageBitmap(image)

        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            ImagePicker.with(this)
                .crop()                     //crop image(optional), check customization for more options
                .compress(1024)             //final image size will be less than 1 MB
                .maxResultSize(1080,1080)   //final image resolution will be less than 1080 x 1080
                .start()
        }
    }


}