package com.example.toolpackapp.activities

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar


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


