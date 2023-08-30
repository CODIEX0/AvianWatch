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
import com.example.avianwatch.R
import com.example.avianwatch.data.Global

class BirdFactsFragment : Fragment() {

    // Declare variables for the UI elements
    private lateinit var factTextView: TextView
    private lateinit var factImageView: ImageView
    private lateinit var factButton: Button
    private lateinit var shareButton: Button

    private var currentFactIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bird_facts, container, false)

        // Initialize the UI elements
        factTextView = view.findViewById(R.id.fact_text_view)
        factImageView = view.findViewById(R.id.fact_image_view)
        factButton = view.findViewById(R.id.fact_button)
        shareButton = view.findViewById(R.id.share_button)

        // Set a click listener for the button to show the next fact
        factButton.setOnClickListener {
            showRandomFact()
        }

        shareButton.setOnClickListener {
            Toast.makeText(requireContext(),"Fact Shared Successfully", Toast.LENGTH_SHORT).show()
        }
        // Show a random fact when the fragment is created
        showRandomFact()

        return view
    }

    private fun showRandomFact() {
        val randomFactIndex = (0 until Global.birdFactsList.size).random()
        showFact(randomFactIndex)
    }

    private fun showFact(index: Int) {
        val fact = Global.birdFactsList[index]
        factTextView.text = fact.fact
        factImageView.setImageResource(fact.imageResource)
    }

    data class BirdFact(val fact: String, val imageResource: Int)
}

