package com.example.avianwatch.data

class Post {
    var userID: String? = null
    var pId: String = ""
    var userName: String = ""
    var text: String = ""
    var likes: Int = 0
    var userHasLiked: Boolean = false
    var imageString: String? = ""

    constructor() {
        // Default constructor required by Firebase
    }

    constructor(
        userID: String?,
        pId: String,
        userName: String,
        text: String,
        likes: Int,
        userHasLiked: Boolean,
        imageString: String?
    ) {
        this.userID = userID
        this.pId = pId
        this.userName = userName
        this.text = text
        this.likes = likes
        this.userHasLiked =userHasLiked
        this.imageString = imageString
    }
}


