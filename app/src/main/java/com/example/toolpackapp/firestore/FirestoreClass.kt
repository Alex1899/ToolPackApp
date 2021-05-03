package com.example.toolpackapp.firestore


import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.LoginActivity
import com.example.toolpackapp.activities.driver.bottomNav.DriverMainViewActivity
import com.example.toolpackapp.activities.driver.bottomNav.account.DriverAccountFragment
import com.example.toolpackapp.activities.driver.bottomNav.home.HomeFragment
import com.example.toolpackapp.activities.manager.ManagerEditPackageDetailsActivity
import com.example.toolpackapp.activities.manager.ui.addDriver.AddDriverFragment
import com.example.toolpackapp.activities.manager.ui.addBuildingSite.AddBuildingSiteFragment
import com.example.toolpackapp.activities.manager.ui.addPackage.AddPackageFragment
import com.example.toolpackapp.activities.manager.ui.addVendor.AddVendorFragment
import com.example.toolpackapp.activities.manager.ui.home.ManagerHomeFragment

import com.example.toolpackapp.models.PackageListItem
import com.example.toolpackapp.models.User
import com.example.toolpackapp.utils.getFileExtension
import com.example.toolpackapp.utils.getFileExtensionFragment
import com.example.toolpackapp.utils.hideDialog
import com.example.toolpackapp.utils.showErrorSnackBar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class FirestoreClass {

    private var mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(fragment: AddDriverFragment, userInfo: User) {
        mFireStore.collection("users")
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                fragment.driverAddedSuccess()
            }
            .addOnFailureListener { e ->
                hideDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while registering the user",
                    e
                )
            }
    }


    fun getUserDetails(activity: Activity) {
        mFireStore.collection("users")
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                if (document == null) {
                    Log.d("FirestoreClass", "There is no document for this user")
                } else {
                    val user = document.toObject(User::class.java)!!
                    when (activity) {
                        is LoginActivity -> {
                            activity.userLoggedInSuccess(user)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error while getting user", e)
            }
    }

    fun getCurrentUserDetailsAsObject(activity: Activity) {
        mFireStore.collection("users")
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!
                when (activity) {
                    is DriverMainViewActivity -> {
                        activity.setCurrentUser(user)
                    }
                }
            }

            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error while getting user", e)
            }

    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser !== null) {
            currentUserId = currentUser.uid
        }

        return currentUserId
    }


    fun getDriverPackages(fragment: Fragment) {
        val list = ArrayList<PackageListItem>()
        mFireStore.collection("users").document(getCurrentUserID()).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!
                mFireStore.collection("packages").whereEqualTo("driver", user.fullname)
                    .whereEqualTo("status", "Pending").get()
                    .addOnCompleteListener { task ->
                        val resultList = getPackageItemList(task, list)
                        when (fragment) {
                            is HomeFragment -> {
                                if (resultList.isEmpty()) {
                                    fragment.getDriverPackagesError()
                                } else {
                                    fragment.getDriverPackagesSuccess(resultList)
                                }
                            }
                        }

                    }
                    .addOnFailureListener { e ->
                        when (fragment) {
                            is HomeFragment -> {
                                fragment.getDriverPackagesError(e.message)
                            }
                        }
                    }
            }
    }

    fun getAllPackages(fragment: Fragment) {
        val list = ArrayList<PackageListItem>()

        mFireStore.collection("packages").get()
            .addOnCompleteListener { task ->
                val resultList = getPackageItemList(task, list)

                when (fragment) {
                    is ManagerHomeFragment -> {
                        if(resultList.isEmpty()){
                            fragment.getPackageItemsError()
                        }else {
                            fragment.setPackageItemsList(resultList)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is ManagerHomeFragment -> {
                        fragment.getPackageItemsError(e.message)
                    }
                }
            }
    }


    private fun getPackageItemList(
        task: Task<QuerySnapshot>,
        list: ArrayList<PackageListItem>
    ): ArrayList<PackageListItem> {
        for (d in task.result!!) {
            val id = d.id
            val name = d.getString("name")!!
            val buildingSite = d.getString("buildingSite")!!
            Log.d("FirestoreClass", "$buildingSite")
            val deliveryDate = d.getString("deliveryDate")!!
            val deliveryTime = d.getString("deliveryTime")!!
            val vendor = d.getString("vendor")!!
            val driver = d.getString("driver")!!
            val status = d.getString("status")!!
            val description = d.getString("description")!!

            val packageListItem = PackageListItem(
                name,
                deliveryDate,
                deliveryTime,
                buildingSite,
                driver,
                status,
                vendor,
                id,
                description
            )
            list.add(packageListItem)
        }
        return list
    }

    fun updateUserDetails(fragment: Fragment, hashmap: HashMap<String, Any>) {
        mFireStore.collection("users").document(getCurrentUserID())
            .update(hashmap)
            .addOnSuccessListener {
                when (fragment) {
                    is DriverAccountFragment -> {
                        fragment.userProfileCompleteSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is DriverAccountFragment -> {
                        fragment.userProfileCompleteError()
                    }
                }
                Log.e("FirestoreClass", "Error while updating user details", e)
            }
    }

    fun uploadImageToStorageFragment(fragment: Fragment, imageFileUri: Uri?) {
        Log.d("FirestoreClass", "image uri $imageFileUri")
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "user_profile_image" + System.currentTimeMillis() + "." + fragment.activity?.let {
                getFileExtension(
                    it, imageFileUri
                )
            }
        )
        sRef.putFile(imageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Log.e("Firebase Image Url", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image Url", uri.toString())
                    when (fragment) {
                        is DriverAccountFragment -> {
                            fragment.uploadImageSuccess(uri.toString())
                        }
                    }
                }
        }
    }

    fun uploadImageToStorage(activity: Activity, imageFileUri: Uri?) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "user_profile_image" + System.currentTimeMillis() + "." + getFileExtension(
                activity,
                imageFileUri
            )
        )
        sRef.putFile(imageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Log.d("Firebase Image Url", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.d("Downloadable Image Url", uri.toString())
                    when (activity) {
                        is ManagerEditPackageDetailsActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }.addOnFailureListener { exception ->
            when (activity) {
                is DriverMainViewActivity -> {
                    activity.userProfileCompleteError()
                }
                is ManagerEditPackageDetailsActivity -> {
                    activity.updatePackageError()
                }
            }
        }
    }

    fun updatePackageDetails(activity: Activity, id: String, packageMap: HashMap<String, Any>) {
        mFireStore.collection("packages").document(id)
            .update(packageMap)
            .addOnSuccessListener {
                when (activity) {
                    is ManagerEditPackageDetailsActivity -> {
                        activity.updatePackageSuccess()
                    }
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is ManagerEditPackageDetailsActivity -> {
                        activity.updatePackageError()
                    }
                }
            }

    }

    fun addNewBuildingSite(fragment: Fragment, buildingSites: HashMap<String, Any>) {
        val buildingSiteRef = mFireStore.collection("buildingSites")
        buildingSiteRef.whereEqualTo("siteName", buildingSites["siteName"]).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    when (fragment) {
                        is AddBuildingSiteFragment -> {
                            fragment.addBuildingFailure("Building site with this name already exists")
                        }
                    }
                } else {
                    buildingSiteRef.document()
                        .set(buildingSites)
                        .addOnSuccessListener {
                            when (fragment) {
                                is AddBuildingSiteFragment -> {
                                    fragment.addBuildingSuccess()
                                    fragment.clearForm()
                                }
                            }
                        }.addOnFailureListener {
                            when (fragment) {
                                is AddBuildingSiteFragment -> {
                                    fragment.addBuildingFailure()
                                    fragment.clearForm()
                                }
                            }
                        }
                }
            }


    }

    fun addNewPackage(fragment: Fragment, packageMap: HashMap<String, Any>) {
        mFireStore.collection("packages")
            .document()
            .set(packageMap)
            .addOnSuccessListener {
                when (fragment) {
                    is AddPackageFragment -> {
                        fragment.addPackageSuccess()
                        fragment.clearForm()
                    }
                }
            }
            .addOnFailureListener {
                when (fragment) {
                    is AddPackageFragment -> {
                        fragment.addPackageError()
                        fragment.clearForm()
                    }
                }
            }
    }

    fun addNewVendor(fragment: Fragment, vendorMap: HashMap<String, Any>) {
        val vendorRef = mFireStore.collection("vendors")
        vendorRef.whereEqualTo("vendor", vendorMap["vendorName"]).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    when (fragment) {
                        is AddVendorFragment -> {
                            fragment.addVendorError("Vendor with this name already exists")
                        }
                    }
                } else {
                    vendorRef.document()
                        .set(vendorMap)
                        .addOnSuccessListener {
                            when (fragment) {
                                is AddVendorFragment -> {
                                    fragment.addVendorSuccess()
                                    fragment.clearForm()
                                }
                            }
                        }.addOnFailureListener {
                            when (fragment) {
                                is AddVendorFragment -> {
                                    fragment.addVendorError()
                                    fragment.clearForm()
                                }
                            }
                        }
                }
            }
    }

    fun loadDatePicker(
        button: Button,
        deliveryDateText: EditText,
        context: Context
    ) {
        /*display datepicker dialog*/
        val format = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat("M/dd/YYYY", Locale.UK)
        } else {
            TODO("VERSION.SDK_INT < N")
        }

        button.setOnClickListener {
            val selectDate = Calendar.getInstance()
            val year = selectDate.get(Calendar.YEAR)
            val month = selectDate.get(Calendar.MONTH)
            val day = selectDate.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    selectDate.set(Calendar.YEAR, year)
                    selectDate.set(Calendar.MONTH, month)
                    selectDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val date = format.format(selectDate.time)
                    deliveryDateText.setText(date)
                }, year, month, day
            )
            dpd.show()
        }

    }


    fun loadTimePicker(button: Button, context: Context, deliveryTimeText: EditText) {
        /*display timepicker dialog*/
        button.setOnClickListener {
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
                deliveryTimeText.setText(time)
            }
            TimePickerDialog(
                context,
                timeSetListener,
                selectTime.get(Calendar.HOUR_OF_DAY),
                selectTime.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    fun loadSpinnerVendors(
        selectVendor: AutoCompleteTextView,
        context: Context,
        vendor: String? = null
    ) {
        val subjectsRef = mFireStore.collection("vendors")
        val subjects: MutableList<String> = ArrayList()
        subjects.add("Select Vendor")
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_dropdown_item_1line,
            subjects
        )
        //adapter.setDropDownViewResource(android.R.layout.select_dialog_item)
        selectVendor.setAdapter(adapter)

        subjectsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val subject = document.getString("vendorName")!!
                    subjects.add(subject)
                }
                adapter.notifyDataSetChanged()

                var position: Int? = null
                if (vendor !== null) {
                    selectVendor.setText(vendor, false)
                } else {
                    selectVendor.setText("Select Vendor", false)
                }

            }
        }

    }

    fun loadSpinnerSelectDriver(
        selectDriver: AutoCompleteTextView,
        context: Context,
        driver: String? = null
    ) {
        val subjects: MutableList<String> = ArrayList()
        subjects.add("Select Driver")

        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_dropdown_item_1line,
            subjects
        )
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectDriver.setAdapter(adapter)

        mFireStore.collection("users").whereEqualTo("accountType", "driver")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val driverObj = document.getString("fullname")!!
                    subjects.add(driverObj)
                }
                adapter.notifyDataSetChanged()
                val text = if (driver !== null) driver else "Select Driver"
                selectDriver.setText(text, false)
            }
    }

    fun loadSpinnerBuildingSite(
        selectBuildingSite: AutoCompleteTextView,
        context: Context,
        buildingSite: String? = null
    ) {
        val subjectsRef = mFireStore.collection("buildingSites")
        val subjects: MutableList<String> = ArrayList()
        subjects.add("Select Building Site")

        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_dropdown_item_1line,
            subjects
        )
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectBuildingSite.setAdapter(adapter)

        subjectsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val subject = document.getString("siteName")!!
                    subjects.add(subject)
                }
                adapter.notifyDataSetChanged()

                var position: Int? = null
                if (buildingSite !== null) {
                    Log.d("FirestoreClass", "Entered if statement")
                    position = adapter.getPosition(buildingSite)
                }
                val text = buildingSite ?: "Select Building Site"
                selectBuildingSite.setText(text, false)
            }
        }

    }

    fun markPackageAsDelivered(fragment: Fragment, id: String, packageMap: HashMap<String, Any>) {
        mFireStore.collection("packages").document(id)
            .update(packageMap)
            .addOnSuccessListener {
                // get the updated package
                mFireStore.collection("packages").document(id).get()
                    .addOnSuccessListener { document ->
                        val packageItem = document.toObject(PackageListItem::class.java)!!

                        when (fragment) {
                            is HomeFragment -> {
                                fragment.markPackageDeliveredSuccess(packageItem)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        when (fragment) {
                            is HomeFragment -> {
                                fragment.markPackageDeliveredError(e.message)
                            }
                        }
                    }

            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is HomeFragment -> {
                        fragment.markPackageDeliveredError(e.message)
                    }
                }
            }
    }
}


