package com.example.arthan.lead.model.responsedata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cust360ResponseData(
    val bankDetailsVO: String?,
    val collateralVO: CollateralVO?,
    val gstDetailsVO: GstDetailsVO?,
    val ipAddressVO: ipAddressVO?,
    val pdVO: PdX?,
    val scVO: SCVO?,
    val pd1DetailsVO: Pd1?,
    val pd23DetailsVO: Pd23?,
    val pdCust: PdCust?
) : Parcelable

@Parcelize
data class PdCust(

    val applicantName: String?,
    val qualification: String?,
    val businessName: String?,
    val vintage: String?,
    val noOfEmployees: String?,
    val businessOwnership: String?,
    val noOfDeviations: String?,
    val purposeOfLoan: String?,
    val bankingWith: String?,
    val incomeConsidered: String?,
    val risk: String?,
    val strength: String?,
    val sanctionConditions: String?,
    val remarks: String?
):Parcelable
