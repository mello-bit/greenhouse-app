package com.example.greenhouse_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.greenhouse_app.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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

            binding.etEmailField.setBackgroundResource(R.drawable.background_for_text)
            binding.etPasswordField.setBackgroundResource(R.drawable.background_for_text)

            if (!email.contains('@') ||
                !email.contains('.') ||
                email.lastIndexOf('.') < email.indexOf('@')
            ) {
                Log.d("Auth", "Email filter error")
                binding.etEmailField.setBackgroundResource(R.drawable.background_error)
                Toast.makeText(this, "Формат почты не соблюдён", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if(password.length < 8) {
                Log.d("Auth", "Password filter error")
                binding.etPasswordField.setBackgroundResource(R.drawable.background_error)
                Toast.makeText(this, "Пароль меньше 8 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
//                if (task.exception is Exception) {
//                    Log.d("Auth", task.exception.toString())
//                }
                if (task.isSuccessful) {
                    Log.d("Auth", "User logged in successfully")
                    val intent = Intent(this, MainActivity::class.java)
                    (application as MyApplication).currentUID = firebaseAuth.currentUser!!.uid
                    val t = Toast.makeText(this, "Вход выполнен", Toast.LENGTH_SHORT)
                    t.show()
                    startActivity(intent)
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            binding.etPasswordField.setBackgroundResource(R.drawable.background_error)
                            binding.etEmailField.setBackgroundResource(R.drawable.background_error)
                            Toast.makeText(this, "Пароль или почта не верны", Toast.LENGTH_SHORT).show()
                            Log.d("Auth", "User entered wrong credentials")

                        }
                        is FirebaseAuthInvalidUserException -> {
                            binding.etEmailField.setBackgroundResource(R.drawable.background_error)
                            Toast.makeText(this, "Пользователя с данной почтой не существует или он отключен.", Toast.LENGTH_SHORT).show()
                            Log.d("Auth", "User entry does not exist")
                        }
                        else -> {
                            Toast.makeText(this, "Возникла непредвиденная ошибка", Toast.LENGTH_SHORT).show()
                            Log.d("Auth", "Unresolved exception: ${task.exception}")
                        }
                    }


//                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                        Log.d("Auth", "User entered wrong password")
//                    } else if(task.exception is FirebaseAuthInvalidUserException) {
//                        Log.d("Auth", "User entry does not exist")
//                    } else {
//                        Log.d("Auth", "Couldn't log in user")
//                        val t =
//                            Toast.makeText(this, "Не удалось выполнить вход", Toast.LENGTH_SHORT)
//                        t.show()
//                    }
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