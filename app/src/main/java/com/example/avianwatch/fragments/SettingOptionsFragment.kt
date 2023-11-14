package com.example.avianwatch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.databinding.FragmentSettingOptionsBinding


class SettingOptionsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentSettingOptionsBinding.inflate(inflater, container, false)

        binding.btnMapSettings.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Settings")

            replaceFragment(SettingsFragment())
        }

        binding.btnPersonalSettings.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Settings")

            replaceFragment(Personal_Settings_Fragment())
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

    companion object {

    }
}