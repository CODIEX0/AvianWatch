package com.example.avianwatch.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.databinding.FragmentHomeBinding
import com.example.avianwatch.objects.Global

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        //change fragments using the card views
        binding.cvGoBirding.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Go Birding")

            replaceFragment(GoBirdingFragment())
        }

        binding.cvAddObservation.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Add Observation")

            replaceFragment(ObservationFragment())
        }

        binding.cvMyObservations.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("My Observations")

            replaceFragment(ObservationListFragment())
        }

        binding.cvBirdFacts.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Bird Facts")

            replaceFragment(BirdFactsFragment())
        }

        binding.cvSettings.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Settings")

            replaceFragment(SettingOptionsFragment())
        }

        binding.cvCommunity.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Community")

            replaceFragment(PostsFragment())
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment) // replace with the new fragment
        fragmentTransaction.addToBackStack(null) //add to back stack
        fragmentTransaction.commit()
    }

}