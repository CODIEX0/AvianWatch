package com.example.avianwatch.objects

import android.util.Log
import com.example.avianwatch.data.Post
import com.example.avianwatch.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseManager {
    // Collection names in the Firebase database
    private const val POSTS_COLLECTION = "Post"
    private const val OBSERVATION_COLLECTION = "BirdObservation"
    private const val PREFERENCES_COLLECTION = "UserPreferences"
    private const val USERS_COLLECTION = "User"

    fun getUsers(uid: String, callback: (MutableList<User>) -> Unit) {
        val users = mutableListOf<User>()

        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference(USERS_COLLECTION)

        // Query the users based on the specified UID
        userRef.orderByChild("uid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Iterate over the retrieved data snapshots
                    for (snapshot in dataSnapshot.children) {
                        // Retrieve the user object from the snapshot
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            // Add the user to the list
                            users.add(it)
                        }
                    }
                    // Invoke the callback function with the retrieved users
                    callback(users)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                    callback(mutableListOf()) // Pass an empty list in case of error
                }
            })
    }

    fun getPosts(uid: String, callback: (MutableList<Post>) -> Unit) {
        val posts = mutableListOf<Post>()

        val database = FirebaseDatabase.getInstance()
        val postRef = database.getReference(POSTS_COLLECTION)

        // Query the posts based on the specified UID
        postRef.orderByChild("uid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Iterate over the retrieved data snapshots
                    for (snapshot in dataSnapshot.children) {
                        // Retrieve the post object from the snapshot
                        val post = snapshot.getValue(Post::class.java)
                        post?.let {
                            // Add the post to the list
                            posts.add(it)
                        }
                    }
                    // Invoke the callback function with the retrieved posts
                    callback(posts)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                    callback(mutableListOf()) // Pass an empty list in case of error
                }
            })
    }

    fun addPost(post: Post, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val postRef = database.getReference(POSTS_COLLECTION)

        // get the current user's id
        val id = Global.currentUser!!.uid

        // Add the post to the Firebase database using the id
        if (id != null) {
            postRef.child(id ?: "").setValue(post)
                .addOnSuccessListener {
                    // post added successfully
                    callback(true) // Invoke the success callback
                }
                .addOnFailureListener { exception ->
                    // Error occurred while adding the post
                    //Do something with exception...
                    callback(false) // Invoke the failure callback
                }
        } else {
            callback(false) // Invoke the failure callback if id is null
        }
    }


    fun updateLikes(likes: Int, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val postRef = database.getReference(POSTS_COLLECTION)

        val id = Global.currentUser!!.uid

        // Add the like to the Firebase database using the generated ID
        if (id != null) {
            val likesRef = postRef.child(id).child("likes")
            likesRef.setValue(likes)
                .addOnSuccessListener {
                    // likes updated successfully
                    callback(true) // Invoke the success callback
                }
                .addOnFailureListener { exception ->
                    // Error occurred while updating the likes
                    //Do something with exception...
                    callback(false) // Invoke the failure callback
                }
        } else {
            callback(false) // Invoke the failure callback if like null
        }
    }

    fun getUserName(uid: String, callback: (String) -> Unit) {

        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference(USERS_COLLECTION)

        val usernameRef = userRef.child(uid).child("username")

        // Query the workcoins based on the specified UID
        usernameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val username: String = dataSnapshot.value.toString()

                if(username!=null){
                    callback(username)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(10.toString()) // Invoke the failure callback
                // Failed to read the value
                Log.d("Failed to read the value","Error: ${databaseError.message}")
            }
        })
    }

}
