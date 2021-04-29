package com.example.toolpackapp.activities.manager.ui.addVendor

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toolpackapp.R
import com.example.toolpackapp.databinding.FragmentAddVendorBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.setErrorTextField
import com.example.toolpackapp.utils.showDialog
import com.example.toolpackapp.utils.showErrorSnackBar

import com.google.firebase.firestore.FirebaseFirestore


/**
 * A simple [Fragment] subclass.
 * Use the [AddVendorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddVendorFragment : Fragment() {
    private var binding: FragmentAddVendorBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.title = "Add Vendor"

        val fragmentBinding = FragmentAddVendorBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addNewVendor()
    }

    private fun addNewVendor() {
        var db = FirebaseFirestore.getInstance()
        binding?.buttonAddNewVendor?.setOnClickListener {
            if (validateVendorDetails()) {
                showDialog(requireContext())
                FirestoreClass().addNewVendor(this@AddVendorFragment, getVendorDetailsMap())
            }
        }
    }

    private fun validateVendorDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                binding?.vendorNameInputText?.text.toString()
            ) -> {
                setErrorTextField(binding?.vendorNameInput!!, true, "Please add vendor name")
                //showErrorSnackBar(binding?.vendorName!!, "Please add vendor name", true)
                false
            }
            TextUtils.isEmpty(
                binding?.vendorEmailInputText?.text.toString()
            ) -> {
                setErrorTextField(binding?.vendorEmailInput!!, true, "Please add vendor email")
                // showErrorSnackBar(binding?.vendorName!!, "Please add vendor email", true)
                false
            }
            TextUtils.isEmpty(
                binding?.vendorAddressInputText?.text.toString()
            ) -> {
                setErrorTextField(binding?.vendorAddressInput!!, true, "Please add vendor address")
                //showErrorSnackBar(binding?.vendorName!!, "Please add vendor address", true)
                false
            }
            TextUtils.isEmpty(
                binding?.vendorPhoneInputText?.text.toString()
            ) -> {
                setErrorTextField(binding?.vendorPhoneInput!!, true, "Please add vendor contact number")
                //showErrorSnackBar(binding?.vendorName!!, "Please add vendor contact number", true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun clearForm() {
        binding?.vendorNameInputText?.setText("")
        binding?.vendorNameInput?.isErrorEnabled = false

        binding?.vendorEmailInputText?.setText("")
        binding?.vendorEmailInput?.isErrorEnabled = false

        binding?.vendorAddressInputText?.setText("")
        binding?.vendorAddressInput?.isErrorEnabled = false

        binding?.vendorPhoneInputText?.setText("")
        binding?.vendorPhoneInput?.isErrorEnabled = false
    }

    private fun getVendorDetailsMap(): HashMap<String, Any> {
        val vendors = HashMap<String, Any>()
        vendors["vendorName"] = binding?.vendorNameInputText?.text.toString()
        vendors["vendorEmail"] = binding?.vendorEmailInputText?.text.toString()
        vendors["vendorAddress"] = binding?.vendorAddressInputText?.text.toString()
        vendors["vendorPhone"] = binding?.vendorPhoneInputText?.text.toString()

        return vendors
    }

    fun addVendorSuccess() {
        hideDialog()
        showErrorSnackBar(binding?.vendorNameInput!!, "Vendor added successfully", false)
    }

    fun addVendorError(msg: String? = null) {
        hideDialog()
        showErrorSnackBar(binding?.vendorNameInput!!, msg?:"Error while adding the vendor", true)
    }

}