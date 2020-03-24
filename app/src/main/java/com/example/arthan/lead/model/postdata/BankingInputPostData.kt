package com.example.arthan.lead.model.postdata

data class BankingInputPostData(
    val accName: String? = "",
    val accNo: String? = "",
    val accType: String? = "",
    val bankName: String? = "",
    val custId: String? = "",
    val entryDetails: MutableList<EntryDetail>? = mutableListOf(),
    val limit: String? = "",
    val loanId: String? = ""
)