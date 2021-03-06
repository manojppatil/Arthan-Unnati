package com.example.arthan.lead.model.responsedata

data class LeadResponseData(
    val apiCode: String?,
    val apiDesc: String?,
    val leadId: String?,
    val timestamp: String?,
    val status: Int?,
    val error: String?,
    val message: String?,
    val canNavigate: String?,
    val path: String?,
    val minLoanAmount: String?,
    val maxLoanAmount: String?,
    val minTenure: String?,
    val maxTenure: String?
)