package com.example.greenhouse_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.greenhouse_app.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginPage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        firebaseAuth = Firebase.auth

        supportActionBar?.hide()
        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmailField.text.toString()
            val password = binding.etPasswordField.text.toString()

            if (!email.contains('@') ||
                !email.contains('.') ||
                email.lastIndexOf('.') < email.indexOf('@')
            ) {
                Log.d("Auth", "Email format is bad")
                return@setOnClickListener
            }

            if (password.length < 8) {
                Log.d("Auth", "Password format is bad")
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    Log.d("Auth", "User logged in successfully")
                    val intent = Intent(this, MainActivity::class.java)
                    val t = Toast.makeText(this, "Вход выполнен", Toast.LENGTH_SHORT)
                    t.show()
                    startActivity(intent)
                } else {
                    Log.d("Auth", "Couldn't log in user")
                    val t = Toast.makeText(this, "Не удалось выполнить вход", Toast.LENGTH_SHORT)
                    t.show()
                }
            }
        }

        binding.btnRestore.setOnClickListener {
            val intent = Intent(this, RestorePage::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
    }
}