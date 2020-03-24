package com.example.arthan.lead.model.postdata

data class NeighborReferencePostData(
    val loanId: String? = "",
    val customerId: String? = "",
    val neighborRef: List<NeighborReference>? = listOf()
)

data class NeighborReference(
    var name: String? = "",
    var rshipWithApplicant: String? = "",
    var mobileNo: String? = "",
    var knownSince: String? = "",
    var loanId: String? = "",
    var customerId: String? = ""
)