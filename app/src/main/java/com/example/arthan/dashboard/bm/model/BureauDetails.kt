package com.example.arthan.dashboard.bm.model

import com.google.gson.annotations.SerializedName

data class BureauDetails(
    @SerializedName("name")
    val name: String?,
    @SerializedName("age")
    val age: String?,
    @SerializedName("noOfInquiries")
    val inquiryCount: String?,
    @SerializedName("fatherName")
    val fatherName: String?
)