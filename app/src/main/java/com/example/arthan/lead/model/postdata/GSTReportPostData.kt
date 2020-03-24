package com.example.arthan.lead.model.postdata

data class GSTReportPostData(
    var consent: String? = "",
    var gstin: String? = "",
    var password: String? = "",
    var username: String? = "",
    var customerId: String? = "",
    var loanId: String? = ""

)