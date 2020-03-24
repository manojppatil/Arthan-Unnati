package com.example.arthan.lead.model.responsedata

data class FiEntry(
    val amount: Double,
    val narration: String,
    val noOfCashDeposits: Int,
    val noOfCredit: Int,
    val suspicionName: String
)