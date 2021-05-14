package com.example.toolpackapp.activities.manager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.toolpackapp.R
import com.example.toolpackapp.databinding.ActivityManagerEditDriverDetailsBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.User
import com.example.toolpackapp.utils.*

class ManagerEditDriverDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private var binding: ActivityManagerEditDriverDetailsBinding? = null
    private lateinit var driverListItem: User
    private var selectedImage: Uri? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerEditDriverDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val actionBar = supportActionBar
        actionBar!!.title = "Edit Driver Details"
        actionBar.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra("driverListItem")) {
            driverListItem = intent.getParcelableExtra("driverListItem")!!
        }

        GlideLoader(this).loadUserPicture(driverListItem.photo.toUri(), binding?.ivUserPhoto!!)

        binding?.fullnameInputText?.setText(driverListItem.fullname)
        binding?.emailInputText?.setText(driverListItem.email)
        binding?.mobileInputText?.setText(driverListItem.mobile)

        binding?.ivUserPhoto?.setOnClickListener(this@ManagerEditDriverDetailsActivity)
        binding?.saveBtn?.setOnClickListener(this@ManagerEditDriverDetailsActivity)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //showErrorSnackBar(v, "You already have permission", false)
                        showImageChooser(this@ManagerEditDriverDetailsActivity)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@ManagerEditDriverDetailsActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2
                        )
                    }
                }
                R.id.save_btn -> {
                    if (validateProfileDetails()) {
                        com.example.toolpackapp.utils.showDialog(this)
                        if (selectedImage != null) {
                            FirestoreClass().uploadImageToStorage(
                                this@ManagerEditDriverDetailsActivity,
                                selectedImage
                            )
                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun uploadImageSuccess(image: String){
        imageUrl = image
        updateUserProfileDetails()
    }

    fun updateUserProfileDetails(){
        val userMap = HashMap<String, Any>()
        val mobileNumber = binding?.mobileInputText?.text.toString().trim{ it <= ' '}

        userMap["id"] = driverListItem.id

        if(mobileNumber.isNotEmpty()){
            userMap["mobile"] = mobileNumber
        }
        if(imageUrl !== null){
            userMap["photo"] = imageUrl!!
        }
        userMap["email"] = binding?.emailInputText?.text.toString().trim{ it <= ' '}
        userMap["fullname"] = binding?.fullnameInputText?.text.toString().trim{ it <= ' '}

        FirestoreClass().managerUpdateUserDetails(this@ManagerEditDriverDetailsActivity, userMap)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 2){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_LONG ).show()
                showImageChooser(this@ManagerEditDriverDetailsActivity)
                Log.d("DriverAcc", "before result image $selectedImage")

            }else{
                showErrorSnackBar(binding?.fullnameInput!!, "Storage Permission Denied", true)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("DriverAcc", "image $selectedImage")

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 1){
                Log.d("DriverAcc", "image $selectedImage")

                if(data != null){
                    try{
                        selectedImage = data.data!!
                        Log.i("DriverAcc", "image $selectedImage")
                        this@ManagerEditDriverDetailsActivity.let { GlideLoader(it).loadUserPicture(
                            selectedImage!!,
                            binding?.ivUserPhoto!!
                        ) }
                    }catch (e: Exception){
                        Log.e("Error", "error", e)
                        Toast.makeText(
                            this,
                            "Image selection failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }



    private fun validateProfileDetails(): Boolean {
        return when{
            TextUtils.isEmpty(binding?.mobileInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.mobileInput!!, true, "Please enter mobile number")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }

            TextUtils.isEmpty(binding?.fullnameInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.fullnameInput!!, true, "Please enter full name")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }
            TextUtils.isEmpty(binding?.emailInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.emailInput!!, true, "Please enter email")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }
            else -> {
                true
            }
        }
    }


    fun userProfileCompleteSuccess(){
        hideDialog()
        showErrorSnackBar(binding?.fullnameInput!!, "Profile completed successfully", false)
    }

    fun userProfileCompleteError(){
        hideDialog()
        showErrorSnackBar(binding?.fullnameInput!!, "Error while completing the profile", true)
    }


}