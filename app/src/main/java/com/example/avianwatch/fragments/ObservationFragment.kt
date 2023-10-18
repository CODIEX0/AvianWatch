package com.example.avianwatch.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.avianwatch.data.BirdObservation
import com.example.avianwatch.data.Hotspot
import com.example.avianwatch.data.HotspotWithMarker
import com.example.avianwatch.databinding.FragmentObservationBinding
import com.example.avianwatch.objects.FirebaseManager
import com.example.avianwatch.objects.Global
import com.example.avianwatch.objects.Image
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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
class ObservationFragment : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentObservationBinding
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var gMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentObservationBinding.inflate(inflater, container, false)

        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )

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

    fun addBirdObservationOnMap(userObservation: BirdObservation) {
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

                    // Create a LatLng object for the observation location
                    val observationLocation = LatLng(latLng.latitude, latLng.longitude)

                    // Create a MarkerOptions object for the bird observation
                    val markerOptions = MarkerOptions()
                        .position(observationLocation)
                        .title(userObservation.birdSpecies)
                        .snippet(userObservation.additionalNotes)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

                    // Add the marker to the map
                    val marker = gMap.addMarker(markerOptions)
                    val hotspot_marker = HotspotWithMarker(
                        userObservation.hotspot,
                        marker
                    )
                    //store the user's hotspot with a marker
                    Global.hotspotsWithMarker.add(hotspot_marker)
                }else{

                }
            }
        }
    }

    fun addObservation(context: Context) {
        val imageData = Image.convertImageToBase64(binding.imgObservationImage).toString()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, create an observation
            val oid = UUID.randomUUID().toString()
            // Get the current date
            val calendar = Calendar.getInstance()
            // Format the date to display in your desired format (e.g., "dd/MM/yyyy")
            val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        var latLng = LatLng(location.latitude, location.longitude)

                        var address: String?

                        CoroutineScope(Dispatchers.Main).launch {
                            address = getAddressName(
                                location!!.latitude,
                                location.longitude,
                                Global.googleMapsApiKey
                            )


                            val hotspot = Hotspot(
                                UUID.randomUUID().toString(),
                                address.toString(),
                                binding.etBirdName.text.toString(),
                                latLng.latitude,
                                latLng.longitude
                            )
                            //store the user's hotspot
                            Global.hotspots.add(hotspot)
                            val observation = BirdObservation(
                                Global.currentUser?.uid.toString(), //Store UID to create relationship
                                oid,
                                binding.etBirdName.text.toString(),
                                binding.etNotes.text.toString(),
                                imageData,
                                formattedDate,
                                hotspot
                            )

                            //Add observation to DB and update local storage
                            FirebaseManager.addObservation(observation) { isSuccess -> //Use callback to wait for results
                                if (isSuccess) {
                                    // add hotspot to map as a marker
                                    addBirdObservationOnMap(observation)
                                    //Update local observations list
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
                        }
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


        if (binding.etBirdName.text.toString() == "") {
            Toast.makeText(context, "Enter the bird species...", Toast.LENGTH_SHORT).show()
            return
        }
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

    // Function to perform reverse geocoding and get the address name
    suspend fun getAddressName(latitude: Double, longitude: Double, apiKey: String): String? {
        return withContext(Dispatchers.IO) {
            val geocodingUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&key=$apiKey"
            var address: String? = null

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
                    }
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

    override fun onMapReady(map: GoogleMap) {
        gMap = map
    }
}