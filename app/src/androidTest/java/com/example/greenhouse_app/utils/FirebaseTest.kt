package com.example.greenhouse_app.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


fun generateRandomEmail(): String {
    val username = UUID.randomUUID().toString().replace("-", "")
    return "$username@mail.com"
}

@RunWith(AndroidJUnit4::class)
class FirebaseAccessTest {
    val email = generateRandomEmail()
    val password = "12345678910"

    @Test
    fun testRegistration() {
        val firebaseAuth = Firebase.auth
        val latch = CountDownLatch(1)

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { signUpTask ->
                assertEquals(true, signUpTask.isSuccessful)
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {loginTask ->
                        assertEquals(true, loginTask.isSuccessful)
                        assertNotEquals(firebaseAuth.currentUser, null)

                        firebaseAuth.currentUser!!.delete()
                            .addOnCompleteListener { deleteTask ->
                                assertEquals(true, deleteTask.isSuccessful)
                                latch.countDown()
                            }
                            .addOnFailureListener {
                                fail("3. Failed to delete created user with exception: $it")
                            }
                    }
                    .addOnFailureListener { fail("2. Failed with $it") }
            }
            .addOnFailureListener {
                fail("1. Failed with $it")
            }

        val success = latch.await(30, TimeUnit.SECONDS)
        assertEquals(true, success)
    }
}