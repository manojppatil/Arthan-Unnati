package com.example.arthan.lead.model.responsedata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GstDetailsVO(
    val gstId: String? = "",
    val last12MonthsPurchaseInvVal: String?,
    val last12MonthsPurchaseTaxVal: String?,
    val purchaseNoOfInvoices: String?,
    val purchaseNoofStates: String?,
    val retPeriod: String? = "",
    val salesNoOfInvoices: String?,
    val salesNoofStates: String?,
    val top3CusShare: String?,
    val top3SupShare: String?,
    val turnover: String?
): Parcelable