package com.example.toolpackapp.activities.driver.bottomNav.account

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.driver.bottomNav.DriverMainViewActivity
import com.example.toolpackapp.databinding.FragmentDriverAccountBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.utils.*

class DriverAccountFragment() : Fragment(), View.OnClickListener {

    private var binding: FragmentDriverAccountBinding? = null
    private var selectedImage: Uri? = null
    private var imageUrl: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentDriverAccountBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        return fragmentBinding.root
    }



   /* override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        Log.d("TAG", "onAttach(): activity = $activity")
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDetails = arguments?.getBundle("user_details")!!
        val photo = userDetails.getString("photo")!!
        val phone = userDetails.getString("phone")!!
        val fullname = userDetails.getString("fullname")!!
        val email = userDetails.getString("email")!!


        GlideLoader(requireContext()).loadUserPicture(photo.toUri(), binding?.ivUserPhoto!!)


        binding?.fullnameInput?.isEnabled = false
        binding?.fullnameInputText?.setText(fullname)

        binding?.emailInput?.isEnabled = false
        binding?.emailInputText?.setText(email)

        binding?.mobileInputText?.setText(phone)

        binding?.ivUserPhoto?.setOnClickListener(this@DriverAccountFragment)
        binding?.saveBtn?.setOnClickListener(this@DriverAccountFragment)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //showErrorSnackBar(v, "You already have permission", false)
                        showImageChooserFragment(this@DriverAccountFragment)
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2
                        )
                    }
                }
                R.id.save_btn -> {
                    if (validateProfileDetails()) {
                        showDialog(requireContext())
                        if (selectedImage != null) {
                            FirestoreClass().uploadImageToStorageFragment(this@DriverAccountFragment, selectedImage)
                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    fun uploadImageSuccess(image: String){
        imageUrl = image
        updateUserProfileDetails()
    }

    fun updateUserProfileDetails(){
        val userMap = HashMap<String, Any>()
        val mobileNumber = binding?.mobileInputText?.text.toString().trim{ it <= ' '}
        if(mobileNumber.isNotEmpty()){
            userMap["mobile"] = mobileNumber
        }
        if(imageUrl !== null){
            userMap["photo"] = imageUrl!!
        }

        userMap["profileCompleted"] = 1
        FirestoreClass().updateUserDetails(this@DriverAccountFragment, userMap)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 2){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_LONG ).show()
                showImageChooserFragment(this@DriverAccountFragment)
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
                        activity?.let { GlideLoader(it).loadUserPicture(
                            selectedImage!!,
                            binding?.ivUserPhoto!!
                        ) }
                    }catch (e: Exception){
                        Log.e("Error", "error", e)
                        Toast.makeText(
                            requireContext(),
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


