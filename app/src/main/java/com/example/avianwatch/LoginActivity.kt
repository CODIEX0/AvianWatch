package com.example.avianwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.avianwatch.objects.Global
import com.example.avianwatch.data.User
import com.example.avianwatch.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/* Code Attribution
   Title: Data Binding and View Binding in Android Studio using Kotlin | Android Knowledge
   Link: https://www.youtube.com/watch?v=1V5g1qd8ToQ
   Author: Android Knowledge
   Date: 2023
*/
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var userDatabaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("User")

        binding.txtSignUp.setOnClickListener {
            val signupIntent = Intent(this, RegisterActivity::class.java)
            startActivity(signupIntent)
        }

        binding.txtForgotPass.setOnClickListener {
            val email = binding.edtEmail.text.toString()

            if (email.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Password reset email sent successfully
                            Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                        } else {
                            // Password reset email sending failed
                            Toast.makeText(this, "Password reset failed. Please check your email.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Handle the case where the email field is empty
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.etPassWord.text.toString()
            validateLogin(email, password)
        }

        val imgIcon: ImageView = findViewById(R.id.imgIcon)
        val drawable = ContextCompat.getDrawable(this, R.mipmap.origami)
        imgIcon.setImageDrawable(drawable)
    }

    private fun validateLogin(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this@LoginActivity, "Empty email or password", Toast.LENGTH_SHORT).show()
            Log.d("LoginActivity", "Empty email or password")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "Login successful")
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid

                    if (uid != null) {
                        userDatabaseReference?.child(uid)?.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val userSnapshot = dataSnapshot.getValue(User::class.java)
                                if (userSnapshot != null) {
                                    Global.currentUser = userSnapshot
                                    val name = userSnapshot.username
                                    Toast.makeText(this@LoginActivity, "Signed In as $name", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Log.d("LoginActivity", "User data not found in the database")
                                    Toast.makeText(this@LoginActivity, "User data not found in the database", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d("LoginActivity", "Error accessing user data in the database: ${databaseError.message}")
                                Toast.makeText(this@LoginActivity, "Error accessing user data in the database", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } else {
                    Log.d("LoginActivity", "Login failed: ${task.exception?.message}")
                    Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
