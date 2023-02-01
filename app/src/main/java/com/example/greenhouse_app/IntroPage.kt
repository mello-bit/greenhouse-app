package com.example.greenhouse_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greenhouse_app.databinding.ActivityIntroPageBinding

class IntroPage : AppCompatActivity() {
    private lateinit var binding: ActivityIntroPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroPageBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.signUp.setOnClickListener {
            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
        }
    }
}