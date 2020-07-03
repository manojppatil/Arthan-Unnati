package com.example.arthan.model

 class ScreeningNavDataResponse (

    val loanId:String?,
    val customerId:String?="",
    val leadId :String?="",
    val continueScreen:String?="",
    val inPrincpAmt:String?="",
    val completedScreens:ArrayList<String> = ArrayList()
){


}