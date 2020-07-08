package com.example.arthan.dashboard.am.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AMKYCDetailsData (
    var aadharAddress: String = "",
    var aadharBackUrl: String = "",
    var aadharFrontUrl: String = "",
    var aadharId: String = "",
    var aadharVerified: String = "",
    var applicantType: String = "",
    var loanId: String = "",
    var paApplicantPhoto: String = "",
    var panDob: String = "",
    var panFathername: String = "",
    var panFirstname: String = "",
    var panId: String = "",
    var panUrl: String = "",
    var panVerified: String = "",
    var voterId: String = "",
    var voterUrl: String = "",
    var voterVerified: String = "",
    var amId: String = ""

):Parcelable