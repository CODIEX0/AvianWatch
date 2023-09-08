package com.example.avianwatch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.data.Global
import com.example.avianwatch.databinding.FragmentBirdFactsBinding
import com.example.avianwatch.databinding.FragmentObservationListBinding

class BirdFactsFragment : Fragment() {

    // Declare variables for the UI elements
    private lateinit var factTextView: TextView
    private lateinit var factImageView: ImageView

    private var currentFactIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBirdFactsBinding.inflate(inflater, container, false)

        factTextView = binding.factTextView
        factImageView = binding.factImageView

        val mainActivity = activity as MainActivity
        mainActivity.updateTitle("Bird Facts")

        // Show a random fact when the fragment is created
        showRandomFact()


        // Set a click listener for the button to show the next fact
        binding.btnNextFact.setOnClickListener {
            showRandomFact()
        }

        binding.shareButton.setOnClickListener {
            Toast.makeText(requireContext(),"Fact Shared Successfully", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun showRandomFact() {
        val randomFactIndex = (0 until Global.birdFactsList.size).random()
        showFact(randomFactIndex)
    }

    private fun showFact(index: Int) {
        val fact = Global.birdFactsList[index]
        factTextView.text = fact.fact
        factImageView.setImageResource(fact.image)
    }

}

