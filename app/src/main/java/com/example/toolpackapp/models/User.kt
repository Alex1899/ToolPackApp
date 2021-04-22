package com.example.toolpackapp.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User(
    val id: String = "",
    val fullname: String ="",
    val email: String ="",
    val photo: String = "",
    val mobile: String = "",
    val accountType: String = "",
    val profileCompleted: Int = 0
): Parcelable