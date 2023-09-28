package com.example.avianwatch.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.avianwatch.data.Post
import com.example.avianwatch.databinding.FragmentAddPostBinding
import com.example.avianwatch.objects.FirebaseManager
import com.example.avianwatch.objects.Global
import com.example.avianwatch.objects.Image.convertImageToBase64
import com.github.dhaval2404.imagepicker.ImagePicker

class AddPostFragment : Fragment() {
    lateinit var binding: FragmentAddPostBinding
    val REQUEST_IMAGE_CAPTURE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(inflater, container, false)

        binding.btnCamera.setOnClickListener {
            ImagePicker.with(this)
                .crop()                     //crop image(optional), check customization for more options
                .compress(1024)             //final image size will be less than 1 MB
                .maxResultSize(1080,1080)   //final image resolution will be less than 1080 x 1080
                .start()
        }

        binding.btnAddObservation.setOnClickListener {
            addPost()
        }

        return binding.root
    }

    fun addPost() {
        val imageData = convertImageToBase64(binding.imgPostImage).toString()

        // Update the global users list
        val post = Post(
            Global.currentUser?.uid.toString(), //Store UID to create relationship
            Global.currentUser?.username.toString(),
            binding.etText.text.toString(),
            0,
            imageData
        )
        Global.posts.add(post)
        //Add post to DB and update local storage
        FirebaseManager.addPost(post) { isSuccess -> //Use callback to wait for results
            if (isSuccess)
            {
                //Update local posts list
                FirebaseManager.getPosts(Global.currentUser!!.uid.toString()) { posts ->
                    Global.posts = posts
                }
                //Toast.makeText(requireContext(), "Post Created Successfully!", Toast.LENGTH_SHORT).show()

            } else {
                //Toast.makeText(requireContext(), "Post Creation Failed...", Toast.LENGTH_LONG).show()
            }

        }

        if (binding.etText.text.toString() == "") {
            //Toast.makeText(requireContext(), "Enter the Caption...", Toast.LENGTH_SHORT).show()
            return
        }
        requireActivity().onBackPressed() // Navigate back to the previous screen
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imgPostImage.setImageBitmap(imageBitmap)
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
        binding.imgPostImage.setImageURI(data?.data)
    }

}