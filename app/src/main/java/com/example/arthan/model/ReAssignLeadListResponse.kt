package com.example.arthan.model

data class ReAssignLeadListResponse (val rmId: String,
val reAssignList: List<ReassignLeadData>)

data class ReassignLeadData(val segment: String,
val assignedDate: String,
val name: String,
val id: String,
val assignedBy: String,
val pending: String)
