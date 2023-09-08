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
import com.example.avianwatch.data.Image
import com.example.avianwatch.data.ObservationItem
import com.example.avianwatch.databinding.ActivityMainBinding
import com.example.avianwatch.databinding.FragmentObservationListBinding
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ObservationListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ObservationListFragment : Fragment(), ObservationAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var userBirdObservations: MutableList<ObservationItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        userBirdObservations = mutableListOf(

            ObservationItem(
                "Crow",
                Date(),
                "west Rd Glen, Austin, Midrand",
                "Saw a group of crows near the park.",
                Image.drawableToBase64(ContextCompat.getDrawable(requireContext(), R.drawable.group_of_crows)!!)

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ObservationListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ObservationListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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