package com.example.arthan.lead.model.responsedata

import com.example.arthan.lead.model.*

data class StatementReportResponseData(
    val apiCode: String,
    val apiDesc: String,
    val avgBnkBal: String,
    val bankName: String,
    val crEntries: List<CrEntry>,
    val cshEntries: List<CshEntry>,
    val emiAmt: String,
    val emiCount: String,
    val fiEntries: List<FiEntry>,
    val frecEntries: List<CashEntry>,
    val fremEntries: List<CashEntry>,
    val inwardBounce: String,
    val outwardBounce: String,
    val totalCashDeposit: String,
    val totalNetCredits: String
)