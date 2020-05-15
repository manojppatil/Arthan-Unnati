package com.example.arthan.dashboard.bm.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Banking360DetailsResponseData(
    var bankName: String,
    var periodStart: String,
    var periodEnd: String,
    var totalNetCredits: String,
    var totalCashDeposit: String,
    var inwardBounce: String,
    var outwardBounce: String,
    var emiCount: String?="",
    var emiAmt: String,
    var avgBankBal: String,
    val emiEntries: ArrayList<EntriesType>?= ArrayList(),
    val cashEntries: ArrayList<EntriesType>?=ArrayList(),
    val creditEntries: ArrayList<EntriesType>?=ArrayList()

):Parcelable

@Parcelize
data class EntriesType(
    val txnDate: String?,
    val narration: String?,
    val amount: String?
):Parcelable