package com.example.toolpackapp.activities.driver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class DriverViewPackagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_view_packages)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // logout user
                Log.d("MyApp","User Logged Out");
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@DriverViewPackagesActivity, LoginActivity::class.java))
                true
            }
            else -> {
                false
            }
        }
    }

}