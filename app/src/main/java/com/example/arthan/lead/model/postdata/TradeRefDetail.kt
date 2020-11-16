package com.example.arthan.lead.model.postdata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TradeRefDetail(
    val contactDetails: String? = "",
    val firmName: String? = "",
    val loanId: String? = "",
    val nameofPersonDealingWith: String? = "",
    val noOfYrsWorkingWith: String? = "",
    val productPurchaseSale: String? = "",
    val rshipWithApplicant: String? = "",
    var customerId: String? = "",

    val tradeRefId: String? = ""
):Parcelable