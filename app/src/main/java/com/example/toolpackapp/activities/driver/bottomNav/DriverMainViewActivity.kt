package com.example.toolpackapp.activities.driver.bottomNav

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.LoginActivity
import com.example.toolpackapp.activities.driver.bottomNav.account.DriverAccountFragment
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.User
import com.example.toolpackapp.utils.GlideLoader
import com.example.toolpackapp.utils.hideDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging


class DriverMainViewActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var currentUser: User? = null
    private var bundle = bundleOf()
    private var selectedImage: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main_bottom_navbar)
        val navView: BottomNavigationView = findViewById(R.id.nav_view_bottombar)

        val navController = findNavController(R.id.nav_host_fragment_bottombar)

        if (intent.hasExtra("user_details")) {
            val user = intent.getParcelableExtra<User>("user_details")!!
            if (user.profileCompleted == 0) {
                val userMap = Bundle()
                userMap.putString("photo", user.photo)
                userMap.putString("phone", user.mobile)
                userMap.putString("fullname", user.fullname)
                userMap.putString("email", user.email)
                bundle.putBundle("user_details", userMap)
                navController.navigate(R.id.driverAccountFragment, bundle)
            }
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.driverAccountFragment, R.id.mapsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setOnNavigationItemSelectedListener { item ->
            Log.d("DriverMain", "$item")
            Log.d("DriverMain", "${item.itemId}")

            when (item.itemId) {
                R.id.driverAccountFragment -> {
                    navController.navigate(item.itemId, bundle)
                    true
                }
                else -> {
                    navController.navigate(item.itemId)
                    true
                }
            }
        }
        FirestoreClass().getCurrentUserDetailsAsObject(this@DriverMainViewActivity)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation_drawer_driver, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // logout user
                Log.d("MyApp", "User Logged Out");
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@DriverMainViewActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> {
                false
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_bottombar)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setCurrentUser(user: User) {
        currentUser = user
        val userMap = Bundle()
        userMap.putString("photo", user.photo)
        userMap.putString("phone", user.mobile)
        userMap.putString("fullname", user.fullname)
        userMap.putString("email", user.email)

        bundle.putBundle("user_details", userMap)
    }

    fun userProfileCompleteError() {
        hideDialog()
        Toast.makeText(this, "Error while completing profile", Toast.LENGTH_SHORT).show()
    }

}