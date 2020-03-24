package com.example.arthan.model

const val ELIGIBILITY_SCREEN= "eligibility"
const val PAYMENT_SCREEN= "payment"

data class UpdateEligibilityAndPaymentReq (var leadId: String?,
var loanId: String?,
var currentScreen: String?)

