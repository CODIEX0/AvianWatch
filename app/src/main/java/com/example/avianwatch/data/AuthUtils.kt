package com.example.avianwatch.data

import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult

object AuthUtils {

    fun checkEmailAvailability(email: String, onComplete: (Boolean, String?) -> Unit) {
        if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            val auth = FirebaseAuth.getInstance()
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result: SignInMethodQueryResult? = task.result
                        if (result?.signInMethods?.isEmpty() == true) {
                            // Email is not registered, availability check is successful
                            onComplete(true, null)
                        } else {
                            // Email is already in use
                            onComplete(false, "Email Taken!")
                        }
                    } else {
                        // Failed to check email availability
                        onComplete(false, "Error checking email availability")
                    }
                }
        } else {
            // Invalid email address
            onComplete(false, "Invalid Email Address")
        }
    }
}