package com.example.arthan.model

 class ScreeningNavDataResponse (

    val loanId:String?,
    val customerId:String?="",
    val leadId :String?="",
    val continueScreen:String?="",
    val inPrincpAmt:String?="",
    val mobNo: String?="",
    val appFee: String?="",
    val gst: String?="",
    val total: String?="",
    val completedScreens:ArrayList<String> = ArrayList()
){


}