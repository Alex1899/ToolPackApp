package com.example.toolpackapp.activities.sidebar.ui.addPackage

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.showErrorSnackBar
import com.example.toolpackapp.databinding.FragmentAddPackageBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [AddPackageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddPackageFragment : Fragment() {

    private var binding: FragmentAddPackageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val fragmentBinding = FragmentAddPackageBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadDatePicker()
        loadTimePicker()
        loadSpinnerVendors()
        loadSpinnerSelectDriver()
        loadSpinnerBuildingsite()
        addNewPackage()
    }

    fun loadDatePicker() {
        /*display datepicker dialog*/
        var format = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat("M/dd/YYYY", Locale.UK)
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        binding?.btnDate?.setOnClickListener {
            val selectDate = Calendar.getInstance()
            val year = selectDate.get(Calendar.YEAR)
            val month = selectDate.get(Calendar.MONTH)
            val day = selectDate.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    selectDate.set(Calendar.YEAR, year)
                    selectDate.set(Calendar.MONTH, month)
                    selectDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val date = format.format(selectDate.time)
                    binding?.DeliveryDate?.setText(date)
                }, year, month, day
            )
            dpd.show()
        }
    }

    fun loadTimePicker() {
        /*display timepicker dialog*/
        binding?.btnTime?.setOnClickListener {
            val selectTime = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val formate =
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        SimpleDateFormat("HH:mm", Locale.UK)
                    } else {
                        TODO("VERSION.SDK_INT < N")
                    }
                selectTime.set(Calendar.HOUR_OF_DAY, hour)
                selectTime.set(Calendar.MINUTE, minute)
                val time = formate.format(selectTime.time)
                binding?.DeliveryTime?.setText(time)
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                selectTime.get(Calendar.HOUR_OF_DAY),
                selectTime.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    fun loadSpinnerVendors() {
        val rootRef = FirebaseFirestore.getInstance()
        val subjectsRef = rootRef.collection("vendors")
        var selectVendor = binding?.selectVendor
        val subjects: MutableList<String?> = ArrayList()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            subjects
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        if (selectVendor != null) {
            selectVendor.adapter = adapter
            subjectsRef.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val subject = document.getString("vendorName")
                        subjects.add(subject)
                    }
                    adapter.notifyDataSetChanged()
                }
            })
        }
    }

    fun loadSpinnerSelectDriver() {
        val rootRef = FirebaseFirestore.getInstance()
        var selectDriver = binding?.selectDriver
        val subjects: MutableList<String?> = ArrayList()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            subjects
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        if (selectDriver != null) {
            selectDriver.adapter = adapter
            rootRef.collection("users").whereEqualTo("accountType", "driver")
                .get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        var driver = document.getString("fullname")
                        subjects.add(driver)
                    }
                    adapter.notifyDataSetChanged()
                }
        }
    }

    fun loadSpinnerBuildingsite() {
        val rootRef = FirebaseFirestore.getInstance()
        val subjectsRef = rootRef.collection("buildingSites")
        var selectbuildingsite = binding?.selectBuildingsite
        val subjects: MutableList<String?> = ArrayList()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            subjects
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        if (selectbuildingsite != null) {
            selectbuildingsite.adapter = adapter
            subjectsRef.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val subject = document.getString("siteName")
                        subjects.add(subject)
                    }
                    adapter.notifyDataSetChanged()
                }
            })
        }
    }

    private fun addNewPackage() {
        var db = FirebaseFirestore.getInstance()
        binding?.buttonAddNewPackage?.setOnClickListener {
            val packages: MutableMap<String, Any> = HashMap()
            packages["vendor"] = binding?.selectVendor?.getSelectedItem().toString()
            packages["driver"] = binding?.selectDriver?.getSelectedItem().toString()
            packages["buildingSite"] = binding?.selectBuildingsite?.getSelectedItem().toString()
            packages["deliveryDate"] = binding?.DeliveryDate?.text.toString()
            packages["deliveryTime"] = binding?.DeliveryTime?.text.toString()
            packages["packageDescription"] = binding?.packageDescripton?.text.toString()
            packages["packageStatus"] = "pending"

            db.collection("packages")
                .document()
                .set(packages)
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
