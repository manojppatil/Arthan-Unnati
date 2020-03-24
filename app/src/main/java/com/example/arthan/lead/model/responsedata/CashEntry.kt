package com.example.arthan.lead.model.responsedata

data class CashEntry(
    val amount: Double,
    val narration: String,
    val noOfCashDeposits: Int,
    val noOfCredit: Int
)