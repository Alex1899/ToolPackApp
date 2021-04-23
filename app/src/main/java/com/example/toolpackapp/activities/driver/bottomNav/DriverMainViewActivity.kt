package com.example.toolpackapp.activities.driver.bottomNav

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.LoginActivity
import com.example.toolpackapp.activities.driver.DriverUpdateDetailsActivity
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class DriverMainViewActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main_bottom_navbar)
        val navView: BottomNavigationView = findViewById(R.id.nav_view_bottombar)

        val navController = findNavController(R.id.nav_host_fragment_bottombar)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
                true
            }
            R.id.action_account -> {
                val intent = Intent(this@DriverMainViewActivity, DriverUpdateDetailsActivity::class.java)
                if(currentUser != null){
                    intent.putExtra("user_details", currentUser)
                    startActivity(intent)
                }
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

    fun setCurrentUser(user: User){
        currentUser = user
    }

}