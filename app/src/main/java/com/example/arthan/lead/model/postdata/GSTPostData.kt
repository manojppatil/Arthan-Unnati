package com.example.arthan.lead.model.postdata

data class GSTPostData(
    val custId: String? = "",
    val gstDetails: List<GstDetail>? = mutableListOf(),
    val loanId: String? = ""
)