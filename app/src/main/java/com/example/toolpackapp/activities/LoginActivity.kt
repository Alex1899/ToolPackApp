package com.example.toolpackapp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.driver.bottomNav.DriverMainViewActivity
import com.example.toolpackapp.activities.driver.bottomNav.account.DriverAccountFragment
import com.example.toolpackapp.activities.manager.ManagerMainActivity
import com.example.toolpackapp.databinding.ActivityLoginBinding
import com.example.toolpackapp.databinding.ActivityManagerEditPackageDetailsBinding
import com.example.toolpackapp.firebaseNotifications.FirebaseService
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.User
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.setErrorTextField
import com.example.toolpackapp.utils.showErrorSnackBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val forgotPwd = findViewById<TextView>(R.id.forgot_pwd)
        forgotPwd.setOnClickListener{
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        val loginBtn = findViewById<Button>(R.id.button_login)
        loginBtn.setOnClickListener{
            logInRegisteredUser(it)
        }
    }

    private fun validateLogInDetails(view: View): Boolean{
        return when {
            TextUtils.isEmpty(binding?.loginEmailInputText?.text.toString().trim{ it <= ' '}) -> {
                setErrorTextField(binding?.loginEmailInput!!, true, resources.getString(R.string.err_msg_enter_email))
                //showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding?.loginPasswordInputText?.text.toString().trim{ it <= ' '}) -> {
                setErrorTextField(binding?.loginPasswordInput!!, true, resources.getString(R.string.err_msg_enter_password))
                //showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }
    private fun clearLogInForm(){
        binding?.loginEmailInputText?.setText("")
        binding?.loginEmailInput?.isErrorEnabled = false

        binding?.loginPasswordInputText?.setText("")
        binding?.loginPasswordInput?.isErrorEnabled = false
    }

    private fun logInRegisteredUser(view: View){
        if(validateLogInDetails(view)){
            com.example.toolpackapp.utils.showDialog(this@LoginActivity)
            val email = binding?.loginEmailInputText?.text.toString().trim{it <= ' '}
            val password = binding?.loginPasswordInputText?.text.toString().trim{it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        FirestoreClass().getUserDetails(this@LoginActivity)
                        clearLogInForm()

                    }else{
                        hideDialog()
                        showErrorSnackBar(view, task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user: User){
        hideDialog()
        FirestoreClass().saveNotificationToken(FirebaseService.token!!)
        when(user.accountType){
            "manager" -> {
                startActivity(Intent(this@LoginActivity, ManagerMainActivity::class.java))
            }
            else -> {
                if(user.profileCompleted == 0){
                    val intent = Intent(this@LoginActivity, DriverAccountFragment::class.java)
                    intent.putExtra("user_details", user)
                    startActivity(intent)
                }else{
                    startActivity(Intent(this@LoginActivity, DriverMainViewActivity::class.java))

                }
            }
        }
        finish()
    }




}