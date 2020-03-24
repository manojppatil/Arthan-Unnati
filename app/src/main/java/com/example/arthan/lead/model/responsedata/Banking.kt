package com.example.arthan.lead.model.responsedata

data class Banking(
    val acType: String,
    val avgBnkBal: String,
    val bankName: String,
    val crEntries: List<Any>,
    val cshEntries: List<Any>,
    val emiAmt: String,
    val emiCount: String,
    val endDate: String,
    val fraudEntries: List<Any>,
    val inwardCount: String,
    val outwardCount: String,
    val startDate: String,
    val top3Credits: String,
    val top3Debits: String,
    val totalCashDeposit: String,
    val totalCashEntries: String,
    val totalNetCredits: String
)