package com.example.arthan.model

import java.io.Serializable

data class VerifyOTPResponse(
    val loanId: String,
    val customerId: String,
    val appFee: String,
    val gst: String,
    val qrCode: String,
    val total: String,
    val verifyStatus: String
) : Serializable