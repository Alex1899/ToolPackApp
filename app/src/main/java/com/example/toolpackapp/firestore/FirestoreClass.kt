package com.example.toolpackapp.firestore


import android.app.Activity
import android.net.Uri
import android.util.Log
import com.example.toolpackapp.activities.LoginActivity
import com.example.toolpackapp.activities.driver.DriverUpdateDetailsActivity
import com.example.toolpackapp.activities.manager.ui.addDriver.AddDriverFragment
import com.example.toolpackapp.models.User
import com.example.toolpackapp.utils.getFileExtension
import com.example.toolpackapp.utils.hideDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


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
                Log.e(fragment.javaClass.simpleName, "Error while registering the user", e)
            }

    }

    fun getUserDetails(activity: Activity) {
        mFireStore.collection("users")
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i("FirestoreClass", document.toString())
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

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser !== null) {
            currentUserId = currentUser.uid
        }

        return currentUserId
    }

    fun updateUserDetails(activity: Activity, hashmap: HashMap<String, Any>) {
        mFireStore.collection("users").document(getCurrentUserID())
            .update(hashmap)
            .addOnSuccessListener {
                when (activity) {
                    is DriverUpdateDetailsActivity -> {
                        activity.userProfileCompleteSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is DriverUpdateDetailsActivity -> {
                        activity.userProfileCompleteError()
                    }
                }
                Log.e("FirestoreClass", "Error while updating user details", e)
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
            Log.e("Firebase Image Url", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image Url", uri.toString())
                    when (activity) {
                        is DriverUpdateDetailsActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }
            .addOnFailureListener { exception ->
                when (activity) {
                    is DriverUpdateDetailsActivity -> {
                        activity.userProfileCompleteError()
                    }
                }
            }
    }
    }

