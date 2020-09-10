package com.example.arthan.model

 class SubmitMultipleDocsRequest() {
     var loanId: String?=null
     var userId: String?=null
     val businessDocs: ArrayList<DocsRequest>?= ArrayList()
     val kycDocs: ArrayList<DocsRequest>?= ArrayList()
     val residentialDocs: ArrayList<DocsRequest>?= ArrayList()
     val bussPremisesDocs: ArrayList<DocsRequest>?=ArrayList()


 }

data class DocsRequest(
  val  docId:String,
  val  docName:String,
  val  docUrl:String
)
