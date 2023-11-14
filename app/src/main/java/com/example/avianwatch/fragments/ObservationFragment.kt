package com.example.avianwatch.fragments

import android.Manifest
import android.R
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.avianwatch.MainActivity
import com.example.avianwatch.data.BirdObservation
import com.example.avianwatch.databinding.FragmentObservationBinding
import com.example.avianwatch.objects.FirebaseManager
import com.example.avianwatch.objects.Global
import com.example.avianwatch.objects.Global.birdSpecies
import com.example.avianwatch.objects.Global.currentLocation
import com.example.avianwatch.objects.Global.selectedBirdName
import com.example.avianwatch.objects.Image
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

/* Code Attribution
   Title: Kotlin Button Click Event | Android Studio Tutorial | 2021 | Foxandroid | #1
   Link: https://www.youtube.com/watch?v=vethIXEYbUk
   Author: Foxandroid
   Date: 2021
*/
class ObservationFragment : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentObservationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var gMap: GoogleMap
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentObservationBinding.inflate(inflater, container, false)

        val mainActivity = activity as MainActivity
        mainActivity.updateTitle("Add Observation")

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        currentLocation = it
                        // Handle the user's last known location
                        Log.d("User Location:"," ${it.latitude}, ${it.longitude}")
                    } ?: run {
                        // Handle the case where the last known location is not available
                    }
                }
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }

        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )

        val autoCompleteTextView = binding.etBirdName
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, birdSpecies)
        autoCompleteTextView.setAdapter(adapter)


        binding.btnCamera.setOnClickListener {
            ImagePicker.with(this)
                .crop()                     //crop image(optional), check customization for more options
                .compress(1024)             //final image size will be less than 1 MB
                .maxResultSize(1080,1080)   //final image resolution will be less than 1080 x 1080
                .start()
        }


        binding.btnAddObservation.setOnClickListener {
            addObservation(requireContext())
        }

        return binding.root
    }

    fun addObservation(context: Context) {
        val imageData = Image.convertImageToBase64(binding.imgObservationImage).toString()

        // Function to handle the location permission and observation creation
        fun handleLocationPermission() {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            // Check for location permission
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, create an observation
                val oid = UUID.randomUUID().toString()
                // Get the current date
                val calendar = Calendar.getInstance()
                // Format the date to display in your desired format (e.g., "dd/MM/yyyy")
                val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
                val formattedDate = dateFormat.format(calendar.time)

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        binding.etBirdName.setOnItemClickListener { _, _, position, _ ->
                            selectedBirdName = adapter.getItem(position).toString()
                            // Handle the selected bird name, e.g., display it or add it to the observation
                        }
                        val observation = BirdObservation(
                            Global.currentUser?.uid.toString(),
                            oid,
                            binding.etBirdName.text.toString(),
                            binding.etNotes.text.toString(),
                            imageData,
                            formattedDate,
                            binding.etLocation.text.toString()
                        )

                        // Add observation to DB and update local storage
                        FirebaseManager.addObservation(observation) { isSuccess ->
                            if (isSuccess) {
                                // Add hotspot to the map as a marker
                                //addBirdObservationOnMap(observation)
                                // Update local observations list
                                FirebaseManager.getObservations(Global.currentUser!!.uid.toString()) { observations ->
                                    Global.observations = observations
                                }
                                Toast.makeText(
                                    context,
                                    "Observation Created Successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Observation Creation Failed...",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        // Handle exceptions, e.g., location not available
                        Toast.makeText(context, "Observation creation failed...", Toast.LENGTH_LONG).show()
                    }

                    if (binding.etBirdName.text.toString().isEmpty()) {
                        Toast.makeText(context, "Enter the bird species...", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                // Request location permission
                Toast.makeText(context, "Observation creation failed...", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        }

        // Check and handle location permission
        handleLocationPermission()
        requireActivity().onBackPressed() // Navigate back to the previous screen
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == cameraRequestCode && resultCode == AppCompatActivity.RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imgObservationImage.setImageBitmap(imageBitmap)
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
        binding.imgObservationImage.setImageURI(data?.data)
    }

    private suspend fun getCityAndSuburbNameFromLatLng(latitude: Double, longitude: Double, apiKey: String): String? {
        return withContext(Dispatchers.IO) {this
            val geocodingUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&key=$apiKey"
            var address: String? = null // Initialize as nullable

            try {
                val url = URL(geocodingUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    inputStream.close()

                    // Parse the JSON response
                    val jsonResponse = JSONObject(response.toString())
                    val status = jsonResponse.getString("status")

                    if (status == "OK") {
                        val results = jsonResponse.getJSONArray("results")
                        if (results.length() > 0) {
                            address = results.getJSONObject(0).getString("formatted_address")
                        }
                    }else{
                        Log.e("GeocodingError", "connection Error")
                    }
                }else{
                    Log.e("connection error", "Connection Error")
                }
                connection.disconnect()
            } catch (e: IOException) {
                Log.e("GeocodingError", "Error during geocoding: $e")
            } catch (e: JSONException) {
                Log.e("JsonError", "Error parsing JSON response: $e")
            }
            address
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
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val cameraRequestCode = 101
        private const val REQUEST_LOCATION_PERMISSION = 123
    }

    private val LOCATION_PERMISSION_REQUEST = 1  // A unique code for the permission request

    // Check if the permission is granted, and request it if not
    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted; request it
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        } else {
            // Permission is already granted
            // Your code for accessing the location goes here
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now access the location
                // Your code for accessing the location goes here
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        gMap = map
    }
}
