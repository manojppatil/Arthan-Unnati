package com.example.arthan.lead.model.responsedata

data class BaseResponseData(
    val apiCode: String?,
    val apiDesc: String?,
    val eligibility: String?,
    val discrepancy: String?="",
    val timestamp: String?,
    val status: Int?,
    val error: String?,
    val message: String?,
    val path: String?,
    val loanType: String?,
    val businessName: String?,
    val annualTurnover: String?,
    val customerId: String?,
    val loanId: String?,
    val leadId: String?,
    val canNavigate: String?="",
    val verifyStatus:String?="",
    val amMobNo: String? = "",
    val validUser: String? = "",
    val mobNo: String? = "",
    val onboarded: String = "",
    val custId: String = "",
    val appFee: String = "",
    val gst: String = "",
    val total: String = ""

)