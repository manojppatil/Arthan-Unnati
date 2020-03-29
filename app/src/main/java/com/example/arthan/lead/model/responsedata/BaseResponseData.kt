package com.example.arthan.lead.model.responsedata

data class BaseResponseData(
    val apiCode: String?,
    val apiDesc: String?,
    val eligibility: String?,
    val discrepancy: String?,
    val timestamp: String?,
    val status: Int?,
    val error: String?,
    val message: String?,
    val path: String?
)