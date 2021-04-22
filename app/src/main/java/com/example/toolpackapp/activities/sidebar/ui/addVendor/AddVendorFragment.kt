package com.example.toolpackapp.activities.sidebar.ui.addVendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.showErrorSnackBar
import com.example.toolpackapp.databinding.FragmentAddVendorBinding
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
    ): View? {
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
            val vendors: MutableMap<String, Any> = HashMap()
            vendors["vendorName"] = binding?.vendorName?.text.toString()
            vendors["vendorEmail"] = binding?.vendorEmail?.text.toString()
            vendors["vendorAddress"] = binding?.vendorAddress?.text.toString()
            vendors["vendorPhone"] = binding?.vedorPhone?.text.toString()

            db.collection("vendors")
                .document()
                .set(vendors)
                .addOnSuccessListener {
                    view?.let { it1 ->
                        showErrorSnackBar(
                            it1,
                            resources.getString(R.string.msg_addsuccessful),
                            false
                        )
                    }
                    false
                }.addOnFailureListener {
                    view?.let { it1 ->
                        showErrorSnackBar(
                            it1,
                            resources.getString(R.string.msg_addfailure),
                            true
                        )
                    }
                    false
                }
        }
    }

}