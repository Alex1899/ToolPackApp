package com.example.toolpackapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import com.example.toolpackapp.R
import com.google.android.material.snackbar.Snackbar

private var dialog: Dialog? = null

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

fun showDialog(context: Context){
    dialog = Dialog(context)
    dialog?.setContentView(R.layout.dialog_progress)
    dialog?.setCancelable(false)
    dialog?.show()
}

fun hideDialog(){
    dialog?.dismiss()
}

fun showImageChooser(activity: Activity){
    val gallerryIntent = Intent(
        Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    activity.startActivityForResult(gallerryIntent, 1)

}

fun getFileExtension(activity: Activity, uri: Uri?): String? {
    return MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
}



