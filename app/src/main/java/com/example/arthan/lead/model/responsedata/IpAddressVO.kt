package com.example.arthan.lead.model.responsedata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IpAddressVO(
    val ageOfCustomer: String? = "",
    val firmPan: String? = "",
    val fullName: String? = "",
    val kycPanId: String? = "",
    val title: String? = ""
): Parcelable