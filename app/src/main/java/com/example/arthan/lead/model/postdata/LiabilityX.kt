package com.example.arthan.lead.model.postdata

data class LiabilityX(
    val emi: String = "",
    val frequencyOfInstallment: String = "",
    val incomeId: String = "",
    val liabilityId: String = "",
    val loanAmount: String = "",
    val loanDocumentUrl: String = "",
    val loanSanctionedBy: String = "",
    val loanTenureFrom: String = "",
    val tenor: String = "",
    val emisPaid: String = "",
    val loanTenureTo: String = "",
    val loanType: String = "",
    val outstandingAmount: String = ""
)