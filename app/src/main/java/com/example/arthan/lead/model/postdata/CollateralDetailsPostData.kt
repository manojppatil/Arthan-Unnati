package com.example.arthan.lead.model.postdata

data class CollateralDetailsPostData(
    var propertyType: String? = "",
    var loanId: String? = "",
    var propertyJurisdiction: String? = "",
    var noOfTenants: String? = "",
    var natureofProperty: String? = "",
    var marketvarue: String? = "",
    var landArea: String? = "",
    var distFromBranch: String? = "",
    var constructionArea: String? = "",
    var customerId: String? = "",

    var addressline1: String? = "",
    var addressline2: String? = "",
    var areaname: String? = "",
    var city: String? = "",
    var district: String? = "",
    var landmark: String? = "",
    var pincode: String? = "",
    var state: String? = ""
)