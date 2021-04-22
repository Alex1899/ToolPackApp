package com.example.toolpackapp.activities.manager.ui.addDriver

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.toolpackapp.R
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.showDialog
import com.example.toolpackapp.utils.showErrorSnackBar
import com.example.toolpackapp.databinding.AddDriverFragmentBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.User
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            showDialog(requireContext())

            val email: String =
                binding?.driverEmail?.text.toString().trim { it <= ' ' }
            val password: String =
                binding?.driverPassword?.text.toString().trim { it <= ' ' }
            val fullname: String = binding?.driverfullname?.text.toString().trim { it <= ' ' }

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
                            FirestoreClass().registerUser(this, user )
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

    fun driverAddedSuccess(){
        hideDialog()
        Toast.makeText(requireContext(), "Driver added successfully", Toast.LENGTH_LONG ).show()
    }

    private fun clearAddDriverForm() {
        binding?.driverEmail?.setText("")
        binding?.driverPassword?.setText("")
        binding?.driverfullname?.setText("")
    }

}