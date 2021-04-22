package com.example.toolpackapp.activities.sidebar.ui.addBuildingSite

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.showErrorSnackBar
import com.example.toolpackapp.databinding.FragmentAddBuildingSiteBinding
import com.google.firebase.firestore.FirebaseFirestore


/**
 * A simple [Fragment] subclass.
 * Use the [AddBuildingSiteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddBuildingSiteFragment : Fragment() {

    private var binding: FragmentAddBuildingSiteBinding? = null

    var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentAddBuildingSiteBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var db = FirebaseFirestore.getInstance()

        addNewBuildingSite()
    }

    private fun addNewBuildingSite() {
        binding?.buttonAddNewBuildingSite?.setOnClickListener {
            val buildingSites: MutableMap<String, Any> = HashMap()
            buildingSites["siteName"] = binding?.buildingSiteName?.text.toString()
            buildingSites["siteAddress"] = binding?.buildingSiteAddress?.text.toString()
            buildingSites["siteAdminEmail"] = binding?.buildingSiteAdminEmail?.text.toString()
            buildingSites["siteAdminFullName"] =
                binding?.buildingSiteAdminFullName?.text.toString()
            buildingSites["sitePhone"] = binding?.buildingSitePhone?.text.toString()

            db.collection("buildingSites")
                .document()
                .set(buildingSites)
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