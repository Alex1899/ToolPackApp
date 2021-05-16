package com.example.toolpackapp.activities.manager.ui.addBuildingSite

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toolpackapp.databinding.FragmentAddBuildingSiteBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.utils.*


/**
 * A simple [Fragment] subclass.
 * Use the [AddBuildingSiteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddBuildingSiteFragment : Fragment() {

    private var binding: FragmentAddBuildingSiteBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.title = "Add Building Site"
        val fragmentBinding = FragmentAddBuildingSiteBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addBuildingSite()
    }

    private fun addBuildingSite() {
        binding?.buttonAddNewBuildingSite?.setOnClickListener {

            if (validateBuildingSiteDetails()) {
                showDialog(requireContext())
                FirestoreClass().addNewBuildingSite(
                    this@AddBuildingSiteFragment,
                    getBuildingSiteMap()
                )
            }
        }
    }

    private fun validateBuildingSiteDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                binding?.buildingSiteNameInputText?.text.toString()
            ) -> {
                setErrorTextField(binding?.buildingSiteNameInput!!, true, "Please add site name")
                //showErrorSnackBar(binding?.buildingSiteName!!, "Please add site name", true)
                false
            }
            TextUtils.isEmpty(
                binding?.buildingSiteAddressInputText?.text.toString()
            ) -> {
                setErrorTextField(
                    binding?.buildingSiteAddressInput!!,
                    true,
                    "Please add site address"
                )
                //showErrorSnackBar(binding?.buildingSiteName!!, "Please add site address", true)
                false
            }
            TextUtils.isEmpty(
                binding?.buildingSiteAdminEmailInputText?.text.toString()
            ) -> {
                setErrorTextField(
                    binding?.buildingSiteAdminEmailInput!!,
                    true,
                    "Please add site admin email"
                )
                //showErrorSnackBar(binding?.buildingSiteAdminEmail!!, "Please add site admin email",true)
                false
            }
            TextUtils.isEmpty(
                binding?.buildingSiteAdminFullNameInputText?.text.toString()
            ) -> {
                setErrorTextField(
                    binding?.buildingSiteAdminFullNameInput!!,
                    true,
                    "Please add site admin fullname"
                )
                //showErrorSnackBar(binding?.buildingSiteName!!,"Please add site admin fullname",true)
                false
            }
            !hasOnlyLetters(binding?.buildingSiteAdminFullNameInputText!!) -> {
                setErrorTextField(
                    binding?.buildingSiteAdminFullNameInput!!,
                    true,
                    "Fullname can only contain letters"
                )
                //showErrorSnackBar(binding?.buildingSiteName!!,"Please add site admin fullname",true)
                false
            }
            TextUtils.isEmpty(
                binding?.buildingSitePhoneInputText?.text.toString()
            ) -> {
                setErrorTextField(
                    binding?.buildingSitePhoneInput!!,
                    true,
                    "Please add site contact number"
                )
                //showErrorSnackBar(binding?.buildingSiteName!!,"Please add site contact number",true)
                false
            }
            else -> {
                true
            }
        }

    }

    fun clearForm() {
        binding?.buildingSiteNameInputText?.setText("")
        binding?.buildingSiteNameInput?.isErrorEnabled = false

        binding?.buildingSiteAddressInputText?.setText("")
        binding?.buildingSiteAddressInput?.isErrorEnabled = false

        binding?.buildingSiteAdminEmailInputText?.setText("")
        binding?.buildingSiteAdminEmailInput?.isErrorEnabled = false

        binding?.buildingSiteAdminFullNameInputText?.setText("")
        binding?.buildingSiteAdminFullNameInput?.isErrorEnabled = false

        binding?.buildingSitePhoneInputText?.setText("")
        binding?.buildingSitePhoneInput?.isErrorEnabled = false
    }

    private fun getBuildingSiteMap(): HashMap<String, Any> {
        val buildingSites = HashMap<String, Any>()
        buildingSites["siteName"] = binding?.buildingSiteNameInputText?.text.toString()
        buildingSites["siteAddress"] = binding?.buildingSiteAddressInputText?.text.toString()
        buildingSites["siteAdminEmail"] = binding?.buildingSiteAdminEmailInputText?.text.toString()
        buildingSites["siteAdminFullName"] =
            binding?.buildingSiteAdminFullNameInputText?.text.toString()
        buildingSites["sitePhone"] = binding?.buildingSitePhoneInputText?.text.toString()
        return buildingSites
    }

    fun addBuildingSuccess() {
        hideDialog()
        showErrorSnackBar(
            binding?.buildingSiteNameInputText!!,
            "Building Site Added Successfully!",
            false
        )
    }

    fun addBuildingFailure(msg: String? = null) {
        hideDialog()
        showErrorSnackBar(
            binding?.buildingSiteNameInput!!,
            msg ?: "Error while adding the building site",
            true
        )
    }


}