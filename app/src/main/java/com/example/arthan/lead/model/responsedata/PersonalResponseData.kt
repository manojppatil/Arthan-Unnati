package com.example.arthan.lead.model.responsedata

data class PersonalResponseData(
    val apiCode: String?,
    val apiDesc: String?,
    val customerId: String?,
    val inPrincipleLnAmt: String?,
    val timestamp: String?,
    val status: Int?,
    val error: String?,
    val onboarded: String = "",
    val message: String?,
    val path: String?,
    val loanId:String?

)