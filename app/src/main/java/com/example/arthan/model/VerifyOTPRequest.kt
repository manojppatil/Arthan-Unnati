package com.example.arthan.model

data class VerifyOTPRequest (val loanId: String,
val customerId: String,
val leadId: String,
val consent: String,
val amt: String,
val otp: String)