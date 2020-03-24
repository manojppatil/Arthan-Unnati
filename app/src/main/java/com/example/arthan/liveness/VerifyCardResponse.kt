package com.example.arthan.liveness

import com.google.gson.annotations.SerializedName

data class VerifyCardResponse(
    @SerializedName("request_id") val requestIdd: String?,
    @SerializedName("result_code") val resultCode: Int?,
    @SerializedName("result_message") var resultMessage: String?,
    var status: String?,
    var reason: String?,
    var result: VerifyCardResult?
)

data class VerifyCardResult(
    @SerializedName("pan_number") var panNumber: String,
    @SerializedName("pan_status") var panStatus: String,
    @SerializedName("first_name") var firstName: String,
    @SerializedName("last_name") var lastName: String,
    @SerializedName("middle_name") var middleName: String,
    @SerializedName("pan_holder_title") var panHolderTitle: String,
    @SerializedName("age_band") var ageBand: String,
    @SerializedName("gender") var gender: String,
    @SerializedName("mobile_number") var mobileNumber: String,
    @SerializedName("state") var state: String
)