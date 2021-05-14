package com.example.toolpackapp.activities.manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.example.toolpackapp.R
import com.example.toolpackapp.databinding.ActivityManagerEditBuildingSiteDetailsBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.BuildingSite
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.setErrorTextField
import com.example.toolpackapp.utils.showErrorSnackBar

class ManagerEditBuildingSiteDetailsActivity : AppCompatActivity(),View.OnClickListener {
    private var binding: ActivityManagerEditBuildingSiteDetailsBinding? = null
    private lateinit var buildingListItem: BuildingSite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerEditBuildingSiteDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val actionBar = supportActionBar
        actionBar!!.title = "Edit Building Site Details"
        actionBar.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra("buildingListItem")) {
            buildingListItem = intent.getParcelableExtra("buildingListItem")!!
        }

        binding?.nameInputText?.setText(buildingListItem.siteName)
        binding?.siteAdminEmailInputText?.setText(buildingListItem.siteAdminEmail)
        binding?.siteAdminNameInputText?.setText(buildingListItem.siteAdminFullname)
        binding?.mobileInputText?.setText(buildingListItem.sitePhone)
        binding?.addressInputText?.setText(buildingListItem.siteAddress)

        binding?.buildingSaveBtn?.setOnClickListener(this@ManagerEditBuildingSiteDetailsActivity)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.building_save_btn -> {
                    if (validateBuildingSiteDetails()) {
                        com.example.toolpackapp.utils.showDialog(this)
                        FirestoreClass().updateBuildingSiteDetails(this@ManagerEditBuildingSiteDetailsActivity, getBuildingSiteDetailsMap())
                    }
                }
            }
        }
    }

    private fun getBuildingSiteDetailsMap(): HashMap<String, String>{
        val buildingMap = HashMap<String, String>()
        buildingMap["id"] = buildingListItem.id
        buildingMap["siteName"] = binding?.nameInputText?.text.toString().trim { it <= ' ' }
        buildingMap["siteAddress"] = binding?.addressInputText?.text.toString().trim { it <= ' ' }
        buildingMap["sitePhone"] = binding?.mobileInputText?.text.toString().trim { it <= ' ' }
        buildingMap["siteAdminEmail"] = binding?.siteAdminEmailInputText?.text.toString().trim { it <= ' ' }
        buildingMap["siteAdminFullName"] = binding?.siteAdminNameInputText?.text.toString().trim { it <= ' ' }


        return buildingMap

    }

    private fun validateBuildingSiteDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding?.mobileInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.mobileInput!!, true, "Please enter mobile number")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }
            TextUtils.isEmpty(binding?.nameInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.nameInput!!, true, "Please enter site name")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }

            TextUtils.isEmpty(binding?.siteAdminEmailInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.siteAdminEmailInput!!, true, "Please enter site admin name")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }
            TextUtils.isEmpty(binding?.siteAdminNameInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.siteAdminNameInput!!, true, "Please enter site admin email")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }
            TextUtils.isEmpty(binding?.addressInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(binding?.addressInput!!, true, "Please enter site address")
                //showErrorSnackBar(binding?.mobile!!, "Please enter mobile number", true)
                false
            }

            else -> {
                true
            }
        }
    }

    fun updateBuildingSiteDetailsSuccess(){
        hideDialog()
        showErrorSnackBar(binding?.addressInput!!, "Building site details updated successfully", false)

    }
    fun updateBuildingSiteDetailsError(msg: String?){
        hideDialog()
        showErrorSnackBar(binding?.addressInput!!, msg?:"Error while updating building site details", true)

    }

}

