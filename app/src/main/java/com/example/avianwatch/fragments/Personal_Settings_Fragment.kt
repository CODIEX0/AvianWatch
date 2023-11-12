package com.example.avianwatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.avianwatch.R
import com.example.avianwatch.data.AuthUtils
import com.example.avianwatch.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Personal_Settings_Fragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var currentUser: User
    private lateinit var userDatabaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal__settings_, container, false)

        // Initialize UI elements
        usernameEditText = view.findViewById(R.id.editTextTextPersonName)
        emailEditText = view.findViewById(R.id.editTextTextEmailAddress)
        saveButton = view.findViewById(R.id.button)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("User")

        // Set a default user to avoid null reference
        currentUser = User()

        // Retrieve current user data from Firebase
        retrieveCurrentUserFromFirebase(auth.currentUser?.uid ?: "") {
            // Display existing username and email in EditText fields
            usernameEditText.setText(currentUser.username)
            emailEditText.setText(currentUser.email)


        }

        // Set a click listener for the Save button
        saveButton.setOnClickListener {
            // Retrieve the current values
            val currentUsername = currentUser.username
            val currentEmail = currentUser.email

            // Retrieve the new values from EditText fields
            val newUsername = usernameEditText.text.toString()
            val newEmail = emailEditText.text.toString()

            // Check if the values are different from the current ones
            val isUsernameChanged = newUsername != currentUsername
            val isEmailChanged = newEmail != currentEmail

            // Validate the input (both username and email are required)
            if (newUsername.isNotEmpty() && newEmail.isNotEmpty()) {
                // Update user data in Firebase if any field is changed
                if (isUsernameChanged || isEmailChanged) {
                    // Update user data in Firebase
                    updateUserDataInFirebase(auth.currentUser?.uid ?: "", newUsername, newEmail)

                    // Display a success message
                    Toast.makeText(requireContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Display a message if no changes are made
                    Toast.makeText(requireContext(), "No changes made", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Display an error message if either username or email is empty
                Toast.makeText(requireContext(), "Username and email cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun retrieveCurrentUserFromFirebase(userId: String, onComplete: () -> Unit) {
        val userRef = userDatabaseReference.child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    currentUser = snapshot.getValue(User::class.java) ?: User()
                    onComplete.invoke() // Invoke the callback after data retrieval
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(requireContext(), "Error retrieving user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkEmailAvailability(email: String) {
        AuthUtils.checkEmailAvailability(email) { isAvailable, errorMessage ->
            requireActivity().runOnUiThread {
                if (isAvailable) {
                    // Email is available, proceed
                    emailEditText.error = null
                } else {
                    // Display error message
                    emailEditText.error = errorMessage
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun updateUserDataInFirebase(userId: String, newUsername: String, newEmail: String) {
        // Update user data in Firebase
        userDatabaseReference.child(userId).child("username").setValue(newUsername)
        userDatabaseReference.child(userId).child("email").setValue(newEmail)

        // Update the local user object
        currentUser.username = newUsername
        currentUser.email = newEmail
    }
}