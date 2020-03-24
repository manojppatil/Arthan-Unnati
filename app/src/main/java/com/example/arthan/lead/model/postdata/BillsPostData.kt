package com.example.arthan.lead.model.postdata

data class BillsPostData(
    val billDetails: List<BillDetail>? = mutableListOf(),
    val custId: String? = "",
    val loanId: String? = ""
)