package com.example.arthan.lead.model.responsedata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SCVO (

    var demographicScore:String?="",
    var financialScore:String?="",
    var alternateScore:String?="",
    var customerRiskProfile:String?=""

):Parcelable{

}
