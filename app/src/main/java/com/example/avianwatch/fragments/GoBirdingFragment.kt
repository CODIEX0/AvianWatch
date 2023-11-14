package com.example.avianwatch.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.api.eBirdApiService
import com.example.avianwatch.objects.Global
import com.example.avianwatch.data.Hotspot
import com.example.avianwatch.data.UserPreferences
import com.example.avianwatch.databinding.BslOptionsBinding
import com.example.avianwatch.databinding.FragmentGoBirdingBinding
import com.example.avianwatch.model.GoBirdingViewModel
import com.example.avianwatch.objects.Global.currentLocation
import com.example.avianwatch.objects.RetrofitClientInstance
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/* Code Attribution
   Title: Fragments Implementation using Kotlin || Fragments using Kotlin || Android Studio Tutorial || 2023
   Link: https://www.youtube.com/watch?v=h-NcxT697Nk&t=479s
   Author: Foxandroid
   Date: 2023
*/
class GoBirdingFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    lateinit var binding: FragmentGoBirdingBinding
    private lateinit var mMap: GoogleMap
    private lateinit var preferencesRef: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var birdingApiService: eBirdApiService
    private lateinit var viewModel: GoBirdingViewModel
    private var isSatelliteView = false // Flag to track the map type
    private var radiusCircle: Circle? = null //variable to hold the circle on the map
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPreferences = UserPreferences()
        preferencesRef = FirebaseDatabase.getInstance().getReference("UserPreferences")

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            preferencesRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Try to get the online UserPreferences data from dataSnapshot; if null, use the default UserPreferences object
                    userPreferences =
                        dataSnapshot.getValue(UserPreferences::class.java) ?: UserPreferences()

                }override fun onCancelled(databaseError: DatabaseError) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error loading user preferences", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGoBirdingBinding.inflate(inflater, container, false)

        val mainActivity = activity as MainActivity
        mainActivity.updateTitle("Go Birding")

        // Initialize map fragment
        val mapView = binding.root.findViewById(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Find the button by ID and set its click listener
        binding.btnSatelliteToggle.setOnClickListener { toggleMapType() }

        binding.btnCurrentLocation.setOnClickListener { requestLocationPermissionAndZoom() }

        binding.fab.setOnClickListener{ showBottomDialog() }

        binding.btnRefresh.setOnClickListener { replaceFragment(GoBirdingFragment()) }

        // initialize Retrofit service
        birdingApiService = RetrofitClientInstance.getRetrofitInstance().create(eBirdApiService::class.java)

        // initialize ViewModel
        viewModel = ViewModelProvider(this).get(GoBirdingViewModel::class.java)

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

            replaceFragment(ObservationFragment())
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

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Set an info window click listener directly on the fragment
        mMap.setOnInfoWindowClickListener { marker ->
            // Handle info window click event here
            val destination = marker.position
            getDirections(destination)
        }
        // Call zoomToCurrentLocation
        zoomToCurrentLocation()

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Create location request
            locationRequest = LocationRequest()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            // Create location callback
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    if (locationResult.lastLocation != null) {
                        val location: Location = locationResult.lastLocation!!
                        val latLng = LatLng(location.latitude, location.longitude)

                        currentLocation = location
                        // Draw the radius boundary
                        drawRadiusBoundary(location.latitude, location.longitude, userPreferences)
                        val marker = MarkerOptions()
                            .position(latLng)
                            .title("Current Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

                        mMap.addMarker(marker)

                        // Load nearby hotspots using Retrofit
                        loadEBirdHotspots(location.latitude, location.longitude, userPreferences)
                    }
                }
            }
            // Start location updates
            startLocationUpdates()
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun getDirections(destination: LatLng) {
        val apiKey = Global.googleMapsApiKey
        val geoApiContext = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            val result: DirectionsResult = DirectionsApi.newRequest(geoApiContext)
                .mode(TravelMode.DRIVING)
                .origin(currentLocation!!.latitude.toString() + "," + currentLocation!!.longitude.toString())
                .destination(destination.latitude.toString() + "," + destination.longitude.toString())
                .await()

            // Handle the result and display the directions on the map
            if (result.routes.isNotEmpty()) {
                val route = result.routes[0]
                val overviewPolyline = route.overviewPolyline
                val encodedPolyline = overviewPolyline.encodedPath
                val points = PolylineEncoding.decode(encodedPolyline)

                // Create a PolylineOptions to define the appearance of the polyline
                val options = PolylineOptions()
                    .color(Color.GREEN)
                    .width(3f)

                // Iterate through the decoded points and add them to the PolylineOptions
                for (point in points) {
                    options.add(LatLng(point.lat, point.lng))
                }

                // Update the UI on the main thread
                withContext(Dispatchers.Main) {
                    val line = mMap.addPolyline(options)
                }
            } else {
                // Update the UI on the main thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "No directions found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Function to calculate zoom level to fit the entire maxRadius on the screen
    private fun calculateZoomLevel(centerLatLng: LatLng, maxRadius: Double): Float {
        val screenSize = Point()
        val zoomScreenPadding = 100 // Padding to ensure the entire radius is visible
        activity?.windowManager?.defaultDisplay?.getSize(screenSize)

        val zoomLevel = (Math.log(156543.03392 * maxRadius * 2.0 / screenSize.x) / Math.log(2.0)).toFloat()

        return zoomLevel.coerceIn(2f, 20f)
    }

    private fun drawRadiusBoundary(latitude: Double, longitude: Double, userPreferences: UserPreferences) {
        // Remove the previous circle if it exists
        radiusCircle?.remove()

        // Get the user's preferences (e.g., maxRadius) from the UserPreferences class
        val localUserPreferences = UserPreferences()

        if (userPreferences != null) {

            // Convert maxRadius to meters if unitSystem is "Metric," otherwise use yards
            val maxRadiusInMeters = if (userPreferences.unitSystem == "Metric") {
                userPreferences.maxRadius.toDouble() * 1000 // Convert kilometers to meters
            } else {
                userPreferences.maxRadius.toDouble() * 1760.0 // Convert Miles to yards
            }
            // Create a circle option with the user's selected radius
            val circleOptions = CircleOptions()
                .center(LatLng(latitude, longitude))
                .radius(maxRadiusInMeters)
                .strokeColor(ContextCompat.getColor(requireContext(), R.color.blue_dark))
                .strokeWidth(2f)
                .fillColor(ContextCompat.getColor(requireContext(), R.color.transparent_blue_dark))

            // Add the circle to the map
            radiusCircle = mMap.addCircle(circleOptions)
        }else{
            // Convert maxRadius to meters if unitSystem is "Metric," otherwise use yards
            val maxRadiusInMeters = if (localUserPreferences.unitSystem == "Metric") {
                localUserPreferences.maxRadius.toDouble() * 1000 // Convert kilometers to meters
            } else {
                localUserPreferences.maxRadius.toDouble() * 1760.0 // Convert Miles to yards
            }

            // Create a circle option with the user's selected radius
            val circleOptions = CircleOptions()
                .center(LatLng(latitude, longitude))
                .radius(maxRadiusInMeters)
                .strokeColor(ContextCompat.getColor(requireContext(), R.color.blue_dark))
                .strokeWidth(2f)
                .fillColor(ContextCompat.getColor(requireContext(), R.color.transparent_blue_dark))

            // Add the circle to the map
            radiusCircle = mMap.addCircle(circleOptions)
        }
    }

    private fun loadEBirdHotspots(latitude: Double, longitude: Double, userPreferences: UserPreferences) {
        // Get the user's preferences (e.g., maxRadius) from the UserPreferences class
        val localUserPreferences = UserPreferences()
        val maxRadius = userPreferences.maxRadius.toDouble()

        Log.d("GoBirdingFragment", "UserPreferences max Radius: ${userPreferences.maxRadius}")

        // Convert maxRadius to kilometers based on the user's unit system preference
        val maxDistance = if (userPreferences.unitSystem == "Metric") {
            maxRadius // No conversion needed for metric system (already in kilometers)
        } else {
            maxRadius * 1.60934 // Convert miles to kilometers
        }

        // Call eBird API using Retrofit
        val call = birdingApiService.getNearbyHotspots(latitude, longitude, maxDistance, Global.eBirdApiKey)

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
                                val hotspotLocation = LatLng(hotspot.lat, hotspot.lng)
                                val markerOptions = MarkerOptions()
                                    .position(hotspotLocation)
                                    .title(hotspot.comName)
                                    .snippet(hotspot.locName + "\n" + hotspot.lat + ", " + hotspot.lng)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                mMap.addMarker(markerOptions)
                                // Store hotspot data in ViewModel or handle marker click as needed
                                if (markerOptions != null) {
                                    viewModel.addHotspot(hotspot, markerOptions)
                                }
                            }
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "Couldn't get online hotspots.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Log.d("hotspot exception","Failed to fetch bird hotspots. Please try again.")
                        Toast.makeText(requireContext(), "Failed to fetch e-bird hotspots. Please refresh screen.", Toast.LENGTH_SHORT).show()
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
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions granted, start location updates

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
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
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

    private fun requestLocationPermissionAndZoom() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted, so request the user's location
            zoomToCurrentLocation()
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    // Function to toggle between standard and satellite map views
    private fun toggleMapType() {
        isSatelliteView = !isSatelliteView
        mMap.mapType = if (isSatelliteView) GoogleMap.MAP_TYPE_SATELLITE else GoogleMap.MAP_TYPE_NORMAL
    }

    private fun zoomToCurrentLocation() {
        // Check if the user's location is available
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, so get the user's last known location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // Obtain the user's location as a LatLng object
                        val userLocation = LatLng(it.latitude, it.longitude)

                        // Calculate the zoom level to fit the entire maxRadius on the screen
                        val zoomLevel = calculateZoomLevel(userLocation, userPreferences.maxRadius.toDouble())

                        // Draw the radius boundary
                        drawRadiusBoundary(it.latitude, it.longitude, userPreferences)

                        // Move the camera to the user's location with the calculated zoom level
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel))
                    }
                }
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        // Handle info window click event here
        val destination = marker.position
        getDirections(destination)
    }
}