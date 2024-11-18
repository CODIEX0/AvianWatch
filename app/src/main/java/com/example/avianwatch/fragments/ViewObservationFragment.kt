package com.example.avianwatch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.avianwatch.R
import com.example.avianwatch.databinding.FragmentViewObservationBinding
import com.example.avianwatch.databinding.FragmentViewPostBinding
import com.example.avianwatch.objects.Image

class ViewObservationFragment : Fragment() {
    private lateinit var binding: FragmentViewObservationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentViewObservationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the arguments Bundle
        val arguments = arguments

        // Check if arguments exist
        if (arguments != null) {
            // Retrieve the data from the bundle
            val birdName = arguments.getString("bird_name")
            val date = arguments.getString("date")
            val location = arguments.getString("location")
            val notes = arguments.getString("notes")
            val image = arguments.getString("image")
            //Update UI elements---

            //Set Values    `   ```````````         ```````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````
            binding.txtBirdName.text = birdName
            binding.txtDate.text = date
            binding.txtLocation.text = location
            binding.txtNotes.text = notes
            Image.setBase64Image(image, binding.imgObservation)
        }
    }
}