package com.example.toolpackapp.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class BuildingSite(
    val id: String = "",
    val siteName: String ="",
    val siteAdminFullname: String ="",
    val siteAdminEmail: String ="",
    val siteAddress: String = "",
    val sitePhone: String = "",
): Parcelable