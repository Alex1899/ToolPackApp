package com.example.toolpackapp.activities.manager.ui.addDriver

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toolpackapp.R
import com.example.toolpackapp.databinding.AddDriverFragmentBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.User
import com.example.toolpackapp.utils.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.regex.Matcher
import java.util.regex.Pattern

class AddDriverFragment : Fragment() {

    //private lateinit var viewModel: AddDriverViewModel
    private var binding: AddDriverFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = AddDriverFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.buttonRegister?.setOnClickListener {
            registerUser(view)
        }
    }


    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                binding?.driverfullnameInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(
                    binding?.driverfullnameInput!!,
                    true,
                    resources.getString(R.string.err_msg_enter_full_name)
                )
                //showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_full_name), true)
                false
            }
            !hasOnlyLetters(binding?.driverfullnameInputText!!) -> {
                setErrorTextField(
                    binding?.driverfullnameInput!!,
                    true,
                    resources.getString(R.string.err_msg_wrong_full_name)
                )
                //showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_full_name), true)
                false
            }
            TextUtils.isEmpty(
                binding?.driverEmailInputText?.text.toString().trim { it <= ' ' }) -> {
                setErrorTextField(
                    binding?.driverEmailInput!!,
                    true,
                    resources.getString(R.string.err_msg_enter_username)
                )
                //showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_username), true)
                false
            }

            TextUtils.isEmpty(
                binding?.driverPasswordInputText?.text.toString()
            ) -> {
                setErrorTextField(
                    binding?.driverPasswordInput!!,
                    true,
                    resources.getString(R.string.err_msg_enter_password)
                )
                //showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            binding?.driverPasswordInputText?.text.toString().length < 6 -> {
                setErrorTextField(
                    binding?.driverPasswordInput!!,
                    true,
                    resources.getString(R.string.err_msg_short_password)
                )
                //showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                //showErrorSnackBar("Registered successfully", false)
                true
            }
        }
    }



    private fun registerUser(view: View) {
        // Check if the entries are valid
        if (validateRegisterDetails()) {
            showDialog(requireContext())

            val email: String =
                binding?.driverEmailInputText?.text.toString().trim { it <= ' ' }
            val password: String =
                binding?.driverPasswordInputText?.text.toString().trim { it <= ' ' }
            val fullname: String =
                binding?.driverfullnameInputText?.text.toString().trim { it <= ' ' }

            //Log.d("AddDriver", "email: ${email}, fullname: $fullname")

            // create firebase instance and create driver
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            clearAddDriverForm()

                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val user = User(
                                firebaseUser.uid,
                                fullname,
                                email,
                                "",
                                "",
                                "driver"
                            )
                            FirestoreClass().registerUser(this, user)
                            //FirebaseAuth.getInstance().signOut()
                            //finish()
                        } else {
                            // if error
                            hideDialog()
                            showErrorSnackBar(view, task.exception!!.message.toString(), true)
                        }
                    }
                )
        }
    }

    fun driverAddedSuccess() {
        hideDialog()
        showErrorSnackBar(binding?.driverEmailInput!!, "Driver added successfully", false)
    }

    fun driverAddError(msg: String? = null) {
        hideDialog()
        showErrorSnackBar(binding?.driverEmailInput!!, msg ?: "Driver added successfully", true)
    }

    private fun clearAddDriverForm() {
        binding?.driverEmailInputText?.setText("")
        binding?.driverEmailInput?.isErrorEnabled = false

        binding?.driverPasswordInputText?.setText("")
        binding?.driverPasswordInput?.isErrorEnabled = false

        binding?.driverfullnameInputText?.setText("")
        binding?.driverfullnameInput?.isErrorEnabled = false
    }

}