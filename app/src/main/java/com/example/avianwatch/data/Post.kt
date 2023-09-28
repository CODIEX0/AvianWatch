package com.example.avianwatch.data

class Post {
    var userID: String? = null
    var userName: String = ""
    var text: String = ""
    var likes: Int = 0
    var imageString: String? = ""

    constructor() {
        // Default constructor required by Firebase
    }

    constructor(
        userID: String?,
        userName: String,
        text: String,
        likes: Int,
        imageString: String?
    ) {
        this.userID = userID
        this.userName = userName
        this.text = text
        this.likes = likes
        this.imageString = imageString
    }
}


