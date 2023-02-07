package com.example.greenhouse_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmailField2.text.toString()
            println("Pass1")
            if (email.isNotEmpty()) {
                println("Pass2")
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener{task ->
                        println("Pass3")
                        if (task.isSuccessful) {
                            Log.d("Auth", "User email sent!~")
                        } else {
                            Log.d("Auth", "Unresolved error occurred!")
                        }
                    }
            }
        }
    }
}