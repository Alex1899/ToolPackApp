package com.example.toolpackapp.activities.manager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.toolpackapp.R
import com.example.toolpackapp.databinding.ActivityManagerEditPackageDetailsBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.PackageListItem
import com.example.toolpackapp.utils.*

class ManagerEditPackageDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityManagerEditPackageDetailsBinding? = null
    private lateinit var packageListItem: PackageListItem
    private var selectedImage: Uri? = null
    private var packageImageUrl: String = ""
    private lateinit var oldDriver: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerEditPackageDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Edit Package"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra("packageListItem")) {
            packageListItem = intent.getParcelableExtra("packageListItem")!!
        }

        oldDriver = packageListItem.driver
        binding?.packageNameInputText?.setText(packageListItem.name)
        loadPackageStatus(packageListItem.status)
        binding?.deliveryDateInputText?.setText(packageListItem.deliveryDate)
        binding?.deliveryTimeInputText?.setText(packageListItem.deliveryTime)

        binding?.packageImage?.setOnClickListener(this@ManagerEditPackageDetailsActivity)
        binding?.updateBtn?.setOnClickListener(this@ManagerEditPackageDetailsActivity)

        FirestoreClass().loadDatePicker(
            binding?.btnDate!!,
            binding?.deliveryDateInputText!!,
            this@ManagerEditPackageDetailsActivity
        )
        FirestoreClass().loadTimePicker(
            binding?.btnTime!!,
            this@ManagerEditPackageDetailsActivity,
            binding?.deliveryTimeInputText!!
        )
        FirestoreClass().loadSpinnerVendors(
            binding?.selectPackageVendorSpinner!!, this@ManagerEditPackageDetailsActivity, packageListItem.vendor
        )
        FirestoreClass().loadSpinnerSelectDriver(
            binding?.selectPackageDriverSpinner!!, this@ManagerEditPackageDetailsActivity, packageListItem.driver
        )
        FirestoreClass().loadSpinnerBuildingSite(
            binding?.selectBuildingSiteSpinner!!, this@ManagerEditPackageDetailsActivity, packageListItem.buildingSite
        )

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.package_image -> {
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
                R.id.update_btn -> {
                    if (validatePackageDetails()) {
                        showDialog(this@ManagerEditPackageDetailsActivity)
                        if (selectedImage != null) {
                            FirestoreClass().uploadImageToStorage(this, selectedImage)
                        } else {
                            updatePackageDetails()
                        }
                    }
                }
            }
        }
    }

    private fun loadPackageStatus(status: String){
        val statusList = ArrayList<String>()
        statusList.addAll(listOf("Pending", "Delivered"))

        val adapter = ArrayAdapter(
            this@ManagerEditPackageDetailsActivity,
            android.R.layout.simple_dropdown_item_1line,
            statusList
        )
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.selectPackageStatusSpinner?.setAdapter(adapter)
        binding?.selectPackageStatusSpinner?.setText(status, false)
    }

    private fun validatePackageDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding?.packageNameInputText?.text.toString()) -> {
                setErrorTextField(binding?.packageNameInput!!, true, "Please enter package name")
                //showErrorSnackBar(binding?.packageName!!, "Please enter package name", true)
                false
            }

            TextUtils.isEmpty(
                binding?.selectPackageDriverSpinner?.text.toString()) -> {
                setErrorTextField(binding?.selectPackageDriver!!, true, "Please enter package driver")
                //showErrorSnackBar(binding?.packageName!!, "Please select package driver", true)
                false
            }
            TextUtils.isEmpty(
                binding?.selectPackageStatusSpinner?.text.toString()) -> {
                setErrorTextField(binding?.selectPackageStatus!!, true, "Please enter package status")
                //showErrorSnackBar(binding?.packageName!!, "Please select package status", true)
                false
            }
            TextUtils.isEmpty(binding?.deliveryDateInputText?.text.toString()) -> {
                setErrorTextField(binding?.deliveryDateInput!!, true, "Please select delivery date")
                //showErrorSnackBar(binding?.packageName!!, "Please select delivery date", true)
                false
            }
            TextUtils.isEmpty(binding?.deliveryTimeInputText?.text.toString()) -> {
                setErrorTextField(binding?.deliveryTimeInput!!, true, "Please select delivery time")
                //showErrorSnackBar(binding?.deliveryTimeInput!!, "Please select delivery time", true)
                false
            }
            TextUtils.isEmpty(
                binding?.selectBuildingSiteSpinner?.text.toString()) -> {
                setErrorTextField(binding?.selectBuildingSite!!, true, "Please select building site")
                //showErrorSnackBar(binding?.packageName!!, "Please select building site", true)
                false
            }
            TextUtils.isEmpty(
                binding?.selectPackageVendorSpinner?.text.toString()) -> {
                setErrorTextField(binding?.selectPackageVendor!!, true, "Please select package vendor")
                //showErrorSnackBar(binding?.selectPackageVendor!!, "Please select package vendor", true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun getPackageDetailsMap(): HashMap<String, Any>{
        val packageMap = HashMap<String,Any>()
        packageMap["name"] = binding?.packageNameInputText?.text.toString()
        packageMap["status"] = binding?.selectPackageStatusSpinner?.text.toString()
        packageMap["driver"] = binding?.selectPackageDriverSpinner?.text.toString()
        packageMap["vendor"] = binding?.selectPackageVendorSpinner?.text.toString()
        packageMap["deliveryDate"] = binding?.deliveryDateInputText?.text.toString()
        packageMap["deliveryTime"] = binding?.deliveryTimeInputText?.text.toString()
        packageMap["buildingSite"] = binding?.selectBuildingSiteSpinner?.text.toString()
        return packageMap
    }

    private fun updatePackageDetails(){
        FirestoreClass().updatePackageDetails(this@ManagerEditPackageDetailsActivity, packageListItem.id, getPackageDetailsMap())
    }

    fun updatePackageSuccess(){
        hideDialog()
        showErrorSnackBar(binding?.packageNameInput!!, "Package updated successfully", false)
        if(oldDriver !== binding?.selectPackageDriverSpinner?.text.toString()){
            FirestoreClass().notifyDriver(binding?.selectPackageDriverSpinner?.text.toString())
        }
    }

    fun updatePackageError(){
        hideDialog()
        showErrorSnackBar(binding?.packageNameInput!!, "Error while updating package", true)
    }

    fun imageUploadSuccess(url: String){
        hideDialog()
        packageImageUrl = url
        updatePackageDetails()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_LONG ).show()
                showImageChooser(this)
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 1) {
                if (data != null) {
                    try {
                        selectedImage = data.data!!
                        GlideLoader(this).loadUserPicture(selectedImage!!, binding?.packageImage!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Image selection failed", Toast.LENGTH_LONG).show()

                    }
                }
            }

    }

}