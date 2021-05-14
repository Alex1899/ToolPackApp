package com.example.toolpackapp.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Vendor(
    val id: String = "",
    val vendorName: String ="",
    val vendorEmail: String ="",
    val vendorAddress: String = "",
    val vendorPhone: String = "",
): Parcelable