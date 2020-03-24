package com.example.arthan.lead.model.responsedata

data class Individual(
    val aadharBackUrl: String?,
    val aadharFrontUrl: String?,
    val aadharNo: String?,
    val applicantType: String?,
    val dob: String?,
    val name: String?,
    val panNo: String?,
    val panUrl: String?,
    val panVerified: String?,
    val voterId: String?,
    val voterUrl: String?
)