package com.example.greenhouse_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.greenhouse_app.databinding.ActivityLoginPageBinding
import com.example.greenhouse_app.utils.AppSettingsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

            binding.etEmailField.setBackgroundResource(R.drawable.background_for_text)
            binding.etPasswordField.setBackgroundResource(R.drawable.background_for_text)

            if (!email.contains('@') ||
                !email.contains('.') ||
                email.lastIndexOf('.') < email.indexOf('@')
            ) {
                Log.d("Auth", "Email filter error")
                binding.etEmailField.setBackgroundResource(R.drawable.background_error)
                MainActivity.showToast(this, this.getString(R.string.invalid_email_format))
                return@setOnClickListener
            } else if(password.length < 8) {
                Log.d("Auth", "Password filter error")
                binding.etPasswordField.setBackgroundResource(R.drawable.background_error)
                MainActivity.showToast(this, this.getString(R.string.invalid_password_format))
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    Log.d("Auth", "User logged in successfully")
                    val intent = Intent(this, MainActivity::class.java)
                    val myApp = application as MyApplication
                    myApp.currentUID = firebaseAuth.currentUser!!.uid
                    myApp.userEmail = email

                    CoroutineScope(Dispatchers.IO).launch {
                        if (binding.cbRememberMe.isChecked) {
                            AppSettingsManager.saveData("cachedUserEmail", email)
                            AppSettingsManager.saveData("cachedUserPassword", password)
                        } else {
                            AppSettingsManager.saveData("cachedUserEmail", "")
                            AppSettingsManager.saveData("cachedUserPassword", "")
                        }
                    }

                    MainActivity.showToast(this, this.getString(R.string.login_success))
                    startActivity(intent)
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            binding.etPasswordField.setBackgroundResource(R.drawable.background_error)
                            binding.etEmailField.setBackgroundResource(R.drawable.background_error)
                            MainActivity.showToast(this, this.getString(R.string.invalid_credentials))
                            Log.d("Auth", "User entered wrong credentials")

                        }
                        is FirebaseAuthInvalidUserException -> {
                            binding.etEmailField.setBackgroundResource(R.drawable.background_error)
                            MainActivity.showToast(this, this.getString(R.string.email_not_found))
                            Log.d("Auth", "User entry does not exist")
                        }
                        else -> {
                            MainActivity.showToast(this, this.getString(R.string.unexpected_error))
                            Log.d("Auth", "Unresolved exception: ${task.exception}")
                        }
                    }

                }
            }
        }

        binding.btnRestore.setOnClickListener {
            val intent = Intent(this, RestorePage::class.java)
            startActivity(intent)
        }
    }
}