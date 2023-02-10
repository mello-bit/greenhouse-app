package com.example.greenhouse_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.greenhouse_app.databinding.ActivityRestorePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RestorePage : AppCompatActivity() {
    private lateinit var binding: ActivityRestorePageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestorePageBinding.inflate(layoutInflater)
        auth = Firebase.auth

        supportActionBar?.hide()
        setContentView(binding.root)

        binding.btnSendEmail.setOnClickListener {
            val email = binding.etEmailField2.text.toString()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful) {
                            Log.d("Auth", "User email sent!~")
                        } else {
                            Log.d("Auth", "Restore exception raised: ${task.exception}")
                        }
                    }
                Toast.makeText(this, "Если учётная запись с такой почтой существует, то мы отправили на неё письмо", Toast.LENGTH_SHORT).show()
            }
        }
    }
}