package com.example.greenhouse_app

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
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.signUp.setOnClickListener {
            val textEmail = binding.email.text
            val password = binding.password.text
            val repeatPassword = binding.repeatedPassword.text
            val enableEmail: Boolean
            val enablePassword: Boolean
            val enableRepPassword: Boolean

            if (!textEmail.contains('@') ||
                !textEmail.contains('.') ||
                textEmail.lastIndexOf('.') < textEmail.indexOf('@')
            ) {
                binding.email.setBackgroundResource(R.drawable.background_error)
                binding.errorEmailData.visibility = View.VISIBLE
                binding.errorEmailData.text = "Введена некорректная почта"
                enableEmail = false
            } else {
                binding.email.setBackgroundResource(R.drawable.background_for_text)
                binding.errorEmailData.visibility = View.GONE
                enableEmail = true
            }
            if (password.length < 8 || !check_pas(password.toString())) {
                binding.password.setBackgroundResource(R.drawable.background_error)
                binding.errorPasswordData.visibility = View.VISIBLE
                binding.errorPasswordData.text = "Пароль некорректен"
                enablePassword = false
            } else {
                binding.password.setBackgroundResource(R.drawable.background_for_text)
                binding.errorPasswordData.visibility = View.GONE
                enablePassword = true
            }
            if (repeatPassword.toString() != password.toString() || repeatPassword.length < 8 ||
                !check_pas(repeatPassword.toString())
            ) {
                binding.repeatedPassword.setBackgroundResource(R.drawable.background_error)
                binding.errorRepeatPasswordData.visibility = View.VISIBLE
                binding.errorRepeatPasswordData.text = "Пароль некорректен"
                enableRepPassword = false
            } else {
                binding.repeatedPassword.setBackgroundResource(R.drawable.background_for_text)
                binding.errorRepeatPasswordData.visibility = View.GONE
                enableRepPassword = true
            }

            if (enableEmail && enablePassword && enableRepPassword) {
                firebaseAuth.createUserWithEmailAndPassword(
                    textEmail.toString(),
                    password.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val t = Toast.makeText(this, "It's ok", Toast.LENGTH_SHORT)
                        t.show()
                    } else {
                        val t = Toast.makeText(
                            this, "Sorry. We have some troubles",
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