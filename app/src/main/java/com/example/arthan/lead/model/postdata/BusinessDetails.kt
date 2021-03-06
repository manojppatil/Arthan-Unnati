package com.example.arthan.lead.model.postdata

import com.google.gson.annotations.SerializedName

data class BusinessDetails(
    val addressline1: String?,
    val addressline2: String?,
    val addressofbusinesslocation: String?,
    val annualturnover: String?,
    @SerializedName("annualturnoverofcurrentfinancialyearLastfinancialyear")
    val annualturnoverofcurrentfinancialyearLastfinancialyear: String?,
    val annualturnoverofpreviousfinancialyear: String?,
    val areaname: String?,
    val associateFirms: List<AssociateFirmX>,
    val businessId: String?,
    val businessactivity: String?,
    val city: String?,
    val constitution: String?,
    val contactpersonname: String?,
    val dateofincorporation: String?,
    val district: String?,
    val emailid: String?,
    val firmpan: String?,
    @SerializedName("form60/61")
    val form60_61: String?,
    val gstcode: String?,
    @SerializedName("landlineMobile")
    val landlineMobile: String?,
    val landmark: String?,
    val bname: String?,
    val natureofassociation: String?,
    @SerializedName("noOfyearsincurrentoffice")
    val noOfyearsincurrentoffice: String?,
    val noofemployees: String?,
    val operatingbusinessaddress: String?,
    val partners: List<PartnerX>,
    val pincode: String?,
    val projectedturnover: String?,
    val registeredbusinessaddress: String?,
    val sameasregisteredaddress: String?,
    val ssiregistrationno: String?,
    val state: String?,
    val udhyogaadhar: String?,
    val whatsappno: String?
)