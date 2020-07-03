package com.example.arthan.dashboard.bm.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BureauDetails(
    @SerializedName("name")
    val name: String?,
    @SerializedName("age")
    val age: String?,
    @SerializedName("noOfInquiries")
    val inquiryCount: String?,
    @SerializedName("fatherName")
    val fatherName: String?,
    val applicantName: String,
    var score: String,
    var noOfLoans: String,
    var noOfActiveLoans: String,
    var noOfUnsecuredLoans: String,
    val noOfSecuredLoans: String,
    val noOfDpdAccounts: String,
    val defaultCGA: String,
    val defaultAO: String,
    val noOfSuitfiledWritten: String,
    val noOfLoanAsGuarantor: String,
    val sixMonthsHistory: String,
    val lastLoanTaken: String
):Parcelable