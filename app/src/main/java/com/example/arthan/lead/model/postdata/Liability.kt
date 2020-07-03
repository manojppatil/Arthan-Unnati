package com.example.arthan.lead.model.postdata

data class Liability(
    val emi: String? = "",
    val frequencyOfInstallment: String? = "",
    val loanAmount: String? = "",
    val loanDocumentUrl: String? = "",
    val loanSanctionedBy: String? = "",
    val loanTenureFrom: String? = "",
    val loanTenureTo: String? = "",
    val loanType: String? = "",
    val outstandingAmount: String? = "",
    val ownerName:String?,
    val address:String?,
    val considerCFA:Boolean?
)