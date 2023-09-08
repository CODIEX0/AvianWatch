package com.example.avianwatch.data

import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView

data class BlogItem(
    val imgBlogImage: String?,
    val txtUserName: String,
    val txtText: String,
    val likes: Int
)
