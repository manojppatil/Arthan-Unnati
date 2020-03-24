package com.example.arthan.lead.model.postdata

data class ObligDetail(
    val consideredFoir: String? = null,
    val emi: String? = null,
    val emisBounced: String? = null,
    val emisOutstanding: String? = null,
    val emisPaid: String? = null,
    val financierName: String? = null,
    val loanAmt: String? = null,
    val loanOutstanding: String? = null,
    val loanTknBy: String? = null,
    val loanType: String? = null,
    val tenor: String? = null
)