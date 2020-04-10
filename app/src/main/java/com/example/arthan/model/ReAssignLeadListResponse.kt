package com.example.arthan.model

data class ReAssignLeadListResponse (val rmId: String,
val details: List<ReassignLeadData>)

data class ReassignLeadData(
    val segment: String,
val assignedDate: String,
val cname: String,
val loginDate: String,
val loanId: String,
val assignedBy: String,
val pending: List<String>)