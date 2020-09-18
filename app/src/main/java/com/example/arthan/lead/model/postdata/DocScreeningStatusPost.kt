package com.example.arthan.lead.model.postdata

import com.example.arthan.lead.model.responsedata.RequireDocs

data class DocScreeningStatusPost(
    val  loanId :String?,
    val custId:String?,
    val userId:String?,
    val documents:ArrayList<DocumentsData>,
    val amId:String?
)
data class DocScreeningStatusPostNew(
    val  loanId :String?,
    val custId:String?,
    val userId:String?,
    val documents:ArrayList<RequireDocs>,
    val amId:String?
)
 data class DocumentsData(

     val docId:String,
     val docName:String,
     val docUrl:String?,
     val docStatus:String

 )
