package com.example.arthan.lead.model.postdata

data class LoanPostData(
    var leadId: String? = "",
    var loanId:String?="",
    var loanAmount: String? = "",
    var tenure: String? = "",
    var tenorMonth: String? = "",
    var loanType: String? = "",
    var purposeofLoan: String? = "",
    var securityJurisdiction: String? = "",
    var propertyValue: String? = "",
    var turnover: String? = "",
    var turnoverFreq: String? = "",
    var netprofitMargin: String? = "",
    var existingLoan: String? = "",
    var existingLoanObligationPm: String? = "",
    var additionalIncomePm: String? = "",
    var householdExpensesPm: String? = "",
    var collateralType: String? = "",


    var indiNonindiLoan: String? = "",
    var ageofCustomer: String? = "",
    var businessVintage: String? = "",
    var createdBy: String? = "",
    var userId: String? = "",
    var securityOffered: String? = "",
    var rcuReceived: String? = "",
    var legalReceived: String? = "",
    var techReceived: String? = "",
    var rcuReportStatus: String? = "",
    var legalReportStatus: String? = "",
    var techReportStatus: String? = "",
    var loanLevel: String? = "",
    var loanLevelApproval: String? = "",
    var loanDate: String? = "",
    var applicantName: String? = "",
    var loanAmt: String? = "",
    var branch: String? = "",
    var netMonthlyIncome: String? = ""

)