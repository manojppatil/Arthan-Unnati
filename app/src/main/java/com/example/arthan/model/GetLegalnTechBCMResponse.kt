package com.example.arthan.model

data class GetLegalnTechBCMResponse(

    val userId:String,
    val loanId:String,
    val ltvOld:String,
    val ltvNew:String,
    val loanAmtOld:String,
    val loanAmtNew:String,
    val techRptUrl:String,
    val legalRptUrl:String
) {

}
