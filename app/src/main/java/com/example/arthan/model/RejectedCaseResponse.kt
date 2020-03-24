package com.example.arthan.model

data class RejectedCaseResponse(val id: String,
val rejectedList: List<RejectedCaseData>)

data class RejectedCaseData(val caseId: String,
                            val name: String,
                            val loanAmount: String,
                            val rejectionReason: String,
                            val rejectionDate: String,
                            val loginDate: String)
