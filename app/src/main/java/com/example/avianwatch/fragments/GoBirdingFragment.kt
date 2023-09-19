package com.example.avianwatch.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.api.eBirdApiService
import com.example.avianwatch.data.Global
import com.example.avianwatch.data.Hotspot
import com.example.avianwatch.databinding.BslOptionsBinding
import com.example.avianwatch.databinding.FragmentGoBirdingBinding
import com.example.avianwatch.model.GoBirdingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class GoBirdingFragment : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentGoBirdingBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var birdingApiService: eBirdApiService
    private lateinit var viewModel: GoBirdingViewModel
    private var isSatelliteView = false // Flag to track the map type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGoBirdingBinding.inflate(inflater, container, false)

        // Find the button by ID and set its click listener
        val satelliteToggleBtn = binding.btnSatelliteToggle
        satelliteToggleBtn.setOnClickListener {
            toggleMapType()
        }

        binding.fab.setOnClickListener(View.OnClickListener { showBottomDialog() })

        // Initialize your Retrofit service
        birdingApiService = RetrofitClientInstance.getRetrofitInstance().create(eBirdApiService::class.java)

        // Initialize your ViewModel
        viewModel = ViewModelProvider(this).get(GoBirdingViewModel::class.java)

        // Initialize map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment) // replace with the new fragment
        fragmentTransaction.addToBackStack(null) //add to back stack
        fragmentTransaction.commit()
    }

    private fun showBottomDialog() {
        val binding = BslOptionsBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        binding.layoutSettings.setOnClickListener {
            dialog.dismiss()
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Settings")

            replaceFragment(SettingsFragment())
        }

        binding.layoutAddObservations.setOnClickListener {
            dialog.dismiss()
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Add Observation")

            replaceFragment(AddPostFragment())
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.Theme_AvianWatch
        dialog.window!!.setGravity(Gravity.BOTTOM)

        dialog.show()
    }

    object RetrofitClientInstance {
        private const val BASE_URL = "https://ebird.org/ws2.0/" // eBird API base URL

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun getRetrofitInstance(): Retrofit {
            return retrofit
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Enable the "My Location" button
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true

            // Initialize location services
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            // Create location request
            locationRequest = LocationRequest()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 10000 // Update location every 10 seconds

            // Create location callback
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    //if (locationResult.lastLocation != null) {
                        val location: Location = locationResult.lastLocation!!
                        val latLng = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f)) // Adjust zoom level

                        // Load nearby hotspots using Retrofit on the main thread
                        loadNearbyHotspots(location.latitude, location.longitude)

                }
            }

            // Start location updates
            startLocationUpdates()
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun loadNearbyHotspots(latitude: Double, longitude: Double) {
        // Call eBird API using Retrofit
        val call = birdingApiService.getNearbyHotspots(latitude, longitude, Global.eBirdApiKey)
        call.enqueue(object : Callback<List<Hotspot>> {
            override fun onResponse(call: Call<List<Hotspot>>, response: Response<List<Hotspot>>) {
                if (response.isSuccessful) {
                    val hotspots = response.body()
                    if (!hotspots.isNullOrEmpty()) {
                        // Clear existing markers
                        mMap.clear()

                        // Add markers for hotspots on the main thread
                        requireActivity().runOnUiThread {
                            for (hotspot in hotspots) {
                                val hotspotLocation = LatLng(hotspot.latitude, hotspot.longitude)
                                val markerOptions = MarkerOptions()
                                    .position(hotspotLocation)
                                    .title(hotspot.locName)
                                val marker = mMap.addMarker(markerOptions)
                                // Store hotspot data in ViewModel or handle marker click as needed
                                if (marker != null) {
                                    viewModel.addHotspot(hotspot, marker)
                                }
                            }
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "No nearby hotspots found.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Log.d("hotspot exception","Failed to fetch birds hotspots. Please try again.")

                    }
                }
            }

            override fun onFailure(call: Call<List<Hotspot>>, t: Throwable) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error: " + t.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onStop() {
        super.onStop()
        // Stop location updates
        if (::fusedLocationProviderClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions granted, start location updates
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
            } else {
                // Location permissions denied, handle it accordingly (e.g., show a message to the user)
                Toast.makeText(
                    requireContext(),
                    "Location permissions denied. You may not be able to use certain features.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Function to toggle between standard and satellite map views
    private fun toggleMapType() {
        isSatelliteView = !isSatelliteView
        mMap.mapType = if (isSatelliteView) GoogleMap.MAP_TYPE_SATELLITE else GoogleMap.MAP_TYPE_NORMAL
    }

}