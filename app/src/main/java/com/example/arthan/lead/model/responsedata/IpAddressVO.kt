package com.example.arthan.lead.model.responsedata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ipAddressVO(
    var loanId:String?="",
    var idAddrData:ArrayList<IdAddrData>?
):Parcelable
@Parcelize
data class IdAddrData(
    val ageOfCustomer: String? = "",
    val firmPan: String? = "",
    val fullName: String? = "",
    val kycPanId: String? = "",
    val title: String? = "",
    val dob: String? = "",
    val applicantType: String? = "",
    val idText: String? = "",
    val idVal: String? = "",
    val idUrl: String? = "",
    val addressText: String? = "",
    val addressVal: String? = "",
    val addressUrl: String? = ""
):Parcelable{

}

