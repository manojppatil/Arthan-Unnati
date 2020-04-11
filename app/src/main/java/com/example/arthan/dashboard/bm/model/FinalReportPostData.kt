package com.example.arthan.dashboard.bm.model

data class FinalReportPostData(
    var loanId:String?,
var custId:String? ,
var bmDecision:String? ,
var rejectReason:String? ,
var remarks:String? ,
var supportingDoc:String,
var sanctionConditions:ArrayList<String>
)