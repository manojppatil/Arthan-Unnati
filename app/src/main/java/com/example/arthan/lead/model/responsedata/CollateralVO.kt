package com.example.arthan.lead.model.responsedata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CollateralVO(
    val addressline1: String? = "",
    val addressline2: String? = "",
    val areaname: String? = "",
    val city: String? = "",
    val collateralId: String? = "",
    val constructionArea: String? = "",
    val customerId: String?,
    val distFromBranch: String? = "",
    val district: String? = "",
    val landArea: String? = "",
    val landmark: String? = "",
    val loanId: String?,
    val marketValue: String? = "",
    val natureofProperty: String? = "",
    val noOfTenants: String? = "",
    val pincode: String? = "",
    val propertyJurisdiction: String? = "",
    val propertyType: String? = "",
    val state: String? = "",
    val ownerName: String? = "",
    val address: String? = "",
    val collateralType: String? = "",
    val occupiedBy: String? = "",
    val areaOfProperty: String? = "",
    val distressValue: String? = "",
    val boundaryMatch: String? = "",
    val ltvConsidered: String? = "",
    val remarks: String? = ""
): Parcelable
