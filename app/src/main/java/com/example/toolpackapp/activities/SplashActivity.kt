package com.example.toolpackapp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.driver.bottomNav.DriverMainViewActivity
import com.example.toolpackapp.activities.driver.bottomNav.account.DriverAccountFragment
import com.example.toolpackapp.activities.manager.ManagerMainActivity
import com.example.toolpackapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                checkIfUserLoggedIn()
            },
            1500
        )
    }

    private fun checkIfUserLoggedIn() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser !== null) {
            Log.d("Splash", "user => $currentUser")
            FirebaseFirestore.getInstance().collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)!!
                    when (user.accountType) {
                        "manager" -> {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    ManagerMainActivity::class.java
                                )
                            )
                        }
                        else -> {
                            if (user.profileCompleted == 0) {
                                val intent = Intent(
                                    this@SplashActivity,
                                    DriverAccountFragment::class.java
                                )
                                intent.putExtra("user_details", user)
                                startActivity(intent)
                            } else {
                                startActivity(
                                    Intent(
                                        this@SplashActivity,
                                        DriverMainViewActivity::class.java
                                    )
                                )

                            }
                        }
                    }
                    finish()
                }
        } else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }

    }
}