package com.example.avianwatch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.avianwatch.R
import com.example.avianwatch.databinding.FragmentPostsBinding
import com.example.avianwatch.databinding.FragmentViewPostBinding
import com.example.avianwatch.objects.Image

class ViewPostFragment : Fragment() {
    private lateinit var binding: FragmentViewPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentViewPostBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the arguments Bundle
        val arguments = arguments

        // Check if arguments exist
        if (arguments != null) {
            // Retrieve the data from the bundle
            val userName = arguments.getString("user_name")
            val text = arguments.getString("text")
            val likes = arguments.getString("likes")
            val image = arguments.getString("image")
            //Update UI elements---

            //Set Values    `   ```````````         ```````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````
            binding.txtUserName.text = userName
            binding.txtCaption.text = text
            binding.txtLikes.text = likes
            Image.setBase64Image(image, binding.imgPost)
        }
    }

}