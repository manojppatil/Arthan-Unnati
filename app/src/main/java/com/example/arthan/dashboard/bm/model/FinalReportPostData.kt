package com.example.arthan.dashboard.bm.model

data class FinalReportPostData(
    var loanId:String?,
var custId:String? ,
var decision:String? ,
var reason:String? ,
var remarks:String? ,
var supportingDoc:String,
var sanctionConditions:ArrayList<String>,
    var userId:String?,
    var amId:String?
)