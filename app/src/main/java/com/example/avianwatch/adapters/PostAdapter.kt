package com.example.avianwatch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.avianwatch.R
import com.example.avianwatch.data.Post
import com.example.avianwatch.objects.FirebaseManager
import com.example.avianwatch.objects.Image

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var likesCount = 0 // Initialize with the current likes count
        var userHasLiked = false // Initialize the flag
        val user_name: TextView = itemView.findViewById(R.id.txtUserName)
        val text: TextView = itemView.findViewById(R.id.txtText)
        val image: ImageView = itemView.findViewById(R.id.imgBirdImage)
        val like: ImageView = itemView.findViewById(R.id.like_icon)
        var likes: TextView = itemView.findViewById(R.id.txtLikes)

        // Function to update the like button state
        fun updateLikeButtonState() {
            if (userHasLiked) {
                // User has already liked, show the "liked" state
                like.setBackgroundResource(R.mipmap.liked) // Change the button background to indicate "liked"
            } else {
                // User has not liked, show the "unliked" state
                like.setBackgroundResource(R.mipmap.like) // Change the button background to indicate "unliked"
            }
        }
        fun bind(post: Post) {
            user_name.text = post.userName
            text.text = post.text
            if (post.imageString.equals("")) {
                image.visibility = View.INVISIBLE
            } else {
                image.visibility = View.VISIBLE
                Image.setBase64Image(post.imageString,image)
            }

            like.setOnClickListener {
                if (userHasLiked) {
                    // User has already liked, unlike the post
                    likesCount--
                } else {
                    // User has not liked, like the post
                    likesCount++
                }

                // Toggle the like status
                userHasLiked = !userHasLiked

                // Update the like button state
                updateLikeButtonState()

                // Update the likes count display
                likes.text = likesCount.toString()

                FirebaseManager.updateLikes(likes.text.toString().toInt()){isSuccess -> //Use callback to wait for results
                    if (isSuccess)
                    {

                    } else {

                    }
                }
            }
        }

        init {
            // Set click listener for the itemView
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val post = posts[position]
                    // Call the onItemClick method of the listener with the clicked blog
                    onItemClickListener?.onItemClick(post)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePostList(postList: List<Post>) {
        var list: MutableList<Post> = postList.toMutableList()
        list.clear()
        list.addAll(postList)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(post: Post)
    }
    private var onItemClickListener: OnItemClickListener? = null

    // Setter for the click listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }
}