package com.example.toolpackapp.activities.manager.ui.addPackage

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toolpackapp.databinding.FragmentAddPackageBinding
import com.example.toolpackapp.firebaseNotifications.Constants.Companion.TOPIC
import com.example.toolpackapp.firebaseNotifications.NotificationsData
import com.example.toolpackapp.firebaseNotifications.PushNotification
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.utils.*
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.*


class AddPackageFragment : Fragment() {

    private var binding: FragmentAddPackageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.title = "Add Package"

        val fragmentBinding = FragmentAddPackageBinding.inflate(inflater, container, false)
        binding = fragmentBinding


        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirestoreClass().loadDatePicker(
            binding?.btnDate!!,
            binding?.deliveryDateInputText!!,
            requireContext()
        )
        FirestoreClass().loadTimePicker(
            binding?.btnTime!!,
            requireContext(),
            binding?.deliveryTimeInputText!!
        )
        FirestoreClass().loadSpinnerVendors(binding?.selectPackageVendorSpinner!!, requireContext())
        FirestoreClass().loadSpinnerSelectDriver(
            binding?.selectPackageDriverSpinner!!,
            requireContext()
        )
        FirestoreClass().loadSpinnerBuildingSite(
            binding?.selectBuildingSiteSpinner!!,
            requireContext()
        )
        addNewPackage()
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        binding?.deliveryDateInputText?.setText(date)
        binding?.deliveryTimeInputText?.setText(time)
    }


    private fun addNewPackage() {
        binding?.buttonAddNewPackage?.setOnClickListener {
            if (validatePackageDetails()) {
                showDialog(requireContext())
                val packages = getPackageDetailsMap()
                FirestoreClass().addNewPackage(this@AddPackageFragment, packages)
            }

        }
    }

    private fun validatePackageDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                binding?.selectPackageVendorSpinner?.text.toString()
            ) || binding?.selectPackageVendorSpinner?.text.toString() == "Select Vendor" -> {
                setErrorTextField(binding?.selectPackageVendor!!, true, "Please select vendor")
                //showErrorSnackBar(binding?.selectPackageVendor!!, "Please select vendor", true)
                false
            }
            TextUtils.isEmpty(
                binding?.selectPackageDriverSpinner?.text.toString()
            ) || binding?.selectPackageDriverSpinner?.text.toString() == "Select Driver" -> {
                setErrorTextField(binding?.selectPackageDriver!!, true, "Please select driver")
                //showErrorSnackBar(binding?.selectDriver!!, "Please select driver", true)
                false
            }

            TextUtils.isEmpty(
                binding?.selectBuildingSiteSpinner?.text.toString()
            ) || binding?.selectBuildingSiteSpinner?.text.toString() == "Select Building Site" -> {
                setErrorTextField(
                    binding?.selectBuildingSite!!,
                    true,
                    "Please select building site"
                )
                //showErrorSnackBar(binding?.selectBuildingsite!!, "Please select building site", true)
                false
            }
            TextUtils.isEmpty(
                binding?.packageNameInputText?.text.toString()
            ) -> {
                setErrorTextField(
                    binding?.packageNameInput!!,
                    true,
                    "Please add package name"
                )
                false
            }

            else -> {
                true
            }
        }
    }

    fun clearForm() {
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        binding?.selectPackageVendorSpinner?.setText("Select Vendor", false)
        binding?.selectPackageVendor?.isErrorEnabled = false

        binding?.selectPackageDriverSpinner?.setText("Select Driver", false)
        binding?.selectPackageDriver?.isErrorEnabled = false

        binding?.selectBuildingSiteSpinner?.setText("Select Building Site", false)
        binding?.selectBuildingSite?.isErrorEnabled = false

        binding?.deliveryDateInputText?.setText(date)
        binding?.deliveryDateInput?.isErrorEnabled = false

        binding?.deliveryTimeInputText?.setText(time)
        binding?.deliveryTimeInput?.isErrorEnabled = false

        binding?.packageNameInputText?.setText("")
        binding?.packageNameInput?.isErrorEnabled = false

        binding?.packageDescriptionInputText?.setText("")
        binding?.packageDescriptionInput?.isErrorEnabled = false

    }

    private fun getPackageDetailsMap(): HashMap<String, Any> {
        val packages = HashMap<String, Any>()
        packages["vendor"] = binding?.selectPackageVendorSpinner?.text.toString()
        packages["driver"] = binding?.selectPackageDriverSpinner?.text.toString()
        packages["buildingSite"] = binding?.selectBuildingSiteSpinner?.text.toString()
        packages["deliveryDate"] = binding?.deliveryDateInputText?.text.toString()
        packages["deliveryTime"] = binding?.deliveryTimeInputText?.text.toString()
        packages["name"] = binding?.packageNameInputText?.text.toString()
        packages["description"] = binding?.packageDescriptionInputText?.text.toString()
        packages["status"] = "Pending"

        return packages
    }

    fun addPackageSuccess() {
        hideDialog()
        showErrorSnackBar(binding?.packageDescriptionInput!!, "Package added successfully", false)
        notifyDriver()
    }

    private fun notifyDriver(){
        val title = "New Package"
        val message = "Hi! You have a new package to deliver"
        PushNotification(
            NotificationsData(title, message),
            TOPIC
        ).also {
            sendNotification(it)
        }

    }

    fun addPackageError() {
        hideDialog()
        showErrorSnackBar(binding?.packageDescriptionInput!!, "Error while adding package", true)
    }

}