package com.example.avianwatch.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.adapters.ObservationAdapter
import com.example.avianwatch.adapters.PostAdapter
import com.example.avianwatch.data.BirdObservation
import com.example.avianwatch.objects.Image
import com.example.avianwatch.data.ObservationItem
import com.example.avianwatch.data.Post
import com.example.avianwatch.databinding.FragmentObservationListBinding
import com.example.avianwatch.objects.FirebaseManager
import com.example.avianwatch.objects.Global
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ObservationListFragment : Fragment(), ObservationAdapter.OnItemClickListener {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the current date
        val calendar = Calendar.getInstance()

        // Format the date to display in your desired format (e.g., "dd/MM/yyyy")
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentObservationListBinding.inflate(inflater, container, false)
        // go to the settings fragment
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
        super.onViewCreated(view, savedInstanceState)

        val lstBirds = view.findViewById<RecyclerView>(R.id.rvObservationList)

        // Set up the LinearLayoutManager for the RecyclerView
        val plantLayoutManager = LinearLayoutManager(requireContext())
        lstBirds.layoutManager = plantLayoutManager

        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        val uid = firebaseUser?.uid

        try{
            // Create an instance of ObservationAdapter and pass the OnItemClickListener
            if (uid != null) {
                FirebaseManager.getObservations(uid) { observation ->
                    // Update the global posts list
                    Global.observations = observation

                    try{
                        val adapter = ObservationAdapter(Global.observations)
                        adapter.setOnItemClickListener(this)
                        // Set the adapter to the RecyclerView
                        lstBirds.adapter = adapter
                    }catch (e:Exception){
                        Toast.makeText(requireContext(),e.message, Toast.LENGTH_SHORT).show()
                        Log.d(ContentValues.TAG, e.message.toString())
                    }
                }
            }

        }catch (e:Exception){
            Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
            Log.d(ContentValues.TAG, e.message.toString())
        }
    }

    override fun onItemClick(bird: BirdObservation) {
        // Handle the click event and navigate to a different fragment
        //Add data to bundle
        val bundle = Bundle()
        bundle.putString("bird_name", bird.birdSpecies)
        bundle.putString("date", bird.dateTime.toString())
        bundle.putString("location", bird.hotspot.locName)
        bundle.putString("notes", bird.additionalNotes)
        bundle.putString("imageData", bird.birdImage)

        try{
            val fragment = ObservationListFragment()
            fragment.arguments = bundle

            //Navigate to fragment, passing bundle
            //findNavController().navigate(R.id.action_ObservationListFragment_to_ViewPlantFragment, bundle)
        }catch (e:Exception){
            Toast.makeText(activity,e.message, Toast.LENGTH_SHORT).show()
            Log.d(ContentValues.TAG, e.message.toString())
        }

    }
}