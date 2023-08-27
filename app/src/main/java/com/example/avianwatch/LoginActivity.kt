package com.example.avianwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat

class LoginActivity : AppCompatActivity() {
    private lateinit var btnSignUp: Button
    private lateinit var btnSignIn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //initializing all the views
        initView()

        //Sign in user
        btnSignIn.setOnClickListener {
            val SigninIntent = Intent(this,MainActivity::class.java)
            startActivity(SigninIntent)
        }
        // Sign up user
        btnSignUp.setOnClickListener {
            val SignUpIntent = Intent(this,RegisterActivity::class.java)
            startActivity(SignUpIntent)
            // Perform input validation
            //performSignUp()
        }

        //rendering the logo to the screen
        val imgIcon : ImageView = findViewById(R.id.imgIcon)
        val drawable = ContextCompat.getDrawable(this, R.mipmap.origami)
        imgIcon.setImageDrawable(drawable)
    }

    private fun initView() {
        btnSignUp = findViewById(R.id.btnSignUp)
        btnSignIn = findViewById(R.id.btnSignIn)
    }
}