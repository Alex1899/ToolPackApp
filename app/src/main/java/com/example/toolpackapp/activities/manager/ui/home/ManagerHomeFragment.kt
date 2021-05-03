package com.example.toolpackapp.activities.manager.ui.home

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.toolpackapp.activities.driver.DriverPackageAdapter
import com.example.toolpackapp.activities.manager.ManagerPackageAdapter
import com.example.toolpackapp.databinding.FragmentHomeManagerBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.PackageListItem
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.showDialog
import java.util.ArrayList

class ManagerHomeFragment : Fragment() {
    private var binding: FragmentHomeManagerBinding? = null
    private lateinit var recyclerView: RecyclerView
    private var packageItemsList = ArrayList<PackageListItem>()
    private var packageItemsBackUp = ArrayList<PackageListItem>()
    private var filterString: String = "Select status"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentHomeManagerBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPackageStatus()
    }

    override fun onResume(){
        super.onResume()
        if(packageItemsList.isNotEmpty() && packageItemsBackUp.isNotEmpty()){
            packageItemsList.clear()
            packageItemsBackUp.clear()
        }
        startFetching()

    }

    private fun loadPackageStatus(){
        val statusList = ArrayList<String>()
        statusList.addAll(listOf("Select status","Pending", "Delivered"))

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item_1line,
            statusList
        )
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.selectPackageTypeSpinner?.setAdapter(adapter)
        binding?.selectPackageTypeSpinner?.setText(filterString, false)
        binding?.selectPackageTypeSpinner?.setOnItemClickListener { _, _, position, _ ->
            val value = adapter.getItem(position)!!
            Log.d("Adapter Click", "clicked on $value")
            filterString = value
            filterPackages(filterString)
        }

    }

    private fun filterPackages(status: String){
        val newList = if(status == "Select status"){
            packageItemsBackUp
        }else{
            packageItemsBackUp.filter{ it.status == status } as ArrayList<PackageListItem>
        }
        packageItemsList.clear()
        packageItemsList.addAll(newList)
        recyclerView.adapter?.notifyDataSetChanged()
    }


    private fun startFetching(){
        showDialog(requireContext())
        recyclerView = binding?.managerRecycleview!!
        FirestoreClass().getAllPackages(this@ManagerHomeFragment)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ManagerPackageAdapter(requireContext(), packageItemsList)
    }
    fun setPackageItemsList(arr: ArrayList<PackageListItem>){
        packageItemsBackUp.addAll(arr)
        filterPackages(filterString)
        binding?.managerHomeTextview?.visibility = View.GONE
        Log.d("HomeFragment", arr.toString())
        recyclerView.adapter?.notifyDataSetChanged()
        hideDialog()
    }

    fun getPackageItemsError(msg: String? = null){
        hideDialog()
        binding?.managerHomeTextview?.text = msg?:"No packages to deliver"
    }


}