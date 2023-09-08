package com.example.avianwatch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.databinding.ActivityMainBinding
import com.example.avianwatch.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val goBirdingFragment = GoBirdingFragment()
            fragmentTransaction.replace(R.id.fragment_container, goBirdingFragment) // replace with the new fragment
            fragmentTransaction.addToBackStack(null) // add to back stack
            fragmentTransaction.commit()
        }

        binding.cvAddObservation.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Add Observation")

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val observationFragment = ObservationFragment()
            fragmentTransaction.replace(R.id.fragment_container, observationFragment) // replace with the new fragment
            fragmentTransaction.addToBackStack(null) //add to back stack
            fragmentTransaction.commit()
        }

        binding.cvMyAviary.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("My Observations")

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val myAviaryFragment = ObservationListFragment()
            fragmentTransaction.replace(R.id.fragment_container, myAviaryFragment) // replace with the new fragment
            fragmentTransaction.addToBackStack(null) //add to back stack
            fragmentTransaction.commit()
        }

        binding.cvBirdFacts.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Bird Facts")

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val birdFactsFragment = BirdFactsFragment()
            fragmentTransaction.replace(R.id.fragment_container, birdFactsFragment) // replace with the new fragment
            fragmentTransaction.addToBackStack(null) //add to back stack
            fragmentTransaction.commit()
        }

        binding.cvSettings.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Settings")

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val settingsFragment = SettingsFragment()
            fragmentTransaction.replace(R.id.fragment_container, settingsFragment) // replace with the new fragment
            fragmentTransaction.addToBackStack(null) //add to back stack
            fragmentTransaction.commit()
        }

        binding.cvCommunity.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Blogs")

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val blogsFragment = BlogsFragment()
            fragmentTransaction.replace(R.id.fragment_container, blogsFragment) // replace with the new fragment
            fragmentTransaction.addToBackStack(null) //add to back stack
            fragmentTransaction.commit()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}