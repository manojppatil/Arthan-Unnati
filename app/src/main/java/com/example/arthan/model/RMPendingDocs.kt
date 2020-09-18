package com.example.arthan.model

data class RMPendingDocs(
var loanId:String,
var pendingDocs:ArrayList<RMDocs> = ArrayList()
)
{

}

data class RMDocs(

    var loanId: String,
    var customerId: String,
    var docId: String,
    var docName: String,
    var docStatus: String,//Upload/PDD/OTC
    var docRemarks: String,
    var docUrl: String
)