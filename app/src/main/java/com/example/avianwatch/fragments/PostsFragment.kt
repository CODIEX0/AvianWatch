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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.avianwatch.MainActivity
import com.example.avianwatch.R
import com.example.avianwatch.adapters.BlogAdapter
import com.example.avianwatch.data.PostItem
import com.example.avianwatch.data.Image
import com.example.avianwatch.databinding.FragmentPostsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostsFragment : Fragment(), BlogAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var blogs: MutableList<PostItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        blogs = mutableListOf(
            PostItem(
                Image.drawableToBase64(ContextCompat.getDrawable(requireContext(), R.mipmap.post_bird)!!),
                "Cody Ntuli",
                "This birds like doing 10 – 15 circular laps just before sunset, around my neighborhood. Do you guys have any idea what does this mean?",
                1243
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentPostsBinding.inflate(inflater, container, false)

        // go to the settings fragment
        binding.ibAddPost.setOnClickListener {
            // Access the MainActivity and call the function to update the tool bar title
            val mainActivity = activity as MainActivity
            mainActivity.updateTitle("Add Post")

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val addBlogsFragment = AddPostFragment()
            fragmentTransaction.replace(R.id.fragment_container, addBlogsFragment) // replace with the new fragment
            fragmentTransaction.addToBackStack(null) //add to back stack
            fragmentTransaction.commit()
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlogsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lstBlogs = view.findViewById<RecyclerView>(R.id.rvBlogList)

        // Set up the LinearLayoutManager for the RecyclerView
        val plantLayoutManager = LinearLayoutManager(requireContext())
        lstBlogs.layoutManager = plantLayoutManager

        try{
            // Create an instance of PlantAdapter and pass the OnItemClickListener
            val adapter = BlogAdapter(blogs)
            adapter.setOnItemClickListener(this)
            // Set the adapter to the RecyclerView
            lstBlogs.adapter = adapter
        }catch (e:Exception){
            Toast.makeText(activity,e.message, Toast.LENGTH_SHORT).show()
            Log.d(ContentValues.TAG, e.message.toString())
        }
    }

    override fun onItemClick(blog: PostItem) {
        // Handle the click event and navigate to a different fragment
        //Add data to bundle
        val bundle = Bundle()
        bundle.putString("user_name", blog.txtUserName)
        bundle.putString("text", blog.txtText)
        bundle.putString("likes", blog.likes.toString())
        bundle.putString("image", blog.imgBlogImage)

        try{
            val fragment = PostsFragment()
            fragment.arguments = bundle

            //Navigate to fragment, passing bundle
            //findNavController().navigate(R.id.action_ObservationListFragment_to_ViewPlantFragment, bundle)
        }catch (e:Exception){
            Toast.makeText(activity,e.message, Toast.LENGTH_SHORT).show()
            Log.d(ContentValues.TAG, e.message.toString())
        }

    }
}