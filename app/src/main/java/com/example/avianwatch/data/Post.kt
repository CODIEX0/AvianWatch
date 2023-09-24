package com.example.avianwatch.data

data class Post(
    val userID: String?,
    val userName: String,
    var text: String,
    var likes: Int,
    var imageString: String?
)
