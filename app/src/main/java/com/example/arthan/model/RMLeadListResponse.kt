package com.example.arthan.model

data class RMLeadListResponse (val rmId: String,
val leadList:List<LeadData>)

data class LeadData(val segment: String,
                         val leadDate: String,
                         val name: String,
                         val id: String,
                    val laterDate: String,
                         val branch: String,
                         val mobileNo: String)

