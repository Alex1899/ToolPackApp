package com.example.toolpackapp.activities.manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.net.toUri
import com.example.toolpackapp.R
import com.example.toolpackapp.databinding.ActivityManagerEditVendorDetailsBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.Vendor
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.setErrorTextField
import com.example.toolpackapp.utils.showErrorSnackBar

class ManagerEditVendorDetailsActivity : AppCompatActivity(), View.OnClickListener  {
    private var binding: ActivityManagerEditVendorDetailsBinding? = null
    private lateinit var vendorListItem: Vendor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerEditVendorDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val actionBar = supportActionBar
        actionBar!!.title = "Edit Vendor Details"
        actionBar.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra("vendorListItem")) {
            vendorListItem = intent.getParcelableExtra("vendorListItem")!!
        }


        binding?.nameInputText?.setText(vendorListItem.vendorName)
        binding?.emailInputText?.setText(vendorListItem.vendorEmail)
        binding?.mobileInputText?.setText(vendorListItem.vendorPhone)
        binding?.addressInputText?.setText(vendorListItem.vendorAddress)


        binding?.vendorSaveBtn?.setOnClickListener(this@ManagerEditVendorDetailsActivity)
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.vendor_save_btn -> {
                    if (validateVendorDetails()) {
                        com.example.toolpackapp.utils.showDialog(this)
                        FirestoreClass().updateVendorDetails(this@ManagerEditVendorDetailsActivity, getVendorDetailsMap())
                    }
                }
            }
        }
    }

    private fun getVendorDetailsMap(): HashMap<String, String>{
        val vendorMap = HashMap<String, String>()
        vendorMap["id"] = vendorListItem.id
        vendorMap["vendorName"] = binding?.nameInputText?.text.toString().trim { it <= ' ' }
        vendorMap["vendorAddress"] = binding?.addressInputText?.text.toString().trim { it <= ' ' }
        vendorMap["vendorPhone"] = binding?.mobileInputText?.text.toString().trim { it <= ' ' }
        vendorMap["vendorEmail"] = binding?.emailInputText?.text.toString().trim { it <= ' ' }

        return vendorMap

    }

    private fun validateVendorDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding?.mobileInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.mobileInput!!, true, "Please enter mobile number")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }
            TextUtils.isEmpty(binding?.nameInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.nameInput!!, true, "Please enter vendor name")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }

            TextUtils.isEmpty(binding?.emailInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.emailInput!!, true, "Please enter vendor email")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }
            TextUtils.isEmpty(binding?.addressInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.addressInput!!, true, "Please enter vendor address")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }

            else -> {
                true
            }
        }
    }

    fun updateVendorDetailsSuccess(){
        hideDialog()
        showErrorSnackBar(binding?.addressInput!!, "Vendor details updated successfully", false)

    }
    fun updateVendorDetailsError(msg: String?){
        hideDialog()
        showErrorSnackBar(binding?.addressInput!!, msg?:"Error while updating vendor details", true)

    }

}
