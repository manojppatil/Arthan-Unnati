package com.example.arthan.lead.model.postdata

data class DocScreeningStatusPost(
    val  loanId :String?,
    val custId:String?,
    val userId:String?,
    val documents:ArrayList<DocumentsData>
)

 data class DocumentsData(

     val docId:String,
     val docName:String,
     val docUrl:String?,
     val docStatus:String

 )
