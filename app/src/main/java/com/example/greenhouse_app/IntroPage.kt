package com.example.greenhouse_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class IntroPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_page)
        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
    }
}