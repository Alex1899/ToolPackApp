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
import com.example.toolpackapp.activities.driver.DriverUpdateDetailsActivity
import com.example.toolpackapp.activities.driver.DriverViewPackagesActivity
import com.example.toolpackapp.activities.driver.bottomNav.DriverMainViewActivity
import com.example.toolpackapp.activities.manager.ManagerMainActivity
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.User
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.showErrorSnackBar
import com.google.firebase.auth.FirebaseAuth

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
            TextUtils.isEmpty(findViewById<EditText>(R.id.loginEmail).text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(findViewById<EditText>(R.id.loginPassword).text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }
    private fun clearLogInForm(){
        findViewById<EditText>(R.id.loginEmail).setText("")
        findViewById<EditText>(R.id.loginPassword).setText("")
    }

    private fun logInRegisteredUser(view: View){
        if(validateLogInDetails(view)){
            com.example.toolpackapp.utils.showDialog(this@LoginActivity)
            val email = findViewById<EditText>(R.id.loginEmail).text.toString().trim{it <= ' '}
            val password = findViewById<EditText>(R.id.loginPassword).text.toString().trim{it <= ' '}

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
        when(user.accountType){
            "manager" -> {
                startActivity(Intent(this@LoginActivity, ManagerMainActivity::class.java))
            }
            else -> {
                if(user.profileCompleted == 0){
                    val intent = Intent(this@LoginActivity, DriverUpdateDetailsActivity::class.java)
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