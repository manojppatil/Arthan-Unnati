package com.example.arthan.model

data class GetRMOpsCasesResponse(

    val loanId:String,
    val documents:ArrayList<DocumentsList>
)

data class DocumentsList(
    var docId:String,
    var docName:String,
    var docUrl:String?,
    var docStatus:String
)
