package com.example.avianwatch.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.adapters.PostAdapter
import com.example.avianwatch.data.Post
import com.example.avianwatch.objects.Image
import com.example.avianwatch.databinding.FragmentPostsBinding
import com.example.avianwatch.objects.FirebaseManager
import com.example.avianwatch.objects.Global

class PostsFragment : Fragment(), PostAdapter.OnItemClickListener {
    lateinit var post: Post

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentPostsBinding.inflate(inflater, container, false)

        val mainActivity = activity as MainActivity
        mainActivity.updateTitle("Community")

        // go to the settings fragment
        binding.ibAddPost.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            mainActivity.updateTitle("Add Post")

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val addBlogsFragment = AddPostFragment()
            fragmentTransaction.replace(R.id.fragment_container, addBlogsFragment) // replace with the new fragment
            fragmentTransaction.addToBackStack(null) //add to back stack
            fragmentTransaction.commit()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lstPosts = view.findViewById<RecyclerView>(R.id.rvBlogList)

        // Set up the LinearLayoutManager for the RecyclerView
        val postLayoutManager = LinearLayoutManager(requireContext())
        lstPosts.layoutManager = postLayoutManager
        // Retrieve updated posts
        FirebaseManager.getAllPosts { posts ->
            // Update the global posts list
            Global.posts = posts

            try{
                // Create an instance of PlantAdapter and pass the OnItemClickListener
                val adapter = PostAdapter(Global.posts)
                //adapter.setOnItemClickListener(this)
                // Set the adapter to the RecyclerView
                lstPosts.adapter = adapter
            }catch (e:Exception){
                Toast.makeText(requireContext(),e.message, Toast.LENGTH_SHORT).show()
                Log.d(ContentValues.TAG, e.message.toString())
            }
        }
    }

    override fun onItemClick(post: Post) {
        // Handle the click event and navigate to a different fragment
        //Add data to bundle
        val bundle = Bundle()
        bundle.putString("postId", post.pId)
        bundle.putString("userID", post.userID)
        bundle.putString("user_name", post.userName)
        bundle.putString("text", post.text)
        bundle.putBoolean("has_liked", post.userHasLiked)
        bundle.putString("likes", post.likes.toString())
        bundle.putString("image", post.imageString)

        try{
            val fragment = PostsFragment()
            fragment.arguments = bundle

            //Navigate to fragment, passing bundle
            findNavController().navigate(R.id.action_PostsFragment_to_ViewPostFragment, bundle)
        }catch (e:Exception){
            Toast.makeText(requireContext(),e.message, Toast.LENGTH_SHORT).show()
            Log.d(ContentValues.TAG, e.message.toString())
        }
    }
}