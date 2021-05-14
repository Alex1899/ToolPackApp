package com.example.toolpackapp.activities.driver.bottomNav.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.toolpackapp.activities.driver.DriverPackageAdapter
import com.example.toolpackapp.databinding.FragmentHomeDriverBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.PackageListItem
import com.example.toolpackapp.utils.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class HomeFragment : Fragment() {
    private var binding: FragmentHomeDriverBinding? = null
    private var packageItemsList = ArrayList<PackageListItem>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var message: String
    private lateinit var number: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentHomeDriverBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onResume(){
        super.onResume()
        if(packageItemsList.isNotEmpty()){
            packageItemsList.clear()
        }
        startFetching()
    }

    private fun startFetching(){
        showDialog(requireContext())
        recyclerView = binding?.driverRecycleview!!
        FirestoreClass().getDriverPackages(this@HomeFragment)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter =
            DriverPackageAdapter(requireContext(), packageItemsList, { id -> onClick(id) }) { item -> onLocationClick(
                item
            )}

    }

    private fun onClick(id: String) {
        val packageMap = HashMap<String, Any>()
        packageMap["status"] = "Delivered"
        FirestoreClass().markPackageAsDelivered(this@HomeFragment, id, packageMap)
    }

    fun sendingSMS(number: String, message: String){
        val uri = Uri.parse("smsto:$number")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", message)
        startActivity(intent)
    }


    private fun onLocationClick(item: PackageListItem){
        showDialog(requireContext())
        FirestoreClass().showPackageLocation(this@HomeFragment, item)
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
        Log.d("Empty", msg!!)
        binding?.driverHomeTextview?.text = msg?:"No packages to deliver"
        binding?.driverHomeTextview?.visibility = View.VISIBLE


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


    fun showPackageLocationOnMap(pickupAddress: String, deliveryAddress: String){
        hideDialog()
        val bundle = Bundle()
        bundle.putString("pickupAddress", pickupAddress)
        bundle.putString("deliveryAddress", deliveryAddress)

        findNavController().navigate(com.example.toolpackapp.R.id.mapsFragment, bundle)

//        val mapFrag = MapsFragment()
//        mapFrag.arguments = bundle
//        val transaction = requireActivity().supportFragmentManager.beginTransaction()
//
//        transaction.replace(com.example.toolpackapp.R.id.container, MapsFragment())
//        transaction.addToBackStack(null)
//        transaction.commit()
    }

}