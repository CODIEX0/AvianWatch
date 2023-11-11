package com.example.avianwatch

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import android.view.View
import androidx.core.content.ContextCompat
import com.example.avianwatch.objects.Global
import com.example.avianwatch.data.User
import com.example.avianwatch.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private var userDatabaseReference : DatabaseReference? = null
    private lateinit var progressBar: ProgressBar
    private var debounceJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = findViewById(R.id.progressBar)

        auth = FirebaseAuth.getInstance()
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("User")

        // Initialize Firebase Auth
        auth = Firebase.auth

        val handler = Handler(Looper.getMainLooper())

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null) // Remove any existing callbacks
                handler.postDelayed({
                    val email = s.toString()
                    checkEmailAvailability(email)
                }, 500) // Delay for 500 milliseconds
            }
        })

        //Sign in user
        binding.txtSignIn.setOnClickListener {
            val signInIntent = Intent(this, LoginActivity::class.java)
            startActivity(signInIntent)
        }
        // Sign up user
        binding.btnSignUp.setOnClickListener {
            // Show loading indicator
            progressBar.visibility = View.VISIBLE
            // Perform input validation
            performSignUp()
        }

        //rendering the logo to the screen
        val imgIcon: ImageView = findViewById(R.id.imgIcon)
        val drawable = ContextCompat.getDrawable(this, R.mipmap.origami)
        imgIcon.setImageDrawable(drawable)
    }

    private fun clearTextBox(){
        // Clear edit text boxes
        binding.etUserName.setText("")
        binding.etEmail.setText("")
        binding.etPassword.setText("")
        binding.etReEnterPassword.setText("")
        // Hide loading indicator
        progressBar.visibility = View.INVISIBLE
    }

    private fun checkEmailAvailability(email: String) {
        if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result?.signInMethods
                        if (result.isNullOrEmpty()) {
                            // Email is not registered, proceed with sign-up
                            binding.etEmail.error = null
                        } else {
                            // Email is already in use
                            binding.etEmail.error = "Email Taken!"
                        }
                    } else {
                        // Failed to check email availability
                        Toast.makeText(this, "Error checking email availability", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            binding.etEmail.error = "Invalid Email Address"
        }
    }

    private fun performSignUp() {
        val username = binding.etUserName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etReEnterPassword.text.toString()

        // Perform input validation
        if (username == "" || password == "" || confirmPassword == "" || email == "") {
            //clear text
            clearTextBox()
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            // Hide loading indicator
            progressBar.visibility = View.INVISIBLE
            return
        }

        if (password != confirmPassword) {
            //clear passwords
            binding.etPassword.setText("")
            binding.etReEnterPassword.setText("")
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            // Hide loading indicator
            progressBar.visibility = View.INVISIBLE
            return
        }

        // Check if the username is already taken
        val isUsernameTaken = Global.users.any { it.username == username }
        if (isUsernameTaken) {
            binding.etUserName.setText("")
            Toast.makeText(this, "Username Taken!", Toast.LENGTH_SHORT).show()
            // Hide loading indicator
            progressBar.visibility = View.INVISIBLE
            return
        }

        // Check if the email is already taken
        (checkEmailAvailability(email))


        // Create a new User object
        val newUser = User(null, username, password, email)
        // Add the user to the Global.users list
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid
                    newUser.uid = uid

                    userDatabaseReference?.child(uid ?: "")?.setValue(newUser)

                    //navigate the user to the login screen
                    Toast.makeText(this, "User Registered!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)

                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
                // Hide loading indicator
                progressBar.visibility = View.INVISIBLE
            }
    }


}
