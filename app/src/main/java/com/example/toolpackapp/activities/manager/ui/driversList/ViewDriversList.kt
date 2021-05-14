package com.example.toolpackapp.activities.manager.ui.driversList

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.manager.ManagerPackageAdapter
import com.example.toolpackapp.databinding.FragmentViewDriversListBinding
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.PackageListItem
import com.example.toolpackapp.models.User
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.showDialog
import java.util.ArrayList

class ViewDriversList : Fragment() {
    private var binding: FragmentViewDriversListBinding? = null
    private lateinit var recyclerView: RecyclerView
    private var driverList = ArrayList<User>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentViewDriversListBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }


    override fun onResume(){
        super.onResume()
        if(driverList.isNotEmpty()){
            driverList.clear()
        }
        startFetching()
    }

    private fun startFetching(){
        showDialog(requireContext())
        recyclerView = binding?.driverListRecycleview!!
        FirestoreClass().getAllDrivers(this@ViewDriversList)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = DriverListPackageAdapter(requireContext(), driverList)
    }

    fun getDriverListSuccess(arr: ArrayList<User>){
        driverList.addAll(arr)
        binding?.driverListTextview?.visibility = View.GONE
        Log.d("ViewDriverList", arr.toString())
        recyclerView.adapter?.notifyDataSetChanged()
        hideDialog()
    }

    fun getDriverListError(msg: String? = null){
        hideDialog()
        binding?.driverListTextview?.text = msg?:"There are currently no drivers in the system"
    }

}