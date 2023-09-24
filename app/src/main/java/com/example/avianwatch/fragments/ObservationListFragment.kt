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
import com.example.avianwatch.objects.Image
import com.example.avianwatch.data.ObservationItem
import com.example.avianwatch.databinding.FragmentObservationListBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ObservationListFragment : Fragment(), ObservationAdapter.OnItemClickListener {
    private lateinit var auth: FirebaseAuth
    lateinit var userBirdObservations: MutableList<ObservationItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the current date
        val calendar = Calendar.getInstance()

        // Format the date to display in your desired format (e.g., "dd/MM/yyyy")
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)

        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        val uid = firebaseUser?.uid

        userBirdObservations = mutableListOf(

            ObservationItem(
                uid,
                "Flamingo",
                formattedDate,
                "Halfway House, Midrand",
                "A pair of Flamingos, they looked liked they were mating.",
                Image.drawableToBase64(ContextCompat.getDrawable(requireContext(), R.mipmap.flamingo_pair)!!)

                )
        )
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

        try{
            // Create an instance of PlantAdapter and pass the OnItemClickListener
            val adapter = ObservationAdapter(userBirdObservations)
            adapter.setOnItemClickListener(this)
            // Set the adapter to the RecyclerView
            lstBirds.adapter = adapter
        }catch (e:Exception){
            Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
            Log.d(ContentValues.TAG, e.message.toString())
        }
    }

    override fun onItemClick(bird: ObservationItem) {
        // Handle the click event and navigate to a different fragment
        //Add data to bundle
        val bundle = Bundle()
        bundle.putString("bird_name", bird.birdName)
        bundle.putString("date", bird.date.toString())
        bundle.putString("location", bird.location)
        bundle.putString("notes", bird.notes)
        bundle.putString("imageData", bird.image)

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