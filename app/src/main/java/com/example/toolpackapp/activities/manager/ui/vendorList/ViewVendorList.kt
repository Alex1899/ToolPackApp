package com.example.toolpackapp.activities.manager.ui.vendorList

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.manager.ui.driversList.DriverListPackageAdapter
import com.example.toolpackapp.databinding.FragmentViewVendorListBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.Vendor
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.showDialog
import java.util.ArrayList

class ViewVendorList : Fragment() {
    private var binding: FragmentViewVendorListBinding? = null
    private lateinit var recyclerView: RecyclerView
    private var vendorList = ArrayList<Vendor>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentViewVendorListBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }


    override fun onResume(){
        super.onResume()
        if(vendorList.isNotEmpty()){
            vendorList.clear()
        }
        startFetching()
    }

    private fun startFetching(){
        showDialog(requireContext())
        recyclerView = binding?.vendorListRecycleview!!
        FirestoreClass().getAllVendors(this@ViewVendorList)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = VendorListPackageAdapter(requireContext(), vendorList)
    }

    fun getVendorListSuccess(arr: ArrayList<Vendor>){
        vendorList.addAll(arr)
        binding?.vendorListTextview?.visibility = View.GONE
        Log.d("ViewDriverList", arr.toString())
        recyclerView.adapter?.notifyDataSetChanged()
        hideDialog()
    }

    fun getVendorListError(msg: String? = null){
        hideDialog()
        binding?.vendorListTextview?.text = msg?:"There are currently no drivers in the system"
    }

}