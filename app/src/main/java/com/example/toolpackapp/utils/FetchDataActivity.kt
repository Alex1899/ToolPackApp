package com.example.toolpackapp.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.toolpackapp.R

class FetchDataActivity: AppCompatActivity() {

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}