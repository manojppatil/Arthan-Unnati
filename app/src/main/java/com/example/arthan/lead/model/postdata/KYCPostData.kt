package com.example.arthan.lead.model.postdata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KYCPostData(
    var loanId: String? = "",
    var customerId: String? = "",

    var aadharAddress: String? = "",
    var aadharId: String? = "",
    var aadharFrontUrl: String? = "",
    var aadharBackUrl: String? = "",
    var aadharVerified: String? = "",

    var panFathername: String? = "",
    var panFirstname: String? = "",
    var panId: String? = "",
    var panLastname: String? = "",
    var panUrl: String? = "",
    var panVerified: String? = "",
    var panDob: String? = "",

    var voterId: String? = "",
    var voterUrl: String? = "",
    var voterVerified: String? = "",

    var paApplicantPhoto: String? = "",
    var applicantType: String? = "",

    var amId: String? = ""
) : Parcelable