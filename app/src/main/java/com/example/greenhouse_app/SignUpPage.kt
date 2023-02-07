package com.example.greenhouse_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.greenhouse_app.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpPage : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpPageBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpPageBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.signUp.setOnClickListener {
            val textEmail = binding.email.text
            val password = binding.password.text
            val repeatPassword = binding.repeatedPassword.text

            val enableEmail: Boolean = if (!textEmail.contains('@') ||
                !textEmail.contains('.') ||
                textEmail.lastIndexOf('.') < textEmail.indexOf('@')
            ) {
                binding.email.setBackgroundResource(R.drawable.background_error)
                false
            } else {
                binding.email.setBackgroundResource(R.drawable.background_for_text)
                true
            }
            val enablePassword: Boolean = if (password.length < 8) {
                binding.password.setBackgroundResource(R.drawable.background_error)
                false
            } else {
                binding.password.setBackgroundResource(R.drawable.background_for_text)
                true
            }
            val enableRepPassword: Boolean = if (repeatPassword.toString() != password.toString() || repeatPassword.length < 8 ||
                !check_pas(repeatPassword.toString())
            ) {
                binding.repeatedPassword.setBackgroundResource(R.drawable.background_error)
                false
            } else {
                binding.repeatedPassword.setBackgroundResource(R.drawable.background_for_text)
                true
            }

            if (enableEmail && enablePassword && enableRepPassword) {
                firebaseAuth.createUserWithEmailAndPassword(
                    textEmail.toString(),
                    password.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val t = Toast.makeText(this, "It's ok", Toast.LENGTH_SHORT)
                        t.show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        val response = R.string.response
                        val t = Toast.makeText(
                            this, response,
                            Toast.LENGTH_SHORT
                        )
                        t.show()
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "You have incorrect data",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }

    private fun check_pas(p: String): Boolean {
        var count = 0
        for (c in p) {
            if (c in 'A'..'Z' || c in 'a'..'z') {
                count += 1
            }
        }
        if (count == p.length) return false

        return true
    }
}