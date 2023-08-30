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

class BirdFactsFragment : Fragment() {

    // Declare variables for the UI elements
    private lateinit var factTextView: TextView
    private lateinit var factImageView: ImageView
    private lateinit var factButton: Button
    private lateinit var shareButton: Button

    // Create a list of bird facts with corresponding image resource IDs
    private val birdFacts = listOf(
        BirdFact("In the continental U.S. alone, between 1.4 billion and 3.7 billion birds are killed by cats annually.", R.drawable.bird_predator),
        BirdFact("Though it looks like a bird’s knee is bending backwards, what is bending backward is actually its ankle. Below its ankle is an extended foot bone, leading to the toes. A bird’s real knee is usually hidden by feathers.", R.drawable.bird_legs),
        BirdFact("Flamingos pair for a lifetime. Some stay with their mates for 50 years or more.", R.drawable.flamingo_pair),
        BirdFact("The song of a European wren is made of more than 700 different notes a minute and can be heard 1,650 feet (500 m) away.", R.drawable.wren_bird_song),
        BirdFact("The Sooty Tern spends more time in the air than any other bird. It takes off over the ocean and flies for at least 3 years without settling on water or land. Swifts also spend most of their lives in the air. They can even sleep in the air by gliding on air currents with their wings outstretched.", R.drawable.sooty_tern_flight),
        BirdFact("A group of crows is called a murder or congress. A group of owls is called a parliament, wisdom, or study. A group of flamingos is called a flamboyance.", R.drawable.group_of_crows),
        BirdFact("The Fieldfare birds have a special way to attack an enemy bird. They gang up on it and make it fly to the ground. Then the Fieldfares fly into the air and drop poop on the bird.", R.drawable.fieldfare_poop),
        BirdFact("Vultures have stomach acid so corrosive that they can digest carcasses infected with anthrax.", R.drawable.eating_vulture),
        BirdFact("The slowest flying bird is the American Woodcock. It can fly at just 5 mph (8 kph). When hummingbirds hover, they move at 0 mph. Additionally, hummingbirds are the only birds that can fly backwards under power, registering a negative speed.", R.drawable.wood_cock),
        BirdFact("The fastest level flight by a bird has been seen in both the Spine-tailed Swift and the Red-breasted Merganser (a duck). They have flown at 100 mph (161 kph) in level flight.", R.drawable.red_breasted_merganser),
        BirdFact("The heaviest bird in the air is the Kori Bustard, from East and South Africa. It weighs about 31 lb. (14 kg.), with the largest on recorded being 40 lb. (18 kg.). Because it is such hard work to fly, it flies only in emergencies and for only short distances.", R.drawable.kori_bustard),
        BirdFact("The fastest flying bird in a dive is the Peregrine Falcon. It averages speeds of over 110 mph (180 kph).", R.drawable.peregrine_falcon)
    )

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
            showNextFact()
        }

        shareButton.setOnClickListener {
            Toast.makeText(requireContext(),"Fact Shared Successfully", Toast.LENGTH_SHORT).show()
        }
        // Show the initial fact when the fragment is created
        showFact(currentFactIndex)

        return view
    }

    private fun showFact(index: Int) {
        val fact = birdFacts[index]
        factTextView.text = fact.fact
        factImageView.setImageResource(fact.imageResource)
    }

    private fun showNextFact() {
        currentFactIndex = (currentFactIndex + 1) % birdFacts.size
        showFact(currentFactIndex)
    }

    data class BirdFact(val fact: String, val imageResource: Int)
}
