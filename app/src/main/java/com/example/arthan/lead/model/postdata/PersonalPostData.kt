package com.example.arthan.lead.model.postdata

data class PersonalPostData(
    var title: String? = "",
    var fullName: String = "",
    var custId : String? = "",
    var fatherOrSpousename: String = "",
    var motherName: String = "",
    var dob: String = "",
    var contactNo: String = "",
    var email: String = "",
    var gender: String = "",
    var nationality: String = "",
    var educationlevel: String = "",
    var occupationType: String = "",
    var occupation: String = "",
    var sourceofIncome: String = "",
    var grossannualIncome: String = "",
    var addressLine1: String = "",
    var addressLine2: String = "",
    var landmark: String = "",
    var pinCode: String = "",
    var areaName: String = "",
    var city: String = "",
    var district: String = "",
    var state: String = "",
    val addrFlag:Boolean=true,
    var addressLine1p: String = "",
    var addressLine2p: String = "",
    var landmarkp: String = "",
    var pinCodep: String = "",
    var areaNamep: String = "",
    var cityp: String = "",
    var districtp: String = "",
    var statep: String = "",
    var loanId: String? = "",
    var applicantPanNo: String? = "",

    var ageofCustomer: String = "",
    var applicantType: String = "",
    var maidenName: String = "",
    var rshipWithApplicant: String = ""
)