package com.example.arthan.model

import java.io.Serializable

data class RMApprovedCaseResponse (val id: String,
val approvedCases: List<ApprovedCaseData>)

data class ApprovedCaseData(
    val caseId: String,
val name: String,
val approvedAmt: String,
val tenure: String,
val emi: String,
val loanType: String,
val loginDate: String,
val approvedDate: String,

val customerId: String,
val rcuStatus: String,
val legalStatus: String,
val techStatus: String,
val rcuReport: String,
val legalReport: String,
val techReport: String,
val feePaidStatus: String,
val roi: String,
val pf: String,
val insurance: String):Serializable

