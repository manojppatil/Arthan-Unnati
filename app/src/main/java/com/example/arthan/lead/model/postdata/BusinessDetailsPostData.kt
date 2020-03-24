package com.example.arthan.lead.model.postdata

import com.google.gson.annotations.SerializedName

data class BusinessDetailsPostData(
    var bname: String? = "",
    var form6061: String? = "",
    var firmpan: String? = "",
    var constitution: String? = "",
    var dateofincorporation: String? = "",
    var loanId : String? = "",
    var customerId : String? = "",
    var udhyogaadhar: String? = "",
    var gstcode: String? = "",
    var noofemployees: String? = "",
    var ssiregistrationno: String? = "",
    var emailid: String? = "",
    var landline_mobile: String? = "",
    var partners: List<Partner> = mutableListOf(Partner()),
    var whatsappno: String? = "",
    var contactpersonname: String? = "",
    var annualturnover: String? = "",
    @SerializedName("annualturnoverofcurrentfinancialyear(lastfinancialyear)")
    var annualturnoverofcurrentfinancialyear_lastfinancialyear: String? = "",
    var annualturnoverofpreviousfinancialyear: String? = "",
    var natureofassociation: String? = "",
    @SerializedName("no.ofyearsincurrentoffice")
    var no_ofyearsincurrentoffice: String? = "",
    var operatingbusinessaddress: String? = "",
    var projectedturnover: String? = "",
    var registeredbusinessaddress: String? = "",


    var addressline1: String? = "",
    var addressline2: String? = "",
    var addressofbusinesslocation: String? = "",
    var areaname: String? = "",
    var associateFirms: List<AssociateFirm> = mutableListOf(AssociateFirm()),
    var businessactivity: String? = "",
    var city: String? = "",
    var district: String? = "",
    var landmark: String? = "",
    var pincode: String? = "",
    var sameasregisteredaddress: String? = "",
    var state: String? = ""
)