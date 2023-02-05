package com.example.greenhouse_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.greenhouse_app.databinding.ActivityIntroPageBinding

class IntroPage : AppCompatActivity() {
    private lateinit var binding: ActivityIntroPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroPageBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.signUp.setOnClickListener {
            val t = Toast.makeText(this, "Go to Sign up", Toast.LENGTH_SHORT)
            t.show()
            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
        }

        binding.login.setOnClickListener {
            val t = Toast.makeText(this, "Go to Login", Toast.LENGTH_SHORT)
            t.show()
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
    }
}