package com.example.toolpackapp.activities.driver.bottomNav.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.toolpackapp.activities.driver.DriverPackageAdapter
import com.example.toolpackapp.databinding.FragmentHomeDriverBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.PackageListItem
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.showDialog
import com.example.toolpackapp.utils.showErrorSnackBar
import java.util.ArrayList

class HomeFragment : Fragment() {
    private var binding: FragmentHomeDriverBinding? = null
    private var packageItemsList = ArrayList<PackageListItem>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentHomeDriverBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showDialog(requireContext())
        recyclerView = binding?.driverRecycleview!!
        FirestoreClass().getDriverPackages(this@HomeFragment)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter =
            DriverPackageAdapter(requireContext(), packageItemsList) { id -> onClick(id) }

    }

    private fun onClick(id: String) {
        val packageMap = HashMap<String, Any>()
        packageMap["status"] = "Delivered"
        FirestoreClass().markPackageAsDelivered(this@HomeFragment, id, packageMap)
    }


    fun getDriverPackagesSuccess(arr: ArrayList<PackageListItem>) {
        packageItemsList.addAll(arr.reversed())
        binding?.driverHomeTextview?.visibility = View.GONE
        Log.d("HomeFragment", arr.toString())
        recyclerView.adapter?.notifyDataSetChanged()
        hideDialog()
    }

    fun getDriverPackagesError(msg: String? = null) {
        hideDialog()
        binding?.driverHomeTextview?.text = msg ?: "No packages to deliver"

    }

    fun markPackageDeliveredSuccess(packageItemId: String) {
        hideDialog()
        val newList =
            packageItemsList.filter { it.id !== packageItemId } as ArrayList<PackageListItem>

        packageItemsList.clear()
        packageItemsList.addAll(newList)
        recyclerView.adapter?.notifyDataSetChanged()
        if(packageItemsList.isEmpty()) {
            binding?.driverHomeTextview?.visibility = View.VISIBLE
            binding?.driverHomeTextview?.text = "No packages to deliver"
        }
        showErrorSnackBar(binding?.driverRecycleview!!, "Package Delivered!", false)


    }

    fun markPackageDeliveredError(msg: String? = null) {
        hideDialog()
        showErrorSnackBar(
            binding?.driverRecycleview!!,
            msg ?: "Error when updating package status",
            true
        )
    }

}