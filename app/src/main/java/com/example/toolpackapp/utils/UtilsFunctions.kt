package com.example.toolpackapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment
import com.example.toolpackapp.R
import com.example.toolpackapp.firebaseNotifications.PushNotification
import com.example.toolpackapp.firebaseNotifications.RetrofitInstance
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


var dialog: Dialog? = null

fun showErrorSnackBar(view: View, message: String, errorMessage: Boolean) {
    val snackBar =
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    val snackBarView = snackBar.view

    if (errorMessage) {
        snackBarView.setBackgroundColor(Color.parseColor("#F72400"))
    } else {
        snackBarView.setBackgroundColor(Color.parseColor("#8BC34A"))
    }
    snackBar.show()
}

fun showDialog(context: Context) {
    dialog = Dialog(context)
    dialog?.setContentView(R.layout.dialog_progress)
    dialog?.setCancelable(false)
    dialog?.show()
}

fun hideDialog() {
    dialog?.dismiss()
    dialog = null
}

fun showImageChooser(activity: Activity) {
    val gallerryIntent = Intent(
        Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    activity.startActivityForResult(gallerryIntent, 1)
}

fun showImageChooserFragment(fragment: Fragment) {
    val gallerryIntent = Intent(
        Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    fragment.startActivityForResult(gallerryIntent, 1)
}


fun getFileExtension(activity: Activity, uri: Uri?): String? {
    return MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
}

fun getFileExtensionFragment(fragment: Fragment, uri: Uri?): String? {
    return MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(fragment.activity?.contentResolver?.getType(uri!!))
}


/*
* Sets and resets the text field error status.
*/
fun setErrorTextField(textField: TextInputLayout, error: Boolean, msg: String) {
    if (error) {
        textField.isErrorEnabled = true
        textField.error = msg
    } else {
        textField.isErrorEnabled = false
        //textInputEditText.text = null
    }
}



fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
    val TAG = "Notification"
    try {
        val response = RetrofitInstance.api.postNotification(notification)
        if(response.isSuccessful){
            Log.d(TAG, "Response: ${Gson().toJson(response)}")
        }else{
            Log.e(TAG, response.errorBody().toString())
        }
    } catch (e: Exception){
        Log.e(TAG, e.toString())
    }
}

fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }
    val bitmap =
        Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun getAddress(latLng: LatLng, context: Context): String {
    // 1
    val geocoder = Geocoder(context)
    val addresses: List<Address>?
    val address: Address?
    var addressText = ""

    try {
        // 2
        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        // 3
        if (null != addresses && addresses.isNotEmpty()) {
            address = addresses[0]
            if(addresses.size == 1){
                addressText = address.getAddressLine(0)
                Log.d("Maps", "Address text => $addressText")

            }else{
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(
                        i
                    )
                }
            }
        }
    } catch (e: IOException) {
        Log.e("MapsActivity", e.localizedMessage)
    }

    Log.d("Maps", addressText)
    return addressText
}

fun getLocationFromPostcode(postcode: String, activity: Activity): LatLng? {
    val mGeocoder = Geocoder(activity, Locale.getDefault())
    val addresses: List<Address>? = mGeocoder.getFromLocationName(postcode, 1)
    Log.d("Maps", "address from postcode: $postcode => $addresses")
    var p: LatLng? = null

    if (addresses != null && addresses.isNotEmpty()) {
        val location: Address = addresses[0]
        p = LatLng(
            location.latitude,
            location.longitude
        )
    }
    return p
}


fun hasOnlyLetters(et: TextInputEditText): Boolean {
    val ps: Pattern = Pattern.compile("^[a-zA-Z ]+$")
    val ms: Matcher = ps.matcher(et.text.toString())
    return ms.matches()
}




