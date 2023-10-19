package com.example.avianwatch.objects

import android.util.Log
import com.example.avianwatch.data.BirdObservation
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

    //posts
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
                            // Add the post to the listcodie@gmail.com
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

    fun getAllPosts(callback: (MutableList<Post>) -> Unit) {
        val posts = mutableListOf<Post>()

        val database = FirebaseDatabase.getInstance()
        val postRef = database.getReference(POSTS_COLLECTION)

        // Query all posts
        postRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
            postRef.child(post.pId).setValue(post)
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


    // Function to update the "likes" variable of an existing post
    fun updatePostLikes(postId: String, likeStatus: Boolean, newLikes: Int, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val postRef = database.getReference(POSTS_COLLECTION)

        // Create a map to update only the "likes" field of the post
        val likesUpdate = HashMap<String, Any>()
        likesUpdate["likes"] = newLikes

        // Create a map to update only the "userHasLiked" field of the post
        val statusUpdate = HashMap<String, Any>()
        statusUpdate["userHasLiked"] = likeStatus

        // Update the specified post's "likes" field
        postRef.child(postId).updateChildren(likesUpdate)
            .addOnSuccessListener {
                // Likes updated successfully
                callback(true) // Invoke the success callback
            }
            .addOnFailureListener { exception ->
                // Error occurred while updating likes
                // Handle the exception...
                callback(false) // Invoke the failure callback
            }

        // Update the specified post's "userHasLiked" field
        postRef.child(postId).updateChildren(statusUpdate)
            .addOnSuccessListener {
                // Likes updated successfully
                callback(true) // Invoke the success callback
            }
            .addOnFailureListener { exception ->
                // Error occurred while updating likes
                // Handle the exception...
                callback(false) // Invoke the failure callback
            }
    }

    // Function to fetch the current "likes" count of a post
    fun getPostLikesCount(postId: String, callback: (Int) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val postRef = database.getReference(POSTS_COLLECTION)

        postRef.child(postId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.getValue(Post::class.java)
                val likesCount = post?.likes ?: 0
                callback(likesCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                callback(0)
            }
        })
    }

    // Function to fetch the current "likes" count of a post
    fun getPostLikesStatus(postId: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val postRef = database.getReference(POSTS_COLLECTION)

        postRef.child(postId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.getValue(Post::class.java)
                val likesStatus = post?.userHasLiked ?: Boolean
                callback(likesStatus as Boolean)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                callback(false)
            }
        })
    }

    //usernames
    fun getUserName(uid: String, callback: (String) -> Unit) {

        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference(USERS_COLLECTION)

        val usernameRef = userRef.child(uid).child("username")

        // Query the username based on the specified UID
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

    //users
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

    //observations
    fun getObservations(uid: String, callback: (MutableList<BirdObservation>) -> Unit) {
        val observations = mutableListOf<BirdObservation>()

        val database = FirebaseDatabase.getInstance()
        val observationRef = database.getReference(OBSERVATION_COLLECTION)

        // Query the observations based on the specified UID
        observationRef.orderByChild("uid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Iterate over the retrieved data snapshots
                    for (snapshot in dataSnapshot.children) {
                        // Retrieve the observation object from the snapshot
                        val observation = snapshot.getValue(BirdObservation::class.java)
                        observation?.let {
                            // Add the observation to the list
                            observations.add(it)
                        }
                    }
                    // Invoke the callback function with the retrieved posts
                    callback(observations)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                    callback(mutableListOf()) // Pass an empty list in case of error
                }
            })
    }
    fun addObservation(observation: BirdObservation, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val observationRef = database.getReference(OBSERVATION_COLLECTION)

        // get the current user's id
        val id = Global.currentUser!!.uid

        // Add the observation to the Firebase database using the id
        if (id != null) {
            observationRef.child(observation.oid).setValue(observation)
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

}

