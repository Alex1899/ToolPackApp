package com.example.toolpackapp.activities.ui.addDriver

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.showErrorSnackBar
import com.example.toolpackapp.databinding.AddDriverFragmentBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AddDriverFragment : Fragment() {

    //private lateinit var viewModel: AddDriverViewModel
    private var binding: AddDriverFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = AddDriverFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding?.buttonRegister?.setOnClickListener {
            registerUser(view)
        }
    }


    private fun validateRegisterDetails(view: View): Boolean {
        return when {
            TextUtils.isEmpty(
                binding?.driverfullname?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_full_name), true)
                false
            }
            TextUtils.isEmpty(
                binding?.driverEmail?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_username), true)
                false
            }

            TextUtils.isEmpty(
                binding?.driverPassword?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(view, resources.getString(R.string.err_msg_enter_password), true)
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
        if (validateRegisterDetails(view)) {

            //showProgressDialog(resources.getString(R.string.please_wait))

            val email: String =
                binding?.driverEmail?.text.toString().trim { it <= ' ' }
            val password: String =
                binding?.driverPassword?.text.toString().trim { it <= ' ' }

            // create firebase instance and create driver
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener { task ->

                        clearAddDriverForm()

                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            showErrorSnackBar(
                                view,
                                "Driver registered successfully. Driver ID is ${firebaseUser.uid}",
                                false
                            )
                            //FirebaseAuth.getInstance().signOut()
                            //finish()
                        } else {
                            // if error
                            showErrorSnackBar(view, task.exception!!.message.toString(), true)
                        }
                    }
                )
        }
    }

    private fun clearAddDriverForm(){
        binding?.driverEmail?.setText("")
        binding?.driverPassword?.setText("")
        binding?.driverfullname?.setText("")
    }

}