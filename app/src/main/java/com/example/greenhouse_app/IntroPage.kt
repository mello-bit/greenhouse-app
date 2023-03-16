package com.example.greenhouse_app

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.greenhouse_app.databinding.ActivityIntroPageBinding
import com.example.greenhouse_app.utils.AppSettingsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class IntroPage : AppCompatActivity() {
    private lateinit var binding: ActivityIntroPageBinding
    private lateinit var firebaseAuth: FirebaseAuth

    // Перевод приложения
    override fun attachBaseContext(newBase: Context?) {
        val language = AppSettingsManager.loadData("Language") ?: "RU"
        val locale = Locale(language.lowercase())
        val config = newBase?.resources?.configuration?.apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        val context = newBase?.createConfigurationContext(config!!)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIntroPageBinding.inflate(layoutInflater)
        firebaseAuth = Firebase.auth
        supportActionBar?.hide()

        val cachedEmail = AppSettingsManager.loadData("cachedUserEmail") ?: ""
        val cachedPassword = AppSettingsManager.loadData("cachedUserPassword") ?: ""

        if (cachedEmail.isNotEmpty() && cachedPassword.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(cachedEmail, cachedPassword)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val myApp = applicationContext as MyApplication
                        myApp.currentUID = firebaseAuth.currentUser!!.uid
                        myApp.userEmail = cachedEmail

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        setContentView(binding.root)
                        MainActivity.showToast(this, "Ваша сессия истекла. Войдите заного.")
                    }
                }
        } else {
            setContentView(binding.root)
        }

        binding.signUp.setOnClickListener {
            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
        }

        binding.login.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
    }

    private fun setLocale(language: String) {
//        val locale = Locale(language.lowercase())
//        Locale.setDefault(locale)
//        val config = Configuration()
//        config.setLocale(locale)
//        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val locale = Locale(language.lowercase())
        Locale.setDefault(locale)
        val config = baseContext.resources.configuration
        config.setLocale(locale)
        baseContext.createConfigurationContext(config)
    }
}