package com.example.arthan.lead.model.responsedata

data class LoanResponseData(
    val apiCode: String?,
    val apiDesc: String?,
    val loanId: String?,
    val eligibility: String?,
    val timestamp: String?,
    val status: Int?,
    val error: String?,
    val message: String?,
    val path: String?
)