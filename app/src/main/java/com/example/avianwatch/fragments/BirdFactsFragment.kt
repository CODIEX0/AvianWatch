package com.example.avianwatch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.objects.Global
import com.example.avianwatch.databinding.FragmentBirdFactsBinding

/* Code Attribution
   Title: #13 Android Application Development with Kotlin - LayoutInflater
   Link: https://www.youtube.com/watch?v=us1gp25WkCw
   Author: Simplified Coding
   Date: 2017
*/
class BirdFactsFragment : Fragment() {

    // Declare variables for the UI elements
    private lateinit var factTextView: TextView
    private lateinit var factImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBirdFactsBinding.inflate(inflater, container, false)

        factTextView = binding.factTextView
        factImageView = binding.factImageView

        val mainActivity = activity as MainActivity
        mainActivity.updateTitle("Random Bird Facts")

        // Show a random fact when the fragment is created
        showRandomFact()

        binding.btnNextFact.setOnClickListener {
            showRandomFact()
        }

        binding.btnShare.setOnClickListener {
            Toast.makeText(requireContext(),"Fact Shared Successfully", Toast.LENGTH_SHORT).show()
        }

        binding.btnPosts.setOnClickListener {
            mainActivity.updateTitle("Posts")
            replaceFragment(PostsFragment())
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

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment) // replace with the new fragment
        fragmentTransaction.addToBackStack(null) //add to back stack
        fragmentTransaction.commit()
    }

}

