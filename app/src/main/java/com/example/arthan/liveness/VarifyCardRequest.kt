package com.example.arthan.liveness

import com.google.gson.annotations.SerializedName

data class VerifyPanRequest(@SerializedName("pan") val pan: String?)
data class VerifyAadhareRequest(@SerializedName("aadhaar_no") val aadhaarNo: String?)
data class VerifyVoterIdRequest(
    @SerializedName("epic_no") val pan: String?,
    @SerializedName("consent") val consent: String?,
    @SerializedName("consent_text") val consentText: String?
)