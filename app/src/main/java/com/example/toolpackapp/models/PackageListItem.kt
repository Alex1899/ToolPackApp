package com.example.toolpackapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PackageListItem(
    val name: String = "",
    val deliveryDate: String = "",
    val deliveryTime: String = "",
    val buildingSite: String = "",
    val driver: String = "",
    val status: String = "Pending",
    val vendor: String = "",
    val id: String = "",
    val description: String = ""

) : Parcelable