package com.example.arthan.lead.model.postdata

data class ObligationPostData(
    val custId: String? = null,
    val loanId: String? = null,
    val obligDetails: List<ObligDetail>? = null
)