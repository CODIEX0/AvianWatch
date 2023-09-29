package com.example.avianwatch.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.data.UserPreferences
import com.example.avianwatch.databinding.FragmentSettingsBinding
import com.example.avianwatch.model.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var preferencesRef: DatabaseReference
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        preferencesRef = FirebaseDatabase.getInstance().getReference("UserPreferences")

        // load and display user preferences if available
        loadUserPreferences()

        // handle Save button click
        binding.btnSave.setOnClickListener {
            saveUserPreferences()
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        // Observe maxRadiusProgress and update maxRadiusText
        viewModel.maxRadiusProgress.observe(viewLifecycleOwner) { progress ->
            viewModel.updateMaxRadiusText(progress)
        }

        // Set up maxRadiusSeekBar (assuming you have a reference to it)
        binding.maxRadiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the ViewModel with the progress
                viewModel.setMaxRadiusProgress(progress)
                binding.txtMaxRadius.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Not needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not needed
            }
        })
        return view
    }

    private fun loadUserPreferences() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            preferencesRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userPreferences = dataSnapshot.getValue(UserPreferences::class.java)
                    if (userPreferences != null) {
                        // set radio button and seek bar values based on user preferences
                        when (userPreferences.unitSystem) {
                            "Imperial" -> binding.radioImperial.isChecked = true
                            "Metric" -> binding.radioMetric.isChecked = true
                        }
                        binding.maxRadiusSeekBar.progress = userPreferences.maxRadius
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), "Couldn't retrieve your user preferences", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun saveUserPreferences() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val selectedUnitSystem = when (binding.buttonGroup.checkedRadioButtonId) {
                R.id.radioImperial -> "Imperial"
                R.id.radioMetric -> "Metric"
                else -> "Imperial" // default selection
            }
            val selectedMaxRadius = binding.maxRadiusSeekBar.progress

            val userPreferences = UserPreferences(uid, selectedUnitSystem, selectedMaxRadius)

            // save user preferences to firebase
            preferencesRef.child(uid).setValue(userPreferences)
                .addOnSuccessListener {
                    // preferences saved successfully
                    requireActivity().onBackPressed() // Navigate back to the previous screen
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Couldn't update user preferences", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
