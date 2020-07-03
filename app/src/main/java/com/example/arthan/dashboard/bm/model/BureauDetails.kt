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
    val lastLoanTaken: String,
    val activeDetails: ArrayList<InnerDetailsBanking>?,
    val secureDetails: ArrayList<InnerDetailsBanking>?,
    val unsecureDetails: ArrayList<InnerDetailsBanking>?,
    val ccGoldAgriDetails: ArrayList<InnerDetailsBanking>?,
    val autoOtherDetails: ArrayList<InnerDetailsBanking>?,
    val suitWrittenDetails: ArrayList<InnerDetailsBanking>?,
    val guarantorDetails: ArrayList<InnerDetailsBanking>?,
    val historyDetails: ArrayList<Last6MonthsHistory>?

) : Parcelable

@Parcelize
data class Last6MonthsHistory (

    val memberName:String?,
    val inquiryDate:String?,
    val purpose:String?,
    val amount:String?,
    val remark:String?
):Parcelable

@Parcelize
data class InnerDetailsBanking(

    val accountType: String?,
    val amount: String?,
    val disbDate: String?,
    val details: DetailsBanking3?
):Parcelable
@Parcelize

data class DetailsBanking3(

    val accountType: String?,
    val ownership: String?,
    val disbursedAmt: String?,
    val disbursedDate: String?,
    val lastPaymentDate: String?,
    val overdueAmount: String?,
    val writeOffAmount: String?,
    val currentBalance: String?,
    val securityStatus: String?,
    val principalWriteOffAmount: String?,
    val writeoffSettleStatus: String?

    ):Parcelable