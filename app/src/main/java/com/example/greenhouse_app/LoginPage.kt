package com.example.greenhouse_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.greenhouse_app.databinding.ActivityLoginPageBinding

class LoginPage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}