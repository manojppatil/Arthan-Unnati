package com.example.arthan.lead.model.postdata

data class CollateralDetailsPostData(
    var loanId: String? = "",
       var custId:String?="",
      var  securityType:String?="",
      var  securitySubType:String?="",
       var immovableSubType:String?="",
       var plotType:String?="",
       var namunaType:String?="",
      var  occupiedBy:String?="",
      var  natureOfDoc:String?="",
       var typeOfDoc:String?="",
       var docDesc:String?="",
      var  docStatus:String?=""



)