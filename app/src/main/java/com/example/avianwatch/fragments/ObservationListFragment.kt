package com.example.avianwatch.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.adapters.ObservationAdapter
import com.example.avianwatch.data.BirdObservation
import com.example.avianwatch.databinding.FragmentObservationListBinding
import com.example.avianwatch.objects.FirebaseManager
import com.example.avianwatch.objects.Global
import com.example.avianwatch.objects.Global.observations
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ObservationListFragment : Fragment(), ObservationAdapter.OnItemClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var userBirdObservations: MutableList<BirdObservation>
    private lateinit var adapter: ObservationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        userBirdObservations = mutableListOf()
        adapter = ObservationAdapter(userBirdObservations)

        // Get the current user's UID and store it in userId
        val firebaseUser = auth.currentUser
        userId = firebaseUser?.uid ?: ""

        Log.d("ObservationListFragment", "User ID: $userId")
        // Load user's observations from Firebase
        loadUserObservations()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentObservationListBinding.inflate(inflater, container, false)

        val mainActivity = activity as MainActivity
        mainActivity.updateTitle("My Observations")

        // go to the add observation fragment
        binding.ibAddObservation.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Add Observation")

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val observationFragment = ObservationFragment()
            fragmentTransaction.replace(R.id.fragment_container, observationFragment) // replace with the new fragment
            fragmentTransaction.addToBackStack(null) //add to back stack
            fragmentTransaction.commit()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val lstBirds = view.findViewById<RecyclerView>(R.id.rvObservationList)

        // Set up the LinearLayoutManager for the RecyclerView
        val plantLayoutManager = LinearLayoutManager(requireContext())
        lstBirds.layoutManager = plantLayoutManager

        // Retrieve updated posts
        FirebaseManager.getObservations(auth.currentUser!!.uid) { observations ->
            // Update the global observations. list
            Global.observations = observations

            try {
                // Create an instance of an ObservationAdapter and pass the OnItemClickListener
                val observationAdapter = ObservationAdapter(Global.observations)
                //observationAdapter.setOnItemClickListener(this)
                // Set the adapter to the RecyclerView
                lstBirds.adapter = adapter
            } catch (e: Exception) {
                Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                Log.d(ContentValues.TAG, e.message.toString())
            }
        }
    }

    private fun loadUserObservations() {
        // Get the current user's UID
        val firebaseUser = auth.currentUser
        val uid = firebaseUser?.uid

        if (uid != null) {
            // Initialize the Realtime Database reference
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("BirdObservation")

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userObservations: MutableList<BirdObservation> = mutableListOf()

                    for (snapshot in dataSnapshot.children) {
                        val birdObservation = snapshot.getValue(BirdObservation::class.java)

                        // Check if the "userID" matches the current user's UID
                        if (birdObservation != null && birdObservation.userID == uid) {
                            Log.d("ObservationListFragment", "name: ${birdObservation.birdSpecies}")
                            userObservations.add(birdObservation)
                        }
                    }

                    // Log the size of userObservations
                    Log.d("ObservationListFragment", "User Observations Size: ${userObservations.size}")

                    // Notify the adapter of the data change and pass the filtered list
                    userBirdObservations.clear()
                    userBirdObservations.addAll(userObservations)
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("ObservationListFragment", "Database Error: ${databaseError.message}")
                }
            })
        } else {
            Log.e("ObservationListFragment", "User is not authenticated.")
        }
    }

    override fun onItemClick(bird: BirdObservation) {
        // Handle the click event and navigate to a different fragment
        //Add data to bundle

        val bundle = Bundle()
        bundle.putString("bird_name", bird.birdSpecies)
        bundle.putString("date", bird.dateTime.toString())
        bundle.putString("location", bird.location)
        bundle.putString("notes", bird.additionalNotes)
        bundle.putString("imageData", bird.birdImage)

        try{
            val fragment = ObservationListFragment()
            fragment.arguments = bundle

            //Navigate to fragment, passing bundle
            findNavController().navigate(R.id.action_ObservationListFragment_to_ViewObservationFragment, bundle)
        }catch (e:Exception){
            Toast.makeText(activity,e.message, Toast.LENGTH_SHORT).show()
            Log.d(ContentValues.TAG, e.message.toString())
        }

    }
}