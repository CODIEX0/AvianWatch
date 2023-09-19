package com.example.avianwatch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.avianwatch.R
import com.example.avianwatch.data.PostItem
import com.example.avianwatch.data.Image

class BlogAdapter(private val blogs: List<PostItem>) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    inner class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val user_name: TextView = itemView.findViewById(R.id.txtUserName)
        val text: TextView = itemView.findViewById(R.id.txtText)
        val blog_image: ImageView = itemView.findViewById(R.id.imgBirdImage)

        fun bind(blog: PostItem) {
            user_name.text = blog.txtUserName
            text.text = blog.txtText
            if (blog.imgBlogImage.equals("")) {
                blog_image.visibility = View.INVISIBLE
            } else {
                blog_image.visibility = View.VISIBLE
                Image.setBase64Image(blog.imgBlogImage,blog_image)
            }
        }

        init {
            // Set click listener for the itemView
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val blog = blogs[position]
                    // Call the onItemClick method of the listener with the clicked blog
                    onItemClickListener?.onItemClick(blog)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogs[position]
        holder.bind(blog)
    }

    override fun getItemCount(): Int {
        return blogs.size
    }

    fun updateBlogList(blogList: List<PostItem>) {
        var list: MutableList<PostItem> = blogList.toMutableList()
        list.clear()
        list.addAll(blogList)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(blog: PostItem)
    }
    private var onItemClickListener: OnItemClickListener? = null

    // Setter for the click listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }
}