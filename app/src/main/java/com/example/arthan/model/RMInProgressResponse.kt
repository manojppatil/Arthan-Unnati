package com.example.arthan.model

data class RMInProgressResponse(
    val casesData:ArrayList<CasesData>?=null,
    val eid:String=""
) {
}
data class CasesData(
    val caseId:String?=null,
    val loanAmt:String?=null,
    val loginDate:String?=null,
    val currentStage:String?=null,
    val cname:String?=null
)