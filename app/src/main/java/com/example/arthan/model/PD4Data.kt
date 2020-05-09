package com.example.arthan.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@Parcelize
data class PD4Data(
    val loanId: String?,
    val assets: ArrayList<Assets>?
) : Parcelable

@Parcelize
data class Assets(
    val assetType: String?,
    val ownerName: String?,
    val purchaseYear: String?,
    val purchaseValue: String?,
    val currentValue: String?,
    val assetAddress: String?
) : Parcelable
