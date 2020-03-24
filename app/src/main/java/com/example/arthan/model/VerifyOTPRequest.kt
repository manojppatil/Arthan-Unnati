package com.example.arthan.model

data class VerifyOTPRequest (val loanId: String,
val customerId: String,
val otp: String)