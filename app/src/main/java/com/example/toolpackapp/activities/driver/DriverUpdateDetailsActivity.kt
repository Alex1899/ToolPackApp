package com.example.toolpackapp.activities.driver


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.toolpackapp.R
import com.example.toolpackapp.databinding.ActivityDriverUpdateDetailsBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.User
import com.example.toolpackapp.utils.*
import com.squareup.picasso.Picasso

class DriverUpdateDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private var binding: ActivityDriverUpdateDetailsBinding? = null
    private lateinit var userDetails: User
    private var selectedImage: Uri? = null
    private var profileImageUrl: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverUpdateDetailsBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        if (intent.hasExtra("user_details")) {
            userDetails = intent.getParcelableExtra("user_details")!!
        }

        if(userDetails.photo.isNotEmpty()){
            Picasso.get().load(userDetails.photo).into(binding?.ivUserPhoto)
        }

        binding?.fullname?.isEnabled = false
        binding?.fullname?.setText(userDetails.fullname)

        binding?.email?.isEnabled = false
        binding?.email?.setText(userDetails.email)



        if (userDetails.mobile == "") {
            binding?.mobile?.hint = "Add mobile"
        } else {
            binding?.mobile?.setText(userDetails.mobile)
        }

        binding?.ivUserPhoto?.setOnClickListener(this@DriverUpdateDetailsActivity)

        binding?.saveBtn?.setOnClickListener(this@DriverUpdateDetailsActivity)
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
                        showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2
                        )
                    }
                }
                R.id.save_btn -> {
                    if(validateProfileDetails()){
                        showDialog(this@DriverUpdateDetailsActivity)
                        if(selectedImage != null) {
                            FirestoreClass().uploadImageToStorage(this, selectedImage)
                        }else{
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails(){
        val userMap = HashMap<String, Any>()
        val mobileNumber = binding?.mobile?.text.toString().trim{ it <= ' '}
        if(mobileNumber.isNotEmpty()){
            userMap["mobile"] = mobileNumber
        }
        if(profileImageUrl.isNotEmpty()){
            userMap["photo"] = profileImageUrl
        }

        userMap["profileCompleted"] = 1
        FirestoreClass().updateUserDetails(this, userMap)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 2){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_LONG ).show()
                showImageChooser(this)
            }else{
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_LONG ).show()

            }
        }
    }

    override fun onActivityResult(requestCode:Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                if(data != null){
                    try{
                        selectedImage = data.data!!
                        GlideLoader(this).loadUserPicture(selectedImage!!, binding?.ivUserPhoto!!)
                    }catch(e: Exception){
                        e.printStackTrace()
                        Toast.makeText(this, "Image selection failed", Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
    }

    private fun validateProfileDetails(): Boolean {
        return when{
            TextUtils.isEmpty(binding?.mobile?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageUrl: String){
        profileImageUrl = imageUrl
        updateUserProfileDetails()
    }

    fun userProfileCompleteSuccess(){
        hideDialog()
        Toast.makeText(this, "Profile Completed Successfully", Toast.LENGTH_SHORT).show()

        startActivity(Intent(this, DriverViewPackagesActivity::class.java))
        finish()
    }

    fun userProfileCompleteError(){
        hideDialog()
        Toast.makeText(this, "Error while completing profile", Toast.LENGTH_SHORT).show()
    }
}