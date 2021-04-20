package com.example.toolpackapp.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.toolpackapp.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val loginBtn = findViewById<Button>(R.id.button_login)
        loginBtn.setOnClickListener{
            validateLogInDetails(it)
        }
    }

    private fun validateLogInDetails(view: View): Boolean{
        return when {
            TextUtils.isEmpty(findViewById<EditText>(R.id.loginEmail).text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(findViewById<EditText>(R.id.loginPassword).text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                showErrorSnackBar(view, "Login details are corrent", false)
                true
            }
        }
    }

    private fun logInRegisteredUser(){

    }


}