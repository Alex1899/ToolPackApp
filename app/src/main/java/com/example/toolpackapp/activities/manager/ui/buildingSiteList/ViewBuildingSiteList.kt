package com.example.toolpackapp.activities.manager.ui.buildingSiteList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.toolpackapp.activities.manager.ui.vendorList.VendorListPackageAdapter
import com.example.toolpackapp.databinding.FragmentViewBuildingSiteListBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.BuildingSite
import com.example.toolpackapp.models.Vendor
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.showDialog
import java.util.ArrayList

class ViewBuildingSiteList : Fragment(){
    private var binding: FragmentViewBuildingSiteListBinding? = null
    private lateinit var recyclerView: RecyclerView
    private var buildingSiteList = ArrayList<BuildingSite>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentViewBuildingSiteListBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }


    override fun onResume(){
        super.onResume()
        if(buildingSiteList.isNotEmpty()){
            buildingSiteList.clear()
        }
        startFetching()
    }

    private fun startFetching(){
        showDialog(requireContext())
        recyclerView = binding?.buildingSiteListRecycleview!!
        FirestoreClass().getAllBuildingSites(this@ViewBuildingSiteList)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = BuildingSitePackageAdapter(requireContext(), buildingSiteList)
    }

    fun getBuildingSitesListSuccess(arr: ArrayList<BuildingSite>){
        buildingSiteList.addAll(arr)
        binding?.buildingSiteListTextview?.visibility = View.GONE
        Log.d("ViewBuildingList", arr.toString())
        recyclerView.adapter?.notifyDataSetChanged()
        hideDialog()
    }

    fun getBuildingSiteListError(msg: String? = null){
        hideDialog()
        binding?.buildingSiteListTextview?.text = msg?:"There are currently no building sites in the system"
    }

}