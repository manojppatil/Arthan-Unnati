package com.example.arthan.model

data class PresanctionDocsRequestData(
    val loanId:String?,
    val custId:String?,
    val userId:String?,
    val documents:ArrayList<Docs?>?
)
data class Docs(
    val docId: String?,
    val docName: String?,
    var docUrl:String,
    val docStatus: String
)